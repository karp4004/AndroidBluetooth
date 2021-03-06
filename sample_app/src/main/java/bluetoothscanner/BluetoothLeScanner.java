package bluetoothscanner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

public class BluetoothLeScanner {
    private final Handler mHandler;
    private final BluetoothAdapter.LeScanCallback mLeScanCallback;
    private boolean mScanning;
    public final static int REQUEST_ENABLE_BT = 2001;
    private final Activity mActivity;
    private final BluetoothAdapter mBluetoothAdapter;

    public BluetoothLeScanner(final Activity activity) {
        mHandler = new Handler();

        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                Log.i("devices", "found le device:" + device.getName() + " " + device.getAddress());
            }
        };

        mActivity = activity;
        final BluetoothManager btManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = btManager.getAdapter();
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void scanLeDevice(final int duration, final boolean enable) {
        if (enable) {
            if (mScanning) {
                return;
            }
            Log.d("TAG", "~ Starting Scan");
            // Stops scanning after a pre-defined scan period.
            if (duration > 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "~ Stopping Scan (timeout)");
                        mScanning = false;
                        getBluetoothAdapter().stopLeScan(mLeScanCallback);
                    }
                }, duration);
            }

            mScanning = true;
            getBluetoothAdapter().startLeScan(mLeScanCallback);

        } else {
            Log.d("TAG", "~ Stopping Scan");
            mScanning = false;
            getBluetoothAdapter().stopLeScan(mLeScanCallback);
    }
    }

    public void askUserToEnableBluetoothIfNeeded() {
        if (isBluetoothLeSupported() && (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public boolean isBluetoothLeSupported() {
        return mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean isBluetoothOn() {
        if (mBluetoothAdapter == null) {
            return false;
        } else {
            return mBluetoothAdapter.isEnabled();
        }
    }
}
