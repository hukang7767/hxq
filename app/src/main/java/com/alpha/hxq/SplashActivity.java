package com.alpha.hxq;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.sendEmptyMessageDelayed(0,2000);
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(SplashActivity.this,SearchActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
