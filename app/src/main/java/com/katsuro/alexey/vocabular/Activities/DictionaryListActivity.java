package com.katsuro.alexey.vocabular.Activities;

import android.support.v4.app.Fragment;

import com.katsuro.alexey.vocabular.Fragments.DictionaryListFragment;

public class DictionaryListActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return DictionaryListFragment.newInstance();
    }
}
