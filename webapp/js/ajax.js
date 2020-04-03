
$("#regist").on("submit", function(e){
	e.preventDefault();
	const params = Object.fromEntries(new URLSearchParams(this.serialize()));
	console.log(params);
});



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
const params = Object.fromEntries(new URLSearchParams(serialize(document.getElementById("regist"))));
console.log( params );



var ajax = function(opt) {
	opt.success = opt.success || function() {};
	opt.fail = opt.fail || function() {};
	var xhr = new XMLHttpRequest();
	xhr.onload = function () {
		opt[ (xhr.status >= 200 && xhr.status < 300) || xhr.status === 304 || xhr.status === 1223  ? "success" : "fail"](xhr);
	};
	xhr.open(opt.type || "GET", opt.url);
	xhr.setRequestHeader("woong","min");
	xhr.send();
}

ajax({
	url : "/index.html"
	, success : function(xhr) {
		console.log("done",xhr.responseURL);
		console.log(xhr);
	}
	, fail : function(e) {
		console.error(e) 
	}
});

ajax({
	url : "/test.json"
	, success : function(xhr) {
		console.log("done",xhr.responseURL);
		var json = JSON.parse(xhr.response);
		console.log(xhr);
		console.log(json);
	}
	, fail : function(e) {
		console.error(e)
	}
});


ajax({
	url : "https://jsonplaceholder.typicode.com/posts"
	, success : function(xhr) {
		console.log("done",xhr.responseURL);
		console.log(xhr);
	}
	, fail : function(e) {
		console.error(e)
	}
});

