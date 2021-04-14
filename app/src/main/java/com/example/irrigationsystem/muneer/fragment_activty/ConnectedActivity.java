package com.example.irrigationsystem.muneer.fragment_activty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.irrigationsystem.R;
import com.example.irrigationsystem.muneer.connection.Repository;
import com.example.irrigationsystem.muneer.connection.VMMain;

import static com.example.irrigationsystem.muneer.utility.Constants.CONNECTED;

public class ConnectedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new ConnectedFragment()).commit();


    }

    public void stop(View view) {
        Repository.getInstance().send("0");
    }

    public void start(View view) {
        Repository.getInstance().send("1");
    }
}