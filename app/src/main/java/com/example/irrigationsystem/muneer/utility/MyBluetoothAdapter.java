package com.example.irrigationsystem.muneer.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.irrigationsystem.R;
import com.example.irrigationsystem.muneer.connection.Repository;

import java.util.List;

public class MyBluetoothAdapter extends RecyclerView.Adapter<MyBluetoothAdapter.Holder>{
    List<MyDevice> devices;
    Context context;
    public MyBluetoothAdapter(List<MyDevice> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new Holder(LayoutInflater.from(context).inflate(R.layout.row_item_devices,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        MyDevice myDevice=devices.get(position);
        holder.tvName.setText(myDevice.getBluetoothDevice().getName());
        holder.tvInfo.setText(myDevice.getBluetoothDevice().getAddress());
        switch (myDevice.getState()){
            case Constants.UNDEFINED:
                holder.error.setVisibility(View.GONE);
                holder.error.pauseAnimation();
                break;
            case Constants.CONNECTION_FAILED:
                holder.error.setVisibility(View.GONE);
                holder.error.pauseAnimation();
                holder.tvInfo.setText("Connection failed, try again.");
                break;
            case Constants.DEVICE_NOT_SUITABLE:
                holder.error.setVisibility(View.VISIBLE);
                holder.error.playAnimation();
                holder.tvInfo.setText("Device not suitable.");
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Repository.getInstance().connect(holder.getAdapterPosition(),myDevice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    class Holder extends RecyclerView.ViewHolder{
        TextView tvName,tvInfo;
        LottieAnimationView error;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvName);
            tvInfo=itemView.findViewById(R.id.tvInfo);
            error=itemView.findViewById(R.id.error);
        }
    }
}
