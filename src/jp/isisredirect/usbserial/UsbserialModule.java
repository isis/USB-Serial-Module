/*
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

 Project home page: https://github.com/isis/USB-Serial-Module

 */
package jp.isisredirect.usbserial;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;

import android.hardware.usb.UsbManager;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import ti.modules.titanium.BufferProxy;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;


@Kroll.module(name="Usbserial", id="jp.isisredirect.usbserial")
public class UsbserialModule extends KrollModule
{
	// Standard Debugging variables
	private static final String LCAT = "UsbserialModule";
	private static final boolean DBG = true;//TiConfig.LOGD;

	@Kroll.constant
	public static final String CONNECTED = "connected";
	@Kroll.constant
	public static final String DISCONNECTED = "disconnected";
	@Kroll.constant
	public static final String RECEIVED = "received";
	@Kroll.constant
	public static final String DATA = "data";
	
	
    /** 5 data bits. */
	@Kroll.constant
    public static final int DATABITS_5 = 5;

    /** 6 data bits. */
	@Kroll.constant
   public static final int DATABITS_6 = 6;

    /** 7 data bits. */
	@Kroll.constant
    public static final int DATABITS_7 = 7;

    /** 8 data bits. */
	@Kroll.constant
    public static final int DATABITS_8 = 8;

    /** No flow control. */
	@Kroll.constant
    public static final int FLOWCONTROL_NONE = 0;

    /** RTS/CTS input flow control. */
	@Kroll.constant
    public static final int FLOWCONTROL_RTSCTS_IN = 1;

    /** RTS/CTS output flow control. */
	@Kroll.constant
    public static final int FLOWCONTROL_RTSCTS_OUT = 2;

    /** XON/XOFF input flow control. */
	@Kroll.constant
    public static final int FLOWCONTROL_XONXOFF_IN = 4;

    /** XON/XOFF output flow control. */
	@Kroll.constant
    public static final int FLOWCONTROL_XONXOFF_OUT = 8;

    /** No parity. */
	@Kroll.constant
    public static final int PARITY_NONE = 0;

    /** Odd parity. */
	@Kroll.constant
    public static final int PARITY_ODD = 1;

    /** Even parity. */
	@Kroll.constant
    public static final int PARITY_EVEN = 2;

    /** Mark parity. */
	@Kroll.constant
    public static final int PARITY_MARK = 3;

    /** Space parity. */
	@Kroll.constant
    public static final int PARITY_SPACE = 4;

    /** 1 stop bit. */
	@Kroll.constant
    public static final int STOPBITS_1 = 1;

    /** 1.5 stop bits. */
	@Kroll.constant
    public static final int STOPBITS_1_5 = 3;

    /** 2 stop bits. */
	@Kroll.constant
    public static final int STOPBITS_2 = 2;
	
   /**
     * The device currently in use, or {@code null}.
     */
    private UsbSerialDriver mSerialDevice;

    /**
     * The system's USB service.
     */
    //private UsbManager mUsbManager;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

	private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
		@Override
		public void onRunError(Exception e) {
			Log.d(LCAT, "Runner stopped.");
		}

		@Override
		public void onNewData(final byte[] data) {
			if (DBG)
				Log.d(LCAT, "onNewData." + data.length);
			BufferProxy rec_buffer = new BufferProxy(data.length);
			rec_buffer.write(0, data, 0, data.length);
			
			KrollDict ret_data = new KrollDict();
			ret_data.put(TiC.EVENT_PROPERTY_SOURCE, UsbserialModule.this);
			ret_data.put(DATA, rec_buffer);
			fireEvent(RECEIVED, ret_data);
		}
	};

		
	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;
	
	public UsbserialModule()
	{
		super();
	}
	
	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		if (DBG) Log.d(LCAT, "inside onAppCreate");
	}

    @Override
    public void onStart(Activity activity) {
        super.onStart(activity);
    }


    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
    }


    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
    }

	@Override
	public void onStop(Activity activity) {
		super.onStop(activity);
	}
    
	@Override
	public void onDestroy(Activity activity) {
		super.onDestroy(activity);
        close();
        //mUsbManager = null;
	}


    private void stopIoManager() {
        if (mSerialIoManager != null) {
        	if (DBG) Log.i(LCAT, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (mSerialDevice != null) {
        	if (DBG) Log.i(LCAT, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(mSerialDevice, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    
	// Methods
	@Kroll.method
	public boolean open(
			@Kroll.argument(optional = true)int baudRate, 
			@Kroll.argument(optional = true)int dataBits, 
			@Kroll.argument(optional = true)int stopBits, 
			@Kroll.argument(optional = true)int parity) {
		
        //if (mUsbManager == null) {
		UsbManager	mUsbManager = (UsbManager) TiApplication.getInstance().getSystemService(Context.USB_SERVICE);
        //}
		//Log.d(LCAT, "baudRate:" + baudRate);
		if (DBG) Log.d(LCAT, "open, mUsbManager=" + mUsbManager);
		mSerialDevice = UsbSerialProber.acquire(mUsbManager);
		if (DBG) Log.d(LCAT, "open, mSerialDevice=" + mSerialDevice);
        if (mSerialDevice == null) {
        	// no serial device
            onDeviceStateChange();
        	return false;
        } else {
            try {
    			mSerialDevice.open();
    			if (-1 != baudRate && -1 != dataBits && -1 != stopBits && -1 != parity) {
    				mSerialDevice.setParameters(baudRate, dataBits, stopBits, parity);
    			}

            } catch (IOException e) {
                Log.e(LCAT, "Error setting up device: " + e.getMessage(), e);
                try {
                    mSerialDevice.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                mSerialDevice = null;
                return false;
            }
        }
        onDeviceStateChange();
        return true;
	}
	
	@Kroll.method
	public void close() {
		stopIoManager();
        if (mSerialDevice != null) {
            try {
                mSerialDevice.close();
            } catch (IOException e) {
                // Ignore.
            }
            finally {
            	mSerialDevice = null;
            }
        }
	}
	
	@Kroll.method
	public String getDeviceName() {
		if (mSerialDevice != null) {
			return mSerialDevice.getClass().getName()
			.replace("com.hoho.android.usbserial.driver", "")
			.replace("SerialDriver", "");
		}else{
			return "";
		}
	}
	
	@Kroll.method
	public boolean setParameters(int baudRate, int dataBits, int stopBits, int parity) {
		if (mSerialDevice != null) {
			try {
				mSerialDevice.setParameters(baudRate, dataBits, stopBits, parity);
			} catch (IOException e) {
				e.printStackTrace();
				close();
				return false;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				close();
				return false;
			}
			return true;
		}
		return false;
	}


	@Kroll.method
	public void sendData(BufferProxy buffer) {
		if (mSerialIoManager != null) {
			mSerialIoManager.writeAsync(buffer.getBuffer());
		}
	}
	
	// Properties
	@Kroll.getProperty @Kroll.method
	public boolean getIsConnected() {
		return (mSerialDevice != null);
	}
}

