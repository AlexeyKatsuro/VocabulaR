package com.katsuro.alexey.vocabular.Activities;

import android.support.v4.app.Fragment;

import com.katsuro.alexey.vocabular.Fragments.DictionaryListFragment;
import com.katsuro.alexey.vocabular.Fragments.NewDictionaryFragment;

public class NewDictionaryActivity extends SingleFragmentActivity {


    @Override
    public Fragment createFragment() {
        return NewDictionaryFragment.newInstance();
    }
}
