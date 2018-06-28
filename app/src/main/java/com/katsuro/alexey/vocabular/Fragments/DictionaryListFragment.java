package com.katsuro.alexey.vocabular.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.katsuro.alexey.vocabular.Activities.NewDictionaryActivity;
import com.katsuro.alexey.vocabular.DataBase.DictionaryProvider;
import com.katsuro.alexey.vocabular.DataBase.VocabDBHelper;
import com.katsuro.alexey.vocabular.Dictionary;
import com.katsuro.alexey.vocabular.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexey on 6/28/18.
 */

public class DictionaryListFragment extends Fragment {

    public static final String TAG = DictionaryListFragment.class.getSimpleName();
    private int REQUEST_DICTIONARY = 0;


    private RecyclerView mDictionaryListRecyclerView;
    private FloatingActionButton mNewDictionaryButton;
    private DictionaryAdapter mAdapter;

    private VocabDBHelper mHelper;
    private List<Dictionary> mDictionaryList;
    private DictionaryProvider mProvider;
    private Gson mGson = new Gson();

    public static DictionaryListFragment newInstance() {
        DictionaryListFragment fragment = new DictionaryListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new VocabDBHelper(getActivity());
        mProvider = new DictionaryProvider(mHelper);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary_list,container,false);
        mDictionaryListRecyclerView = view.findViewById(R.id.dictionary_recycler_view);
        mNewDictionaryButton = view.findViewById(R.id.new_dictionary_botton);

        mNewDictionaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), NewDictionaryActivity.class);
                startActivityForResult(intent, REQUEST_DICTIONARY);
            }
        });
        mDictionaryListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    private void updateUI() {


        mDictionaryList = mProvider.getAllDictionaries();

        if(mAdapter==null){
            mAdapter = new DictionaryAdapter(getActivity(),mDictionaryList);
            mDictionaryListRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDictionaryList(mDictionaryList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class DictionaryHolder extends RecyclerView.ViewHolder{
        private TextView mNameTextView;
        private TextView mDirectionTextView;
        private Dictionary mDictionary;

        public DictionaryHolder(View itemView) {
            super(itemView);

            mNameTextView = itemView.findViewById(R.id.name_dictionary_text_view);
            mDirectionTextView = itemView.findViewById(R.id.direction_text_view);
        }

        public void bindDictionary(Dictionary dictionary){
            mDictionary = dictionary;
            mNameTextView.setText(mDictionary.getName());
            mDirectionTextView.setText(String.format("%s-%s",
                    mDictionary.getSourceLang(),mDictionary.getTargetLang()));
        }
    }

    private class DictionaryAdapter extends RecyclerView.Adapter<DictionaryHolder>{

        private List<Dictionary> mDictionaryList;
        private Context mContext;

        private DictionaryAdapter(Context context, List<Dictionary> dictionaryList) {
            mDictionaryList = dictionaryList;
            mContext = context;
        }

        @Override
        public DictionaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dictionary_list_item,parent,false);
            return new DictionaryHolder(view);
        }

        @Override
        public void onBindViewHolder(DictionaryHolder holder, int position) {
            holder.bindDictionary(mDictionaryList.get(position));
        }

        @Override
        public int getItemCount() {
            return mDictionaryList.size();
        }

        public List<Dictionary> getDictionaryList() {
            return mDictionaryList;
        }

        public void setDictionaryList(List<Dictionary> dictionaryList) {
            mDictionaryList = dictionaryList;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"onActivityResult");

        if(resultCode != Activity.RESULT_OK){
            Log.i(TAG,"RESULT_NOT_OK");
            return;
        }

        if (requestCode == REQUEST_DICTIONARY) {
            Bundle bundle = data.getExtras();
            String jsonDictionary = bundle.getString(NewDictionaryFragment.EXTRA_DICTIONARY,null);
            Dictionary dictionary = mGson.fromJson(jsonDictionary,Dictionary.class);
            mProvider.addDictionary(dictionary);
            updateUI();
            //Toast.makeText(getActivity(),dictionary.getName(),Toast.LENGTH_SHORT).show();
        }
    }
}
