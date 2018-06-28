package com.katsuro.alexey.vocabular.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.katsuro.alexey.vocabular.Dictionary;

import java.util.ArrayList;

import static com.katsuro.alexey.vocabular.DataBase.DBSchema.*;

public class DictionaryProvider {
    private static final String TAG = DictionaryProvider.class.getSimpleName();

    private VocabDBHelper mHelper;

    public DictionaryProvider(VocabDBHelper helper) {
        mHelper = helper;
    }

    public void addDictionary(Dictionary dictionary){
        try {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DictionariesTable.Cols.DICTIONARY_NAME, dictionary.getName());
            contentValues.put(DictionariesTable.Cols.SOURCE_LANG, dictionary.getSourceLang());
            contentValues.put(DictionariesTable.Cols.TARGET_LANG, dictionary.getTargetLang());
            contentValues.put(DictionariesTable.Cols.DATE, dictionary.getDate().getTime());
            database.insert(DictionariesTable.NAME, null, contentValues);
            database.close();
        } catch (SQLiteException e){
            Log.e(TAG,"addDictionary Fail|DB Error",e);
            e.printStackTrace();
        }

    }

    public Dictionary getDictionary(int id) {
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            String[] colums = new String[]{
                    DictionariesTable.Cols.DICTIONARY_NAME,
                    DictionariesTable.Cols.SOURCE_LANG,
                    DictionariesTable.Cols.TARGET_LANG,
                    DictionariesTable.Cols.DATE};
            Cursor cursor =
                    db.query(DictionariesTable.NAME, colums,
                            "_id=?", new String[]{String.valueOf(id)}, null, null, null);
            DictionaryCursorWrapper cursorWrapper = new DictionaryCursorWrapper(cursor);
            if (cursor != null) {
                cursorWrapper.moveToFirst();
                Dictionary dictionary = cursorWrapper.getDictionary();
                cursorWrapper.close();
                cursor.close();
                db.close();
                return dictionary;
            } else {
                return null;
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "getDictionary Fail|DB Error", e);
            e.printStackTrace();
            return null;
        }


    }

    public ArrayList<Dictionary> getAllDictionaries() {
        ArrayList<Dictionary> dictionaries = new ArrayList<>();
        // String query = "SELECT * FROM " + mDictionary.getName();
        try {
            DictionaryCursorWrapper cursor = new DictionaryCursorWrapper(getAllCursor());
            if (cursor.moveToFirst()) {
                do {
                    Dictionary dictionary = cursor.getDictionary();
                    dictionaries.add(dictionary);
                } while (cursor.moveToNext());
            }

            return dictionaries;
        } catch (SQLiteException e) {
            Log.e(TAG,"getAllDictionaries Fail|DB Error",e);
            e.printStackTrace();
            return null;
        }
    }
    public Cursor getAllCursor() {
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            Cursor cursor = db.query(DictionariesTable.NAME, null, null, null, null, null, "_id " +" DESC");
            return cursor;
        } catch (SQLiteException e) {
            Log.e(TAG,"getAllCursor Fail|DB Error", e);
            e.printStackTrace();
            return null;
        }

    }

    public void close(){
        mHelper.close();;
    }
}
