package com.example.irrigationsystem.muneer.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;
import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED;
import static android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;

public class BluetoothConnection extends BroadcastReceiver {

    private static final String TAG = "broadcast tag";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        BluetoothDevice bluetoothDevice;
        switch(action)
        {
            case ACTION_STATE_CHANGED:
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == STATE_OFF) {
                    Repository.getInstance().setBluetooth(STATE_OFF).clearOldDevices().setSearching(false);
                }else if(state == STATE_ON) {
                    Repository.getInstance().setBluetooth(STATE_ON);
                }
                break;
            case ACTION_ACL_CONNECTED:
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(context, "Connected to "+bluetoothDevice.getName(),
                        Toast.LENGTH_SHORT).show();
                Log.d("BroadcastActions", "Connected to "+bluetoothDevice.getName());
                break;
            case ACTION_ACL_DISCONNECTED:
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(context, "Disconnected from "+bluetoothDevice.getName(),
                        Toast.LENGTH_SHORT).show();
                break;

            case ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Repository.getInstance().addDevice(device);
                Log.e(TAG, "onReceive: device found "+device);
                break;
        }

    }
}
