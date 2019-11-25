package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.management.ObjectName;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


@RestController
@RequestMapping("/api")
public class SalvoController {

  @Autowired
  private GameRepository gameRepository;
  @Autowired
  private PlayerRepository playerRepository;
  @Autowired
  private GamePlayerRepository gamePlayerRepository;
  @Autowired
  private ShipRepository shipRepository;
  @Autowired
  private SalvoRepository salvoRepository;
  @Autowired
  private ScoreRepository scoreRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;


  @RequestMapping(path = "/players", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String username,@RequestParam String password) {

    if(username.isEmpty() || password.isEmpty()){
      return new ResponseEntity<>(makeMap("error", "Missing data" ),HttpStatus.FORBIDDEN);
    }

    Player player = playerRepository.findByUserName(username);

    if(player == null){ //todo esta bien
      player = new Player(username, passwordEncoder.encode(password));
      playerRepository.save(player);
      return new ResponseEntity<>(makeMap("id",player.getId()), HttpStatus.CREATED);
    }else{
      return new ResponseEntity<>(makeMap("error", "The user already exists"),HttpStatus.CONFLICT);
    }
  }

  private Map<String, Object> makeMap(String key, Object value) {
    Map<String, Object> map = new HashMap<>();
    map.put(key, value);
    return map;}


  @RequestMapping("/games")
  public Map<String,Object> getGame(Authentication authentication){
    Map<String,Object> dto = new LinkedHashMap<>();

    if(isGuest(authentication)){
      dto.put("player", "Guest");
    }else{
      Player player = playerRepository.findByUserName(authentication.getName());
      dto.put("player", player.makePlayerDTO());
    }
    dto.put("games", gameRepository.findAll().stream().map(Game::makeGameDTO).collect(Collectors.toList()));
    return dto;
  }

  @RequestMapping(path = "/games", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){
    if(isGuest(authentication)){
      return new ResponseEntity<>(makeMap("error", "cannot create game being a guest" ),HttpStatus.FORBIDDEN);
    }else{

        Player player = playerRepository.findByUserName(authentication.getName());
        Date date = new Date();
        Game game = gameRepository.save(new Game (date));
        GamePlayer gp = new GamePlayer(date, game, player);
        gamePlayerRepository.save(gp);

      return new ResponseEntity<>(makeMap("game", gp.makeGamePlayerDTO()), HttpStatus.CREATED);
    }
  }

  private boolean isGuest(Authentication authentication) {
    return authentication == null || authentication instanceof AnonymousAuthenticationToken;
  }


  @RequestMapping("/game_view/{gamePlayerId}")
  public Map<String,Object> getGameView(@PathVariable Long gamePlayerId){
    return gameViewDTO(gamePlayerRepository.findById(gamePlayerId).orElse(null));
  }


  @RequestMapping("/leaderboard")
  public List<Map<String,Object>> getLeaderBoard(){
    return playerRepository.findAll().stream().map(Player::makeLeaderBoardDTO).collect(toList());
  }

  //DTO GAMEVIEW

  private Map<String,Object> gameViewDTO(GamePlayer gamePlayer){
    Map<String,Object> dto = new LinkedHashMap<>();

    dto.put("id", gamePlayer.getGame().getId());
    dto.put("created", gamePlayer.getGame().getCreationDate());
    dto.put("gamePlayers",gamePlayer.getGame().getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO));
    dto.put("ships",gamePlayer.getShips().stream().map(Ship::makeShipDTO));
    dto.put("salvoes",gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(Salvo::makeSalvoDTO)).collect(Collectors.toList()));
    return dto;
  }

  //SHIP LIST
  public List <Map<String,Object>> getShipList(Set<Ship> ships){
    return ships.stream().map(Ship::makeShipDTO).collect(Collectors.toList());
  }


}

