package com.katsuro.alexey.vocabular;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

import com.katsuro.alexey.vocabular.API.API_KEYS;
import com.katsuro.alexey.vocabular.API.TranslateAPI;

import java.io.IOException;
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
    private TextView mTextView;
    private ImageButton mTranslateButton;

    private Map<String, String> mLangs;
    private OnlineVocalizer mVocalizer;
    private String API_KEY = API_KEYS.SPEECHKIT;

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



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fagment_main, container, false);
        mEditText = view.findViewById(R.id.edit_text);
        mTranslateButton = view.findViewById(R.id.translate_button);
        mProgressBar = view.findViewById(R.id.progress);
        mTextView = view.findViewById(R.id.translated_text);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mTextView.getText().toString();
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


                String mSourceLang = String.valueOf(mSourceSpinner.getSelectedItem());
                String mTargetLang = String.valueOf(mTargetSpinner.getSelectedItem());

                Log.d(TAG,"s: "+ mSourceLang);
                Log.d(TAG,"t: "+ mTargetLang);
                String text = mEditText.getText().toString();
                new TranslateTask().execute(text, mSourceLang,mTargetLang);
            }
        });

        mSourceSpinner = view.findViewById(R.id.source_spinner);
        mSourceSpinner.setSelection(1);
        mTargetSpinner = view.findViewById(R.id.target_spinner);
        mTargetSpinner.setSelection(0);
        mSwapButton = view.findViewById(R.id.swap_button);
        mSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = mSourceSpinner.getSelectedItemPosition();
                mSourceSpinner.setSelection(mTargetSpinner.getSelectedItemPosition());
                mTargetSpinner.setSelection(temp);
            }
        });

        return view;
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
            try {
            if(mLangs==null){
               mLangs = new TranslateAPI().getLangs(getString(R.string.system_lang));
               if(mLangs==null){
                   return null;
               }
               Log.d(TAG,"LANGS: " + mLangs.toString());
            }

            String text = strings[0];
            String sourceLangKey = new TranslateAPI().getKey(mLangs,strings[1]);
            String targetLangKey = new TranslateAPI().getKey(mLangs,strings[2]);
            String output =  new TranslateAPI().translate(text,sourceLangKey,targetLangKey);
                return output;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
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
            mTextView.setText(s);
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


}
