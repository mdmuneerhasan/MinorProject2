package com.example.irrigationsystem.muneer.fragment_activty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.irrigationsystem.R;
import com.example.irrigationsystem.muneer.connection.VMMain;
import com.example.irrigationsystem.muneer.utility.Motor;

public class MotorFragment extends Fragment {
    private static final String ID = "id";
    private String id;
    VMMain vmMain;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vmMain=new ViewModelProvider.AndroidViewModelFactory(getActivity()
                .getApplication()).create(VMMain.class);

//        Motor motor=;
    }

    public MotorFragment() {
    }
    public static MotorFragment newInstance(String id, String param2) {
        MotorFragment fragment = new MotorFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_motor, container, false);
    }
}