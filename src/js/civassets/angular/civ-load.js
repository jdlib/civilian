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
 
(function() {'use strict';
//------------------------


var module = angular.module('civLoad', []);
module.provider('civLoad',  function CivLoadProvider() {

	this.defaults = { 
		templateHttpConfig: {
			cache: true,
			headers: { Accept: 'text/html' }
		} 
	};

	this.$get = ['$q', '$templateCache', '$http', function($q, $templateCache, $http) {
		return new CivLoad($q, $templateCache, $http, this.defaults);
	}];
}); 


function CivLoad($q, $templateCache, $http, defaults) {

	var service = this;
	

 	service.loadScripts = function($scope, scriptUrls) {
		var deferred = $q.defer();
		if (!scriptUrls)
			deferred.resolve(scriptUrls);
		else {
			// $script is defined by script.js
			$script(scriptUrls, function() { 
				$scope.$apply(function() { deferred.resolve(scriptUrls); });
			});
		}
		return deferred.promise;
	};
	
 	
 	service.loadTemplate = function($scope, templateUrl, config) {
 		if (!templateUrl)
 			return $q.when("");
 		else {
			templateUrl = templateUrl.toString();
			config = angular.extend(config || {}, defaults.templateHttpConfig, { method: "GET" });
			if (config.cache === true)
				config.cache = $templateCache;
			config.url = templateUrl;
			return $http(config).then(function(result) { 
				return result.data; 
			});
		}
	};
	

 	service.load = function($scope, src) {
		var deferred = $q.defer();
		service.loadScripts($scope, src.scriptUrls).then(function() {
			service.loadTemplate($scope, src.templateUrl).then(function(template) { 
				deferred.resolve(template);
			})
		});
		return deferred.promise;
	};
	
	
 	return service;
};


module.directive('civInclude', [ '$compile', 'civLoad',
	function ($compile, civLoad) {
		function runInclude(scope, element, src) {
			civLoad.load(scope, src).then(function(template) {
				if (template) {
					var compiled = $compile(template)(scope);
					element.replaceWith(compiled);
				}
				else
					element.replaceWith("");
			});
		}
		
		return {
			restrict: 'EA',
			link: function (scope, element, attr) {
				var src = scope.$eval(attr.src) || {};
				if (!src.templateUrl)
					src.templateUrl = attr.templateUrl;
				if (!src.scriptUrls) {
					src.scriptUrls = attr.scriptUrls;
					if (src.scriptUrls)
						src.scriptUrls = src.scriptUrls.split(';');
				}
				if (attr.waitFor) {
					var unregisterWatch = scope.$watch(attr.waitFor, function(include) {
			        	if (include) {
            				runInclude(scope, element, src);
            				unregisterWatch();
            			}
          			});
				}
				else
					runInclude(scope, element, src);
			}
		};
}]);		



//------------------------
})();
