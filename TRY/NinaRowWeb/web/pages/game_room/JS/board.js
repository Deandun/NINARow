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
