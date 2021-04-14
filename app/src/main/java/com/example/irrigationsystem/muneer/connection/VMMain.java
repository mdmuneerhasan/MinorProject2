package com.example.irrigationsystem.muneer.connection;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.irrigationsystem.muneer.utility.Motor;
import com.example.irrigationsystem.muneer.utility.MyDevice;

import java.util.ArrayList;
import java.util.List;

public class VMMain  extends ViewModel {
    MutableLiveData<Integer> connection;
    Repository repository;
    MutableLiveData<Integer> bluetooth;
    MutableLiveData<List<MyDevice>> devices;
    private MutableLiveData<Boolean> searching;
    private MutableLiveData<ArrayList<Motor>> motors;


    public VMMain() {
        init();
    }

    private VMMain init() {
        repository=Repository.getInstance();
        connection=repository.getConnection();
        bluetooth=repository.getBluetooth();
        devices=repository.getDevices();
        searching=repository.getSearching();
        motors=repository.getMotors();
        return this;
    }

    public MutableLiveData<Integer> getConnection() {
        return connection;
    }

    public MutableLiveData<Integer> getBluetooth() {
        return bluetooth;
    }

    public MutableLiveData<List<MyDevice>> getDevices() {
        return devices;
    }

    public MutableLiveData<Boolean> getSearching() {
        return searching;
    }

    public MutableLiveData<ArrayList<Motor>> getMotors() {
        return motors;
    }
}
