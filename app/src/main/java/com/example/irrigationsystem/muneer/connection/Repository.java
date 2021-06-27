package com.example.irrigationsystem.muneer.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.irrigationsystem.muneer.utility.Constants;
import com.example.irrigationsystem.muneer.utility.Motor;
import com.example.irrigationsystem.muneer.utility.MyDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Repository {
    public static final Repository instance = new Repository();
    private static final String TAG = "repository tag";
    private BluetoothAdapter bluetoothAdapter;
    private MutableLiveData<Integer> connection;
    MutableLiveData<Integer> bluetooth;
    MutableLiveData<List<MyDevice>> devices;
    private MutableLiveData<Boolean> searching;
    Map<String, String> marker;
    Set<BluetoothDevice> pairedDevices;
    BluetoothService bluetoothService;
    private MutableLiveData<ArrayList<String>> messages;
    private MutableLiveData<ArrayList<Motor>> motors;
    boolean testing =false;
    private Repository() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();

        connection = new MutableLiveData<>();
        bluetooth = new MutableLiveData<>();
        devices = new MutableLiveData<>();
        searching = new MutableLiveData<>();
        marker = new HashMap<>();
        messages=new MutableLiveData<>();
        motors=new MutableLiveData<>();

        initBluetooth();
        if(testing){
            connection.setValue(Constants.CONNECTED);
        }else{
            connection.setValue(Constants.UNDEFINED);
        }
        devices.setValue(new ArrayList<>());
        messages.postValue(new ArrayList<>());
        motors.setValue(new ArrayList<>());

        searching.setValue(false);
        addPaired();

    }

    private void initBluetooth() {
        if (bluetoothAdapter == null) {
            bluetooth.setValue(BluetoothAdapter.STATE_OFF);
        } else if (bluetoothAdapter.isEnabled()) {
            bluetooth.setValue(BluetoothAdapter.STATE_ON);
        } else {
            bluetooth.setValue(BluetoothAdapter.STATE_OFF);
        }
    }

    public MutableLiveData<Integer> getBluetooth() {
        return bluetooth;
    }

    public Repository setBluetooth(int bluetooth) {
        if (bluetooth == BluetoothAdapter.STATE_ON) {
            addPaired();
        } else {
            clearOldDevices();
        }
        this.bluetooth.postValue(bluetooth);
        return this;
    }

    public static Repository getInstance() {
        return instance;
    }

    public MutableLiveData<Integer> getConnection() {
        return connection;
    }

    public Repository setConnection(int connection) {
        this.connection.postValue(connection);
        return this;

    }

    public void addDevice(BluetoothDevice device) {
        if (marker.containsKey(device.getAddress())) return;
        List<MyDevice> devices = this.devices.getValue();
        marker.put(device.getAddress(), String.valueOf(devices.size()));
        devices.add(new MyDevice(device));
        this.devices.postValue(devices);
        setSearching(false);
    }

    public Repository setSearching(boolean searching) {
        this.searching.postValue(searching);
        return this;
    }

    public MutableLiveData<Boolean> getSearching() {
        return searching;
    }

    public MutableLiveData<List<MyDevice>> getDevices() {
        return devices;
    }

    public Repository clearOldDevices() {
        devices.getValue().clear();
        marker.clear();
        addPaired();
        return this;
    }

    private void addPaired() {
        List<MyDevice> d = devices.getValue();
        for (BluetoothDevice bluetooth : pairedDevices) {
            if (!marker.containsKey(bluetooth.getAddress())) {
                marker.put(bluetooth.getAddress(), String.valueOf(d.size()));
                d.add(new MyDevice(bluetooth));
            }
        }
        devices.postValue(d);
    }

    public void connect(int position, MyDevice myDevice) {
        bluetoothService=new BluetoothService(myDevice.getBluetoothDevice(),position);
        bluetoothService.connect();
    }

    public void connected(int position) {
        connection.postValue(Constants.CONNECTED);
        List<MyDevice> device = devices.getValue();
        device.get(position).setState(Constants.CONNECTED);
        devices.postValue(device);
    }

    public void failed(int position) {
        connection.postValue(Constants.CONNECTION_FAILED);
        List<MyDevice> d = devices.getValue();
        d.get(position).setState(Constants.CONNECTION_FAILED);
        devices.postValue(d);

    }


    public void messageReceived(String message) {
        ArrayList<String> messages=this.messages.getValue();
        messages.add(message);
        this.messages.postValue(messages);
        Log.e(TAG, "messageReceived: "+message);
    }

    public void communicating() {

    }

    public void stop() {
        if(bluetoothService!=null)bluetoothService.stop();
    }

    public boolean send(String message) {
        if(bluetoothService!=null && connection.getValue()==Constants.CONNECTED){
            bluetoothService.write(message.getBytes());
            Log.e(TAG, "send: " );
            return true;
        }
        Log.e(TAG, "send: failed" );
        return false;
    }

    public MutableLiveData<ArrayList<Motor>> getMotors() {
        if(testing){
            motors.getValue().add(new Motor());
        }
        return motors;
    }

    public void connect() {
        int i=0;
        for(MyDevice device: devices.getValue()){
            if(device.getBluetoothDevice().getName().startsWith("HC")){
                connect(i,device);
                return;
            }
            i++;
        }

    }
}