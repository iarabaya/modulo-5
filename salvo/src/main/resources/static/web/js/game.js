$(function(){
  loadData();
});

function getParameterByName(name) {
  var match = RegExp("[?&]" + name + "=([^&]*)").exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, " "));
}

function loadData() {
  $.get("/api/game_view/" + getParameterByName('gp'))
  .done(function(data){
      console.log(data);
      var playerInfo;

      if (data.gamePlayers[0].gpid == getParameterByName("gp")) {
        playerInfo = [
          data.gamePlayers[0].player.name,
          data.gamePlayers[1].player.name
        ];
      } else {
        playerInfo = [
          data.gamePlayers[1].player.name,
          data.gamePlayers[0].player.name
        ];
      }

      console.log(playerInfo);
      $("#playerInfo").text(playerInfo[0] + "(you) vs " + playerInfo[1]);

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
    }).fail(function(jqXHR, textStatus){
      alert("Failed: Error at " + textStatus);
    });
}
