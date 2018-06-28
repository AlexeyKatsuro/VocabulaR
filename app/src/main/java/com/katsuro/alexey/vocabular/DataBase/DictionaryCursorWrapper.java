package com.katsuro.alexey.vocabular.DataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.katsuro.alexey.vocabular.Dictionary;
import com.katsuro.alexey.vocabular.Word;

import java.util.Date;

import static com.katsuro.alexey.vocabular.DataBase.DBSchema.*;



public class DictionaryCursorWrapper extends CursorWrapper {
    public DictionaryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Dictionary getDictionary(){
        String sourceLang = getString(getColumnIndex(DictionariesTable.Cols.SOURCE_LANG));
        String targetLang = getString(getColumnIndex(DictionariesTable.Cols.TARGET_LANG));
        String name = getString(getColumnIndex(DictionariesTable.Cols.DICTIONARY_NAME));
        long date = getLong(getColumnIndex(DictionariesTable.Cols.DATE));

        Dictionary dictionary = new Dictionary();
        dictionary.setName(name);
        dictionary.setSourceLang(sourceLang);
        dictionary.setTargetLang(targetLang);
        dictionary.setDate(new Date(date));
        return dictionary;
    }
}
