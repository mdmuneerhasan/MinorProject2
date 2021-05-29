package com.example.irrigationsystem.muneer.fragment_activty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.example.irrigationsystem.R;
import com.example.irrigationsystem.muneer.connection.Repository;
import com.example.irrigationsystem.muneer.connection.VMMain;

import static com.example.irrigationsystem.muneer.utility.Constants.CONNECTED;

public class ConnectedActivity extends AppCompatActivity {
    LottieAnimationView loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        loading = findViewById(R.id.loading);

        ((RadioGroup)findViewById(R.id.toggle)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.start:
                        start();
                        break;
                    case R.id.stop:
                        stop();
                        break;
                }
            }
        });
    }

    public void stop() {
        loading.pauseAnimation();
        Repository.getInstance().send("0");
    }
    public void start() {
        loading.playAnimation();
        Repository.getInstance().send("1");
    }
}