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

// TODO: write your module tests here
var USBSERIAL = require('jp.isisredirect.usbserial');
Ti.API.info("module is => " + USBSERIAL);
/*
self.addEventListener('itemSelected', function(e) {
	lbl.text = e.name + ': ' + e.address + ' paired:' + e.paired;
	if (USBSERIAL.bond(e.address, "1234")) {
		USBSERIAL.connect(e.address, true);
	} else {
		var dialog = Ti.UI.createAlertDialog({
			cancel : 1,
			buttonNames : ['OK'],
			message : 'pairing result',
			title : 'Not paired'
		});
		dialog.show();
	}
});
*/

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

win.open();
