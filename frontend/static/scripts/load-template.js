const TEMPLATES_PATH = "../pages/commons/"

function loadHead() {
    $.get(TEMPLATES_PATH + "head.html", heads => $("head").append(heads));
}

function loadNavbar() {
    $.get(TEMPLATES_PATH + "navbar.html", nav => $("body").prepend(nav));
}

function loadFooter() {
    $.get(TEMPLATES_PATH + "footer.html", footer => $("body").append(footer));
}

$(document).ready(() => {
    loadHead();
    loadNavbar();
    loadFooter();
});