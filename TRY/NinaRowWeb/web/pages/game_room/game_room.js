var PLAYER_LIST_URL = buildUrlWithContextPath("playerslist");
var GAME_DATA_URL = buildUrlWithContextPath("gamedata");

$(function() {
    var pullTimer = 1500;
    //window.setInterval(pullTurnsDelta, pullTimer);
    //window.setInterval(pullPlayersData, pullTimer);
    //TODO: getGameDataAndSetUI();
    initUI();
});

//TODO: set all IDs/classes in html to match
function pullPlayersData() {
    $.ajax({
        url: PLAYER_LIST_URL,
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(objectsDataArray) {
            $('#player-names').children().remove();
            $.each(objectsDataArray || [], addPlayerData);
        }
    });
}

function pullTurnsDelta() {
    //TODO
}

function getGameDataAndSetUI() {
    $.ajax({
        url: GAME_DATA_URL,
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(currentGameData) {
            $('#game-board-table').children().remove();
            initUI(currentGameData);
        }
    });
}


// TODO: move to UI JS file.
function initUI() {
    var data = window.currentGameData;
    initGameDetailsUI(data);
    initBoard(data);
}

function initGameDetailsUI(gameData) {
    $('#tdGameType').append(gameData.mVariant);
    $('#tdTarget').append(gameData.mTarget);
    $('#tdBoardSize').append(gameData.mRows + "X" + gameData.mColumns);
    $('#tdGameUploader').append(gameData.mUploaderName);
    $('#tdGameState').append(gameData.mGameState);
}

function initBoard(gameData) {
    var rows = gameData.mRows;
    var columns = gameData.mColumns;
    var boardTable = $("#game-board-table");

    for(var i = 0; i < rows; i++) {
        var row = createRow(i, columns);
        boardTable.append(row);
    }

    //initPopoutIfNeeded();
}

function createRow(rowIndex, columns) {
    var row = $("<tr>").addClass("row-" + rowIndex).addClass("board-row");

    for(var i = 0; i < columns; i++) {
        var cell = createCell(rowIndex, i);
        row.append(cell);
    }

    return row;
}

function createCell(rowIndex, columnIndex) {
    var cell = $("<td>(" + rowIndex + ',' + columnIndex + "</td>")
        .addClass("cell-at-row-" + rowIndex).addClass("cell-at-column-" + columnIndex).addClass("board-cell").append();

    //TODO: not working - cell.onclick(onCellClick);

    return cell;
}

function onCellClick(cell) {
    console.log("Cell clicked!");
}
