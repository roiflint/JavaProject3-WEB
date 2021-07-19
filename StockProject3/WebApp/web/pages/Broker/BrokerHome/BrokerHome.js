var refreshRate = 2000; //milli seconds
var refreshMessageRate = 15000;
var USER_LIST_URL = buildUrlWithContextPath("usersList");
var STOCK_LIST_URL = buildUrlWithContextPath("stockList");
var TRANSACTIONS_LIST_URL = buildUrlWithContextPath("transactionsList");
var UPDATE_LIST_URL = buildUrlWithContextPath("userUpdatesList");
var UPDATE_WALLET_URL = buildUrlWithContextPath("updateWallet");

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
    //clear all current users
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

function refreshTransactionsList(transactions) {
    //clear all current users
    $("#transactionsList tbody").empty();
    // rebuild the list of users: scan all users and add them to the list of users
    $.each(transactions || [], function(index) {
        console.log("Adding transaction #" + index +  " action: " + this.action.toString() + " symbol: " + this.symbol.toString() + ", price: " + this.price.toString() + ", oldBalance: " + this.oldBalance.toString() + ", newBalance: " + this.newBalance.toString() + ", date: " + this.date.toString());

        $('<tr><td>' + (index + 1) + '</td>' + '<td>' + this.action.toString() + '</td>' + '<td>' + this.symbol.toString() + '</td>' + '<td>' + this.price.toString() + '</td>' + '<td>' + this.oldBalance.toString() + '</td>' + '<td>' + this.newBalance.toString() + '</td>' + '<td>' + this.date.toString() + '</td></tr>')
            .appendTo($("#transactionsList tbody"));
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

function ajaxTransactionsList() {
    $.ajax({
        url: TRANSACTIONS_LIST_URL,
        success: function(transactions) {
            refreshTransactionsList(transactions);
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

function ajaxUpdateWallet() {
    $.ajax({
        url: UPDATE_WALLET_URL,
        success: function(currency) {
            console.log("Wallet is: " + currency)
            data = "Wallet: " + currency;
            document.getElementById("myWallet").innerText = data;
        }
    });
}


//activate the timer calls after the page is loaded
$(function() {
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    setInterval(ajaxStockList,refreshRate);
    setInterval(ajaxTransactionsList,refreshRate);
    setInterval(ajaxUpdateList,refreshRate)
    setInterval(ajaxUpdateWallet,refreshRate);
    setInterval(clearLables,refreshMessageRate);
});

$(function() { // onload...do
    //add a function to the submit event
    $("#addFunds").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout: 2000,
            error: function(errorObject) {
                console.error("Failed to add funds !");
            },
            success: function(currency) {
                data = "Wallet: " + currency;
                document.getElementById("myWallet").innerText = data;
                document.getElementById("addFunds").reset();
            }
        });

        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});

$(function() { // onload...do
    //add a function to the submit event
    $("#issueStockform").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout: 2000,
            error: function() {
                document.getElementById("Stock-Error-Placeholder").innerText = "Stock already exist in the system";
            },
            success: function() {
                document.getElementById("Stock-Error-Placeholder").innerText = "Stock was added to system successfully";
                document.getElementById("issueStockform").reset();
            }
        });

        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});

$(function (){
    ajaxUsersList();
    ajaxStockList();
    ajaxTransactionsList();
    ajaxUpdateList();
    ajaxUpdateWallet();
})

function stockUrl(symbol) {
   // window.location.href = "../BrokerView/BrokerView.html?symbol=" + encodeURIComponent(symbol)
    //window.location.replace = "BrokerView/BrokerView.html?symbol=" + encodeURIComponent(symbol.value);
    localStorage.setItem("symbol",symbol);
    window.location.href = "../BrokerView/BrokerView.html";
    return true
}

$(function() {
    $('#loadXMLform').ajaxForm({
        success: function(msg) {

            document.getElementById("XML-Error-Placeholder").innerText = "File loaded successfully";
            document.getElementById("loadXMLform").reset();

        },
        error: function(msg) {
            document.getElementById("XML-Error-Placeholder").innerText = msg.responseText;
        }
    });
    return false;
});

function clearLables(){
    document.getElementById("Stock-Error-Placeholder").innerText = "";
    document.getElementById("XML-Error-Placeholder").innerText = "";
}

