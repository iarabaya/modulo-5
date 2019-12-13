//**************PRINCIPALMENTE LOS GET Y POST DE SHIPS Y SALVOES************

$(function(){
  loadData();
});

var players;
var shipsInfo;

function getParameterByName(name) {
  var match = RegExp("[?&]" + name + "=([^&]*)").exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, " "));
}

function loadData() {
  $.get("/api/game_view/" + getParameterByName('gp'))
  .done(function(data){
      shipsInfo = data;

      if (data.gamePlayers[0].gpid == getParameterByName("gp")) {
        players = [
          data.gamePlayers[0].player.name,
          data.gamePlayers[1].player.name
        ];
      } else {
        players = [
          data.gamePlayers[1].player.name,
          data.gamePlayers[0].player.name
        ];
      }

      $("#playerInfo").text(players[0] + "(you) vs " + players[1]);
       //placePieces(data, playerInfo);


    }).fail(function(jqXHR, textStatus){
      alert("Failed: Error at " + textStatus);
    });
}

//FUNCTION TO ADD SHIPS TO THE GAMEPLAYER REPO
function placeShips(){
  var defaultArray =  [{ shipType: "SUBMARINE", locations: ["E1", "F1", "G1"] },
                   { shipType: "PATROL_BOAT", locations: ["B4", "B5"]},
                   { shipType: "DESTROYER", locations: ["C2", "C3", "C4"]},
                   { shipType: "CARRIER", locations: ["A2", "A3", "A4", "A5", "A6"]},
                   { shipType: "BATTLESHIP", locations: ["F2", "F3", "F4","F5"]}
                  ]

   gpid = players[0] == shipsInfo.gamePlayers[0].player.name? shipsInfo.gamePlayers[0].gpid : shipsInfo.gamePlayers[1].gpid;

  $.post({
    url: "/api/games/players/"+ gpid +"/ships",
    data: JSON.stringify(defaultArray),
    dataType: "text",
    contentType: "application/json"
  }).done(function (response) {
      alert( "Ships added!" );
      console.log(response);
  }).fail(function (jqXHR, textStatus, httpError) {
      alert("Failed to add ships: " + textStatus );
      });


}

function setShips(){
 var shipsArray = getLocations();
 console.log(shipsArray)
 //return shipsArray
}

//FUNCTION TO PLACE THE SHIPS, SALVOES AND HITS IN THE GRID
function placePieces(data,playerInfo){
     var ships = data.ships;
      ships.forEach(function(shipPiece) {
        shipPiece.locations.forEach(function(shipLocation) {
          $("#" + shipLocation).addClass("ship-piece");
        });
      });

      var salvoes = data.salvoes;
      salvoes.forEach(function(salvo) {
        if (salvo.player == playerInfo[0]) {
          salvo.locations.forEach(function(salvoLocation) {
            console.log(salvoLocation);
            $("#" + salvoLocation)
              .addClass("salvo")
              .text(salvo.turn);
          });
        }
      });

      var shipHited = data.salvoes;
      shipHited.forEach(function(salvo) {
        if (salvo.player == playerInfo[1]) {
          salvo.locations.forEach(function(salvoLocation) {
            $("#T2_" + salvoLocation)
              .addClass("ship-piece-hited")
              .text(salvo.turn);
          });
        }
      });
}