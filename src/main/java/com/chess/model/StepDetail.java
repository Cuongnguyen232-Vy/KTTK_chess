package com.chess.model;

import jakarta.persistence.*;

@Entity
@Table(name = "step_detail")
public class StepDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String fromCell;

    @Column(length = 10)
    private String toCell;

    @Column(columnDefinition = "TEXT")
    private String boardFen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Account player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private PlaySession session;

    public StepDetail() {}

    public Long        getId()       { return id; }
    public String      getFromCell() { return fromCell; }
    public String      getToCell()   { return toCell; }
    public String      getBoardFen() { return boardFen; }
    public Account     getPlayer()   { return player; }
    public PlaySession getSession()  { return session; }

    public void setId(Long id)              { this.id = id; }
    public void setFromCell(String fromCell){ this.fromCell = fromCell; }
    public void setToCell(String toCell)    { this.toCell = toCell; }
    public void setBoardFen(String boardFen){ this.boardFen = boardFen; }
    public void setPlayer(Account player)   { this.player = player; }
    public void setSession(PlaySession s)   { this.session = s; }
}
