function initOnColumnClicks() {
    var createClickHandler = function(column) {
        return function() {
            var cell = row.getElementsByTagName("td")[0];
            var id = cell.innerHTML;
            alert("id:" + id);
        };
    };
};

function initBoard(gameData) {
    var rows = gameData.mRows;
    var columns = gameData.mColumns;
    var boardTable = $("#game-board-table");

    for(var i = 0; i < rows; i++) {
        var row = createRow(i, columns);
        boardTable.append(row);
    }

    initOnColumnClicks();

    //initPopoutIfNeeded();
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
    var cell = $("<td>")
        .addClass("cell-at-row-" + rowIndex).addClass("cell-at-column-" + columnIndex).addClass("board-cell").append();

    return cell;
}

function onCellClick(cell) {
    console.log("Cell clicked!");
}
