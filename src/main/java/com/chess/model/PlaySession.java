package com.chess.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "play_session")
public class PlaySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SessionMember> members;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StepDetail> steps;

    public PlaySession() {}

    public Long                getId()        { return id; }
    public LocalDateTime       getStartTime() { return startTime; }
    public LocalDateTime       getEndTime()   { return endTime; }
    public List<SessionMember> getMembers()   { return members; }
    public List<StepDetail>    getSteps()     { return steps; }

    public void setId(Long id)                      { this.id = id; }
    public void setStartTime(LocalDateTime startTime){ this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime)    { this.endTime = endTime; }
    public void setMembers(List<SessionMember> m)    { this.members = m; }
    public void setSteps(List<StepDetail> s)         { this.steps = s; }
}
