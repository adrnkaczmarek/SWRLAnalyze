$(function () {

    var selected_class;

    $(".dropdown-menu li a").click(function(){

        var text = $(this).text();
        $(this).parents(".dropdown").children(".dropdown-toggle").text(text);
        selected_class = $(this).text();
    });

    $("#filter_post").click(function () {
        var button = $(this);
        $.post("filter", {filter: selected_class},
            function(data, status)
            {
                $("#swrl_list").html(data);
                $(".loader").hide();
                button.show();
            });
        button.hide();
        $(".loader").show();
    });

    $("#filter_functional_post").click(function () {
            var button = $(this);
            $.get("filterFunctional",
                function(data, status)
                {
                    $("#swrl_list").html(data);
                    $(".loader").hide();
                    button.show();
                });
            button.hide();
            $(".loader").show();
        });

    $("#filter_symmetric_post").click(function () {
            var button = $(this);
            $.get("filterSymmetric",
                function(data, status)
                {
                    $("#swrl_list").html(data);
                    $(".loader").hide();
                    button.show();
                });
            button.hide();
            $(".loader").show();
        });

    $("#filter_as_subof_post").click(function () {
        var button = $(this);
        $.post("filterAsSubOfProperty", {rule: selected_class},
            function(data, status)
            {
                $("#swrl_list").html(data);
                $(".loader").hide();
                button.show();
            });
        button.hide();
        $(".loader").show();
    });

    $("#filter_as_inverse_post").click(function () {
        var button = $(this);
        $.post("filterAsInverseProperty", {rule: selected_class},
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