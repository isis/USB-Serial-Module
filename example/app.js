// This is a test harness for your module
// You should do something interesting in this harness
// to test out the module and to provide instructions
// to users on how to use it by example.

// open a single window
var win = Ti.UI.createWindow({
	backgroundColor : 'white'
});
var clearbutton = Ti.UI.createButton({
	title : 'clear',
	height : '7%',
	width : '35%',
	top : '90%',
	left : '65%'
});
var sendbutton = Ti.UI.createButton({
	title : 'send',
	height : '7%',
	width : '35%',
	top : '80%',
	left : '65%'
});
var sendText = Ti.UI.createTextArea({
	title : 'send',
	height : '10%',
	width : '57%',
	top : '80%',
	left : '5%'
});
var conTextField = Ti.UI.createTextArea({
	title : 'send',
	editable : false,
	verticalAlign : 'top',
	enableReturnKey : false,
	height : '70%',
	width : '90%',
	top : '5%',
	left : '5%'
});
win.add(clearbutton);
win.add(conTextField);
win.add(sendText);
win.add(sendbutton);

var USBSERIAL = require('jp.isisredirect.usbserial');
Ti.API.info("module is => " + USBSERIAL);


clearbutton.addEventListener("click", function(e) {
	conTextField.value = "";
});
sendbutton.addEventListener("click", function(e) {
	var buffer = Ti.createBuffer({
		value:sendText.value
	});
	USBSERIAL.sendData(buffer);
});


USBSERIAL.addEventListener(USBSERIAL.RECEIVED, function(e) {
	conTextField.value += '\n' + e.data.length + ":" + e.data +";";
});

USBSERIAL.open();
conTextField.value += '\ndevice' + USBSERIAL.getDeviceName();

win.open();
