$(document).ready(function () {

    $(".search_box a").click(function () {
        $("form").submit();
    });

    $("#sendSms").click(function (e) {
        e.preventDefault();
        $(".validate-sendSms").submit();
    });

    $(".validate-sendSms").submit(function (e) {
        e.preventDefault();
        if ($(this).valid()) {
            //var msisdn = $("#prefix").val() + $("#tbMsisdn").val();
            var msisdn = $("#body_phonenumber").val();
            var message = $("#tbMessage").val();
            sendSms(msisdn, message);
        }
    });

    //Highlight the numbers that was searched
    window.highlightNumbers = function () {
        $('.numbers').find('span').css('background-color', 'lightblue');
    };

    window.searchParameter = $(".searchField").val();

    if ($(".searchField").val() == $("#body_default_search_value").val()) {
        window.searchParameter = "";
    }

    highlightNumbers();

    //Cufon.replace('.delivery_s, .support_info h2, #tarrifs_outter ul li a span, #tarrifs_outter ul li.active a span, .recommended_options, #support_phone, .tarrif_info, .service_title, .price, #main_menu span, #left_menu_head, #customers_block_head', { fontFamily: 'rockwell' });
    //Cufon.replace('.cols_head, a.refresh, .delivery_s, .support_info h2, #tarrifs_outter ul li a span, #tarrifs_outter ul li.active a span, .recommended_options, #support_phone, #heading h1, .tarrif_info, .service_title, .price, #main_menu span, .news_head, #left_menu_head, #customers_block_head', { fontFamily: 'rockwell' });
    //Cufon.replace('.cols_info, #main_menu li a, .heading2 ul li a', { hover: true });
    //Cufon.replace('.cols_info, .heading2 ul li a', { hover: true });

    var refreshNumbers = function (e) {
        e.preventDefault();
        var $this = $(this);
        $this.unbind("click");
        var targetDiv = $(this).prev('.numberDiv');
        var speed = 400;
        var data = { type: $(this).attr('id'), combination: searchParameter, dummieDate: e.timeStamp };

        if ($('.refreshLoadingText').length > 0) {
            $this.html($('.refreshLoadingText').val());
        }
        else // default text in RU
        {
            $this.html('Пожалуйста, подождите');
        }

        //Cufon.replace($this);
        var bindRefreshEvent = function () {
            $this.click(refreshNumbers);
        };
        if (targetDiv.children().length > 0) {
            //fadeout
            targetDiv.children('div:last').fadeOut(speed, function () {
                $(this).prev().fadeOut(speed, function () {
                    $(this).prev().fadeOut(speed, function () {
                        ajaxRefreshNumbers($this, targetDiv, speed, data, function () {
                            $this.html($('.refreshDefaultText').val());
                            //Cufon.replace($this);
                            highlightNumbers();
                            bindRefreshEvent();
                        }, bindRefreshEvent);
                    });


                });
            });
        } else {
            ajaxRefreshNumbers($this, targetDiv, speed, data, bindRefreshEvent, bindRefreshEvent);
        }
        //$this.html($('.refreshDefaultText').val());
    };

    $('.refresh').bind('click', refreshNumbers);


    $('.heading2 ul a').click(function (e) {
        e.preventDefault();
    });

    $('a.confirm').click(function (e) {
        e.preventDefault();
        $('#form').submit();
    });

    $('#printButton').click(function (event) {
        event.preventDefault();
        var toPrint = $('#confirm2');
        toPrint.printElement({ leaveOpen: false, printMode: 'popup' });
    });

    $('#body_sameAddress').click(function () {
        //        $('#body_address_afterwork').val($('#body_address_work').val());
        $('#body_tbWorkCity').val($('#body_tbCity').val());
        $('#body_tbWorkStreet').val($('#body_tbStreet').val());
        $('#body_tbWorkHouseNr').val($('#body_tbHouseNr').val());
        $('#body_tbWorkBlockNr').val($('#body_tbBlockNr').val());
        $('#body_tbWorkFlatNr').val($('#body_tbFlatNr').val());
    });


    $("#pick_up_at_store_button").click(function (e) {
        e.preventDefault();
        var data = { "type": "Shipping" };

        //If not showing the addresses
        if ($(".store_address").hasClass('hidden')) {

            data = { "type": "Instore" };

            $(".pnlShipping").fadeOut(900, function () {
                $(".store_address").removeClass('hidden').fadeIn(600);
                //If input field exists
                if ($('.pickup_at_store_no').length > 0) {
                    $("#pick_up_at_store_button").html('<strong>' + $('.pickup_at_store_no').val() + '</strong><span>&nbsp;</span>');
                }
                else {
                    $("#pick_up_at_store_button").html('<strong>Нет</strong><span>&nbsp;</span>');
                }
            });
        }
        else if ($(".pnlShipping").css("display") == "none") {

            data = { "type": "Shipping" };

            $(".store_address").fadeOut(900, function () {
                $(".store_address").addClass('hidden');
                $(".pnlShipping").fadeIn(600);

                if ($('.pickup_at_store_yes').length > 0) {
                    $("#pick_up_at_store_button").html('<strong>' + $('.pickup_at_store_yes').val() + '</strong><span>&nbsp;</span>');
                }
                else {
                    $("#pick_up_at_store_button").html('<strong>Да</strong><span>&nbsp;</span>');
                }
            });
        }



        //    var data = { "type": "STORE" }; //eller tex DELIVERY

        $.ajax({
            type: 'POST',
            url: "WebServices/ShoppingService.asmx/SetDeliveryType",
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (data) {

                //            alert(data);
                //            if (isResponseOk(data)) {

                //            }
            },
            error: function (x) {
                console.log(x);
            }
        });




    });

    $.validator.addMethod(
        "kazakMobileNumber",
        function (value, input) {
            return /^[0-9]{10}$/i.test(value);
        }

    );

    $(".authenticateForm").validate({
        errorClass: "auth-error",
        rules: {
            ctl00$body$phonenumber: {
                required: true,
                kazakMobileNumber: true
            },
            password: {
                required: true
            }
        },
        messages: {
        },
        errorElement: "span",
        onkeyup: false,
        onclick: false
    });

    $(".validate-sendSms").validate({
        errorClass: "auth-error",
        rules: {
            ctl00$body$phonenumber: {
                required: true,
                kazakMobileNumber: true
            },
            tbMessage: {
                required: true,
                max: 160
            }
        },
        messages: {

            password: {
                kazakMobileNumber: "must be kazak number"
            }
        },
        errorElement: "span",
        onkeyup: false,
        onclick: false
    });

    var voidEvent = function (e) {
        e.preventDefault();
    }

    var loginEvent = function (e) {
        e.preventDefault();

        $(".authenticateForm").submit();
    };

    $("#checkAuth").click(loginEvent);



    $(".authenticateForm").submit(submitFormEvent);

    $("#logOut").click(logOutTele2Number);

});




function ajaxRefreshNumbers($this, targetDiv, speed, data, callback) {
    var updateText = $this.text();
    $.ajax({
        type: 'POST',
        cache: false,
        url: "WebServices/NumberService.asmx/GetNewNumbers",
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (x) {
            if (isResponseOk(x)) {
                if (x.d.NewNumbers.length > 0) {
                    targetDiv.html('');
                    //                    $this.html('Обновить');
                    $this.html(updateText);
                    //Cufon.replace($this);
                    $(x.d.NewNumbers).each(function (k, v) {
                        addPhoneNumberItem(targetDiv, v, data);
                    });
                    fadeInNumbers(targetDiv, speed, callback);
                } else {
                    //                    $this.html('Нет доступных номеров');

                    $this.html($('.refreshErrorText').val());
                    //Cufon.replace($this);
                    fadeInNumbers(targetDiv, speed, callback);
                }
            } else {
                $this.html(x.d.ResponseMessage);
                //Cufon.replace($this);
                fadeInNumbers(targetDiv, speed, callback);
            }
        }
    });
}

function fadeInNumbers(targetDiv, speed, callback) {
    //fadeIn
    targetDiv.children('div:first').fadeIn(speed, function () {
        $(this).next().fadeIn(speed, function () {
            $(this).next().fadeIn(speed, function () {
                if (typeof (callback) === 'function') {
                    callback();
                }
            });
        });
    });

}

function isResponseOk(resp) {
    if (resp.d.Rc == 200) {
        return true;
    }
    return false;
}

function addPhoneNumberItem($div, item, name) {

    var modifiedSearchParameter;
    var modifiedNumber = item;

    //If a search was done then modify the number and set spans around it so the highlight function can set a css class on it
    if (searchParameter != "") {
        modifiedSearchParameter = "<span>" + searchParameter + "</span>";
        modifiedNumber = item.replace(searchParameter, modifiedSearchParameter);
    }

    $div.append('<div class="mt15 ' + getCorrectCssClass(name) + '" id="container" runat="server" style="display:none"><span class="fl numbers" ID="buyThisNumber">' + modifiedNumber + '</span><a class="buy_button fl a0" id="buyThisNumberLink" href="/PrepareToBuyNr.aspx?number=' + item + '">' + $(".buyNumberText").val() + '</a><div class="clear"></div></div>');
}

function getCorrectCssClass(name) {
    switch (name.type) {
        case "Gold":
            return "gold_line";
        case "Silver":
            return "silver_line";
        default:
            return "simple_line";
    }
}


function sendSms(msisdn, message) {
    $("#body_errorMessage").hide();
    $("#statusSendingSms").show();
    $.ajax({
        type: 'POST',
        url: "http://www.almaty.tele2.kz/WebServices/smsService.asmx/SendSms",
        data: JSON.stringify({ msisdn: msisdn, message: message }),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (smsResult) {
            $("#statusSendingSms").hide();
			document.getElementById("results").innerHTML
				+= " smsResult.d.ErrorCode "+smsResult.d.ErrorCode;
            if (smsResult.d.ErrorCode === 0) {
                //ALL GOOD
                $(".error").html(smsResult.d.ResponseMessage);
                $(".error").css('color', 'green');
                $(".error").show();
                $(".amountSmsLeft").html(smsResult.d.AmountSmsLeft);
                $("#captchaArea").hide();
            }
            else if (smsResult.d.ErrorCode === 103) {
                alert(smsResult.d.ResponseMessage);
                window.location.href = "authenticate.aspx"
            } else {
                $(".error").html(smsResult.d.ResponseMessage);
                $(".error").css('color', 'red');
                $(".error").show();
            }

        }
    });
}

var preventForm = function (e) {
    e.preventDefault();
};



var submitFormEvent = function (e) {
    e.preventDefault();
    var $this = $(this);
    if ($this.valid()) {
        $this.unbind("submit");
        $this.bind("submit", preventForm);
        $("#loginError").hide();
        $("#loggingIn").show();
        //var $optionListValue = $this.find("select.prefix");
        //var $number = $this.find("input.number");
        var $number = $this.find("#body_phonenumber");
        var $password = $this.find("input.password");
        $.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            url: "/WebServices/authenticate.asmx/Authenticate",
            data: JSON.stringify({
                number: $number.val(),
                password: $password.val()
            }),
            dataType: "json",
            success: function (result) {
                if (result.d.Success) {
                    window.location.href = "SmsForm.aspx";
                } else {
                    $this.unbind("submit");
                    $this.bind("submit", submitFormEvent);
                    $("#loginError").show();
                }
                $("#loggingIn").hide();
            },
            error: function (req, status, error) {
                $("#loginError").show();
                $this.unbind("submit");
                $this.bind("submit", submitFormEvent);
                $("#loggingIn").hide();
            }
        });
    }
};

var logOutTele2Number = function (e) {
    e.preventDefault();
    var $this = $(this);
    $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: "/WebServices/authenticate.asmx/LogOut",
        data: {},
        dataType: "json",
        success: function (result) {
            if (result.d.Success) {
            } else {
                $this.unbind("click");
                $this.bind("click", logOutTele2Number);
            }
            $("#loggingIn").hide();
            window.location.href = "authenticate.aspx";
        },
        error: function (req, status, error) {
            $this.unbind("click");
            $this.bind("click", logOutTele2Number);
        }
    });

};


   

   
