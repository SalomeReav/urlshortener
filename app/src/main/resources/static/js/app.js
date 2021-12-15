$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/api/link",
                    data : $(this).serialize(),
                    success : function(msg, status, request) {
                            var qrComponent = "";
                             if(msg.qr != null) qrComponent =
                                    "<br/><p class='card-text'> QRCode at <a target='_blank' href='"
                                    + msg.qr
                                    + "'>"
                                    + msg.qr
                                    + "</a></p>"
                                    + "</div>";
                            $("#result").html(
                                "<div class='card text-center border-primary mx-auto' "
                                +"style='max-width: 70%; background-color:#E0FFFF;'>"
                                + "<div class='card-body'>"
                                + "<h6 class='card-title'><a target='_blank' href='"
                                + request.getResponseHeader('Location')
                                + "'>"
                                + request.getResponseHeader('Location')
                                + "</a></h6>"
                                + qrComponent
                                + "</div>"
                                );
                    },
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead mx-auto' style='max-width: 70%;'>ERROR</div>");
                    }
                });
            });
    });