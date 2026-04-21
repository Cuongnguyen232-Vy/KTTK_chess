/**
 * websocket.js
 * Xử lý kết nối WebSocket (STOMP over SockJS)
 * - Kết nối đến server
 * - Lắng nghe lời mời thách đấu
 * - Gửi/nhận lời mời
 */

let stompClient = null;
let pendingInvitations = [];
let pendingChallenge = null;

/**
 * Khởi tạo kết nối WebSocket
 * @param {string} username - Tên đăng nhập của người dùng hiện tại
 * @param {number} userId   - ID của người dùng hiện tại
 */
function initWebSocket(username, userId) {
    if (!username) return;

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Tắt debug log

    stompClient.connect({}, function(frame) {
        console.log('✅ WebSocket kết nối thành công:', frame);

        // Lắng nghe lời mời thách đấu gửi đến mình
        stompClient.subscribe('/user/queue/invite', function(message) {
            const payload = JSON.parse(message.body);
            onInviteReceived(payload);
        });

        // Lắng nghe thông báo game bắt đầu
        stompClient.subscribe('/user/queue/game-start', function(message) {
            const payload = JSON.parse(message.body);
            onGameStart(payload);
        });

        // Lắng nghe thông báo lời mời bị từ chối
        stompClient.subscribe('/user/queue/invite-rejected', function(message) {
            hideModal('waiting-modal');
            showAlert('❌ Đối thủ đã từ chối lời mời!');
        });

    }, function(error) {
        console.error('❌ WebSocket lỗi:', error);
        // Tự động reconnect sau 3 giây
        setTimeout(() => initWebSocket(username, userId), 3000);
    });
}

/* ============================================
   GỬI LỜI MỜI THÁCH ĐẤU
   ============================================ */
function sendChallenge(button) {
    if (!stompClient || !stompClient.connected) {
        alert('Mất kết nối! Vui lòng tải lại trang.');
        return;
    }

    const targetId       = parseInt(button.getAttribute('data-target-id'));
    const targetUsername = button.getAttribute('data-target-username');
    const targetFullname = button.getAttribute('data-target-fullname');

    const payload = {
        invitationId:      null,
        initiatorId:       currentUserId,
        initiatorUsername: currentUsername,
        initiatorFullName: '',
        targetId:          targetId,
        targetUsername:    targetUsername
    };

    stompClient.send('/app/invite', {}, JSON.stringify(payload));

    // Lưu lại để xử lý sau
    pendingChallenge = payload;

    // Hiển thị modal chờ
    document.getElementById('waiting-msg').textContent =
        `Đang chờ ${targetFullname || targetUsername} phản hồi...`;
    showModal('waiting-modal');
}

/* ============================================
   NHẬN LỜI MỜI THÁCH ĐẤU
   ============================================ */
function onInviteReceived(payload) {
    // Thêm vào danh sách nếu chưa có (tránh trùng)
    if (!pendingInvitations.find(inv => inv.invitationId === payload.invitationId)) {
        pendingInvitations.push(payload);
    }
    renderInvitations();
    showModal('invite-modal');
}

function renderInvitations() {
    const listDiv = document.getElementById('invitation-list');
    if (!listDiv) return;
    listDiv.innerHTML = '';
    
    pendingInvitations.forEach(inv => {
        const item = document.createElement('div');
        item.style.display = 'flex';
        item.style.alignItems = 'center';
        item.style.justifyContent = 'space-between';
        item.style.background = 'rgba(255,255,255,0.05)';
        item.style.padding = '10px';
        item.style.borderRadius = '5px';
        item.style.marginBottom = '10px';
        item.style.borderLeft = '3px solid #10b981';
        
        item.innerHTML = `
            <div style="display:flex; flex-direction:column; color:#f8fafc;">
                <span style="font-weight:bold;">${inv.initiatorUsername}</span>
                <span style="font-size:0.75rem; color:#94a3b8;">Đang mời bạn...</span>
            </div>
            <div style="display:flex; gap:8px;">
                <button class="btn btn-primary btn-sm" onclick="acceptInvitation(${inv.invitationId})" style="padding:0.4rem 0.6rem; border-radius:4px;">Chấp nhận</button>
                <button class="btn btn-outline btn-sm" onclick="rejectInvitation(${inv.invitationId})" style="padding:0.4rem 0.6rem; border-radius:4px; border-color:#ef4444; color:#ef4444;">Từ chối</button>
            </div>
        `;
        listDiv.appendChild(item);
    });
}

function acceptInvitation(id) {
    const chosen = pendingInvitations.find(inv => inv.invitationId === id);
    if (!chosen) return;
    
    // Từ chối TẤT CẢ các lời mời còn lại (Yêu cầu của thầy)
    pendingInvitations.forEach(inv => {
        if (inv.invitationId !== id) {
            respondToSingle(inv, false);
        }
    });
    
    // Chấp nhận người được chọn
    respondToSingle(chosen, true);
    
    // Xóa list và ẩn modal
    pendingInvitations = [];
    hideModal('invite-modal');
}

function rejectInvitation(id) {
    const chosen = pendingInvitations.find(inv => inv.invitationId === id);
    if (chosen) {
        respondToSingle(chosen, false);
        // Xóa khỏi danh sách hiện tại
        pendingInvitations = pendingInvitations.filter(inv => inv.invitationId !== id);
    }
    
    if (pendingInvitations.length > 0) {
        renderInvitations();
    } else {
        hideModal('invite-modal');
    }
}

function rejectAllInvitations() {
    pendingInvitations.forEach(inv => respondToSingle(inv, false));
    pendingInvitations = [];
    hideModal('invite-modal');
}

/* ============================================
   PHẢN HỒI LỜI MỜI
   ============================================ */
function respondToSingle(invitation, accepted) {
    const payload = {
        invitationId:      invitation.invitationId,
        accepted:          accepted,
        initiatorId:       invitation.initiatorId,
        initiatorUsername: invitation.initiatorUsername,
        targetId:          currentUserId, 
        targetUsername:    currentUsername 
    };

    stompClient.send('/app/respond', {}, JSON.stringify(payload));
}

/* ============================================
   HỦY LỜI MỜI
   ============================================ */
function cancelChallenge() {
    hideModal('waiting-modal');
    pendingChallenge = null;
}

/* ============================================
   GAME BẮT ĐẦU → CHUYỂN HƯỚNG
   ============================================ */
function onGameStart(payload) {
    hideModal('waiting-modal');
    hideModal('invite-modal');
    // Redirect đến trang chơi cờ
    window.location.href = `/play-game?sessionId=${payload.sessionId}`;
}

/* ============================================
   HELPER FUNCTIONS
   ============================================ */
function showModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.remove('hidden');
}

function hideModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add('hidden');
}

function showAlert(msg) {
    const div = document.createElement('div');
    div.className = 'alert alert-error';
    div.style.cssText = 'position:fixed;top:80px;right:20px;z-index:9999;min-width:280px;animation:slideUp 0.3s ease';
    div.textContent = msg;
    document.body.appendChild(div);
    setTimeout(() => div.remove(), 4000);
}
