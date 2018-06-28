package com.katsuro.alexey.vocabular.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.katsuro.alexey.vocabular.Dictionary;
import com.katsuro.alexey.vocabular.Word;

import java.util.ArrayList;

import static com.katsuro.alexey.vocabular.DataBase.DBSchema.*;

/**
 * Created by alexey on 6/28/18.
 */

public class WordProvider {

    private static final String TAG = WordProvider.class.getSimpleName();

    private VocabDBHelper mHelper;
    private Dictionary mDictionary;

    public WordProvider(VocabDBHelper helper, Dictionary dictionary) {
        mHelper = helper;
        mDictionary = dictionary;
    }

    public void addWord(Word word){
        try {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(WordsTable.Cols.SOURCE_TEXT, word.getSourceText());
            contentValues.put(WordsTable.Cols.TARGET_TEXT, word.getTargetText());
            contentValues.put(WordsTable.Cols.AUDIO_FILE_PATH, word.getAudioFilePath());
            contentValues.put(WordsTable.Cols.DATE, word.getDate().getTime());
            database.insert(mDictionary.getName(), null, contentValues);
            database.close();
        } catch (SQLiteException e){
            Log.e(TAG,"addWord Fail|DB Error",e);
            e.printStackTrace();
        }

    }

    public Word getWord(int id) {
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            String[] colums = new String[]{WordsTable.Cols.SOURCE_TEXT, WordsTable.Cols.TARGET_TEXT,
                    WordsTable.Cols.AUDIO_FILE_PATH, WordsTable.Cols.DATE};
            Cursor cursor =
                    db.query(mDictionary.getName(), colums,
                            "_id=?", new String[]{String.valueOf(id)}, null, null, null);
            WordCursorWrapper cursorWrapper = new WordCursorWrapper(cursor);
            if (cursor != null) {
                cursorWrapper.moveToFirst();
                Word word = cursorWrapper.getWord();
                cursorWrapper.close();
                cursor.close();
                db.close();
                return word;
            } else {
                return null;
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "getWord Fail|DB Error", e);
            e.printStackTrace();
            return null;
        }


    }

    public ArrayList<Word> getAllWords() {
        ArrayList<Word> words = new ArrayList<>();
       // String query = "SELECT * FROM " + mDictionary.getName();
        try {
            WordCursorWrapper cursor = new WordCursorWrapper(getAllCursor());
            if (cursor.moveToFirst()) {
                do {
                    Word word = cursor.getWord();
                    words.add(word);
                } while (cursor.moveToNext());
            }

            return words;
        } catch (SQLiteException e) {
            Log.e(TAG,"getAllWords Fail|DB Error",e);
            e.printStackTrace();
            return null;
        }
    }
    public Cursor getAllCursor() {
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            Cursor cursor = db.query(mDictionary.getName(), null, null, null, null, null, "_id " +" DESC");
            db.close();
            return cursor;
        } catch (SQLiteException e) {
            Log.e(TAG,"getAllCursor Fail|DB Error", e);
            e.printStackTrace();
            return null;
        }

    }

    public void close(){
        mHelper.close();
    }
}
