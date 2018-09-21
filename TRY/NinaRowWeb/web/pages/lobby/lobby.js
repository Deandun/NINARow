$(function() {
    var pullTimer = 1500
    window.setInterval(pullUserNames, pullTimer); // for periodical timeout events

}

function pullUserNames() {
    $.ajax({
        data: "",  // no data - can be omitted
        url: "ColorAjaxResponse",
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(objectsDataArray) {
            $('#users-names').children().remove();
            $.each(objectsDataArray || [], addUserName);
        }
    });
}

// dataJson = {userName = ""}
function addUserName(index, dataJson) {
    var tableRow = $("<tr>")
        .append("<td></td>")
        .text(dataJson.userName);

    $('#users-names')
        .append(newBox);
}