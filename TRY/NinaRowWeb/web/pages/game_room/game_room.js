var PLAYER_LIST_URL = buildUrlWithContextPath("playerslist");
var GAME_DETAILS_URL = buildUrlWithContextPath("gameDetails");

$(function() {
    var pullTimer = 1500;
    window.setInterval(pullGameDetails, pullTimer);
    window.setInterval(pullPlayerNames, pullTimer);
    $("#uploadForm").submit(onFormSubmit);
});

function pullPlayerNames() {
    $.ajax({
        url: PLAYER_LIST_URL,
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(objectsDataArray) {
            $('#player-names').children().remove();
            $.each(objectsDataArray || [], addPlayerName);
        }
    });
}


// dataJson = "playername"
function addPlayerName(index, dataJson) {
    var tableData = $("<td>").append(dataJson);
    var tableRow = $("<tr>").append(tableData);

    $('#player-names')
        .append(tableRow);
}


function pullGameDetails() {
    $.ajax({
        url: GAME_DETAILS_URL,
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(objectsDataArray) {
            //TODO: implement
        }
    });
}