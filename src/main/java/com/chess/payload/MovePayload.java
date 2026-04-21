package com.chess.payload;

public class MovePayload {
    private Long    sessionId;
    private Long    playerId;
    private String  playerUsername;
    private String  fromCell;
    private String  toCell;
    private String  boardFen;
    private String  piece;
    private boolean gameOver;
    private String  winner;

    public MovePayload() {}

    public Long    getSessionId()        { return sessionId; }
    public Long    getPlayerId()         { return playerId; }
    public String  getPlayerUsername()   { return playerUsername; }
    public String  getFromCell()         { return fromCell; }
    public String  getToCell()           { return toCell; }
    public String  getBoardFen()         { return boardFen; }
    public String  getPiece()            { return piece; }
    public boolean isGameOver()          { return gameOver; }
    public String  getWinner()           { return winner; }

    public void setSessionId(Long v)        { this.sessionId       = v; }
    public void setPlayerId(Long v)         { this.playerId        = v; }
    public void setPlayerUsername(String v) { this.playerUsername  = v; }
    public void setFromCell(String v)       { this.fromCell        = v; }
    public void setToCell(String v)         { this.toCell          = v; }
    public void setBoardFen(String v)       { this.boardFen        = v; }
    public void setPiece(String v)          { this.piece           = v; }
    public void setGameOver(boolean v)      { this.gameOver        = v; }
    public void setWinner(String v)         { this.winner          = v; }
}
