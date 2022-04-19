window.onload = function () {
    // get tab container
    var container = document.getElementById("tabContainer");
    var tabcon = document.getElementById("tabscontent");

    // set current tab
    var navitem = document.getElementById("tabHeader_4");

    //store which tab we are on
    var ident = navitem.id.split("_")[1];

    navitem.parentNode.setAttribute("data-current", ident);

    //set current tab with class of activetabheader
    navitem.setAttribute("class", "tabActiveHeader");

    //hide two tab contents we don't need
    var current = navitem.parentNode.getAttribute("data-current");
    var pages = tabcon.getElementsByTagName("div");


    for (var i = 1; i < pages.length; i++) {
        var trimmed = pages.item(i).className.replace(/^\s+|\s+$/g, '');
//        alert(trimmed);
        if (trimmed == "fieldcontain" || trimmed == "fieldcontain  required") {
            alert("okk");
            pages.item(i).style.display = "block";
        } else {
            pages.item(i).style.display = "none";
        }
    }

    //this adds click event to tabs
    var tabs = container.getElementsByTagName("li");
    document.getElementById("tabpage_1").style.display = "none";
    document.getElementById("tabpage_4").style.display = "block";

    for (var i = 0; i < tabs.length; i++) {
        tabs[i].onclick = displayPage;
    }
}

// on click of one of tabs
function displayPage() {
    var current = this.parentNode.getAttribute("data-current");
    //remove class of activetabheader and hide old contents
    document.getElementById("tabHeader_" + current).removeAttribute("class");
    document.getElementById("tabpage_" + current).style.display = "none";

    var ident = this.id.split("_")[1];
    //add class of activetabheader to new active tab and show contents
    this.setAttribute("class", "tabActiveHeader");
    document.getElementById("tabpage_" + ident).style.display = "block";
    this.parentNode.setAttribute("data-current", ident);
}