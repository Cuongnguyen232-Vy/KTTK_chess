package com.chess.model;

import com.chess.model.enums.RequestStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime sentTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "initiator_id", nullable = false)
    private Account initiator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_id", nullable = false)
    private Account target;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    public Invitation() {}

    public Long          getId()        { return id; }
    public LocalDateTime getSentTime()  { return sentTime; }
    public Account       getInitiator() { return initiator; }
    public Account       getTarget()    { return target; }
    public RequestStatus getStatus()    { return status; }

    public void setId(Long id)                  { this.id = id; }
    public void setSentTime(LocalDateTime t)    { this.sentTime = t; }
    public void setInitiator(Account initiator) { this.initiator = initiator; }
    public void setTarget(Account target)       { this.target = target; }
    public void setStatus(RequestStatus status) { this.status = status; }
}
