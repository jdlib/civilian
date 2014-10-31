prolog /**
prolog  * Template for IndexController.
prolog  */
template mixin html
{{
	<!DOCTYPE html>
	<html>
		<head>
			<title>Hello World</title>
			@html.linkCss("style.css");
		</head>
		<body>
		<h1>Hello World</h1>
		<a href="<%html.url(!{resourcesClass}.root.users)%>">Users</a><br>
		<a href="<%html.url(!{resourcesClass}.root.users.$userId).setPathParam(new Integer(123))%>">User 123</a><br> 
		</body>
	</html>
}}