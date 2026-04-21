package com.chess.payload;

public class ResponsePayload {
    private Long    invitationId;
    private boolean accepted;
    private Long    initiatorId;
    private String  initiatorUsername;
    private Long    targetId;
    private String  targetUsername;

    public ResponsePayload() {}
    public ResponsePayload(Long invitationId, boolean accepted, Long initiatorId,
                           String initiatorUsername, Long targetId, String targetUsername) {
        this.invitationId       = invitationId;
        this.accepted           = accepted;
        this.initiatorId        = initiatorId;
        this.initiatorUsername  = initiatorUsername;
        this.targetId           = targetId;
        this.targetUsername     = targetUsername;
    }

    public Long    getInvitationId()      { return invitationId; }
    public boolean isAccepted()           { return accepted; }
    public Long    getInitiatorId()       { return initiatorId; }
    public String  getInitiatorUsername() { return initiatorUsername; }
    public Long    getTargetId()          { return targetId; }
    public String  getTargetUsername()    { return targetUsername; }

    public void setInvitationId(Long v)      { this.invitationId      = v; }
    public void setAccepted(boolean v)       { this.accepted          = v; }
    public void setInitiatorId(Long v)       { this.initiatorId       = v; }
    public void setInitiatorUsername(String v){ this.initiatorUsername = v; }
    public void setTargetId(Long v)          { this.targetId          = v; }
    public void setTargetUsername(String v)  { this.targetUsername    = v; }
}
