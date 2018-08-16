package com.workout.sixpacksabs.view.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.workout.sixpacksabs.helper.MyExceptionHandler;

/**
 * Created by AdnanAli on 3/21/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
               // new MyExceptionHandler(BaseActivity.this);


    }
}
