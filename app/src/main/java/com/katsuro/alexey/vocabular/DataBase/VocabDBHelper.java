package com.katsuro.alexey.vocabular.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.katsuro.alexey.vocabular.Word;

import java.util.ArrayList;

import static com.katsuro.alexey.vocabular.DataBase.DBSchema.*;


/**
 * Created by Alexey on 01.09.2017.
 */

public class VocabDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "VocabulaRBase.db";
    private static final String TAG = VocabDBHelper.class.getSimpleName();

    public VocabDBHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        upDateDadabase(db,0,VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upDateDadabase(db,oldVersion,newVersion);
    }

    private void upDateDadabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<1){
            db.execSQL("create table " + WordsTable.NAME +"("+
                    " _id integer primary key autoincrement, " +
                    WordsTable.Cols.SOURCE_TEXT + ", " +
                    WordsTable.Cols.TARGET_TEXT + ", " +
                    WordsTable.Cols.AUDIO_FILE_PATH + ", " +
                    WordsTable.Cols.DATE + ")"
            );
        }
//        if(oldVerison<2) db.execSQL("ALTER TABLE TABLE_NAME ADD COLUMN COLUMN_NAME TEXT;");
    }

    public void addWord(Word word){
        try {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(WordsTable.Cols.SOURCE_TEXT, word.getSourceText());
            contentValues.put(WordsTable.Cols.TARGET_TEXT, word.getTargetText());
            contentValues.put(WordsTable.Cols.AUDIO_FILE_PATH, word.getAudioFilePath());
            contentValues.put(WordsTable.Cols.DATE, word.getDate().getTime());
            database.insert(WordsTable.NAME, null, contentValues);
            database.close();
        } catch (SQLiteException e){
            Log.e(TAG,"addWord Fail|DB Error",e);
            e.printStackTrace();
        }

    }

    public Word getWord(int id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] colums = new String[]{WordsTable.Cols.SOURCE_TEXT, WordsTable.Cols.TARGET_TEXT,
                    WordsTable.Cols.AUDIO_FILE_PATH, WordsTable.Cols.DATE};
            Cursor cursor =
                    db.query(WordsTable.NAME, colums,
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
        String query = "SELECT * FROM " + WordsTable.NAME;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            WordCursorWrapper cursor = new WordCursorWrapper(getAllCursor());
            if (cursor.moveToFirst()) {
                do {
                    Word word = cursor.getWord();
                    words.add(word);
                } while (cursor.moveToNext());
            }
            return words;
        } catch (SQLiteException e) {
            Log.e(TAG,"getAllSessions Fail|DB Error",e);
            e.printStackTrace();
            return null;
        }
    }
    public Cursor getAllCursor() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query(WordsTable.NAME, null, null, null, null, null, "_id " +" DESC");

        } catch (SQLiteException e) {
            Log.e(TAG,"getAllCursor Fail|DB Error", e);
            e.printStackTrace();
            return null;
        }

    }
}
