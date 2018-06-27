package com.katsuro.alexey.vocabular;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by alexey on 6/22/18.
 */

public class QueryPreferences {

    public static final String PREF_VOCAB_DIRECTION = "pref_vocab_direction";

    public static void setVocabDirection(Context context, String direction){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_VOCAB_DIRECTION,direction)
                .apply();
    }

    public static String getVocabDirection(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_VOCAB_DIRECTION,null);
    }

}
