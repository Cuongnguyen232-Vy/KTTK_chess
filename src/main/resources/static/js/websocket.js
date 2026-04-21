/**
 * websocket.js
 * Xử lý kết nối WebSocket (STOMP over SockJS)
 * - Kết nối đến server
 * - Lắng nghe lời mời thách đấu
 * - Gửi/nhận lời mời
 */

let stompClient = null;
let currentInvitation = null;
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
    currentInvitation = payload;

    document.getElementById('invite-msg').textContent =
        `${payload.initiatorUsername} muốn thách đấu với bạn!`;

    showModal('invite-modal');

    document.getElementById('btn-accept').onclick = function() {
        respondToInvitation(true);
    };
    document.getElementById('btn-reject').onclick = function() {
        respondToInvitation(false);
    };
}

/* ============================================
   PHẢN HỒI LỜI MỜI
   ============================================ */
function respondToInvitation(accepted) {
    if (!currentInvitation) return;

    const payload = {
        invitationId:      currentInvitation.invitationId,
        accepted:          accepted,
        initiatorId:       currentInvitation.initiatorId,
        initiatorUsername: currentInvitation.initiatorUsername,
        targetId:          currentUserId,
        targetUsername:    currentUsername
    };

    stompClient.send('/app/respond', {}, JSON.stringify(payload));
    hideModal('invite-modal');
    currentInvitation = null;
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
