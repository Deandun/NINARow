// On load
$(function() {
    $("#loginInput").submit(saveUserName);
});

var loggedInUserName;

function saveUserName() {
    window.loggedInUserName = $("#userNameText").textContent;

    return true;
}