package com.katsuro.alexey.vocabular.DataBase;

/**
 * Created by Alexey on 08.02.2018.
 */

public class DBSchema {

    public static final class DictionariesTable{
        public static final String NAME = "Dictionaries";

        public static final class Cols {

            public static final String DICTIONARY_NAME = "dictionary_name";
            public static final String SOURCE_LANG = "source_lang";
            public static final String TARGET_LANG = "target_lang";
            public static final String DATE = "date";
        }
    }

    public static final class WordsTable {

        public static final class Cols {

            public static final String SOURCE_TEXT = "source_text";
            public static final String TARGET_TEXT = "target_text";
            public static final String AUDIO_FILE_PATH = "audio_file_path";
            public static final String DATE = "date";
        }
    }
}
