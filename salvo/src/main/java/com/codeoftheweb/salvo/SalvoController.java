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

    //TO CREATE NEW PLAYER (POST)
  @RequestMapping(path = "/players", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String username,@RequestParam String password) {

    if(username.isEmpty() || password.isEmpty()){
      return new ResponseEntity<>(makeMap("error", "Missing data" ),HttpStatus.FORBIDDEN);
    }

    Player player = playerRepository.findByUserName(username);

    if(player == null){ //todo esta bien, no existe un usuario con este nombre
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

    //TO GET THE GAME LIST OF THAT PLAYER (GET)
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

    //TO CREATE A NEW GAME (POST)
  @RequestMapping(path = "/games", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){
    if(isGuest(authentication)){
      return new ResponseEntity<>(makeMap("error", "cannot create game being a guest" ),HttpStatus.UNAUTHORIZED);
    }else{

        Player player = playerRepository.findByUserName(authentication.getName());
        if(player != null) { //hay alguien logueado
            Date date = new Date();
            Game game = gameRepository.save(new Game(date));
            GamePlayer gp = new GamePlayer(date, game, player);
            gamePlayerRepository.save(gp);

            return new ResponseEntity<>(makeMap("game", gp.makeGamePlayerDTO()), HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(makeMap("error", "user not found"),HttpStatus.FORBIDDEN);
        }

    }
  }

  private boolean isGuest(Authentication authentication) {
    return authentication == null || authentication instanceof AnonymousAuthenticationToken;
  }

  //TO JOIN AN EXISTANT GAME (POST)
    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication){

        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error","login needed"), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepository.findByUserName(authentication.getName());

        boolean exists = gameRepository.findById(gameId).isPresent();
        if(!exists){
            return new ResponseEntity<>(makeMap("error", "no such game"), HttpStatus.I_AM_A_TEAPOT);
        }

        Game game = gameRepository.findById(gameId).get();
        if(game.getPlayers().size() >= 2){
            return new ResponseEntity<>(makeMap("Game is full", "Game is full"), HttpStatus.FORBIDDEN);
        }

        gameRepository.save(game);
        Date date = new Date();
        GamePlayer gp = new GamePlayer(date, game, player);
        gamePlayerRepository.save(gp);

      return new ResponseEntity<>(makeMap("gpid", gp.getId()),HttpStatus.CREATED);
    }


  //TO GET THE GAME VIEW (GET)
 @RequestMapping("/game_view/{gamePlayerId}")
  public ResponseEntity<Map<String, Object>> getGameViewAuthenticated(@PathVariable Long gamePlayerId, Authentication authentication){
      if(isGuest(authentication)){
          return new ResponseEntity<>(makeMap("error", "cannot see game as guest" ),HttpStatus.FORBIDDEN);
      }
      Player player =  playerRepository.findByUserName(authentication.getName());
      GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

      if(gamePlayer.getPlayer().getId() != player.getId()){
        return new ResponseEntity<>(makeMap("error", "you are not a player of this game" ),HttpStatus.UNAUTHORIZED);
      }

      return new ResponseEntity<>(gameViewDTO(gamePlayer),HttpStatus.OK);
  }

//TO GET THE LEADERBOARD (GET)
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

