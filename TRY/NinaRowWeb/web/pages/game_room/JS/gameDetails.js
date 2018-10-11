
function initGameDetailsUI(gameData) {
    $('#tdGameType').append(gameData.mVariant);
    $('#tdTarget').append(gameData.mTarget);
    $('#tdBoardSize').append(gameData.mRows + "X" + gameData.mColumns);
    $('#tdGameUploader').append(gameData.mUploaderName);
    $('#tdGameState').append(gameData.mGameState);
}

function handleGameStateChanged(gameState) {
    $('#tdGameState').empty().append(gameState);

    switch(gameState) { //TODO
        case "Active":
            hadleGameStarted();
            writeNotification("Game is active!");
            break;
        case "Inactive":
            writeNotification("Waiting for more players.");
            break;
        case "Won":
            fetchPlayerToWinningSequenceMap();
            break;
        case "Draw":
            writeNotification("Game has ended in a draw!");
            break;
    }
}
