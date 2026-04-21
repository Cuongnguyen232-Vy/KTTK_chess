package com.chess.payload;

public class InvitationPayload {
    private Long   invitationId;
    private Long   initiatorId;
    private String initiatorUsername;
    private String initiatorFullName;
    private Long   targetId;
    private String targetUsername;

    public InvitationPayload() {}
    public InvitationPayload(Long invitationId, Long initiatorId, String initiatorUsername,
                             String initiatorFullName, Long targetId, String targetUsername) {
        this.invitationId      = invitationId;
        this.initiatorId       = initiatorId;
        this.initiatorUsername = initiatorUsername;
        this.initiatorFullName = initiatorFullName;
        this.targetId          = targetId;
        this.targetUsername    = targetUsername;
    }

    public Long   getInvitationId()       { return invitationId; }
    public Long   getInitiatorId()        { return initiatorId; }
    public String getInitiatorUsername()  { return initiatorUsername; }
    public String getInitiatorFullName()  { return initiatorFullName; }
    public Long   getTargetId()           { return targetId; }
    public String getTargetUsername()     { return targetUsername; }

    public void setInvitationId(Long v)       { this.invitationId      = v; }
    public void setInitiatorId(Long v)        { this.initiatorId       = v; }
    public void setInitiatorUsername(String v){ this.initiatorUsername  = v; }
    public void setInitiatorFullName(String v){ this.initiatorFullName  = v; }
    public void setTargetId(Long v)           { this.targetId           = v; }
    public void setTargetUsername(String v)   { this.targetUsername     = v; }
}
