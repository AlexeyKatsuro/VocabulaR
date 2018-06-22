package com.katsuro.alexey.vocabular.DataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.katsuro.alexey.vocabular.DataBase.DBSchema.WordsTable;
import com.katsuro.alexey.vocabular.Word;

import java.util.Date;

public class WordCursorWrapper extends CursorWrapper {

    public WordCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Word getWord(){
        String sourceText = getString(getColumnIndex(WordsTable.Cols.SOURCE_TEXT));
        String targetText = getString(getColumnIndex(WordsTable.Cols.TARGET_TEXT));
        String audioFilePath = getString(getColumnIndex(WordsTable.Cols.AUDIO_FILE_PATH));
        long date = getLong(getColumnIndex(WordsTable.Cols.DATE));

        Word word = new Word();
        word.setSourceText(sourceText);
        word.setTargetText(targetText);
        word.setAudioFilePath(audioFilePath);
        word.setDate(new Date(date));
        return word;
    }
}
