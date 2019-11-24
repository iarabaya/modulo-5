package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name="native",strategy="native")
  private Long id;

  private Date creationDate;

  @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
  private Set<GamePlayer> gamePlayers = new HashSet<>();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="ship_id")
  private Ship ship;

  @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
  private List<Score> score;

  //CONSTRUCTORS
  public Game() { }

  public Game(Date creationDate) {
    this.creationDate = creationDate;
  }

  //GETTERS
  public Long getId() {
    return id;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  @JsonIgnore
  public Set<GamePlayer> getGamePlayers() {
    return gamePlayers;
  }

  @JsonIgnore
  public Ship getShip() { return ship;}

  @JsonIgnore
  public List<Player> getPlayers(){
      return gamePlayers.stream()
              .map(sub -> sub.getPlayer())
              .collect(Collectors.toList());
  }

  //DTO

  public Map<String, Object> makeGameDTO(){
      Map<String, Object> dto = new LinkedHashMap<String, Object>();
      dto.put("id", this.getId());
      dto.put("created", this.getCreationDate());
      dto.put("gamePlayers", this.getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO).collect(Collectors.toList()));
      return dto;
  }
}
