var refreshRate = 2000; //milli seconds
var STOCK_INFO_URL = buildUrlWithContextPath("stockInfo");
var PREVIOUS_LIST_URL = buildUrlWithContextPath("previousTransactionsList");
var BUY_LIST_URL = buildUrlWithContextPath("buyTransactionsList");
var SELL_LIST_URL = buildUrlWithContextPath("sellTransactionsList");
$(function(){
    if (sessionStorage.getItem("username") == null){
        window.location.replace("../../../index.html")
    }
    return false;
});

$(function () {
    var data = localStorage.getItem("symbol")
    if (data == null){
        window.location.href = "../AdminHome/AdminHome.html"
    }
    document.getElementById('symbolPlaceholder').innerHTML = data;
    ajaxStockInfo();
    ajaxPreviousList();
    ajaxBuyList();
    ajaxSellList();
});

function refreshStocksInfo(info) {
    //clear all current users
    $("#stockInformationTable tbody").empty();
    $('<tr><td>' + info.Symbol.toString() + '</td>' + '<td>' + info.CompanyName.toString() +  '</td>' + '<td>' + info.Price.toString() + '</td>' + '<td>' + info.Cycle.toString() + '</td>'  +'</tr>')
        .appendTo($("#stockInformationTable tbody"));
}

function ajaxStockInfo() {
    $.ajax({
        data: {stock: localStorage.getItem("symbol")},
        url: STOCK_INFO_URL,
        timeout: 2000,
        error: function(errorObject) {
             window.location.href = "../BrokerHome/BrokerHome.html";
            alert("error")
        },
        success: function(info) {
            refreshStocksInfo(info);
        }
    });

}

function refreshPreviousList(transactions) {
    //clear all current users
    $("#previousTable tbody").empty();
    // rebuild the list of users: scan all users and add them to the list of users
    $.each(transactions || [], function(index) {
        console.log("Adding transaction #" + index +  " number of stocks: " + this.NumberOfStocks.toString() + ", price: " + this.Cost.toString() + ", date: " + this.Date.toString());

        $('<tr><td>' + (index + 1) + '</td>' + '<td>' + this.NumberOfStocks.toString() + '</td>' + '<td>' + this.Cost.toString() + '</td>' + '<td>' + this.Date.toString() + '</td></tr>')
            .appendTo($("#previousTable tbody"));
    });
}

function refreshBuyList(transactions) {
    $("#buyTable tbody").empty();
    $.each(transactions || [], function(index) {
        console.log("Adding transaction #" + index +  " number of stocks: " + this.NumberOfStocks.toString() + ", price: " + this.Cost.toString() + ", date: " + this.Date.toString());

        $('<tr><td>' + (index + 1) + '</td>' + '<td>' + this.NumberOfStocks.toString() + '</td>' + '<td>' + this.Cost.toString() + '</td>' + '<td>' + this.Date.toString() + '</td></tr>')
            .appendTo($("#buyTable tbody"));
    });
}

function refreshSellList(transactions) {
    $("#sellTable tbody").empty();
    $.each(transactions || [], function(index) {
        console.log("Adding transaction #" + index +  " number of stocks: " + this.NumberOfStocks.toString() + ", price: " + this.Cost.toString() + ", date: " + this.Date.toString());

        $('<tr><td>' + (index + 1) + '</td>' + '<td>' + this.NumberOfStocks.toString() + '</td>' + '<td>' + this.Cost.toString() + '</td>' + '<td>' + this.Date.toString() + '</td></tr>')
            .appendTo($("#sellTable tbody"));
    });
}

function ajaxPreviousList() {
    $.ajax({
        data: {stock: localStorage.getItem("symbol")},
        url: PREVIOUS_LIST_URL,
        timeout: 2000,
        error: function(errorObject) {
             window.location.href = "../BrokerHome/BrokerHome.html";
            console.log("error prev")
        },
        success: function(info) {
            refreshPreviousList(info);
        }
    });
}

function ajaxBuyList() {
    $.ajax({
        data: {stock: localStorage.getItem("symbol")},
        url: BUY_LIST_URL,
        timeout: 2000,
        error: function(errorObject) {
             window.location.href = "../BrokerHome/BrokerHome.html";
            console.log("error buy")
        },
        success: function(info) {
            refreshBuyList(info);
        }
    });
}

function ajaxSellList() {
    $.ajax({
        data: {stock: localStorage.getItem("symbol")},
        url: SELL_LIST_URL,
        timeout: 2000,
        error: function(errorObject) {
             window.location.href = "../BrokerHome/BrokerHome.html";
            console.log("error sell")
        },
        success: function(info) {
            refreshSellList(info);
        }
    });
}

$(function() {
    //The users list is refreshed automatically every second
    setInterval(ajaxPreviousList,refreshRate);
    setInterval(ajaxBuyList, refreshRate);
    setInterval(ajaxSellList,refreshRate);
    setInterval(ajaxStockInfo,refreshRate);
});

