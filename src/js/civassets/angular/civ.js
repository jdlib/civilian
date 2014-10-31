/*
 * Copyright (C) 2014 Civilian Framework.
 *
 * Licensed under the Civilian License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.civilian-framework.org/license.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
'use strict';


//-----------------------------------------------
// the global civilian object
//-----------------------------------------------

var civilian = civilian || {};
civilian.service = {};



//-----------------------------------------------
// messages
//-----------------------------------------------


civilian.message = function(type, text, title)  {
	if (civilian.message[type])
		civilian.message[type](text, title, options);
	else
		alert((title ? title + ": " : "") + text);
};


//-----------------------------------------------
// toast
//-----------------------------------------------


civilian.toast = function(type, text, title)  {
	if (civilian.toast[type])
		civilian.toast[type](text, title, options);
	else
		alert((title ? title + ": " : "") + text);
};

/* 
civilian tries to use toastr to display toast messsage
usage: include the toastr.js script in your web page
*/
if (toastr) {
	civilian.toast.error 	= toastr.error;
	civilian.toast.info  	= toastr.info;
	civilian.toast.warning	= toastr.warning;
	civilian.toast.success	= toastr.success;
}

//-----------------------------------------------
// path
//-----------------------------------------------

civilian.Path = function(value, skipNorm) {
	this.value_ = skipNorm ? value : civilian.Path.norm(value);
}

civilian.Path.norm = function(path) {
	if (path)
		path = path.toString();
	if (!path || path === '' || path === '/')
		return '';
	if (!civilian.util.startsWith(path, '/'))
		path = '/' + path;
	path = civilian.util.stringCutEnd(path, '/');
	if (path === '/')
		path = '';
	return path;
}

civilian.Path.createBasePath = function(path) {
	if (!path)
		path = window.location.pathname;
	// strip of matrix-param (jsessionid)
	path = path.replace(/;[^\/]*$/g, '');
	// strip of extension
	path = path.replace(/\.[^\/]*$/g, '');
	// strip of trailing index
	path = path.replace(/\/index$/g, '');
	return new civilian.Path(path);
}

civilian.Path.prototype.equals = function(other) {
	return this.value_ == other.value_;
}

civilian.Path.prototype.toString = function() {
	return this.value_;
}

civilian.Path.prototype.append = function(path) {
	var p = civilian.util.stringCutEnd(this.value_, '/');
	return new civilian.Path(p + civilian.Path.norm(path), true);	
}

civilian.Path.prototype.extension = function(extension) {
	if (!civilian.util.startsWith(extension, '.'))
		extension = '.' + extension;
	return new civilian.Path(this.value_ + extension, true);	
}

civilian.Path.prototype.parent = function() {
	var path = this.value_;
	if (path == '')
		return this;
	var p = path.lastIndexOf('/');
	return new civilian.Path(p == -1 ? '' : path.substring(0, p));
}

civilian.Path.prototype.sibling = function(path) {
	return this.parent().append(path); 
}

civilian.Path.prototype.stripExt = function() {
	var p = this.value_.indexOf('.');
	return p == -1 ? this : new civilian.Path(this.value_.substring(0, p));
}

civilian.Path.prototype.endsWith = function(s) {
	return civilian.util.endsWith(this.value_, s.toString());
}


civilian.Path.prototype.startsWith = function(s) {
	return civilian.util.startsWith(this.value_, s.toString());
}


//-----------------------------------------------
// util
//-----------------------------------------------

civilian.util = {
	stringCutEnd: function(s, end) {
		if (civilian.util.endsWith(s, end))
			s = s.substring(0, s.length - end.length); 
	    return s;
	},  		

	endsWith: function(s, end) {
	    var len = s.length - end.length;
	    return len >= 0 && s.indexOf(end, len) === len;
	},  		

	startsWith: function(s, start) {
		return s.lastIndexOf(start, 0) === 0;
	}
}


//-----------------------------------------------
// server response handlers
//-----------------------------------------------


civilian.responseHandler = function(name, data, scope) {
	if (civilian.responseHandler[name])
		civilian.responseHandler[name](data, scope);
}


// "messages" == see org.civ.resource.ActionResult#messages_
civilian.responseHandler.messages = function(messages) {
	angular.forEach(messages, function(msg) { 
		civilian.message(angular.lowercase(msg.type), msg.text, msg.title); 
	});
}


// "toasts" == see org.civ.resource.ActionResult#toasts_
civilian.responseHandler.toasts = function(toasts) {
	angular.forEach(toasts, function(msg) { 
		civilian.message(angular.lowercase(msg.type), msg.text, msg.title); 
	});
}


// "scopeVars" == see org.civ.resource.ActionResult#scopeVars_
civilian.responseHandler.scopeVars = function(scopeVars, scope) {
	for (var name in scopeVars) {
		var parts = name.split('.');
		var obj = scope;
		for (var i=0; i<parts.length-1; i++) {
			obj = obj[parts[i]];
			if (!obj)
				break;
		}
		if (obj)
			obj[parts[parts.length - 1]] = scopeVars[name]; 
	}
}


// "logs" == see org.civ.resource.ActionResult#logs_
civilian.responseHandler.logs = function(messages) {
	var log = civilian.service.$log;
	angular.forEach(messages, function(msg) { 
		log.log(msg); 
	});
}


// "alerts" == see org.civ.resource.ActionResult#alerts_
civilian.responseHandler.alerts = function(messages) {
	angular.forEach(messages, function(msg) { 
		alert(msg); 
	});
}


// "error" handler for http errors
civilian.responseHandler.error = function(result) {
	var message = "An error occurred";
	if (result) {
		if (result.status)
			message += ': ' + result.status;
		if (civilian.develop && result.data)
			message += '\n' + result.data;
	}
	civilian.message('error', message, 'Error');
}


//-----------------------------------------------
// server request
//-----------------------------------------------


civilian.request = function(scope, url, config) {
	return new CivRequest(scope, url, config);
}


function CivRequest(scope, url, config) {
	if (!scope)
		throw new Error('no scope');
	if (!url)
		throw new Error('no url');
	this.scope_  = scope;
	this.config_ = angular.extend({cache: false}, config);
	this.config_.url = url.toString();
}

CivRequest.prototype.method = function(name) {
	this.config_.method = name;
	return this;
}

CivRequest.prototype.config = function(name, value) {
	this.config_[name] = value;
	return this;
}

CivRequest.prototype.cache = function(flag) {
	return this.config("cache", flag);
}

CivRequest.prototype.header = function(name, value) {
	if (!this.config_.headers)
		this.config_.headers = {};
	this.config_.headers[name] = value;
	return this;
}

CivRequest.prototype.param = function(name, value) {
	if (!this.config_.params)
		this.config_.params = {};
	this.config_.params[name] = value;
	return this;
}

CivRequest.prototype.data = function(value) {
	this.config_.data = value;
	return this;
}

// requires jquery
CivRequest.prototype.formData = function(value) {
	this.header("Content-Type", "application/x-www-form-urlencoded;char-set=UTF-8");
	return this.data($.param(value));
}

CivRequest.prototype.get = function(name, value) {
	return this.method("GET").send();
}

CivRequest.prototype.post = function(name, value) {
	return this.method("POST").send();
}

CivRequest.prototype.put = function(name, value) {
	return this.method("PUT").send();
}

CivRequest.prototype.send = function(name, value) {
	var config = this.config_;
	var scope  = this.scope_;
	
	if (!config.headers)
		config.headers = {};
	config.headers['X-Civilian'] = '';
	
	if (!config.method)
		config.method = config.data ? "POST" : "GET";
			
	var fsuccess = function(result) {
		var status = result.status || 503;
		var data;
		var contentType = result.headers ? result.headers("Content-Type") : null; 
		if (contentType && (contentType.indexOf("html") != -1))
		{
			// response is a html snippet not a json answer. normalize for further processing 
			data = { html: result.data, success: true };
		}
		else if (angular.isObject(result.data))
			data = result.data;
		else
			data = {};
		
		for (var name in data)
			civilian.responseHandler(name, data[name], scope);
		return result;
	}
	var ferror = function(result) {
		civilian.responseHandler.error(result);
		return result;
	}
	
	return civilian.service.$http(config).then(fsuccess, ferror);
};



//-----------------------------------------------
// init
//-----------------------------------------------

(function() {

	var civModule = angular.module('civilian', []);
	
	civModule.config(['$httpProvider', function($httpProvider) {
   		$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
	}]);
	
	civModule.factory('civilian', function() {
		return civilian;
	});

	civModule.run(['$http', '$log', function ($http, $log) {
		civilian.service.$http = $http;
		civilian.service.$log  = $log;
	}]);
	
})();

