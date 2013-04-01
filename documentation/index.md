# *USB Serial Module*

## Description

*USB Serial Module* is the wrapper module of [*usb-serial-for-android*](https://code.google.com/p/usb-serial-for-android/).

*USB Serial Module* is for communication with Arduinos and other USB serial hardware on Android.

If you use Titanium with USB Serial module, you can write Android app for USB serial communication using JavaScript, and can communicate with USB device very easily.

Current compatibility list is here :

[CompatibleSerialDevices](https://code.google.com/p/usb-serial-for-android/wiki/CompatibleSerialDevices)

[CompatibleAndroidDevices](https://code.google.com/p/usb-serial-for-android/wiki/CompatibleAndroidDevices)

Note: You need the OTG (USB On-the-Go) cable that connects between Android and other USB hardware.

## Requrement

Android min-sdk: Android 2.3.3 (API Level 10)
Titanium 2.1.0.GA


## Accessing the USB Serial Module

To access this module from JavaScript, you would do the following:

	var usbserial = require("jp.isisredirect.usbserial");

The usbserial variable is a reference to the Module object.	

## Reference

### module constants
#### DATABITS_5
5 data bits.

#### DATABITS_6

6 data bits.

#### DATABITS_7

 7 data bits.

#### DATABITS_8

 8 data bits.

#### FLOWCONTROL_NONE

 No flow control.

#### FLOWCONTROL_RTSCTS_IN

 RTS/CTS input flow control.

#### FLOWCONTROL_RTSCTS_OUT

 RTS/CTS output flow control.

#### FLOWCONTROL_XONXOFF_IN

 XON/XOFF input flow control.

#### FLOWCONTROL_XONXOFF_OUT

 XON/XOFF output flow control.

#### PARITY_NONE

 No parity.

#### PARITY_ODD

 Odd parity.

#### PARITY_EVEN

 Even parity.

#### PARITY_MARK

 Mark parity.

#### PARITY_SPACE

 Space parity.

#### STOPBITS_1

 1 stop bit.

#### STOPBITS_1_5

 1.5 stop bits.

#### STOPBITS_2

 2 stop bits.


### module methods
#### open( int baudRate, int dataBits, int stopBits, int parity) : Boolean

##### Parameters

+ baudRate

(optional)

+ dataBits

(optional)

+ stopBits

(optional)

+ parity

(optional)


##### Returns

+ Boolean

true if succeeded.

#### close() : void

##### Parameters

+ void

##### Returns

+ void

#### getDeviceName() : String 

##### Parameters

+ void

##### Returns

+ String
device name

#### setParameters(int baudRate, int dataBits, int stopBits, int parity) : Boolean

##### Parameters

+ baudRate

+ dataBits

+ stopBits

+ parity

##### Returns

+ Boolean

#### sendData(Titanium.Buffer buffer) : void

##### Parameters

+ buffer : Titanium.Buffer

##### Returns

+ void

### module property
#### isConnected : Boolean
+ Boolean


### module events

#### receive

##### property
+data : Titanium.Buffer

## Usage
For details of setting configurations of your Titanium project that is necessary to use USB serial, see [Step by step](./stepbystep.html).
Here only to show sample code for a simple explanation about the usage of USB Serial module.  
Sample code is below:

	var USBSERIAL = require('jp.isisredirect.usbserial');
	Ti.API.info("module is => " + USBSERIAL);
	
	USBSERIAL.addEventListener(USBSERIAL.RECEIVED, function(e) {
		conTextField.value += '\n' + e.data.length + ":" + e.data +";";
	});
	USBSERIAL.open();
	var buffer = Ti.createBuffer({
		value:sendText.value
	});
	USBSERIAL.sendData(buffer);

## Author

Kastumi ISHIDA (isis re-direct) in k.i.office.

isis.redirect4@gmail.com

## License
Copyright 2013 Katsumi ISHIDA. All rights reserved.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 USA.

## License of third party
[*usb-serial-for-android*](https://code.google.com/p/usb-serial-for-android/)

usb-serial-for-android is written and maintained by mike wakerly.

This library is licensed under LGPL Version 2.1. Please see LICENSE.txt for the complete license.

Copyright 2011-2012, Google Inc. All Rights Reserved.

Portions of this library are based on libftdi (http://www.intra2net.com/en/developer/libftdi). Please see FtdiSerialDriver.java for more information.

