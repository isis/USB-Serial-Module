/**
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
	private static final boolean DBG = TiConfig.LOGD;

	@Kroll.constant
	public static final String CONNECTED = "connected";
	@Kroll.constant
	public static final String DISCONNECTED = "disconnected";
	@Kroll.constant
	public static final String RECEIVED = "received";
	@Kroll.constant
	public static final String DATA = "data";
   /**
     * The device currently in use, or {@code null}.
     */
    private UsbSerialDriver mSerialDevice;

    /**
     * The system's USB service.
     */
    private UsbManager mUsbManager;

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
	/*
	private void fireConnected() {
		KrollDict data = new KrollDict();
		data.put(TiC.EVENT_PROPERTY_SOURCE, UsbserialModule.this);
		fireEvent(CONNECTED, data);
	}
	*/
	
	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		if (DBG) Log.d(LCAT, "inside onAppCreate");
	}

    @Override
    public void onStart(Activity activity) {
        super.onStart(activity);
        mUsbManager = (UsbManager) TiApplication.getInstance().getSystemService(Context.USB_SERVICE);
    }


    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        open();
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
        mUsbManager = null;
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

	public void open() {
		mSerialDevice = UsbSerialProber.acquire(mUsbManager);
		if (DBG) Log.d(LCAT, "Resumed, mSerialDevice=" + mSerialDevice);
        if (mSerialDevice == null) {
        	// no serial device
        } else {
            try {
    			mSerialDevice.open();
            } catch (IOException e) {
                Log.e(LCAT, "Error setting up device: " + e.getMessage(), e);
                try {
                    mSerialDevice.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                mSerialDevice = null;
                return;
            }
        }
        onDeviceStateChange();
	}
	
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

	// Methods
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

