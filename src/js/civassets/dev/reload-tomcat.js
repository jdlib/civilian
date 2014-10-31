function reload(containerPath, user, pwd)
{
	containerPath = containerPath || '/';
	user = user || 'manager';
	pwd = pwd || 'manager'; 
	
	var url = 'http://' + window.location.hostname;
	if (window.location.port)
		url += ':' + window.location.port;
	url += '/manager/text/reload?path=' + containerPath + '&timestamp=' + new Date().getTime();
	$.ajax({ url: url, username: user, password: pwd, async: false });
	window.location.reload();
}
