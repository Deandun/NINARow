
function initBoard(gameData) {
    var rows = gameData.mRows;
    var columns = gameData.mColumns;
    var boardTable = $("#game-board-table");

    for(var i = 0; i < rows; i++) {
        var row = createRow(i, columns);
        boardTable.append(row);
    }

    initOnColumnClicks(columns);

    //initPopoutIfNeeded();
}

function initOnColumnClicks(numberOfColumns) {

    $("#game-board-table").onclick = function() {
        console.log("Hi!");
    }
    // for(var i = 0; i < numberOfColumns; i++) {
    //     var createClickHandler = function(column) {
    //         return function() {
    //             console.log("Column " + column + " clicked");
    //         };
    //     };
    //
    //     $("cell-at-column-" + i).onclick = createClickHandler(i);
    // }
}

function createRow(rowIndex, columns) {
    var row = $("<tr>").addClass("row-" + rowIndex).addClass("board-row");

    if(rowIndex % 2 == 0) {
        row.addClass("even-row");
    } else {
        row.addClass("odd-row");
    }

    for(var i = 0; i < columns; i++) {
        var cell = createCell(rowIndex, i);
        row.append(cell);
    }

    return row;
}

function createCell(rowIndex, columnIndex) {
    var cell = $("<td>").click(function() {
        onCellClick(rowIndex, columnIndex); // Call onclick function with row and column.
    }).addClass("cell-at-row-" + rowIndex).addClass("cell-at-column-" + columnIndex).addClass("board-cell").append();

    return cell;
}

function onCellClick(row, column) {
    console.log("Cell clicked! row: " + row + " column: " + column);
    onColumnClick(column);
}
