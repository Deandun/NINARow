var USER_LIST_URL = buildUrlWithContextPath("userslist");

$(function() {
    var pullTimer = 1500;
    window.setInterval(pullUserNames, pullTimer); // for periodical timeout events

});

function pullUserNames() {
    $.ajax({
        data: "",  // no data - can be omitted
        url: USER_LIST_URL,
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
    var name = dataJson
    var tableData = $("<td>").append(name);
    var tableRow = $("<tr>").append(tableData);


    $('#users-names')
        .append(tableRow);
}