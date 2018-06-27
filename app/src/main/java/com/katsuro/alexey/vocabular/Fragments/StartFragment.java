package com.katsuro.alexey.vocabular.Fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.katsuro.alexey.vocabular.API.API_KEYS;
import com.katsuro.alexey.vocabular.API.TranslateAPI;
import com.katsuro.alexey.vocabular.FileWriterReader;
import com.katsuro.alexey.vocabular.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import ru.yandex.speechkit.SpeechKit;

/**
 * Created by alexey on 6/22/18.
 */

public class StartFragment  extends Fragment {

    private static final String TAG = StartFragment.class.getSimpleName();
    private TextView mTitle;
    private TextView mDescription;
    private Spinner mSourceSpinner;
    private Spinner mTargetSpinner;
    private ProgressBar mDownloadProgressBar;
    private ImageView mArrowImageView;
    private ImageView mReplayImageView;

    private FileWriterReader mFileWriterReader;
    private TranslateAPI.LangsResult mLangsAndDirs;
    private String mLangsFilename  = "LangsAndDirs.txt";
    private LangAdapter mLangAdapter;
    private String mSystemLang;

    public static StartFragment newInstance() {
        StartFragment fragment = new StartFragment();
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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_start,container,false);
        mTitle = view.findViewById(R.id.title);
        mDescription = view.findViewById(R.id.description);
        mSourceSpinner = view.findViewById(R.id.source_lang_spinner);
        mTargetSpinner = view.findViewById(R.id.target_lang_spinner);
        mDownloadProgressBar = view.findViewById(R.id.download_progress);
        mArrowImageView = view.findViewById(R.id.arrow);
        mReplayImageView = view.findViewById(R.id.replay_image_view);
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(),"fonts/Purisa.ttf");
        mTitle.setTypeface(face);
        mReplayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    new DownloadLangList().execute(mSystemLang);
                    mReplayImageView.setVisibility(View.INVISIBLE);
                    mDescription.setText(R.string.choose_a_direction);
                }
            }
        });

        updateUI();

        return view;
    }

    private List<String> getLangsValues(Map<String, String> langs) {
        List<String> langList = new ArrayList<>();

        for (String key : langs.keySet()) {
            langList.add( langs.get(key));
        }

        return langList;
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
            mArrowImageView.setVisibility(View.VISIBLE);
            if(isOk){
                updateUI();
            }


        }
    }

    private void updateUI() {
        File root = getActivity().getFilesDir();
        File file = new File(root,mLangsFilename);
        if(!file.exists()){
            mSystemLang = Locale.getDefault().getLanguage();
            if(isNetworkAvailable()) {
                new DownloadLangList().execute(mSystemLang);
            } else {
                mDescription.setText(R.string.internet_necessity);
                mArrowImageView.setVisibility(View.INVISIBLE);
                mReplayImageView.setVisibility(View.VISIBLE);

            }
        } else {
            if(mLangAdapter==null) {
                mLangsAndDirs = loadLangs();
                List<String> langlist =  getLangsValues(mLangsAndDirs.getLangs());
                mLangAdapter = new LangAdapter(getActivity(),langlist);
                mLangAdapter.setDropDownViewResource(R.layout.lang_dropdown_list_item);
                mSourceSpinner.setAdapter(mLangAdapter);
                mTargetSpinner.setAdapter(mLangAdapter);
                int pos =  langlist.indexOf(mLangsAndDirs.getLangs().get(mSystemLang));
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

    private class LangAdapter extends ArrayAdapter<String>{

        private Context mContext;
        private List<String> mLangList = new ArrayList<>();



        public LangAdapter(@NonNull Context context,List<String> langList) {
            super(context,0,langList);
            mContext = context;
            mLangList = langList;
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
