package com.katsuro.alexey.vocabular.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.katsuro.alexey.vocabular.Fragments.MainFragment;

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
