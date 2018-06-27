package com.katsuro.alexey.vocabular.Activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.katsuro.alexey.vocabular.Fragments.StartFragment;
import com.katsuro.alexey.vocabular.SingleFragmentActivity;

public class StartActivity extends SingleFragmentActivity {


    @Override
    public Fragment createFragment() {
        return StartFragment.newInstance();
    }
}
