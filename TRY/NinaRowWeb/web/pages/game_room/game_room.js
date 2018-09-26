var GAME_STATE_URL = buildUrlWithContextPath("gamestate");

$(function() {
    var pullTimer = 1500;
    init();
    //window.setInterval(pullTurnsDelta, pullTimer);
    window.setInterval(pullGameState, pullTimer);
    window.setInterval(pullPlayersData, pullTimer);
    initUI();
});

function init() {
}

function pullTurnsDelta() {
    //TODO
}

// we need this function only if we can't get game data from lobby.js
// function getGameDataAndSetUI() {
//     $.ajax({
//         url: GAME_DATA_URL,
//         timeout: 2000,
//         error: function(e) {
//             console.error("Failed to send ajax");
//         },
//         success: function(playersData) {
//             $('#').children().remove();
//             initPlayersUI(playersData);
//         }
//     });
// }

function initUI() {
    //var data = window.currentGameData;
    //initGameDetailsUI(data); //TODO
    initBoard();
}

function initGameDetailsUI(gameData) {
    $('#tdGameType').append(gameData.mVariant);
    $('#tdTarget').append(gameData.mTarget);
    $('#tdBoardSize').append(gameData.mRows + "X" + gameData.mColumns);
    $('#tdGameUploader').append(gameData.mUploaderName);
    $('#tdGameState').append(gameData.mGameState);
}

function pullGameState() {
    $.ajax({
        url: GAME_STATE_URL,
        data: { "gamename": "Small+Game" }, //TODO: remove dummy game name - use game name from data.
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(gameState) {
            handleGameState(gameState);
            console.log("Fetched game state: " + gameState);
        }
    });
}

function handleGameState(gameState) {
    if (gameState === "Active") {
        //TODO: cancel pull game state interval
        //TODO: update tdGameState to be "active"
        hadleGameStarted();
    } else {
        //TODO: update tdGameState to be "inactive"
    }
}

function hadleGameStarted() {
    //TODO:
    // 1. Alert participants that game has started.
    // 2. Start pulling game turns delta.

}