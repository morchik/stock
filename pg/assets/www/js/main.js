(function($, window){

var $dashboard,
    $dashboardList,
    $dialogImg,
    $fullImg,
    $options,
    $optionsName,
    // Суффиксы для блоков картинок
    suffix =  ['a', 'b', 'c'],
    // Текущий пользователь
    currentUser;


/**
 * Загружает и отрисовывает контент
 *
 */
var getN = function () {
	$.mobile.pageLoading();
	console.log("main.js getN start");
	    $.ajax({
			dataType: "html",
            url: "http://www.almaty.tele2.kz/ru/private_clients/SmsForm.aspx",
			/*xhrFields: {
				withCredentials: true
			},*/
            success: function (result) {
				$.mobile.pageLoading(true);
				//console.log("main.js getN: success "+result);
				var div = $('<div>');
				div.html(result);
				var content = div.find('.amountSmsLeft');
				//console.log("main.js getN amountSmsLeft "+content);
				//$("#element").attr('attribute', content.html());
                if (content != undefined) {
					console.log("main.js getN get_left "+content.html());
					
					if (content.html() != null)
						$("#get_left").html(content.html());
					else
						$("#get_left").html("ошибка");
					//amountSmsLeft
                    //window.location.href = "#dashboard";
					//$('#dashboard-list').html(result.d.Success);
					//$("#loginError").hide();
					//$.mobile.changePage('#dashboard');
                } else {
					$("#get_left").html("err");
                    //$this.unbind("submit");
                    //$this.bind("submit", submitFormEvent);
                    //$("#loginError").show();
					//$("#loginError").html();
                }
                
            },
            error: function (req, status, error) {
				$.mobile.pageLoading(true);
				console.log("main.js getN: error "+status+" "+error);
				$("#get_left").html("error "+status);
                //$("#loginError").show();
                //$("#loggingIn").hide();
            }
        });
}


/**
 * Загружает и отрисовывает контент
 *
 * @param {String} [user='', pass='']
 */
var load = function (user, pass) {
	//console.log("main.js load user: "+user);
	//console.log("main.js load pass: "+pass);
    // Включаем крутилку
    $.mobile.pageLoading();

    //Добавляем на всякий случай
    var timeoutId = window.setTimeout(function () {
        $.mobile.pageLoading(true);
    }, 5000);
    
    // Загружаем логинимся
		var $number = user;
		console.log("main.js load number: "+$number);
        var $password = pass;
		console.log("main.js load password: "+$password);
		$("#loggingIn").show();
		$("#loginError").hide();
		$("#loginErrorText").html("");
        $.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            url: "http://www.almaty.tele2.kz/WebServices/authenticate.asmx/Authenticate",
            data: JSON.stringify({
                number: $number,
                password: $password
            }),
            dataType: "json",
            success: function (result) {
				$.mobile.pageLoading(true);
				console.log("main.js ajax: success "+result.d.Success);
                if (result.d.Success) {
                    //window.location.href = "#dashboard";
					//$('#dashboard-list').html(result.d.Success);
					$("#loginError").hide();
					$.mobile.changePage('#dashboard');
					getN();
                } else {
                    //$this.unbind("submit");
                    //$this.bind("submit", submitFormEvent);
                    $("#loginError").show();
					//$("#loginError").html();
                }
                $("#loggingIn").hide();
            },
            error: function (req, status, error) {
				$.mobile.pageLoading(true);
				console.log("main.js ajax: error "+status+" "+error);
				$("#loginErrorText").html(status+" ajax "+error+" "+req);
                $("#loginError").show();
                $("#loggingIn").hide();
            }
        });
		

	// Останавливаем крутилку
    //$.mobile.pageLoading(true);
	console.log("main.js pageLoading stop ");
	/*
        // Убиваем таймер
        window.clearTimeout(timeoutId);
    });
	*/
};


/**
 * Загружает и отрисовывает контент
 *
 * @param {String} [user='', pass='']
 */
var send = function (msisdn, message) {
	console.log(" 1 main.js SEND msisdn: "+msisdn);
	console.log(" 2 main.js SEND message: "+message);
    // Включаем крутилку
    $.mobile.pageLoading(false);
	$("#results").html("ждите");
    //Добавляем на всякий случай
    var timeoutId = window.setTimeout(function () {
        $.mobile.pageLoading(true);
    }, 5000);
    $("#error").hide();
    // Загружаем логинимся
    $.ajax({
		type: "POST",
		url: "http://www.almaty.tele2.kz/WebServices/smsService.asmx/SendSms",
        data: JSON.stringify({ msisdn: msisdn, message: message }),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (smsResult) {
			$("#results").html("");
			$.mobile.pageLoading(true);      //$("#statusSendingSms").hide();
			//document.getElementById("results").innerHTML
			//	+= " smsResult.d.ErrorCode "+smsResult.d.ErrorCode;
            if (smsResult.d.ErrorCode === 0) {
                //ALL GOOD
				console.log("main.js send ok: error "+smsResult.d.ResponseMessage);
                $("#error").html(smsResult.d.ResponseMessage);
                $("#error").css('color', 'green');
                $("#error").show();
                $("#get_left").html(smsResult.d.AmountSmsLeft);
            }
            else if (smsResult.d.ErrorCode === 103) {
				console.log("main.js send 103: error "+smsResult.d.ResponseMessage);
                alert(smsResult.d.ResponseMessage);
                $.mobile.changePage('#options');
            } else {
				console.log("main.js send else: error "+smsResult.d.ResponseMessage);
                $("#error").html(smsResult.d.ResponseMessage);
                $("#error").css('color', 'red');
                $("#error").show();
			}
		},
        error: function (req, status, error) {
				$("#results").html("");
				$.mobile.pageLoading(true);
				console.log("main.js ajax: error "+status+" "+error);
				$("#error").html(status+" ajax "+error+" "+req);
                $("#error").show();
            }
        });
		

	console.log("main.js send stop ");
};

var init = function () {
    // Если 1 раз инициализировался не вызываем повторно
    if (init.called) {
        return;
    }
    init.called = true;

    // Инициализация блоков
    $dashboard = $('#dashboard');
    $dashboardList = $('#dashboard-list');
    $dialogImg = $('#dialog').find('img');
    $fullImg = $('#full-image');
    $options = $('#options');
    $optionsName = $('#options-name');
	$optionsPass = $('#options-pass');
	$to_phoneNum = $('#to_phonenum');
	$sms_text	 = $('#sms_text');

    // Клик на любой тумбнайл
    $dashboardList.delegate('a', 'click', function () {
        var $img =  $(this).find('img');

        // Меняем опции диалога
        $dialogImg.attr('src', $img.data('big-src'));
        $fullImg.attr('href', $img.data('full-src'));
    });
	
	// Загружаем или берем значение по умолчанию number
    currentUser = window.localStorage.getItem("tumblr-reader-user");
	console.log(" 1 loaded currentUser = "+currentUser);
	if (currentUser === undefined) {	
		$.mobile.changePage('#options');
	}
    // Закидываем в опции
    $optionsName.val(currentUser);

	
    // Загружаем или берем значение по умолчанию pass
    currentPass = window.localStorage.getItem("tumblr-reader-pass");
	console.log(" 2 loaded currentPass = "+currentPass);
	if (currentPass === undefined) {	
		$.mobile.changePage('#options');
	}
    // Закидываем в опции
    $optionsPass.val(currentPass);

	
	// Загружаем или берем значение по умолчанию to nbmr
    currentTPN = window.localStorage.getItem("tumblr-reader-to_phoneNum");
	console.log(" 3 loaded to_phoneNum = "+currentTPN);
    // Закидываем в 
    $to_phoneNum.val(currentTPN);

	// Загружаем или берем значение по умолчанию number
    currentSTxt = window.localStorage.getItem("tumblr-reader-sms_text");
	console.log(" 4 loaded currentSTxt = "+currentSTxt);
    // Закидываем в опции
    $sms_text.val(currentSTxt);

    // Нажатие кнопки отправить
    $dashboard.find('#btn_send').click(function () {
        var newNbmr = $to_phoneNum.val();
		var newText = $sms_text.val();
		// Сохраняем опции
        window.localStorage.setItem("tumblr-reader-to_phoneNum", newNbmr);
		window.localStorage.setItem("tumblr-reader-sms_text", newText);
        // Загружаем данные    
        console.log(" dashboard saved "+newNbmr+newText);
        send(newNbmr, newText);
    });
	// Нажатие кнопки обновить
	$dashboard.find('#btn_refresh').click(function () {
        console.log(" dashboard refresh ");
        getN();
    });
	
	// Нажатие кнопки  отправить
    $options.find('a').click(function () {
        var newUser = $optionsName.val();
		var newPass = $optionsPass.val();
		
        // Если пользователь поменялся
        if ((currentUser != newUser) || (currentPass != newPass)) {
			console.log(" currentUser != newUser) || (currentPass != newPass) ");
            // Меняем текущего пользователя
            currentUser = newUser;
			currentPass = newPass
            // Сохраняем опции
            window.localStorage.setItem("tumblr-reader-user", currentUser);
			window.localStorage.setItem("tumblr-reader-pass", currentPass);
            // Загружаем данные
            
        }
		load(currentUser, currentPass);
        // Переходим на главную
        //$.mobile.changePage('#dashboard');
    });

    // Первичная загрузка
	$("#loginError").hide();
    $("#loggingIn").hide();
    // check
	load(currentUser, currentPass);
};
    
init.called = false;

// Девайс готов, этого события нет в браузере
document.addEventListener("deviceready", init, true);
    
// Оставляем для отладки в обычном браузере
$(init);

}(jQuery, window));
