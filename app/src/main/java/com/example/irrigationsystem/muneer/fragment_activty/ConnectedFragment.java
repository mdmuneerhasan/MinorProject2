package com.example.irrigationsystem.muneer.fragment_activty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.irrigationsystem.R;
import com.example.irrigationsystem.muneer.connection.VMMain;
import com.example.irrigationsystem.muneer.utility.MotorAdapter;

public class ConnectedFragment extends Fragment {
    RecyclerView recyclerView;
    MotorAdapter motorAdapter;
    VMMain vmMain;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView=view.findViewById(R.id.recyclerView);

        vmMain=new ViewModelProvider.AndroidViewModelFactory(getActivity()
                .getApplication()).create(VMMain.class);


        initRecyclerView();
    }

    private void initRecyclerView() {
        motorAdapter=new MotorAdapter(vmMain.getMotors().getValue()){
            @Override
            public void onBindViewHolder(@NonNull MotorAdapter.Holder holder, int position) {
                super.onBindViewHolder(holder, position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.container,MotorFragment.newInstance(motors.get(position).id,""))
                                .commit();
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(motorAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connected, container, false);
    }
}