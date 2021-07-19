var refreshRate = 2000; //milli seconds
var refreshMessageRate = 15000;
var STOCK_INFO_URL = buildUrlWithContextPath("stockInfo");
var PREVIOUS_LIST_URL = buildUrlWithContextPath("previousTransactionsList");
var UPDATE_LIST_URL = buildUrlWithContextPath("userUpdatesList");

$(function(){
    if (sessionStorage.getItem("username") == null){
        window.location.replace("../../../index.html")
    }
    return false;
});

$(function () {
    var data = localStorage.getItem("symbol")
    if (data == null){
        console.log("no stock symbol")
        window.location.href = "../BrokerHome/BrokerHome.html"
    }
    document.getElementById('symbolPlaceholder').innerHTML = data;
    ajaxStockInfo();
    ajaxPreviousList();
    ajaxUpdateList();
    document.getElementById("symbolHolder").value = data;
});

function refreshStocksInfo(info) {
    $("#stockInformationTable tbody").empty();
    $('<tr><td>' + info.Symbol.toString() + '</td>' + '<td>' + info.CompanyName.toString() +  '</td>' + '<td>' + info.Price.toString() + '</td>' + '<td>' + info.Cycle.toString() + '</td>' + '<td>' + info.Holdings.toString() + '</td>' +'</tr>')
        .appendTo($("#stockInformationTable tbody"));
}

function ajaxStockInfo() {
    $.ajax({
        data: {stock: localStorage.getItem("symbol")},
        url: STOCK_INFO_URL,
        timeout: 2000,
        error: function(errorObject) {
            window.location.href = "../BrokerHome/BrokerHome.html";
        },
        success: function(info) {
            refreshStocksInfo(info);
        }
    });

}

function refreshPreviousList(transactions) {

    $("#previousTable tbody").empty();

    $.each(transactions || [], function(index) {
        console.log("Adding transaction #" + index +  " number of stocks: " + this.NumberOfStocks.toString() + ", price: " + this.Cost.toString() + ", date: " + this.Date.toString());

        $('<tr><td>' + (index + 1) + '</td>' + '<td>' + this.NumberOfStocks.toString() + '</td>' + '<td>' + this.Cost.toString() + '</td>' + '<td>' + this.Date.toString() + '</td></tr>')
            .appendTo($("#previousTable tbody"));
    });
}

function refreshUpdatesList(updates) {
    //clear all current users
    $("#updateList tbody").empty();
    // rebuild the list of users: scan all users and add them to the list of users
    $.each(updates || [], function(index) {
        console.log("Adding transaction #" + index +  " update: " + this.toString());

        $('<tr><td>' + (index + 1) + '</td>' + '<td>' + this.toString() + '</td></tr>')
            .appendTo($("#updateList tbody"));
    });
}

function ajaxPreviousList() {
    $.ajax({
        data: {stock: localStorage.getItem("symbol")},
        url: PREVIOUS_LIST_URL,
        timeout: 2000,
        error: function(errorObject) {
             window.location.href = "../BrokerHome/BrokerHome.html";
        },
        success: function(info) {
            refreshPreviousList(info);
        }
    });
}

function ajaxUpdateList() {
    $.ajax({
        url: UPDATE_LIST_URL,
        success: function(updates) {
            refreshUpdatesList(updates);
        }
    });
}

$(function() {
    setInterval(ajaxPreviousList,refreshRate);
    setInterval(ajaxStockInfo,refreshRate);
    setInterval(clearMessage,refreshMessageRate);
    setInterval(ajaxUpdateList,refreshRate);
});

function priceRequired(){
    if (document.getElementById("mkt").checked){
        document.getElementById("price").required = false;
        return;
    }
    document.getElementById("price").required = true;
}

function clearMessage(){
    document.getElementById("Error-Placeholder").innerText = "";
}


$(function() { // onload...do
    //add a function to the submit event
    $("#transactionForm").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: buildUrlWithContextPath("makeTransaction"),
            timeout: 2000,
            error: function() {
                console.error("error");
                window.location.href = "../BrokerHome/BrokerHome.html";
            },
            success: function(message) {
                console.log(message)
                document.getElementById("Error-Placeholder").innerText = message;
                document.getElementById("transactionForm").reset();
            }
        });

        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});



