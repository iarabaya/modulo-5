//VUE CODE
fetch("/api/games")
.then(res => res.json())
.then(json => {
    app.games = json

    console.log(app.games);
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

//JQuery VERSION
$(function() {
    loadData()
});

$(document).ready(function() {
    $('#login-btn').on('click', function() {

        var user = $('#username').val();
        var pwd = $('#password').val();

        $.post("/api/login", { username: user, password: pwd })
            .done(function() {
                var msg = '<h2>Logged in!</h2>';
                $('#log').html(msg);

            }).fail(function() {
                alert('Failed at log');
            });

    });
});

// load and display JSON sent by server for /players

function loadData() {
    $.get("/api/games")
        .done(function(data) {
            updateView(data);
        })
        .fail(function(jqXHR, textStatus) {
            alert("Failed: " + textStatus);
        });
}

function updateView(data) { //for the JSON list of current games and their players
    let htmlList = data.games.map(function(games) {
        return '<li>' + new Date(games.creationDate).toLocaleString() + ' ' +
            games.gamePlayers.map(function(gp) {
                return gp.player.userName
            }).join(' , ') + '</li>';
    }).join('');
    document.getElementById("game-list1").innerHTML = htmlList;
}

function createGame() {
    $.post("/api/games") //add new game with logged in user as new gameplayer
        .done(function(data) {
            var gpid = data.gamePlayers.gpid;

            console.log('Game created successfully')
        })
        .fail(function(jqXHR, textStatus) {
            alert('Failed: ' + textStatus)
        });
}

/*function joinGame(){ //add a new game player entry
    $.post("/api/games/{}/players")
        .done(function(){
            if(){ //logged in condition

            }

            if(){ //the user is a player in that game

            }
        })
        .fail(function(jqXHR, textStatus){
            alert('Failed: '+textStatus)
        });
}*/