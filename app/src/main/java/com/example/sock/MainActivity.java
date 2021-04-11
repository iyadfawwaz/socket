package com.example.sock;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.hhh);
        runnex();

    }
    private void runnex(){
        UDPListenerService.textView=textView;
        startService(new Intent(getApplicationContext(), UDPListenerService.class));
       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
       //     startForegroundService(new Intent(getApplicationContext(), TcpServerService.class));
     //   } else {

      //  }
    }
}