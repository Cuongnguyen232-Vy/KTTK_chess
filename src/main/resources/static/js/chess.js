/**
 * chess.js
 * Engine cờ vua - xử lý bàn cờ, nước đi, luật chơi, WebSocket
 *
 * Quân cờ:
 *  wK=♔  wQ=♕  wR=♖  wB=♗  wN=♘  wP=♙
 *  bK=♚  bQ=♛  bR=♜  bB=♝  bN=♞  bP=♟
 */

// ============================================
//  CẤU HÌNH
// ============================================
const PIECES = {
    wK:'♔', wQ:'♕', wR:'♖', wB:'♗', wN:'♘', wP:'♙',
    bK:'♚', bQ:'♛', bR:'♜', bB:'♝', bN:'♞', bP:'♟'
};

// Bàn cờ khởi đầu (hàng 0 = hàng 8 = phía đen)
const INITIAL_BOARD = [
    ['bR','bN','bB','bQ','bK','bB','bN','bR'],
    ['bP','bP','bP','bP','bP','bP','bP','bP'],
    ['','','','','','','',''],
    ['','','','','','','',''],
    ['','','','','','','',''],
    ['','','','','','','',''],
    ['wP','wP','wP','wP','wP','wP','wP','wP'],
    ['wR','wN','wB','wQ','wK','wB','wN','wR']
];

// ============================================
//  BIẾN TOÀN CỤC
// ============================================
let board          = [];
let selectedSquare = null;    // {row, col}
let possibleMoves  = [];
let currentTurn    = 'w';     // 'w' hoặc 'b'
let isMyTurn       = false;
let myColor        = 'WHITE'; // 'WHITE' hoặc 'BLACK'
let sessionId      = null;
let myUsername     = null;
let myId           = null;
let opponentName   = null;
let lastMove       = null;    // {from:{row,col}, to:{row,col}}
let gameStompClient = null;
let timerMe, timerOpponent;
let timeMe = 600, timeOpp = 600; // 10 phút mỗi người

// ============================================
//  KHỞI TẠO GAME
// ============================================
function initChessGame(sid, username, color, uid, oppName) {
    sessionId    = sid;
    myUsername   = username;
    myColor      = color;
    myId         = uid;
    opponentName = oppName;

    // Màu của mình
    const myColorChar = (color === 'WHITE') ? 'w' : 'b';
    isMyTurn = (myColorChar === 'w'); // Trắng đi trước

    // Clone bàn cờ khởi đầu
    board = INITIAL_BOARD.map(row => [...row]);

    renderBoard();
    updateTurnIndicator();
    startTimers();
    subscribeToGameChannel();
}

// ============================================
//  KẾT NỐI WEBSOCKET GAME
// ============================================
function subscribeToGameChannel() {
    // Đợi stompClient từ websocket.js
    const waitInterval = setInterval(() => {
        if (stompClient && stompClient.connected) {
            clearInterval(waitInterval);

            // Lắng nghe nước đi từ kênh game
            stompClient.subscribe(`/topic/game/${sessionId}`, function(message) {
                const move = JSON.parse(message.body);
                onMoveReceived(move);
            });

            // Lắng nghe xin hòa
            stompClient.subscribe(`/user/queue/draw-offer`, function(message) {
                showModal('draw-offer-modal');
            });

            console.log(`✅ Đã subscribe kênh game/${sessionId}`);
        }
    }, 200);
}

// ============================================
//  RENDER BÀN CỜ
// ============================================
function renderBoard() {
    const boardEl = document.getElementById('chess-board');
    boardEl.innerHTML = '';

    const isFlipped = (myColor === 'BLACK'); // Lật bàn cờ nếu đi đen

    for (let r = 0; r < 8; r++) {
        for (let c = 0; c < 8; c++) {
            // Nếu lật: hiển thị từ hàng 7→0, cột 7→0
            const row = isFlipped ? 7 - r : r;
            const col = isFlipped ? 7 - c : c;

            const square = document.createElement('div');
            const isLight = (row + col) % 2 === 0;
            square.className = `square-${isLight ? 'light' : 'dark'}`;
            square.dataset.row = row;
            square.dataset.col = col;
            square.id = `sq-${row}-${col}`;

            // Quân cờ
            const piece = board[row][col];
            if (piece) {
                const pieceEl = document.createElement('span');
                pieceEl.className = 'piece';
                pieceEl.textContent = PIECES[piece] || piece;
                square.appendChild(pieceEl);
            }

            // Highlight nước đi cuối
            if (lastMove) {
                if (lastMove.from.row === row && lastMove.from.col === col) square.classList.add('square-last-from');
                if (lastMove.to.row   === row && lastMove.to.col   === col) square.classList.add('square-last-to');
            }

            // Highlight ô đang chọn
            if (selectedSquare && selectedSquare.row === row && selectedSquare.col === col) {
                square.classList.add('square-selected');
            }

            // Highlight nước đi có thể
            const isPossible = possibleMoves.some(m => m.row === row && m.col === col);
            if (isPossible) {
                square.classList.add('square-possible');
                if (piece) square.classList.add('has-piece');
            }

            square.addEventListener('click', () => onSquareClick(row, col));
            boardEl.appendChild(square);
        }
    }
}

// ============================================
//  XỬ LÝ CLICK Ô CỜ
// ============================================
function onSquareClick(row, col) {
    if (!isMyTurn) {
        showAlert('⏳ Chưa đến lượt của bạn!');
        return;
    }

    const piece = board[row][col];
    const myColorChar = myColor === 'WHITE' ? 'w' : 'b';

    if (selectedSquare) {
        // Đã có ô được chọn → thử di chuyển
        const isPossible = possibleMoves.some(m => m.row === row && m.col === col);

        if (isPossible) {
            // Di chuyển quân cờ
            executeMove(selectedSquare.row, selectedSquare.col, row, col);
        } else if (piece && piece.startsWith(myColorChar)) {
            // Chọn ô khác của mình
            selectedSquare = {row, col};
            possibleMoves  = getLegalMoves(row, col);
        } else {
            // Bỏ chọn
            selectedSquare = null;
            possibleMoves  = [];
        }
    } else {
        // Chưa chọn ô nào
        if (piece && piece.startsWith(myColorChar)) {
            selectedSquare = {row, col};
            possibleMoves  = getLegalMoves(row, col);
        }
    }

    renderBoard();
}

// ============================================
//  THỰC HIỆN NƯỚC ĐI
// ============================================
function executeMove(fromRow, fromCol, toRow, toCol) {
    const piece = board[fromRow][fromCol];

    // Bắt quân
    board[toRow][toCol]   = piece;
    board[fromRow][fromCol] = '';

    // Phong cấp tốt (auto-queen)
    if (piece === 'wP' && toRow === 0) board[toRow][toCol] = 'wQ';
    if (piece === 'bP' && toRow === 7) board[toRow][toCol] = 'bQ';

    lastMove = { from:{row:fromRow,col:fromCol}, to:{row:toRow,col:toCol} };
    selectedSquare = null;
    possibleMoves  = [];

    // Đổi lượt
    currentTurn = currentTurn === 'w' ? 'b' : 'w';
    isMyTurn = false;

    // Tên ô
    const fromCell = colToLetter(fromCol) + (8 - fromRow);
    const toCell   = colToLetter(toCol)   + (8 - toRow);

    // Thêm vào lịch sử
    addMoveHistory(piece, fromCell, toCell);

    // Kiểm tra kết thúc game (vua bị bắt)
    const oppColorChar = myColor === 'WHITE' ? 'b' : 'w';
    const gameOver = !findKing(board, oppColorChar);
    const winner   = gameOver ? myUsername : null;

    // Gửi nước đi qua WebSocket
    const movePayload = {
        sessionId:      sessionId,
        playerId:       myId,
        playerUsername: myUsername,
        fromCell:       fromCell,
        toCell:         toCell,
        boardFen:       boardToFEN(),
        piece:          piece,
        gameOver:       gameOver,
        winner:         winner
    };

    stompClient.send(`/app/move/${sessionId}`, {}, JSON.stringify(movePayload));

    renderBoard();
    updateTurnIndicator();

    if (gameOver) {
        setTimeout(() => showGameOver(true, null), 500);
    }
}

// ============================================
//  NHẬN NƯỚC ĐI TỪ ĐỐI THỦ
// ============================================
function onMoveReceived(move) {
    // Bỏ qua nước đi của chính mình (đã xử lý local)
    if (move.playerUsername === myUsername) return;

    // Parse from/to
    const fromCol = letterToCol(move.fromCell[0]);
    const fromRow = 8 - parseInt(move.fromCell[1]);
    const toCol   = letterToCol(move.toCell[0]);
    const toRow   = 8 - parseInt(move.toCell[1]);

    board[toRow][toCol]     = board[fromRow][fromCol];
    board[fromRow][fromCol] = '';

    // Phong cấp
    if (board[toRow][toCol] === 'wP' && toRow === 0) board[toRow][toCol] = 'wQ';
    if (board[toRow][toCol] === 'bP' && toRow === 7) board[toRow][toCol] = 'bQ';

    lastMove = { from:{row:fromRow,col:fromCol}, to:{row:toRow,col:toCol} };
    currentTurn = currentTurn === 'w' ? 'b' : 'w';
    isMyTurn = true;

    addMoveHistory(move.piece, move.fromCell, move.toCell);
    renderBoard();
    updateTurnIndicator();

    if (move.gameOver) {
        // Mình thua
        setTimeout(() => showGameOver(false, move.winner), 500);
    }
}

// ============================================
//  TÍNH CÁC NƯỚC ĐI HỢP LỆ
// ============================================
function getLegalMoves(row, col) {
    const piece = board[row][col];
    if (!piece) return [];

    const color    = piece[0]; // 'w' hoặc 'b'
    const pieceType = piece[1]; // K,Q,R,B,N,P
    let moves = [];

    switch(pieceType) {
        case 'P': moves = getPawnMoves(row,col,color); break;
        case 'R': moves = getSlidingMoves(row,col,color,[[0,1],[0,-1],[1,0],[-1,0]]); break;
        case 'B': moves = getSlidingMoves(row,col,color,[[1,1],[1,-1],[-1,1],[-1,-1]]); break;
        case 'Q': moves = getSlidingMoves(row,col,color,[[0,1],[0,-1],[1,0],[-1,0],[1,1],[1,-1],[-1,1],[-1,-1]]); break;
        case 'N': moves = getKnightMoves(row,col,color); break;
        case 'K': moves = getKingMoves(row,col,color); break;
    }

    return moves;
}

function getPawnMoves(row, col, color) {
    const moves = [];
    const dir   = color === 'w' ? -1 : 1; // Trắng đi lên (row giảm), đen đi xuống
    const startRow = color === 'w' ? 6 : 1;

    // Tiến 1
    if (inBounds(row+dir,col) && !board[row+dir][col]) {
        moves.push({row:row+dir, col});
        // Tiến 2 từ dòng đầu
        if (row === startRow && !board[row+2*dir][col]) {
            moves.push({row:row+2*dir, col});
        }
    }

    // Bắt chéo
    for (const dc of [-1,1]) {
        if (inBounds(row+dir, col+dc)) {
            const target = board[row+dir][col+dc];
            if (target && target[0] !== color) {
                moves.push({row:row+dir, col:col+dc});
            }
        }
    }
    return moves;
}

function getSlidingMoves(row, col, color, directions) {
    const moves = [];
    for (const [dr,dc] of directions) {
        let r = row+dr, c = col+dc;
        while (inBounds(r,c)) {
            const target = board[r][c];
            if (!target) {
                moves.push({row:r,col:c});
            } else {
                if (target[0] !== color) moves.push({row:r,col:c}); // Bắt quân địch
                break;
            }
            r+=dr; c+=dc;
        }
    }
    return moves;
}

function getKnightMoves(row, col, color) {
    const moves = [];
    for (const [dr,dc] of [[2,1],[2,-1],[-2,1],[-2,-1],[1,2],[1,-2],[-1,2],[-1,-2]]) {
        const r=row+dr, c=col+dc;
        if (inBounds(r,c)) {
            const target = board[r][c];
            if (!target || target[0] !== color) moves.push({row:r,col:c});
        }
    }
    return moves;
}

function getKingMoves(row, col, color) {
    const moves = [];
    for (const [dr,dc] of [[0,1],[0,-1],[1,0],[-1,0],[1,1],[1,-1],[-1,1],[-1,-1]]) {
        const r=row+dr, c=col+dc;
        if (inBounds(r,c)) {
            const target = board[r][c];
            if (!target || target[0] !== color) moves.push({row:r,col:c});
        }
    }
    return moves;
}

function inBounds(r,c) { return r>=0 && r<8 && c>=0 && c<8; }

function findKing(b, color) {
    for(let r=0;r<8;r++) for(let c=0;c<8;c++) {
        if(b[r][c] === color+'K') return {row:r,col:c};
    }
    return null;
}

// ============================================
//  TIMER
// ============================================
function startTimers() {
    clearInterval(timerMe);
    clearInterval(timerOpponent);

    timerMe = setInterval(() => {
        if (isMyTurn) {
            timeMe--;
            if (timeMe <= 0) { clearInterval(timerMe); resign(); }
            updateTimerDisplay('timer-me', timeMe);
        }
    }, 1000);

    timerOpponent = setInterval(() => {
        if (!isMyTurn) {
            timeOpp--;
            if (timeOpp <= 0) { clearInterval(timerOpponent); }
            updateTimerDisplay('timer-opponent', timeOpp);
        }
    }, 1000);
}

function updateTimerDisplay(id, seconds) {
    const el = document.getElementById(id);
    if (!el) return;
    const m = Math.floor(seconds/60).toString().padStart(2,'0');
    const s = (seconds%60).toString().padStart(2,'0');
    el.textContent = `${m}:${s}`;
    el.classList.toggle('danger', seconds <= 30);
}

// ============================================
//  UI HELPERS
// ============================================
function updateTurnIndicator() {
    const el = document.getElementById('turn-indicator');
    if (!el) return;
    if (currentTurn === 'w') {
        el.textContent = '⚪ Lượt: TRẮNG';
        el.style.color = '#f8fafc';
    } else {
        el.textContent = '⚫ Lượt: ĐEN';
        el.style.color = '#94a3b8';
    }
}

function addMoveHistory(piece, from, to) {
    const list = document.getElementById('move-list');
    if (!list) return;
    const item = document.createElement('span');
    item.className = 'move-item';
    item.textContent = `${from}→${to}`;
    list.appendChild(item);
    list.scrollTop = list.scrollHeight;
}

function showGameOver(isWinner, winnerName) {
    clearInterval(timerMe);
    clearInterval(timerOpponent);

    const icon  = document.getElementById('game-over-icon');
    const title = document.getElementById('game-over-title');
    const msg   = document.getElementById('game-over-msg');

    if (winnerName === null) {
        icon.textContent  = '🤝';
        title.textContent = 'Hòa cờ!';
        msg.textContent   = 'Ván đấu kết thúc hòa. +3 ELO';
    } else if (isWinner || winnerName === myUsername) {
        icon.textContent  = '🏆';
        title.textContent = 'Bạn thắng!';
        msg.textContent   = `Bạn đã đánh bại ${opponentName}! +15 ELO`;
    } else {
        icon.textContent  = '😔';
        title.textContent = 'Bạn thua!';
        msg.textContent   = `${opponentName} chiến thắng. -10 ELO`;
    }

    showModal('game-over-modal');
}

// ============================================
//  CÁC NÚT HÀNH ĐỘNG
// ============================================
function offerDraw() {
    if (!stompClient || !stompClient.connected) return;
    stompClient.convertAndSendToUser && stompClient.send('/app/draw-offer', {}, JSON.stringify({
        sessionId,
        playerUsername: myUsername
    }));
    showAlert('🤝 Đã gửi đề nghị hòa cờ!');
}

function acceptDraw() {
    hideModal('draw-offer-modal');
    // Gửi nước đi game-over với winner=null
    const movePayload = { sessionId, playerId:myId, playerUsername:myUsername,
                          fromCell:'', toCell:'', boardFen:'', piece:'', gameOver:true, winner:null };
    stompClient.send(`/app/move/${sessionId}`, {}, JSON.stringify(movePayload));
    showGameOver(false, null);
}

function rejectDraw() {
    hideModal('draw-offer-modal');
}

function resign() {
    if (!confirm('Bạn có chắc muốn đầu hàng không?')) return;

    // Người kia thắng
    const oppColorChar = myColor === 'WHITE' ? 'b' : 'w';
    const movePayload = {
        sessionId, playerId: myId, playerUsername: myUsername,
        fromCell:'', toCell:'', boardFen:'', piece:'',
        gameOver: true, winner: null // Server xử lý qua /resign
    };
    stompClient.send(`/app/resign/${sessionId}`, {}, JSON.stringify({
        sessionId, playerUsername: myUsername, winner: null, gameOver: true
    }));
    showGameOver(false, opponentName);
}

// ============================================
//  HELPER: chuyển đổi cột / FEN
// ============================================
function colToLetter(col) { return String.fromCharCode(97 + col); }
function letterToCol(letter) { return letter.charCodeAt(0) - 97; }

function boardToFEN() {
    // Đơn giản hóa: chỉ encode phần mảnh ghép bàn cờ
    let fen = '';
    for(let r=0;r<8;r++) {
        let empty=0;
        for(let c=0;c<8;c++) {
            const p=board[r][c];
            if(!p) { empty++; }
            else {
                if(empty>0){ fen+=empty; empty=0; }
                const letter = p[1];
                fen += p[0]==='w' ? letter.toUpperCase() : letter.toLowerCase();
            }
        }
        if(empty>0) fen+=empty;
        if(r<7) fen+='/';
    }
    return fen + ' ' + currentTurn + ' - - 0 1';
}
