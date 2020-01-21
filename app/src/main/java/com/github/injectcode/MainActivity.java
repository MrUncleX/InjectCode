package com.github.injectcode;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;



public class MainActivity extends Activity {

    final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "just Test.");
    }

}
