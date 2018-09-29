var GAME_DATA_URL = buildUrlWithContextPath("gamedata");
var GAME_STATE_URL = buildUrlWithContextPath("gamestate");
var PLAYER_LIST_URL = buildUrlWithContextPath("playerslist");
var pullTimer = 1500;


var currentGameData;
var currentPlayerName;
var gameState = "InActive";
var gameNameForPullingData;

$(function() {
    init();
    getGameData();
});

function init() {
}

function pullTurnsDelta() {
    //TODO
}

function getGameData() {
    $.ajax({
        url: GAME_DATA_URL,
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: onFetchedGameData
    });
}

function startPullingIntervals() {
    gameNameForPullingData = currentGameData.mGameName.replace(' ', '+');
    window.setInterval(pullGameState, pullTimer);
    window.setInterval(pullPlayersData, pullTimer);
    //window.setInterval(pullTurnsDelta, pullTimer);
}

// gameDataJson = { mLoggedInPlayerName = "", mGameDescriptionData = { mGameName = "", mGameState = "", mCurrentNumberOfPlayers = 0, mMaxPlayers = 4, mRows = 7, mColumns = 8, mTarget = 4, mUploaderName = "" }}
function onFetchedGameData(gameDataJson) {
    currentPlayerName = gameDataJson.mCurrentPlayerName;
    currentGameData = gameDataJson.mGameDescriptionData;
    startPullingIntervals();

    initUI(currentGameData);
}

function initUI(gameData) {
    initGameDetailsUI(gameData);
    initBoard(gameData);
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
        data: { "gamename": gameNameForPullingData },
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(newGameState) {
            if(newGameState !== gameState) {
                gameState = newGameState;
                console.log("Game state changed to " + newGameState);
                handleGameStateChanged(gameState);
            }
        }
    });
}

function handleGameStateChanged(gameState) {
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