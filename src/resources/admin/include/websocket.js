if (typeof WebSocket == 'undefined') {
	alert("{{not-support-wx}}")
}
var ws = new WebSocket("ws://" + document.domain + ":{{ws.port}}");

function toString(json) {
    return JSON.stringify(json);
}

function toJson(str) {
    return JSON.parse(str);
}

function send(string) {
    if(ws.readyState > 1) {
        tips("{{ws-disconnect}}");
        return;
    }
    ws.send(string);
}

function setToken(token) {
    $.cookie('token', token, { expires: 7, path: '{{web.entry}}' });
}

function getToken() {
    return $.cookie('token');;
}

function gotoPage(page) {
    window.location.href="http://" + document.domain+":" + location.port + "{{web.entry}}/" + page;
}

function quit() {
    $.cookie('token', '', { expires: 7, path: '{{web.entry}}' });;
    gotoPage("login");
}

function tips(str) {
//    var p = {};
//    p.position = "top";
//    mdui.snackbar(str,p);
    mdui.alert(str);
}

function bar(str) {
    var p = {};
    p.position = "top";
    mdui.snackbar(str,p);
}

function endsWith(str, target) {
    var start = str.length-target.length;
    var arr = str.substr(start,target.length);
    if(arr == target){
        return true;
    }
    return false;
}