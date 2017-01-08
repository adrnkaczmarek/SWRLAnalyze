$(function () {

    var selected_class;

    $(".dropdown-menu li a").click(function(){

        $(this).parents(".dropdown").children(".dropdown-toggle").text($(this).text());
        selected_class = $(this).text();
    });

    $("#filter_post").click(function () {
        var button = $(this);
        $.post("filter", selected_class,
            function(data, status)
            {
                $("#swrl_list").html(data);
                $(".loader").hide();
                button.show();
            });
        button.hide();
        $(".loader").show();
    });
});