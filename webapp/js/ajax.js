
var params;
$("#regist").on("submit", function(e){
	e.preventDefault();
	params = Object.fromEntries(new URLSearchParams(serialize(this)));
	console.log(params);
	ajax({
		type : "POST"
		, url : "/user/create"
		, data : params
		, contentsType : "application/x-www-form-urlencoded"
		, success : function(xhr) {
			console.log("done", xhr.responseURL);
			console.log(xhr);
		}
	});
});

$('#regist input[name="id"]').val("4ox");
$('#regist input[name="pw"]').val("pw");
$('#regist input[name="name"]').val("김웅민");
$('#regist input[name="email"]').val("mail@4ox.kr");

$(function(){
	$("#regist").trigger('submit');
})

function serialize(form) {
	var serialized = [];
	for (var i = 0; i < form.elements.length; i++) {
		var field = form.elements[i];
		if (!field.name || field.disabled || field.type === 'file' || field.type === 'reset' || field.type === 'submit' || field.type === 'button') continue;
		if (field.type === 'select-multiple') {
			for (var n = 0; n < field.options.length; n++) {
				if (!field.options[n].selected) continue;
				serialized.push(encodeURIComponent(field.name) + "=" + encodeURIComponent(field.options[n].value));
			}
		}
		else if ((field.type !== 'checkbox' && field.type !== 'radio') || field.checked) {
			serialized.push(encodeURIComponent(field.name) + "=" + encodeURIComponent(field.value));
		}
	}
	return serialized.join('&');
};

var ajax = function(opt) {
	opt.success = opt.success || function() {};
	opt.fail = opt.fail || function() {};
	var xhr = new XMLHttpRequest();
	xhr.onload = function () {
		opt[ (xhr.status >= 200 && xhr.status < 300) || xhr.status === 304 || xhr.status === 1223  ? "success" : "fail"](xhr);
	};
	opt.type = opt.type || "GET";
	if( opt.type === "GET") {
		if( opt.data) {
			let u = new URLSearchParams(opt.data).toString();
			opt.url += "?" + u;
		}
		xhr.open("GET", opt.url);
		if( opt.headers && opt.headers["Content-type"] ) {
			xhr.setRequestHeader('Content-type', pt.headers["Content-type"] );
			xhr.send(JSON.stringify(data));
		}
		xhr.send();
	}
	else {
		xhr.open("POST", opt.url);
		var formData = new FormData();
		if( opt.data ) {
			Object.entries(opt.data).forEach(function(key,val) { 
				formData.append(key,val); 
			});
			xhr.send(formData);
		}
		else {
			xhr.send();	
		}
	}
}

//ajax({
//	url : "/index.html"
//	, success : function(xhr) {
//		console.log("done",xhr.responseURL);
//		console.log(xhr);
//	}
//	, fail : function(e) {
//		console.error(e) 
//	}
//});
//
//ajax({
//	url : "/test.json"
//	, success : function(xhr) {
//		console.log("done",xhr.responseURL);
//		var json = JSON.parse(xhr.response);
//		console.log(xhr);
//		console.log(json);
//	}
//	, fail : function(e) {
//		console.error(e)
//	}
//});
//
//
//ajax({
//	url : "https://jsonplaceholder.typicode.com/posts"
//	, success : function(xhr) {
//		console.log("done",xhr.responseURL);
//		console.log(xhr);
//	}
//	, fail : function(e) {
//		console.error(e)
//	}
//});

