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
            db.execSQL("create table " + DictionariesTable.NAME +"("+
                    " _id integer primary key autoincrement, " +
                    DictionariesTable.Cols.DICTIONARY_NAME + ", " +
                    DictionariesTable.Cols.SOURCE_LANG + ", " +
                    DictionariesTable.Cols.TARGET_LANG + ", " +
                    DictionariesTable.Cols.DATE + ")"
            );
        }
//        if(oldVerison<2) db.execSQL("ALTER TABLE TABLE_NAME ADD COLUMN COLUMN_NAME TEXT;");
    }
}
