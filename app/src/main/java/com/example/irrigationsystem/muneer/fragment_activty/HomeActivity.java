package com.example.irrigationsystem.muneer.fragment_activty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.irrigationsystem.R;
import com.example.irrigationsystem.muneer.utility.Constants;
import com.example.irrigationsystem.muneer.utility.PreferenceUtil;
import com.example.irrigationsystem.muneer.connection.Repository;
import com.example.irrigationsystem.muneer.connection.VMMain;

import static com.example.irrigationsystem.muneer.utility.Constants.CONNECTED;
import static com.example.irrigationsystem.muneer.utility.Constants.CONNECTING;
import static com.example.irrigationsystem.muneer.utility.Constants.CONNECTION_FAILED;
import static com.example.irrigationsystem.muneer.utility.Constants.NOT_CONNECTED;

public class HomeActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 345;
    PreferenceUtil preferenceUtil;
    FrameLayout frameLayout;
    LottieAnimationView loading;
    VMMain vmMain;
    ConnectionBottomSheet connectionBottomSheet;
    private int i=0;
    Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        frameLayout = findViewById(R.id.container);
        loading = findViewById(R.id.loading);
        btnStop = findViewById(R.id.btnClose);

        preferenceUtil = PreferenceUtil.getInstance(this);
        vmMain = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(VMMain.class);



        checkPermission();
        splash();
        addObserver();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Repository.getInstance().setConnection(NOT_CONNECTED);

            }
        }).start();

        

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        PERMISSION_CODE);
            }
        }

    }

    private void addObserver() {
        vmMain.getConnection().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (connectionBottomSheet != null && connectionBottomSheet.isVisible()) {
                    connectionBottomSheet.dismiss();
                    i--;
                }
                switch (integer) {
                    case NOT_CONNECTED:
                    case CONNECTION_FAILED:
                        loading.setVisibility(View.GONE);
                        btnStop.setVisibility(View.GONE);
                        loading.pauseAnimation();
                        if(i<=0) {
                            connectionBottomSheet = new ConnectionBottomSheet();
                            connectionBottomSheet.show(getSupportFragmentManager(), "connection");
                            connectionBottomSheet.setCancelable(false);
                            i++;
                        }
                        break;
                    case CONNECTING:
                        loading.setVisibility(View.VISIBLE);
                        btnStop.setVisibility(View.VISIBLE);
                        loading.playAnimation();
                        break;
                    case CONNECTED:
                        finish();
                        startActivity(new Intent(getBaseContext(),ConnectedActivity.class));
                }
            }
        });
    }

    private void splash() {

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new IntroFragment()).commit();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                frameLayout.animate().translationX(-2000).rotation(270)
                        .setDuration(1000);
            }
        }).start();

    }


    public void disconnect(View view) {
        Repository.getInstance().stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(vmMain.getConnection().getValue()== CONNECTED){
            finish();
            startActivity(new Intent(this,ConnectedActivity.class));
        }
    }
}