package com.chess.payload;

public class GameStartPayload {
    private Long   sessionId;
    private String whiteUsername;
    private String blackUsername;
    private String yourColor;

    public GameStartPayload() {}
    public GameStartPayload(Long sessionId, String whiteUsername, String blackUsername, String yourColor) {
        this.sessionId     = sessionId;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.yourColor     = yourColor;
    }

    public Long   getSessionId()      { return sessionId; }
    public String getWhiteUsername()  { return whiteUsername; }
    public String getBlackUsername()  { return blackUsername; }
    public String getYourColor()      { return yourColor; }

    public void setSessionId(Long v)       { this.sessionId     = v; }
    public void setWhiteUsername(String v) { this.whiteUsername = v; }
    public void setBlackUsername(String v) { this.blackUsername = v; }
    public void setYourColor(String v)     { this.yourColor     = v; }
}
