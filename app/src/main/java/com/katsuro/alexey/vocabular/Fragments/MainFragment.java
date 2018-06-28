package com.katsuro.alexey.vocabular.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.katsuro.alexey.vocabular.API.API_KEYS;
import com.katsuro.alexey.vocabular.API.TranslateAPI;
import com.katsuro.alexey.vocabular.DataBase.VocabDBHelper;
import com.katsuro.alexey.vocabular.DataBase.WordProvider;
import com.katsuro.alexey.vocabular.FileWriterReader;
import com.katsuro.alexey.vocabular.R;
import com.katsuro.alexey.vocabular.Word;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import ru.yandex.speechkit.Emotion;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Language;
import ru.yandex.speechkit.OnlineVocalizer;
import ru.yandex.speechkit.SpeechKit;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;
import ru.yandex.speechkit.Voice;
import ru.yandex.speechkit.gui.RecognizerActivity;

/**
 * Created by alexey on 6/10/18.
 */

public class MainFragment extends Fragment implements VocalizerListener {

    private static final String TAG = MainFragment.class.getSimpleName();
    private static final int REQUEST_UI_CODE = 0;
    private Spinner mSourceSpinner;
    private Spinner mTargetSpinner;
    private ImageButton mSwapButton;

    private EditText mEditText;
    private ProgressBar mProgressBar;
    private TextView mOutputTextView;
    private ImageButton mTranslateButton;
    private ImageButton mReproduceButton;
    private ImageButton mAddButton;

    private RecyclerView mRecyclerView;
    private WordsAdapter mAdapter;
    private List<Word> mWordList = new ArrayList<>();
    private String mCurrendDirection;

    private Map<String, String> mLangs;
    private OnlineVocalizer mVocalizer;
    private String API_KEY = API_KEYS.SPEECHKIT;
    private String mLangsFilename  = "LangsAndDirs.txt";

    private VocabDBHelper mHelper;
    private FileWriterReader mFileWriterReader;
    private WordProvider mWordProvider;

    public static MainFragment newInstance() {

        MainFragment fragment = new MainFragment();
        return fragment;
    }

    private void setSubtitle(String subtitle) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        try {
            SpeechKit.getInstance().init(getActivity().getApplicationContext(), API_KEY);
            SpeechKit.getInstance().setUuid(UUID.randomUUID().toString());
        } catch (SpeechKit.LibraryInitializationException ex) {
            //do not ignore in a real app!
            ex.printStackTrace();
        }
        mFileWriterReader = new FileWriterReader(getActivity());
        mLangs = loadLangs().getLangs();
//        mHelper =new VocabDBHelper(getActivity());
//        mWordProvider = new WordProvider(mHelper,null);
//        mWordList = mWordProvider.getAllWords();


    }
    private TranslateAPI.LangsResult loadLangs() {
        String jsonLangs = mFileWriterReader.readFileInInternalStorage(mLangsFilename);
        return new Gson().fromJson(jsonLangs,TranslateAPI.LangsResult.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fagment_main, container, false);
        mEditText = view.findViewById(R.id.edit_text);
        mTranslateButton = view.findViewById(R.id.translate_button);
        mProgressBar = view.findViewById(R.id.progress);
        mOutputTextView = view.findViewById(R.id.translated_text);
        mReproduceButton = view.findViewById(R.id.reproduce);
        mAddButton = view.findViewById(R.id.add);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word = new Word();
                String sourceLang = String.valueOf(mSourceSpinner.getSelectedItem());
                String targetLang = String.valueOf(mTargetSpinner.getSelectedItem());
                if((sourceLang+"-"+targetLang).equals("en-ru")){
                    word.setSourceText(mEditText.getText().toString());
                    word.setTargetText(mOutputTextView.getText().toString());
                    word.setDate(new Date());
                } else {
                    word.setTargetText(mEditText.getText().toString());
                    word.setSourceText(mOutputTextView.getText().toString());
                    word.setDate(new Date());
                }
                mWordProvider.addWord(word);
                updateUI();
            }
        });

        mReproduceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mOutputTextView.getText().toString();
                if(text !=null && !text.equals("") ){
                    Language language = String.valueOf(mTargetSpinner.getSelectedItem()).equals("English") ? Language.ENGLISH : Language.RUSSIAN;
                    mVocalizer = new OnlineVocalizer.Builder(language,MainFragment.this)
                            .setEmotion(Emotion.GOOD)
                            .setVoice(Voice.JANE)
                            .build();
                    mVocalizer.prepare();
                    Log.d(TAG, "synthesize text: " + text);
                    mVocalizer.synthesize(text, Vocalizer.TextSynthesizingMode.APPEND);
                    mVocalizer.play();
//                    Intent intent = new Intent(getActivity().getApplicationContext(), RecognizerActivity.class); // 1
//                    intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, Language.RUSSIAN.getValue()); // 2
//                    intent.putExtra(RecognizerActivity.EXTRA_MODEL, OnlineModel.QUERIES.getName()); // 2
//                    intent.putExtra(RecognizerActivity.EXTRA_SHOW_PARTIAL_RESULTS, true); // 3
//                    intent.putExtra(RecognizerActivity.EXTRA_SHOW_HYPOTHESES, true); // 3
//                    intent.putExtra(RecognizerActivity.EXTRA_NIGHT_THEME, false); // 3
                    
//                    startActivityForResult(intent, REQUEST_UI_CODE);
                }
            }
        });


        mTranslateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String sourceLang = String.valueOf(mSourceSpinner.getSelectedItem());
                String targetLang = String.valueOf(mTargetSpinner.getSelectedItem());

                Log.d(TAG,"s: "+ sourceLang);
                Log.d(TAG,"t: "+ targetLang);
                String text = mEditText.getText().toString();
                new TranslateTask().execute(text, sourceLang,targetLang);
            }
        });

        mSourceSpinner = view.findViewById(R.id.source_spinner);
        //mSourceSpinner.setSelection(1);
        mTargetSpinner = view.findViewById(R.id.target_spinner);
        //mTargetSpinner.setSelection(0);
        List<String> langlist =  TranslateAPI.getValueList(mLangs);
        NewDictionaryFragment.LangAdapter mLangAdapter = new NewDictionaryFragment.LangAdapter(getActivity(),langlist);
        mSourceSpinner.setAdapter(mLangAdapter);
        mTargetSpinner.setAdapter(mLangAdapter);
        mSwapButton = view.findViewById(R.id.swap_button);
        mSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = mSourceSpinner.getSelectedItemPosition();
                mSourceSpinner.setSelection(mTargetSpinner.getSelectedItemPosition());
                mTargetSpinner.setSelection(temp);
            }
        });

        mRecyclerView = view.findViewById(R.id.word_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    private void updateUI() {
        //mWordList = mWordProvider.getAllWords();
        if(mAdapter==null){
            mAdapter = new WordsAdapter(mWordList);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setWordList(mWordList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main,menu);


    }

    @Override
    public void onSynthesisDone(@NonNull Vocalizer vocalizer) {
        Log.d(TAG,"onSynthesisDone");
    }

    @Override
    public void onPartialSynthesis(@NonNull Vocalizer vocalizer, @NonNull Synthesis synthesis) {
        Log.d(TAG,"onPartialSynthesis");
    }

    @Override
    public void onPlayingBegin(@NonNull Vocalizer vocalizer) {
        Log.d(TAG,"onPlayingBegin");
    }

    @Override
    public void onPlayingDone(@NonNull Vocalizer vocalizer) {
        Log.d(TAG,"onPlayingDone");
    }


    @Override
    public void onVocalizerError(@NonNull Vocalizer vocalizer, @NonNull Error error) {
        Log.d(TAG,"onVocalizerError");
        Log.d(TAG,error.toString());
        Log.d(TAG,"Message: " + error.getMessage());

    }


    private class TranslateTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... strings) {

            String output = null;
            try {
                if (mLangs == null) {
                    String systemLang = Locale.getDefault().getDisplayLanguage();
                    ;
                    TranslateAPI.LangsResult langsResult = new TranslateAPI().getLangs(systemLang);

                    String jsonLangs = new Gson().toJson(langsResult, TranslateAPI.LangsResult.class);
                    mFileWriterReader.saveFileInInternalStorage("Langs.txt",jsonLangs);

                    String json1 = mFileWriterReader.readFileInInternalStorage("Langs.txt");

                    mLangs = langsResult.getLangs();
                    if (mLangs == null) {
                        return null;
                    }
                    Log.d(TAG, "LANGS: " + mLangs.toString());
                }

                String text = strings[0];
                String sourceLangKey = new TranslateAPI().getKey(mLangs, strings[1]);
                String targetLangKey = new TranslateAPI().getKey(mLangs, strings[2]);
                mCurrendDirection = sourceLangKey + "-" + targetLangKey;
                TranslateAPI.TranslateResult translateResult = new TranslateAPI().translate(text, sourceLangKey, targetLangKey);

                if(translateResult.getCode() == TranslateAPI.Codes.SUCCESS){
                    output = translateResult.getTranslation().get(0);
                } else {
                    output = getMessage(translateResult.getCode());
                }

                return output;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return output;
        }

        private String getMessage(int code) {
            switch (code){
                case TranslateAPI.Codes.BLOCKED_API_KEY:
                    return getString(R.string.blocked_api_key);
                case TranslateAPI.Codes.INVALID_API_KEY:
                    return getString(R.string.invalid_api_key);
                case TranslateAPI.Codes.EXCEEDED_DAILY_LIMIT:
                    return getString(R.string.exceeded_daily_limit);
                case TranslateAPI.Codes.EXCEEDED_MAX_TEXT_SIZE:
                    return getString(R.string.exceeded_max_text_size);
                case TranslateAPI.Codes.TRANSLATION_DIRECTION_ERROR:
                    return getString(R.string.transation_direction_error);
                case TranslateAPI.Codes.TEXT_FORMAT_ERROR:
                    return getString(R.string.text_format_error);
                default:
                    return getString(R.string.unknown_error);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTranslateButton.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mTranslateButton.setVisibility(View.VISIBLE);
            mOutputTextView.setText(s);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == REQUEST_UI_CODE) {
                if (resultCode == RecognizerActivity.RESULT_OK) {
                    final String result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT); // 1
                    Log.d(TAG,"Result: " +result);
                } else if (resultCode == RecognizerActivity.RESULT_CANCELED) {
                    final String language = data.getStringExtra(RecognizerActivity.EXTRA_LANGUAGE); // 2
                    Log.d(TAG,"Language: " +language);
                } else if (resultCode == RecognizerActivity.RESULT_ERROR) {
                    final Error error = (Error) data.getSerializableExtra(RecognizerActivity.EXTRA_ERROR);// 3
                    Log.d(TAG,"Error: " + error.getMessage());
                }
            }
        }
    }

    private class WordsHolder extends RecyclerView.ViewHolder{
        private TextView mSourceTextView;
        private TextView mTargetTextView;
        private Word mWord;

        public WordsHolder(View itemView) {
            super(itemView);
            mSourceTextView = itemView.findViewById(R.id.source_text);
            mTargetTextView = itemView.findViewById(R.id.target_text);
        }

        private void bindWord(Word word){
            mWord = word;
            mSourceTextView.setText(word.getSourceText());
            mTargetTextView.setText(mWord.getTargetText());
        }
    }

    private class WordsAdapter extends RecyclerView.Adapter<WordsHolder>{
        private List<Word> mWordList = new ArrayList<>();

        public WordsAdapter(List<Word> wordList) {
            mWordList = wordList;
        }

        @Override
        public WordsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.vocab_list_item,parent,false);
            return new WordsHolder(view);
        }

        @Override
        public void onBindViewHolder(WordsHolder holder, int position) {
            Word word = mWordList.get(position);
            holder.bindWord(word);
        }

        @Override
        public int getItemCount() {
            return mWordList.size();
        }

        public List<Word> getWordList() {
            return mWordList;
        }

        public void setWordList(List<Word> wordList) {
            mWordList = wordList;
        }
    }



}
