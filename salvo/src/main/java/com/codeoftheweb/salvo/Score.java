package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name="native",strategy="native")
  private Long id;

  private Double score;

  private Date finishDate;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "game_id")
  private Game game;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "player_id")
  private Player player;

  //CONSTRUCTOR

  public Score() {
  }

  public Score(Double score, Game game, Player player) {
    this.score = score;
    this.game = game;
    this.player = player;
    this.finishDate = new Date();
  }

  //GETTERS

  public Double getScore() {
    return score;
  }

  @JsonIgnore
  public Date getFinishDate() {
    return finishDate;
  }

  @JsonIgnore
  public Game getGame() {
    return game;
  }

  @JsonIgnore
  public Player getPlayer() {
    return player;
  }

  public long getGameId(Game game){
    return game.getId();
  }

  public long getPlayerId(Player player){
    return player.getId();
  }

}
