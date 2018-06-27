package com.katsuro.alexey.vocabular.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.katsuro.alexey.vocabular.Fragments.MainFragment;
import com.katsuro.alexey.vocabular.SingleFragmentActivity;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Fragment createFragment() {
        return MainFragment.newInstance();
    }
}
