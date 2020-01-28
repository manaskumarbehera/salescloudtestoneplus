function showNotification(componentId, messagecount){
	timeout = 5000 + (messagecount * 500);
	$('div#' + componentId).fadeTo('normal', 1.0);
	setTimeout("$('div#" + componentId + "').fadeTo('normal', 0.6)", timeout);
	timeout += 2000; 
	setTimeout("$('div#" + componentId + "').fadeOut('normal')", timeout);
}
