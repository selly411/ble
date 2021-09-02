package com.superdroid.test.zzafire;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void mOnClick_WD(View v){
        Intent intent = new Intent(this, find_wd.class);
        startActivity(intent);
    }
    public void mOnClick_Data(View v){
        Intent intent = new Intent(this, see_data.class);
        startActivity(intent);
    }
    public void mOnClick_Rename(View v){
        Intent intent = new Intent(this, rename.class);
        startActivity(intent);
    }
}
