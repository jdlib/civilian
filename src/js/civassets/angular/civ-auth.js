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

var failedRequests = [];
var module = angular.module('civAuth', []);

module.config(['$httpProvider', function($httpProvider) {
	
	// configure an HTTP interceptor which reacts on 401 response
	
    var interceptor = ['$rootScope', '$q', function($rootScope, $q) {

		function success(response) {
			return response;
		}
 
		function error(response) {
        	if (response.status === 401) {
          		var deferred = $q.defer();
        		failedRequests.push({
          			config: response.config, 
          			deferred: deferred
        		});                 		
      			$rootScope.$broadcast('civ:login'); 
          		return deferred.promise;
        	} 
        	else
		        return $q.reject(response);
		}

    	return function(promise) {
        	return promise.then(success, error);
		};
    }];
	$httpProvider.responseInterceptors.push(interceptor);
}]);

module.factory('civAuth', ['$http', function($http) {

 	return {
 		resendRequests: function() {
			var requests = failedRequests;
			failedRequests = [];
			for (var i=0; i<requests.length; i++) {
				var request = requests[i];
				$http(request.config).then(function(response) {
					request.deferred.resolve(response);
				}); 	
			}
		}
 	};
}]);
 	

//------------------------
})();