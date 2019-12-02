// VUE.JS VERSION
var app = new Vue({
    el: "#app",
    data: {
        games: [],
        player: {},
        leaderboard:[],
    },
    methods: {
        logIn: function (){
                        var user = $('#username').val();
                        var pwd = $('#password').val();

                       $.post("/api/login", { username: user, password: pwd })
                                   .done(function() {
                                       alert('Logged in successfully!');
                                        location.reload();
                                   }).fail(function() {
                                       alert('Failed at log');
                                   });

                      console.log('logged in');

                    },

        logOut: function(){
            $.post("/api/logout")
                            .done(function(){
                            alert('Logged out successfully!');
                            }).fail(function(){
                            alert('Failed at log out')
                            });

             console.log('logged out');
             this.player = 'Guest';
        },

        signUp: function(){

                var user = $('#username').val();
                var pwd = $('#password').val();

                $.post("/api/players", { username: user, password: pwd })
                .done(function(){
                    alert('Account created')
                }).fail(function(){
                    alert('Failed at sign up')
                });
                this.logIn();
                console.log('sign in');
        },

        createGame: function(){
              $.post("/api/games")
                  .done(function() {
                      console.log('Game created successfully')
                  })
                  .fail(function(jqXHR, textStatus) {
                      alert('Failed: ' + textStatus)
                  });
        },

        joinGame: function(){
          console.log('joining');
        },

    //RETURN TO GAME (should do this if (1) there is logged in user, and (2) that user is a player in that game)
    //JOIN AN EXISTANT GAME THAT needs one more player, and is not the same player
        statusBtn: function(gamePlayers){
            var status = '';
            var btn = '';
            var gpid = 0;

            if(gamePlayers.length <= 1 && gamePlayers[0].player.name != this.player.name){
                status = 'join game';
                btn = '<button class="inline btn btn-primary mb-2">'+ status +'</button>';
            }else if(gamePlayers[0].player.name == this.player.name || gamePlayers[1].player.name == this.player.name){
                status = 'return to game';
                gpid = gamePlayers[0].player.name == this.player.name? gamePlayers[0].gpid : gamePlayers[1].gpid;
                btn = '<button class="inline btn btn-warning mb-2"><a href="game.html?gp='+ gpid +'">'+ status +'</a></button>';
             }else{
                status = 'Full';
             }


            return status == 'Full'? '<button class="btn btn-danger mb-2" disabled>Full</button>': btn;
        }
    },
    computed:{
      logged: function(){
        if(this.player == 'Guest'){
          return false;
        }else{
          return true;
        }
      }
    }
});

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
.then(response=> response.json())
.then(json => {
    app.leaderboard = json.sort((a,b) => b.total - a.total);
});

function changeDateFormat (){
    for (i in app.games){
        var newDate = new Date(app.games[i].created).toLocaleString();
        app.games[i].created = newDate
    }
}

