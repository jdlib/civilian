var count = 0;


var appUrl = 'ajax';


var chat = {};

chat.init = function() {
	chat.$loginName		= $('#loginName');
	chat.$loginButton	= $('#loginButton');
	chat.$loginForm		= $('#loginForm');
	chat.$msgLabel		= $('#msgLabel');
	chat.$msgForm		= $('#msgForm');
	chat.$message		= $('#message');
	chat.$display		= $('#display');
	chat.$postButton	= $('#postButton');
	chat.$logoutButton	= $('#logoutButton');
	chat.$cometFrame	= $('#cometFrame');
	
	chat.$loginName.focus();
    
    chat.$loginButton.on('click', chat.login);
    chat.$logoutButton.on('click', chat.logout);
    chat.$postButton.on('click', chat.post);
    
 	function trackEnter(field, button) {
		field.on('keydown', function(event) {
			if (event.keyCode == 13) 
	    		button.click();
		});
	}
	
    trackEnter(chat.$loginName, chat.$loginButton);
    trackEnter(chat.$message, chat.$postButton);
};

chat.login = function() {
	var name = chat.$loginName.val();
	if (!name) {
		chat.$loginName.focus();
		return;
	}

	var query = 'action=login&name=' + encodeURI(name);
	$.ajax({
		type: 	'POST',
		url:	appUrl, 
		data: 	query,
		success: function() {
			chat.$msgLabel.text(name);
            chat.$loginForm.hide();
            chat.$msgForm.show();
	    	chat.$message.focus();
	    	chat.$display.empty();
			chat.$cometFrame.attr('src', appUrl + '?' + count++);
		},
		error: function(data) {
			// error
			chat.$display.css('color', 'red');
			chat.$display.text('Cannot login: ' + data.status + ' error'); 
		}
	});
};

chat.logout = function() {
	chat.$cometFrame.attr('src', '');
	chat.$msgForm.hide();
	chat.$loginForm.show();
	chat.$loginName.focus();
}

chat.post = function() {
	var message = chat.$message.val();
	if (!message)
		return;

	chat.$message.prop('disabled', true);
	chat.$postButton.prop('disabled', true);

	var query = 'action=post&name=' + encodeURI(chat.$loginName.val()) + '&message=' + encodeURI(message);
	$.ajax({
		type:	'POST',
		url:	appUrl, 
		data:	query,
		complete: function() {
			chat.$postButton.prop('disabled', false);
			chat.$message.prop('disabled', false);
			chat.$message.focus();
	    	chat.$message.val('');
	}});
};


chat.update = function(data) {
	var p = document.createElement('p');
	p.innerHTML = data.name + ': ' + data.message;
      
	chat.$display.append(p);
	chat.$display.scrollTop(chat.$display[0].scrollHeight);
};


$(function() {
	chat.init();
});

