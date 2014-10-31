'use strict';


var crm = crm || {};
crm.services = {};
crm.modules = {
	customer:    new Module('customers', true),
	opportunity: new Module('opportunities', false),
	contact:     new Module('contacts', false),
	user:        new Module('users', false),
}


var crmModule = angular.module('crm', [ 'ui.bootstrap', 'ui.date', 'civilian', 'civAuth', 'civLoad' ]);
crmModule.config(['civLoadProvider', function(civLoadProvider) {
	civLoadProvider.defaults.templateHttpConfig = { 
		cache: !civilian.develop,
		headers: { Accept: 'text/x-template' } 
	};
}]);

crmModule.run(['$modal', 'civLoad', function($modal, civLoad) {
	crm.services.$modal = $modal;
	crm.services.civLoad = civLoad;
}]);


//--- directives for filtering input in textfields with int or double values

var INTEGER_REGEXP = /^\-?\d*$/;
crmModule.directive (
	"crmInteger", 
	function() {
		return {
			require: 'ngModel',
			link: function(scope, elm, attrs, ctrl) {
				ctrl.$parsers.unshift(function(viewValue) {
					var valid = INTEGER_REGEXP.test(viewValue); 
					ctrl.$setValidity("integer", valid);
					// we are not converting since the input field is defined as type="number"
					return valid ? viewValue : undefined;
				});
			}
		};
	}
);


var DOUBLE_REGEXP = /^\-?\d+((\.|\,)\d+)?$/;
crmModule.directive (
	"crmDouble", 
	function() {
		return {
			require: 'ngModel',
			link: function(scope, elm, attrs, ctrl) {
				ctrl.$parsers.unshift(function(viewValue) {
					var valid = DOUBLE_REGEXP.test(viewValue); 
					ctrl.$setValidity("double", valid);
					// we are not converting since the input field is defined as type="text"
					// we cannot use type="number" since this would reject ','
					return valid ? parseFloat(viewValue.replace(',', '.')) : undefined;
				});
			}
		};
	}
);



//----------------------------------
// CrmController: topmost controller

function CrmController($scope, $modal, $q) {
	
	// listen on civ login requests and open the login dialog
	$scope.$on('civ:login', function() {
		var dialog = $modal.open({
      		templateUrl: 'login.template',
      		controller: LoginController,
      		backdrop: 'static',
      		keyboard: false
		});
	});
}


// fallback controller
function EmptyController() {
}

//----------------------------------
// LoginController: for the ajax login popup

function LoginController($scope, $modalInstance, civAuth) {
	$scope.login = function(name, password, language) {
		var data    = { name: name, password: password, language: language };
		var path 	= civilian.appPath.append('login');
		civilian.request($scope, path).formData(data).post().then(function(result) {
			$modalInstance.close();
			civAuth.resendRequests(); 
		});
	};
}


//----------------------------------
// MenuController 
// handles the main navigation bar and the tab bar
// which is only used when the landing page is loaded
//
function MenuController($scope) {
	$scope.tabs = [];
	

	$scope.openModule = function(label, templateUrl, scriptUrls) {
		var tab = { label: label, templateUrl: templateUrl, scriptUrls: scriptUrls, active: true };
		$scope.tabs.push(tab);
	}

	$scope.closeModule = function(tab) {
		var pos = $scope.tabs.indexOf(tab);
		if (pos >= 0)
			$scope.tabs.splice(pos, 1);
		if (tab.scope)
			tab.scope.$destroy();
	}
}


//----------------------------------
// SearchResult 
// .cols: column definitions
// .rows: row data
// .selected: index of selected row

function SearchResult() {
	this.clear();
}

SearchResult.prototype.clear = function() {
	this.cols = [];
	this.rows = [];
	this.selected = -1;
}

SearchResult.prototype.size = function() {
	return this.rows.length;
}

SearchResult.prototype.empty = function() {
	return this.rows.length == 0;
}

SearchResult.prototype.isFirstSelected = function() {
	return this.selected <= 0;
}

SearchResult.prototype.isLastSelected = function() {
	return this.selected >= this.rows.length - 1;
}

SearchResult.prototype.getSelected = function() {
	if (this.selected >= 0) {
		var row = this.rows[this.selected];
		return { id: row.id, name: row.data[0] };
	}
	else
		return null;
}

SearchResult.prototype.init = function(result) {
	this.cols = result.cols;
	this.rows = result.rows;
	this.selected = this.empty() ? -1 : 0;
}

SearchResult.prototype.setSelected = function(index) {
	if ((index >= 0) && (index < this.rows.length)) {
		this.selected = index;
		return true;
	}
	else
		return false;
}


//----------------------------------
// SearchController
// handles a search page. it has these scope variables 
// - a list of available searchFilters (defined differently for each crm object) 
// - a list of searchParams, entered by the user
// - and a SearchResult object, which holds the current search result

function SearchController($scope) {

	// initially the search params consist of an empty array 
	$scope.searchParams = [];
	
	// if the search result object is already defined by a parent scope
	// (e.g. ModuleController) we share it, else we create a new one 
	$scope.searchResult = $scope.searchResult || new SearchResult();
		
	// handle a click on the '+' button	to add filter	
	$scope.addSearchParam = function() {
		var newFilter = { filter: $scope.searchFilters[0].value }; 		
		$scope.searchParams.push(newFilter);
	}

	// handle a click on the '-' button	to remove a filter	
	$scope.removeSearchParam = function(index) {
		$scope.searchParams.splice(index, 1);
	}

	// handle a click on the 'search' button		
	$scope.search = function() {
		civilian.request($scope, $scope.module.searchPath).
			data($scope.searchParams).
			post().
			then(function(result) { 
				$scope.searchResult.init(result.data); 
		});
	}

	// handle a click on the 'reset' button		
	$scope.reset = function() {
		$scope.searchResult.clear();
		$scope.searchParams = [];
		$scope.addSearchParam();
	}

	// load search filters: add extension 'filter' to invoke the correct server-side method
	// we also cache the response since the filter definition will not change
	var filterPath = $scope.module.searchPath.append('filter'); 
	civilian.request($scope, $scope.module.searchFilterPath).
		cache(true).
		get().
		then(function() {
			// the response has initialized the searchFilter variable
			// now that we have the filter definitions, add a first parameter 
			if ($scope.searchFilters)  
				$scope.addSearchParam();
		});
}

//----------------------------------
// ModuleController 
// handles a crm module
//
// A module consists of a search page (using SearchController),
// and a detail view for accessing a single object (having a object dependent controller).
//
// You start by a search and then navigate to a single object contained in the search result.
// A toolbar on the left allows you to switch between search panel and detail view.
// 
// The detail view can have multiple sub-panels, where each sub-panel presents different data of the object. 
// The sub-panels are handled by a module specific controller. 
// The server URLs of a module are as follows:
// /<module>        	     module entry point
// /<module>/search          module search panel
// /<module>/navigation      list of subpanels in the module
// /<module>/:id             entry point for receiving object data
// /<module>/:id/<subpanel>  entry point for a object subpanel
// e.g.
// /customers
// /customers/search
// /customers/navigation
// /customers/123
// /customers/123/masterdata

function ModuleController($scope) {
	// the search result: it is used and filled by the SearchController
	$scope.searchResult = new SearchResult();
	
	// this flag triggers if we show the search panel or the detail view
	$scope.showSearch = true;
	
	// called by the SearchController when a select action on a search result row occurs 
	// (via search toolbar button "arrow-right" or double-click): we switch to the object view
	$scope.openResult = function(index) {
		if (!arguments.length)
			index = $scope.searchResult.selected;
		if ($scope.searchResult.setSelected(index)) {
			$scope.showSearch = false;
			// showObject(id) must be provided by object dependent controller, e.g. the
			// MultiPanelController
			$scope.showObject($scope.searchResult.rows[index].id);
		}
	}
	
	// invoked by the toolbar button "->": switch to next result entity
	$scope.openNextResult = function() {
		$scope.openResult($scope.searchResult.selected + 1);
	}
	
	// invoked by the toolbar button "<-": switch to prev result entity
	$scope.openPrevResult = function() {
		$scope.openResult($scope.searchResult.selected - 1);
	}
}


//----------------------------------
// MultiPanelController
// a controller for the detail view of a crm object
// which consists of multiple panels to display the object
// data, and a navigation list to the left, to switch between those panels 
//
function MultiPanelController($scope, navItemPath) {
	// the navigation items used to fill the navigation list
	$scope.navItems = [];
	
	// the sub-panels which are used to view a data object
	$scope.panels = [];
	
	$scope.objectChanged = function() {
		// make sure that a detail panel is active
		for (var i=0; i<$scope.panels.length; i++) {
			if ($scope.panels[i].nav.active) 
			return;
		}
		if ($scope.navItems.length)
			$scope.openPanel($scope.navItems[0]);		
	}
	
	$scope.openPanel = function(nav) {
		var panel = null;
		for (var i=0; i<$scope.panels.length; i++) {
			if ($scope.panels[i].nav == nav) {
				panel = $scope.panels[i];
				break;
			}
		}
		if (!panel) {
			// copy the nav-item which contains info needed for lazy-loading
			// of the panel content
			panel = { nav: nav };
			$scope.panels.push(panel);
		}
	    angular.forEach($scope.panels, function(panel) { panel.nav.active = false; });
	    panel.nav.active = true;
	}	

	// load navigation items for detail panels
	civilian.request($scope, navItemPath).cache(true).get().then(function(result) {
		// we need a copy of the navItems since we change the active-flag
		// of the navItems
		$scope.navItems = angular.copy(result.data);
		if ($scope.objectId)
			$scope.objectChanged();						
	});
}


function LookupController($scope, $modalInstance) {
	$scope.searchResult = new SearchResult();
	
	$scope.openResult = function() {
		$scope.onOk();
	}
	
	$scope.onOk = function() {
		var result = $scope.searchResult.getSelected();
		if (result)
			$modalInstance.close(result);
		else
			$scope.onCancel();
	}

	$scope.onCancel = function() {
		$modalInstance.dismiss();
	}
}


//----------------------------------
// Modules


function Module(segment, hasMultiPanel) {
	this.segment     		= segment;
	this.path 		 		= civilian.appPath.append(segment);	
	this.lookupPath	 		= this.path.append("lookup");	
	this.searchPath	 		= this.path.append("search");	
	this.searchFilterPath	= this.searchPath.append("filter");	
	this.navigationPath		= this.path.append("navigation");	
	this.pathPattern 		= new RegExp("\\/" + segment + "\\/(\\d+)?(\\/.+$)?");
	this.hasMultiPanel		= hasMultiPanel;
}

// calls /<module>/:objectId
Module.prototype.initScope = function($scope) {
	$scope.module = this;

	$scope.showObject = function(objectId) {
		if (objectId !== $scope.objectId) {
			$scope.module.loadObject($scope, objectId).then(function() {
				$scope.objectId = objectId;
				if ($scope.objectChanged)
					$scope.objectChanged();						
			});
		}
	};

	var inModulePanel = this.path.startsWith(civilian.basePath); 
	if (inModulePanel)
		ModuleController($scope);
		
	var context  = this.pathPattern.exec(civilian.basePath.toString());
	var objectId = context && context[1] ? parseInt(context[1]) : null;
	
	if (inModulePanel || objectId) {	
		if (this.hasMultiPanel)
			MultiPanelController($scope, this.navigationPath, objectId);
	}
	
	if (!inModulePanel && objectId)
		$scope.showObject(objectId);
}


// returns the path /<module>/:objectId
Module.prototype.getObjectPath = function(objectId) {
	return this.path.append(objectId);
}


// calls /<module>/:objectId
Module.prototype.loadObject = function($scope, objectId) {
	var path = this.getObjectPath(objectId);
	return civilian.request($scope, path).send();
}

Module.prototype.lookup = function($scope, callback) {
	var module = this;
	crm.services.civLoad.loadTemplate($scope, $scope.module.lookupPath).then(
		function(template) {
			var modal = crm.services.$modal.open({
				template: template,
				backdrop: "static",
				scope: $scope,
				windowClass: "lookup",
				controller: LookupController
			});
			if (callback)
				modal.result.then(callback);
		});
}


