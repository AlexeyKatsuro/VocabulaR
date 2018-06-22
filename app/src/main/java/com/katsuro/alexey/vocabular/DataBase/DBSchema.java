package com.katsuro.alexey.vocabular.DataBase;

/**
 * Created by Alexey on 08.02.2018.
 */

public class DBSchema {

    public static final class WordsTable {
        public static final String NAME = "words";

        public static final class Cols {

            public static final String SOURCE_TEXT = "source_text";
            public static final String TARGET_TEXT = "target_text";
            public static final String AUDIO_FILE_PATH = "audio_file_path";
            public static final String DATE = "date";
        }
    }
}
