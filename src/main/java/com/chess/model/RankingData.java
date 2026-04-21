package com.chess.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ranking_data")
public class RankingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int winTotal  = 0;
    private int lossTotal = 0;
    private int drawTotal = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public RankingData() {}

    public Long    getId()        { return id; }
    public int     getWinTotal()  { return winTotal; }
    public int     getLossTotal() { return lossTotal; }
    public int     getDrawTotal() { return drawTotal; }
    public Account getAccount()   { return account; }

    public void setId(Long id)           { this.id = id; }
    public void setWinTotal(int v)       { this.winTotal = v; }
    public void setLossTotal(int v)      { this.lossTotal = v; }
    public void setDrawTotal(int v)      { this.drawTotal = v; }
    public void setAccount(Account a)    { this.account = a; }
}
