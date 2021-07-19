$(function() { // onload...do
    //add a function to the submit event
    $("#LoginForm").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout: 2000,
            error: function(errorObject) {
                console.error("Failed to login !");
                $("#Error-Placeholder").html("");
                $("#Error-Placeholder").append(errorObject.responseText)
            },
            success: function(nextPageUrl) {
                sessionStorage.setItem("username","valid");
                window.location.href=nextPageUrl;
              //  window.location.replace(nextPageUrl);
            }
        });

        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});
