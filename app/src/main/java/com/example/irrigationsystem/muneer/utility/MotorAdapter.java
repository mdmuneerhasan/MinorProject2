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

import java.util.ArrayList;

public class MotorAdapter extends RecyclerView.Adapter<MotorAdapter.Holder>{
    Context context;
    public ArrayList<Motor> motors;

    public MotorAdapter(ArrayList<Motor> motors) {
        this.motors = motors;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new Holder(LayoutInflater.from(context).inflate(R.layout.row_item_motors,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Motor motor=motors.get(position);
        if(motor.name==null){
            holder.tvName.setText("Id: "+motor.id);
        }else{
            holder.tvName.setText(motor.name);
        }
        if(motor.isRunning){
            holder.lt_running.playAnimation();
        }else{
            holder.lt_running.pauseAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return motors.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        LottieAnimationView lt_running;
        TextView tvName,tvInfo;
        public Holder(@NonNull View itemView) {
            super(itemView);
            lt_running=itemView.findViewById(R.id.lt_running);
            tvName=itemView.findViewById(R.id.tvName);
            tvInfo=itemView.findViewById(R.id.tvInfo);

        }
    }
}
