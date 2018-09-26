var PLAYER_LIST_URL = buildUrlWithContextPath("playerslist");

function pullPlayersData() {
    $.ajax({
        url: PLAYER_LIST_URL,
        data: { "gamename": "Small+Game" }, //TODO: remove dummy game name - use game name from data.
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(playersArray) {
            console.log("Fetched players: " + playersArray);
            $('#player-details-tbody').children().remove();
            $.each(playersArray || [], addPlayerData);
        }
    });
}

// dataJson = { mName = "", mType = "", mTurnCounter = 0 }
function addPlayerData(index, dataJson) {
    var tableRow = getPlayerRow(index, dataJson);

    $('#player-details-tbody')
        .append(tableRow);
}

// dataJson = { mName = "", mType = "", mTurnCounter = 0 }
this.getPlayerRow = function(index, playerJson) {
    var playerNameLabel = $("<h1>").addClass("player-name").append(playerJson.mName);
    var playerTypeLabel = $("<h2>").addClass("player-type").append(playerJson.mType);
    var playerTurnLabel = $("<h2>").addClass("player-turn").append(playerJson.mTurnCounter);

    //TODO
    //var imageForPlayer = getImageForPlayer(playerJson.mName);
    //var playerDiscImage = $("<image>").addClass("player-image").append(imageForPlayer);

    var playerDiv = $("<div>").addClass("playerDiv").addClass("player-" + index + "-div");
    playerDiv.append(playerNameLabel).append(playerTypeLabel).append(playerTurnLabel); //TODO: append playerDiscImage

    var tableData = $("<td>").addClass("playerData").append(playerDiv);
    var tableRow = $("<tr>").append(tableData);

    return tableRow;
};

this.getImageForPlayer = function(playerName){
    //TODO
}