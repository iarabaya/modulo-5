package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name="native",strategy="native")
    private Long id;

    public enum ShipType {CARRIER, BATTLESHIP, SUBMARINE, DESTROYER, PATROL_BOAT }
    private ShipType shipType;

    @ElementCollection
    @Column(name = "location")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;


    public Ship() {
    }

    public Ship(ShipType shipType, List<String> locations, GamePlayer gamePlayer) {
        this.shipType = shipType;
        this.locations = locations;
        this.gamePlayer = gamePlayer;
    }

    public Long getId() {
        return id;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public List<String> getLocations() {
        return locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Map<String, Object> makeShipDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", this.getShipType());
        dto.put("locations", this.getLocations());
        return dto;
    }
}

