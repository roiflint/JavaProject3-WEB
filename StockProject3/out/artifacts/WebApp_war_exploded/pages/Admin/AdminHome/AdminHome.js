var refreshRate = 2000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("usersList");
var STOCK_LIST_URL = buildUrlWithContextPath("stockList");

//users = a list of usernames, essentially an array of javascript strings:
// ["moshe","nachum","nachche"...]
function refreshUsersList(users) {
    //clear all current users
    $("#usersList tbody").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function(index) {
        console.log("Adding user #" + index + ": " + this.username + " - " + this.role);

        $('<tr><td>' + this.username + '</td>' + '<td>' + this.role + '</td>' + '</tr>')
            .appendTo($("#usersList tbody"));
    });
}

function refreshStocksList(stocks) {
    $("#stocksList tbody").empty();
    $.each(stocks || [], function(index) {
        console.log("Adding stock #" + index + " symbol: " + this.Symbol.toString() + ", company name: " + this.CompanyName.toString() + ", price: " + this.Price.toString() + ", cycle: " + this.Cycle.toString());

        $('<tr><td>' + (index + 1) + '</td>' + '<td>' + '<div class="sym" onclick="stockUrl(this.id)">' +  this.Symbol.toString() + '</div>' +  '</td>' + '<td>' + this.CompanyName.toString() + '</td>' + '<td>' + this.Price.toString() + '</td>' + '<td>' + this.Cycle.toString() + '</td>' + '</tr>')
            .appendTo($("#stocksList tbody"));
    })

    var $div = $("#stocksList div");
    $div.attr('id',function(index){
        symbol = stocks[index].Symbol.toString()
        return symbol;
    })

}

function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            refreshUsersList(users);
        }
    });
}

function ajaxStockList() {
    $.ajax({
        url: STOCK_LIST_URL,
        success: function(stocks) {
            refreshStocksList(stocks);
        }
    });
}

//activate the timer calls after the page is loaded
$(function() {
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    setInterval(ajaxStockList,refreshRate);
});

function stockUrl(symbol) {
    // window.location.href = "../BrokerView/BrokerView.html?symbol=" + encodeURIComponent(symbol)
    //window.location.replace = "BrokerView/BrokerView.html?symbol=" + encodeURIComponent(symbol.value);
    localStorage.setItem("symbol",symbol);
    window.location.href = "../AdminView/AdminView.html";
    return true
}



$(function (){
    ajaxUsersList();
    ajaxStockList();
})