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
    // $("a[href='#']").click(function() {
    //     return false
    // });


    $(".score").each(function(){
        $(this).countTo({
        "interval": 30,
        "startNumber": 0,
        "endNumber": $(this).text(),
        });
    });

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
                $("#dashboard.replay").slideLeftOut();
                $("#dashboard.social").slideLeftOut();
                $("#dashboard.economic").slideLeftOut();
                $("#dashboard.rewards").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.overview").slideRightIn();
                }, 400);
            } else if ($(clicked).is("#recent")) {
                $("#dashboard.overview").slideLeftOut();
                $("#dashboard.replay").slideLeftOut();
                $("#dashboard.social").slideLeftOut();
                $("#dashboard.economic").slideLeftOut();
                $("#dashboard.rewards").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.recent").slideRightIn();
                    google.maps.event.trigger(map, 'resize');
                }, 400);

            } else if ($(clicked).is("#economic")) {
                $("#dashboard.overview").slideLeftOut();
                $("#dashboard.replay").slideLeftOut();
                $("#dashboard.social").slideLeftOut();
                $("#dashboard.recent").slideLeftOut();
                $("#dashboard.rewards").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.economic").slideRightIn();
                }, 400);

            } else if ($(clicked).is("#social")) {
                $("#dashboard.overview").slideLeftOut();
                $("#dashboard.replay").slideLeftOut();
                $("#dashboard.recent").slideLeftOut();
                $("#dashboard.economic").slideLeftOut();
                $("#dashboard.rewards").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.social").slideRightIn();
                }, 400);

            } else if ($(clicked).is("#replay")) {
                $("#dashboard.overview").slideLeftOut();
                $("#dashboard.social").slideLeftOut();
                $("#dashboard.recent").slideLeftOut();
                $("#dashboard.economic").slideLeftOut();
                $("#dashboard.rewards").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.replay").slideRightIn();
                    var c = 0;
                     var interval = setInterval(function() {
                          $('#ecoscorereplay').html(Math.round(ecos[c]));
                          $('#totalmilesreplay').html(Math.round(totals[c]));
                          $('#secondsreplay').html(Math.round(c));
                          $('#fuelreplay').html(Math.round(fuels[c]*1000)/100);
                          c++;
                          if(c >= ecos.length) clearInterval(interval);
                   }, 50);
                }, 400);

            } else if ($(clicked).is("#rewards")) {
                $("#dashboard.overview").slideLeftOut();
                $("#dashboard.social").slideLeftOut();
                $("#dashboard.recent").slideLeftOut();
                $("#dashboard.economic").slideLeftOut();
                $("#dashboard.replay").slideLeftOut();
                window.setTimeout(function() {
                    $("#dashboard.rewards").slideRightIn();
                }, 400);

            }
          }
        });
      });


    var ctx = $("#myChart").get(0).getContext("2d");
    //This will get the first returned node in the jQuery collection.

    var data = {
        labels : ["January","February","March","April","May","June","July"],
        datasets : [
            {
                fillColor : "rgba(29, 221, 184, 0.5)",
                strokeColor : "#1ABC9C",
                pointColor : "#1ABC9C",
                pointStrokeColor : "#fff",
                data : [70,56,60,66,70,90,88]
            }
        ]
    }
    var myNewChart = new Chart(ctx).Line(data);



    var catx = $("#analyticsgraph").get(0).getContext("2d");
    //This will get the first returned node in the jQuery collection.

    var datanew = {
        labels : ["January","February","March","April","May","June","July"],
        datasets : [
            {
                fillColor : "rgba(29, 221, 184, 0.5)",
                strokeColor : "#1ABC9C",
                pointColor : "#1ABC9C",
                pointStrokeColor : "#fff",
                data : [70,56,60,66,70,90,88]
            }
        ]
    }
    var anal = new Chart(catx).Bar(datanew);
});

