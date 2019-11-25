//JQuery VERSION
$(function() {
    loadGames()
    loadLeaderBoard()
});

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


function loadAccount(){

}


// load and display JSON of Game List and Leaderboard
function loadGames() {
    $.get("/api/games")
        .done(function(data){
            updateGamesView(data);
        })
        .fail(function(jqXHR, textStatus) {
            alert("Failed: " + textStatus);
        });
}

function loadLeaderBoard(){
    $.get("/api/leaderboard")
    .done(function(data){
        updateLeaderboard(data);
    })
    .fail(function(jqXHR, textStatus){
        alert("Failed: " + textStatus);
    });
}

function updateGamesView(data) { //for the JSON Table of current games and their players
    let htmlList = data.games.map(function(games) {
        return '<tr><th scope="row">'+ games.id +'</th><td>' + new Date(games.created).toLocaleString()
        +'</td><td>' + games.gamePlayers.map(function(gp) {
                return gp.player.name
            }).join('  VS  ') + '</td></tr>';
    }).join('');
    document.getElementById("game-list").innerHTML = htmlList;
}

function updateLeaderboard(data){ //for the JSON Leaderboard Table
    var users = data.sort((a,b) => b.total - a.total);

    let htmlList = users.map(function(data){
        return '<tr><th scope="row">'
        + data.user +'</th><td>'
        + data.wins +'</td><td>'
        + data.loses +'</td><td>'
        + data.ties +'</td><td>'
        + data.total + '</td></tr>' }).join('');
    document.getElementById("leaderboard").innerHTML = htmlList;
}

//JOIN GAME

function joinGame(){
var joinButton ='<button class="inline btn btn-warning mb-2" id="join-btn" name="join" disabled>Join Game</button>'


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

 /* VUE.JS VERSION

fetch("/api/games")
.then(res => res.json())
.then(json => {
    app.games = json
})

var app = new Vue({
    el: "#app",
    data: {
        games: []
    }
});

function changeDateFormat (){
    for (i in app.games){
        var newDate = new Date(app.games[i].created).toLocaleString();
        app.games[i].created = newDate
    }
}

*/