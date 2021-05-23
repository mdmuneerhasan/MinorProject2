package com.example.irrigationsystem.muneer.fragment_activty;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.irrigationsystem.R;
import com.example.irrigationsystem.muneer.utility.MyBluetoothAdapter;
import com.example.irrigationsystem.muneer.utility.MyDevice;
import com.example.irrigationsystem.muneer.connection.Repository;
import com.example.irrigationsystem.muneer.connection.VMMain;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class ConnectionBottomSheet extends BottomSheetDialogFragment {
    private static final int REQUEST_ENABLE_BT = 676;
    VMMain vmMain;
    TextView tvBluetooth;
    SwitchMaterial btnBluetooth;
    LottieAnimationView ltBluetoothError,ltBluetoothOk,ltSearch;
    private BluetoothAdapter mBluetoothAdapter;
    RecyclerView recyclerView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBluetooth=view.findViewById(R.id.tvBluetooth);
        ltBluetoothOk =view.findViewById(R.id.ltBluetoothOk);
        ltBluetoothError =view.findViewById(R.id.ltBluetoothError);
        ltSearch =view.findViewById(R.id.start);
        btnBluetooth=view.findViewById(R.id.btnBluetooth);
        recyclerView=view.findViewById(R.id.recyclerView);

        btnBluetooth.setChecked(Repository.getInstance().getBluetooth()
                .getValue()==BluetoothAdapter.STATE_ON);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        vmMain=new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())
                .create(VMMain.class);

        vmMain.getBluetooth().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case BluetoothAdapter.STATE_OFF:
                        tvBluetooth.setVisibility(View.VISIBLE);
                        ltBluetoothError.setVisibility(View.VISIBLE);
                        ltBluetoothOk.setVisibility(View.GONE);
                        ltSearch.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        tvBluetooth.setVisibility(View.GONE);
                        ltBluetoothError.setVisibility(View.GONE);
                        ltBluetoothOk.setVisibility(View.VISIBLE);
                        ltSearch.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    //    search();
                        break;
                }
            }
        });

        vmMain.getSearching().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) {
                    ltSearch.setRepeatCount(-1);
                    ltSearch.playAnimation();
                }
                else ltSearch.setRepeatCount(0);
            }
        });

        ltSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        btnBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                }else{
                    mBluetoothAdapter.disable();
                }
            }
        });
        view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                dismiss();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyBluetoothAdapter myBluetoothAdapter=new MyBluetoothAdapter(vmMain.getDevices().getValue());
        recyclerView.setAdapter(myBluetoothAdapter);

        vmMain.getDevices().observe(this, new Observer<List<MyDevice>>() {
            @Override
            public void onChanged(List<MyDevice> myDevices) {
                myBluetoothAdapter.notifyDataSetChanged();
            }
        });
    }

    private void search() {
        if(!Repository.getInstance().getSearching().getValue()) {
            mBluetoothAdapter.startDiscovery();
            Repository.getInstance().setSearching(true).clearOldDevices();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_connectiom,null);
    }
}
