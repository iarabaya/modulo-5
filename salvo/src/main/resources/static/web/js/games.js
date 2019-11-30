// VUE.JS VERSION
var app = new Vue({
    el: "#app",
    data: {
        games: [],
        player:{},
        leaderboard:[]
    },
    methods: {
        logIn: function (){

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
.then(json=> {
    app.leaderboard = json.sort((a,b) => b.total - a.total);
});

function changeDateFormat (){
    for (i in app.games){
        var newDate = new Date(app.games[i].created).toLocaleString();
        app.games[i].created = newDate
    }
}


//LOG IN - SING UP - LOG OUT
$(document).ready(function() {

    $('#login-btn').on('click', function() {

        if($('#username').val() || $('#password').val()){
            var user = $('#username').val();
            var pwd = $('#password').val();

            $.post("/api/login", { username: user, password: pwd })
            .done(function() {
                alert('Logged in successfully!');
                $('#login-card').hide(1000);
                $('#logout-btn').show(1000);
                $('#account').text("User: " + user);

            }).fail(function() {
                alert('Failed at log');
            });

        }else{
            $('#missing').text("Missing data: write your email or password (if you don't have an account you can Sign UP)");
            $("#signup-btn").show()
        }
    });

    $('#signup-btn').on('click', function(){
        var user = $('#username').val();
        var pwd = $('#password').val();

        $.post("/api/players", { username: user, password: pwd })
        .done(function(){
            alert('Account created')
            $('#missing').text('Now log in!')
        }).fail(function(){
            alert('Failed at sign up')
        });
    });

    $('#logout-btn').on('click', function(){
        $.post("/api/logout")
        .done(function(){
        alert('Logged out successfully!')
        $('#login-card').show(1000);
        $('#logout-btn').hide();
        $('#account').text('User: guest')

        }).fail(function(){
        alert('Failed at log out')
        });
    });

});


//RETURN TO GAME (should do this if (1) there is logged in user, and (2) that user is a player in that game)
function returnGame(){
    var returnBtn ='<button class="inline btn btn-warning mb-2" id="return-btn"  disabled>Return to Game</button>';

}

//JOIN AN EXISTANT GAME THAT needs one more player, and is not the same player

function updateJoinGame(info){
    var joinButton ='<button class="inline btn btn-warning mb-2" id="join-btn" name="join" disabled>Join Game</button>'

    return console.log(info.games);
}

//CREATE A NEW GAME add new game with logged in user as new gameplayer

function createGame() {
    $.post("/api/games")
        .done(function(data) {
            var gpid = data.gamePlayers.gpid;

            console.log('Game created successfully')
        })
        .fail(function(jqXHR, textStatus) {
            alert('Failed: ' + textStatus)
        });
}



