package com.superdroid.test.zzafire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class see_data extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_data);
    }

    public void mOnClick_realtime(View v){
        Intent intent = new Intent(this, realtime_data.class);
        startActivity(intent);
    }

    public void mOnClick_selecttime(View v){
        Intent intent = new Intent(this, selecttime_data.class);
        startActivity(intent);
    }
}
