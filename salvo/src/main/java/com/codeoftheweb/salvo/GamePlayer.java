package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name="native",strategy="native")
    private Long id;

    private Date joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private Set<Salvo> salvoes = new HashSet<>();

    //CONSTRUCTOR

    public GamePlayer() {
    }

    public GamePlayer(Date joinDate, Game game, Player player) {
        this.joinDate = joinDate;
        this.game = game;
        this.player = player;
    }

    //GETTERS

    public Long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    @JsonIgnore
    public Date getJoinDate() {
        return joinDate;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }

    @JsonIgnore
    public long getGameId(){
        return game.getId();
    }

    @JsonIgnore
    public Set<Ship> getShips(){
        return ships;
    }

    @JsonIgnore
    public Set<Salvo> getSalvoes() {
        return salvoes;
    }


    //DTO

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gpid",this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());
        //dto.put("score", this.getPlayer().getScoreByGame(this.getGame()).getScore());
        return dto;
    }


}
