package com.example.irrigationsystem.muneer.fragment_activty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.irrigationsystem.R;
import com.example.irrigationsystem.muneer.connection.Repository;
import com.example.irrigationsystem.muneer.connection.VMMain;

import java.util.Locale;

import static com.example.irrigationsystem.muneer.utility.Constants.CONNECTED;

public class ConnectedActivity extends AppCompatActivity {
    LottieAnimationView loading;
    private static final long START_TIME_IN_MILLIS = 600000;
    private TextView ShowTime;
    private Button Reset;

    private RadioButton start;
    private CountDownTimer mCountDownTime;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        ShowTime = findViewById(R.id.ShowTime);
        Reset = findViewById(R.id.Reset);
        loading = findViewById(R.id.loading);

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
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

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        Reset.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        ShowTime.setText(timeLeftFormatted);
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