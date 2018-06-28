package com.katsuro.alexey.vocabular.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.katsuro.alexey.vocabular.API.API_KEYS;
import com.katsuro.alexey.vocabular.API.TranslateAPI;
import com.katsuro.alexey.vocabular.DataBase.DictionaryProvider;
import com.katsuro.alexey.vocabular.DataBase.VocabDBHelper;
import com.katsuro.alexey.vocabular.Dictionary;
import com.katsuro.alexey.vocabular.FileWriterReader;
import com.katsuro.alexey.vocabular.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import ru.yandex.speechkit.SpeechKit;

/**
 * Created by alexey on 6/22/18.
 */

public class NewDictionaryFragment extends Fragment {

    private static final String TAG = NewDictionaryFragment.class.getSimpleName();
    public static final String EXTRA_DICTIONARY = "extra_dictionary_json";

    private TextView mTitle;
    private TextView mDescription;
    private Spinner mSourceSpinner;
    private Spinner mTargetSpinner;
    private ProgressBar mDownloadProgressBar;
    private ImageView mArrowImageView;
    private ImageView mReplayImageView;
    private EditText mVocabNameEditText;
    private FloatingActionButton mCompleteButton;
    private FileWriterReader mFileWriterReader;

    private TranslateAPI.LangsResult mLangsAndDirs;
    private String mLangsFilename  = "LangsAndDirs.txt";
    private LangAdapter mLangAdapter;
    private String mSystemKeyLang;
    private Dictionary mDictionary;
    private DictionaryProvider mProvider;
    private String mDefaultName;
    private ArrayList<Dictionary> mAllDictionaries;

    public static NewDictionaryFragment newInstance() {
        NewDictionaryFragment fragment = new NewDictionaryFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        try {
            SpeechKit.getInstance().init(getActivity().getApplicationContext(), API_KEYS.SPEECHKIT);
            SpeechKit.getInstance().setUuid(UUID.randomUUID().toString());
        } catch (SpeechKit.LibraryInitializationException ex) {
            ex.printStackTrace();
        }

        mFileWriterReader = new FileWriterReader(getActivity());
        mProvider = new DictionaryProvider(new VocabDBHelper(getActivity()));
        mAllDictionaries = mProvider.getAllDictionaries();
        mDefaultName = getString(R.string.dictionary_num)+String.valueOf(mAllDictionaries.size()+1);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_new_dictionary,container,false);
        mTitle = view.findViewById(R.id.title);
        mVocabNameEditText = view.findViewById(R.id.vocab_name_edit_text);
        mDescription = view.findViewById(R.id.description);
        mSourceSpinner = view.findViewById(R.id.source_lang_spinner);
        mTargetSpinner = view.findViewById(R.id.target_lang_spinner);
        mDownloadProgressBar = view.findViewById(R.id.download_progress);
        mArrowImageView = view.findViewById(R.id.arrow);
        mReplayImageView = view.findViewById(R.id.replay_image_view);
        mCompleteButton = view.findViewById(R.id.complete_button);

        mCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete();
            }
        });
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(),"fonts/Purisa.ttf");
        mTitle.setTypeface(face);
        mReplayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });

        mVocabNameEditText.setText(mDefaultName);
        mVocabNameEditText.setHint(mDefaultName);

        updateUI();

        return view;
    }

    private void complete() {
        String name = mVocabNameEditText.getText().toString();
        String sourceLang = String.valueOf(mSourceSpinner.getSelectedItem());
        String sourceKeyLang = TranslateAPI.getKey(mLangsAndDirs.getLangs(), sourceLang);

        String targetLang = String.valueOf(mTargetSpinner.getSelectedItem());
        String targetKeyLang = TranslateAPI.getKey(mLangsAndDirs.getLangs(), targetLang);
        String direction = sourceKeyLang + "-" + targetKeyLang;


        mDictionary = new Dictionary();
        mDictionary.setName(name);
        mDictionary.setSourceLang(sourceLang);
        mDictionary.setTargetLang(targetLang);
        mDictionary.setDate(new Date());


        if(mDictionary.getName() == null || mDictionary.getName().equals("") ) {
            mDictionary.setName(mDefaultName);
        }


        for(Dictionary dictionary : mAllDictionaries){
            if(mDictionary.getName().equals(dictionary.getName())){
                Toast.makeText(getActivity(),R.string.busy_name,Toast.LENGTH_LONG).show();
                return;
            }
        }

        sendResult();
    }

    private void sendResult() {
        Intent data = new Intent();
        String jsonDictionary = new Gson().toJson(mDictionary);
        data.putExtra(EXTRA_DICTIONARY,jsonDictionary);

        getActivity().setResult(Activity.RESULT_OK,data);
        getActivity().finish();
    }


    private class DownloadLangList extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"onPreExecute");
        mDownloadProgressBar.setVisibility(View.VISIBLE);
        mArrowImageView.setVisibility(View.INVISIBLE);

        }

        @Override
        protected Boolean doInBackground(String... systemLang) {
            Log.d(TAG,"doInBackground");
            TranslateAPI.LangsResult langsResult;
            try {
                langsResult = new TranslateAPI().getLangs(systemLang[0]);
                String jsonLangs = new Gson().toJson(langsResult);
                mFileWriterReader.saveFileInInternalStorage(mLangsFilename,jsonLangs);
                return true;
            } catch (IOException e){
                Log.e(TAG,"DownloadLangList Error",e);
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean isOk) {
            Log.d(TAG,"onPostExecute");
            mDownloadProgressBar.setVisibility(View.INVISIBLE);
            if(isOk){
                updateUI();
            }


        }
    }

    private void updateUI() {
        File root = getActivity().getFilesDir();
        File file = new File(root,mLangsFilename);
        mSystemKeyLang = Locale.getDefault().getLanguage();
        if(!file.exists()){
            if(isNetworkAvailable()) {
                new DownloadLangList().execute(mSystemKeyLang);
                mReplayImageView.setVisibility(View.INVISIBLE);
                mDescription.setText(R.string.choose_a_direction);
            } else {
                mDescription.setText(R.string.internet_necessity);
                mArrowImageView.setVisibility(View.INVISIBLE);
                mCompleteButton.setVisibility(View.INVISIBLE);
                mReplayImageView.setVisibility(View.VISIBLE);


            }
        } else {
            mReplayImageView.setVisibility(View.INVISIBLE);
            mArrowImageView.setVisibility(View.VISIBLE);
            mCompleteButton.setVisibility(View.VISIBLE);
            if(mLangAdapter==null) {
                mLangsAndDirs = loadLangs();
                List<String> langlist =  TranslateAPI.getValueList(mLangsAndDirs.getLangs());
                Collections.sort(langlist);
                mLangAdapter = new LangAdapter(getActivity(),langlist);
                mSourceSpinner.setAdapter(mLangAdapter);
                mTargetSpinner.setAdapter(mLangAdapter);
                int pos =  langlist.indexOf(mLangsAndDirs.getLangs().get(mSystemKeyLang));
                mTargetSpinner.setSelection(pos);
            }
        }
    }

    private TranslateAPI.LangsResult loadLangs() {
        String jsonLangs = mFileWriterReader.readFileInInternalStorage(mLangsFilename);
        return new Gson().fromJson(jsonLangs,TranslateAPI.LangsResult.class);
    }

    private boolean isNetworkAvailable(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        return connected;
    }

    public static class LangAdapter extends ArrayAdapter<String>{

        private Context mContext;
        private List<String> mLangList = new ArrayList<>();



        public LangAdapter(@NonNull Context context,List<String> langList) {
            super(context,0,langList);
            mContext = context;
            mLangList = langList;
            setDropDownViewResource(R.layout.lang_dropdown_list_item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if(listItem==null){
                listItem = LayoutInflater.from(mContext).inflate(R.layout.lang_list_item,parent,false);
            }

            String lang = mLangList.get(position);

            TextView textView  = listItem.findViewById(R.id.text);
            textView.setText(lang);

            return listItem;
        }

        public List<String> getLangList() {
            return mLangList;
        }

        public void setLangList(List<String> langList) {
            mLangList = langList;
        }
    }
}