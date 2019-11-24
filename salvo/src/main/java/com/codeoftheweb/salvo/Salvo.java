package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name="native",strategy="native")
    private long id;

    private int turn;

    @ElementCollection
    @Column(name = "salvoLocation")
    private List<String> salvoLocation = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    //CONSTRUCTOR

    public Salvo() {
    }

    public Salvo(int turn, List<String> salvoLocation, GamePlayer gamePlayer) {
        this.turn = turn;
        this.salvoLocation = salvoLocation;
        this.gamePlayer = gamePlayer;
    }

    //GETTERS

    public long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getSalvoLocation() {
        return salvoLocation;
    }

    @JsonIgnore
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    //DTO

    public Map<String,Object> makeSalvoDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.getTurn());
        dto.put("player",this.getGamePlayer().getPlayer().getUserName());
        dto.put("locations", this.getSalvoLocation());
        return dto;
    }
}
