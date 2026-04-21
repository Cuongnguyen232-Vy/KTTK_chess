package com.chess.model;

import com.chess.model.enums.AccountState;
import jakarta.persistence.*;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String userName;

    @Column(nullable = false, length = 255)
    private String passWord;

    @Column(length = 100)
    private String fullName;

    @Column(name = "eloPoint")
    private int eloPoint = 1500;

    @Column(length = 50)
    private String userRole;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private AccountState state = AccountState.OFFLINE;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RankingData rankingData;

    public Account() {}

    // Getters
    public Long        getId()          { return id; }
    public String      getUserName()    { return userName; }
    public String      getPassWord()    { return passWord; }
    public String      getFullName()    { return fullName; }
    public int         getEloPoint()    { return eloPoint; }
    public String      getUserRole()    { return userRole; }
    public AccountState getState()      { return state; }
    public RankingData getRankingData() { return rankingData; }

    // Setters
    public void setId(Long id)                  { this.id = id; }
    public void setUserName(String userName)     { this.userName = userName; }
    public void setPassWord(String passWord)     { this.passWord = passWord; }
    public void setFullName(String fullName)     { this.fullName = fullName; }
    public void setEloPoint(int eloPoint)        { this.eloPoint = eloPoint; }
    public void setUserRole(String userRole)     { this.userRole = userRole; }
    public void setState(AccountState state)     { this.state = state; }
    public void setRankingData(RankingData r)    { this.rankingData = r; }
}
