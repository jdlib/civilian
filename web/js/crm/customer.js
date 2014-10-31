'use strict';


function CustomerController($scope) {
	crm.modules.customer.initScope($scope);
}


function MasterDataController($scope) {
	$scope.lookupCustomer = function() {
		$scope.module.lookup($scope, function(result) {
			$scope.object.parent = result;
			$scope.form.$setDirty();
		});
	}
}

