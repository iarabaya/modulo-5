//button component which changes style and function depending on the game status
Vue.component("status-button", {
  template:
    '<button :class=buttonType(status) v-on:click="action()">{{ status }}</button>',
  props: ["status", "game-players", "action"],
  methods: {
    buttonType: function(status) {
      var type;
      if (status == "Full") {
        type = '"inline btn btn-danger mb-2"" disabled';
      } else if (status == "join game") {
        type = "inline btn btn-primary mb-2" ;
      } else {
        type = "inline btn btn-warning mb-2";
      }
      return type;
    }
  }
});

// VUE.JS APP VERSION
var app = new Vue({
  el: "#app",
  data: {
    games: [],
    player: {},
    leaderboard: [],
    username:'',
    password:''
  },
  methods: {
    logIn: function() {

      $.post("/api/login", { username: this.username, password: this.password })
        .done(function() {
          alert("Logged in successfully!");
          location.reload();
        })
        .fail(function() {
          alert("Failed at log");
        });
    },

    logOut: function() {
      $.post("/api/logout")
        .done(function() {
          alert("Logged out successfully!");
        })
        .fail(function() {
          alert("Failed at log out");
        });

      this.player = "Guest";
    },

    signUp: function() {

      $.post("/api/players", { username: this.username, password: this.password })
        .done(function() {
          alert("Account created");
        })
        .fail(function() {
          alert("Failed at sign up");
        });
      this.logIn();
    },

    createGame: function() {
      $.post("/api/games")
        .done(function(response) {
          console.log("Game created successfully");
          var gpid = response.game.gpid;

          location.replace("game.html?gp=" + gpid);
        })
        .fail(function(jqXHR, textStatus) {
          alert("Failed: " + textStatus);
        });
    },

    //sets up the button message depending on the needed function
    statusText: function(gamePlayers) {
      var status = "";
      var gpid = 0;

      if (gamePlayers.length <= 1 && gamePlayers[0].player.name != this.player.name)
      {
        status = "join game";
      } else if (gamePlayers[0].player.name == this.player.name || gamePlayers[1].player.name == this.player.name)
      {
        gpid = gamePlayers[0].player.name == this.player.name ? gamePlayers[0].gpid: gamePlayers[1].gpid;
        status = "return to game";

      } else {
        status = "Full";
      }

      return status;
    },
    //RETURN TO GAME (should do this if (1) there is logged in user, and (2) that user is a player in that game)
    //JOIN AN EXISTANT GAME THAT needs one more player, and is not the same player
    getAction: function(gamePlayers, gameId) {
      var action;
      var gpid;

      if ( gamePlayers.length <= 1 && gamePlayers[0].player.name != this.player.name )
      {
        action = function() {
          var content;
          $.post("/api/game/" + gameId + "/players")
            .done(function(data) {
              gpid = data.gpid;
              location.replace("game.html?gp=" + gpid);
            })
            .fail(function(jqXHR, textStatus) {
              alert("Failed: " + textStatus);
            });
        };
      } else if ( gamePlayers[0].player.name == this.player.name || gamePlayers[1].player.name == this.player.name)
      {
        gpid = gamePlayers[0].player.name == this.player.name ? gamePlayers[0].gpid : gamePlayers[1].gpid;
        action = function() {
          location.replace("game.html?gp=" + gpid);
        };
      } else {
        action = function(){console.log('full game')}

      }
      return action;
    }
  }, //just a property to check if someone is logged or not
  computed: {
    logged: function() {
      if (this.player == "Guest") {
        return false;
      } else {
        return true;
      }
    }
  }
});

/********************** THE AJAX CALLS *********************************************************************************************/
//GET GAMES FOR GAME LIST
fetch("/api/games")
  .then(response => response.json())
  .then(json => {
    app.games = json.games;
    app.player = json.player;
    changeDateFormat();
  });

//GET LEADERBOARD
fetch("/api/leaderboard")
  .then(response => response.json())
  .then(json => {
    app.leaderboard = json.sort((a, b) => b.total - a.total);
  });

function changeDateFormat() {
  for (i in app.games) {
    var newDate = new Date(app.games[i].created).toLocaleString();
    app.games[i].created = newDate;
  }
}

function handleErrors(response) {
    if (!response.ok) {
        throw Error(response.statusText);
        }
    return response;
}