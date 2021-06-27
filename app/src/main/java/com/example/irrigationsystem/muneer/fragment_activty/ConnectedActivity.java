package com.example.irrigationsystem.muneer.fragment_activty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.irrigationsystem.R;
import com.example.irrigationsystem.app1.MainActivity;
import com.example.irrigationsystem.muneer.connection.Repository;
import com.example.irrigationsystem.muneer.connection.VMMain;

import java.util.Locale;

import static com.example.irrigationsystem.muneer.utility.Constants.CONNECTED;

public class ConnectedActivity extends AppCompatActivity {
    LottieAnimationView loading;
    private static final long START_TIME_IN_MILLIS = 600000;
    private TextView ShowTime;
    private Button Reset;
    private EditText setTime;
    private Button set;

    private RadioGroup toggle;
    private CountDownTimer mCountDownTime;

    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        ShowTime = findViewById(R.id.ShowTime);
        Reset = findViewById(R.id.Reset);
        toggle = findViewById(R.id.toggle);
        setTime = findViewById(R.id.setTime);
        set = findViewById(R.id.set);
        loading = findViewById(R.id.loading);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = setTime.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(ConnectedActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(ConnectedActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);
                setTime.setText("");
            }
        });

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
    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void resetTimer() {

        mTimeLeftInMillis = mStartTimeInMillis;

        updateCountDownText();

    }

    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        ShowTime.setText(timeLeftFormatted);
    }

    public void stop() {
        pauseTimer();
        loading.pauseAnimation();
        Repository.getInstance().send("0");
    }
    public void start() {
        startTimer();
        loading.playAnimation();
        Repository.getInstance().send("1");
    }
    private void pauseTimer() {
        mCountDownTime.cancel();


    }
    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTime = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {

                stop();
            }
        }.start();


    }


}