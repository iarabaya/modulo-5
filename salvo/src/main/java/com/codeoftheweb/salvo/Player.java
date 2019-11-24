package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name="native",strategy="native")
  private Long id;

  private String userName;

  @OneToMany(mappedBy = "player",fetch = FetchType.EAGER)
  private Set<GamePlayer> gamePlayers = new HashSet<>() ;

  @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
  private List<Score> scores;

  private String password;


  //CONSTRUCTOR
  public Player() {
  }

  public Player(String userName, String password) {
    this.userName = userName;
    this.password = password;
  }

  //GETTERS
  public Long getId() {
    return id;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword(){ return password;}

  @JsonIgnore
  public Set<GamePlayer> getGamePlayers() {
    return gamePlayers;
  }

  @JsonIgnore
  public List<Score> getScores() {
    return scores;
  }

  @JsonIgnore
  public Score getScoreByGame(Game game) {
  return this.scores.stream().filter(score ->score.getGame().getId() == game.getId()).findFirst().orElse(null);
  }

 @JsonIgnore
  public List<Game> getGames(){
    return this.gamePlayers.stream()
            .map(sub -> sub.getGame())
            .collect(Collectors.toList());
  }

  //DTOS

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("name", this.getUserName());
        return dto;
    }

    public Map<String, Object> makeLeaderBoardDTO(){
    Map<String,Object> dto = new LinkedHashMap<String, Object>();
    dto.put("user", userName);
    dto.put("wins", scores.stream().filter(score -> score.getScore() == 1).count());
    dto.put("loses",scores.stream().filter(score -> score.getScore() == 0).count());
    dto.put("ties",scores.stream().filter(score -> score.getScore() == 0.5).count());
    dto.put("total", getTotalScore());
    return dto;
    }

  public double getTotalScore(){
    double total;
    total = (scores.stream().filter(score ->score.getScore() == 1).count()) 
    + ((scores.stream().filter(score ->score.getScore() == 0.5).count())/2.0);
    return total;

  }
}
