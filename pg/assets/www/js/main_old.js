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
 * Отрисоывывает тумбнайлсы
 *
 * @param {Object} posts
 *
 * @returns {String}
 */
var render = function (posts) {
    var view = '',
        // Используем правильный индекс т.к.
        // Берем не все посты
        index = 0;

    // Собираем
    posts.forEach(function(post){
        // Нужны только фотографии
        if (post.type !== 'photo') {
            return;
        }

        view +=
        '<div class="ui-block-' + suffix[index % 3] + '">' +

            // При клике автоматом откроется страница dialog
            '<a href="#dialog">' +
                '<img data-full-src="' + post['photo-url-1280'] + '" data-big-src="' + post['photo-url-250'] + '" src="' + post['photo-url-75'] + '" />' +
            '</a>' +
        '</div>';

        index++;
    });
    
    return view;
};

/**
 * Загружает и отрисовывает контент
 *
 * @param {String} [user='tumblr']
 */
var load = function (user) {
    user = user || 'tumblr';

    // Включаем крутилку
    $.mobile.pageLoading();

    // Добавляем на всякий случай
    var timeoutId = window.setTimeout(function () {
         $.mobile.pageLoading(true);
    }, 3000);
    
    // Загружаем
    $.getScript('http://' + user + '.tumblr.com/api/read/json', function (data, status) {

        // Если все хорошо
        if (status === 'success'){
            // Меняем заголовок
            $dashboard.find('h1').text(tumblr_api_read.tumblelog.title);

            // Рендерим контент
            $('#dashboard-list').html(render(tumblr_api_read.posts));
        } else {
            // Что-то не так
            alert('Error occurred while loading ' + user);
        }

        // Останавливаем крутилку
        $.mobile.pageLoading(true);

        // Убиваем таймер
        window.clearTimeout(timeoutId);
    });
    
};

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

    // Клик на любой тумбнайл
    $dashboardList.delegate('a', 'click', function () {
        var $img =  $(this).find('img');

        // Меняем опции диалога
        $dialogImg.attr('src', $img.data('big-src'));
        $fullImg.attr('href', $img.data('full-src'));
    });

    // Загружаем или берем значение по умолчанию
    currentUser = window.localStorage.getItem("tumblr-reader-user") || 'design';

    // Закидываем в опции
    $optionsName.val(currentUser);

    // Нажатие кнопки ОК в опциях
    $options.find('a').click(function () {
        var newUser = $optionsName.val();

        // Если пользователь поменялся
        if (currentUser !== newUser) {
            // Меняем текущего пользователя
            currentUser = newUser;

            // Сохраняем опции
            window.localStorage.setItem("tumblr-reader-user", currentUser);

            // Загружаем данные с тумблера
            load(currentUser);
        }

        // Переходим на главную
        $.mobile.changePage('#dashboard');
    });

    // Первичная загрузка
    load(currentUser);
};
    
init.called = false;

// Девайс готов, этого события нет в браузере
document.addEventListener("deviceready", init, true);
    
// Оставляем для отладки в обычном браузере
$(init);

}(jQuery, window));
