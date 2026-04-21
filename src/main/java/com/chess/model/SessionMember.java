package com.chess.model;

import com.chess.model.enums.GameRole;
import com.chess.model.enums.MatchEnding;
import jakarta.persistence.*;

@Entity
@Table(name = "session_member")
public class SessionMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int eloInitial;
    private int eloFinal;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private GameRole role;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MatchEnding result;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private PlaySession session;

    public SessionMember() {}

    public Long        getId()         { return id; }
    public int         getEloInitial() { return eloInitial; }
    public int         getEloFinal()   { return eloFinal; }
    public GameRole    getRole()       { return role; }
    public MatchEnding getResult()     { return result; }
    public Account     getUser()       { return user; }
    public PlaySession getSession()    { return session; }

    public void setId(Long id)                  { this.id = id; }
    public void setEloInitial(int eloInitial)    { this.eloInitial = eloInitial; }
    public void setEloFinal(int eloFinal)        { this.eloFinal = eloFinal; }
    public void setRole(GameRole role)           { this.role = role; }
    public void setResult(MatchEnding result)    { this.result = result; }
    public void setUser(Account user)            { this.user = user; }
    public void setSession(PlaySession session)  { this.session = session; }
}
