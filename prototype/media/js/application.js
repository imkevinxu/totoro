// Some general UI pack related JS

$(function () {
    // Custom selects
    $("select").dropkick();
});

$(document).ready(function() {
    // Todo list
    $(".todo li").click(function() {
        $(this).toggleClass("todo-done");
    });

    // Init tooltips
    $("[data-toggle=tooltip]").tooltip();

    // Init tags input
    $("#tagsinput").tagsInput();

    // Init jQuery UI slider
    $("#slider").slider({
        min: 1,
        max: 5,
        value: 2,
        orientation: "horizontal",
        range: "min",
    });

    // JS input/textarea placeholder
    $("input, textarea").placeholder();

    // Make pagination demo work
    $(".pagination a").click(function() {
        if (!$(this).parent().hasClass("previous") && !$(this).parent().hasClass("next")) {
            $(this).parent().siblings("li").removeClass("active");
            $(this).parent().addClass("active");
        }
    });

    $(".btn-group a").click(function() {
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
    });

    // Disable link click not scroll top
    $("a[href='#']").click(function() {
        return false
    });

    // $(".score").countTo({
    //     "interval": 100,
    //     "startNumber": 0,
    //     "endNumber": $(self).text(),
    // });

    // $('.donut-arrow').trigger('updatePercentage', 0);
    // $('.donut-arrow').eachtrigger('updatePercentage', $(this).data('percentage'));


jQuery.fn.slideLeftOut = function(speed, easing, callback) {
    return this.animate({opacity: 0, left: '-200'}, speed, easing, function() { $(this).removeClass('active'); });
};
jQuery.fn.slideRightIn = function(speed, easing, callback) {
    return this.animate({opacity: 1.0, left: '0px'}, speed, easing, callback).addClass('active');
};

      $('#sidebar a').each(function() {
        $(this).on('click', function(e) {
          e.preventDefault();
          var clicked = $(this).children()[0];
          if (!$(clicked).hasClass('active')) {
            $('#sidebar a').children().removeClass('active');
            $(clicked).addClass('active');
            if ($(clicked).is("#overview")) {
                $("#dashboard.recent").slideLeftOut();
                $("#dashboard.social").slideLeftOut();
                $("#dashboard.economic").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.overview").slideRightIn();
                }, 400);
            } else if ($(clicked).is("#recent")) {
                $("#dashboard.overview").slideLeftOut();
                $("#dashboard.social").slideLeftOut();
                $("#dashboard.economic").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.recent").slideRightIn();
                    google.maps.event.trigger(map, 'resize');
                }, 400);

            } else if ($(clicked).is("#economic")) {
                $("#dashboard.overview").slideLeftOut();
                $("#dashboard.social").slideLeftOut();
                $("#dashboard.recent").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.economic").slideRightIn();
                }, 400);

            } else if ($(clicked).is("#social")) {
                $("#dashboard.overview").slideLeftOut();
                $("#dashboard.recent").slideLeftOut();
                $("#dashboard.economic").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.social").slideRightIn();
                }, 400);

            }
          }
        });
      });

});

