package com.katsuro.alexey.vocabular;

/**
 * Created by alexey on 6/28/18.
 */

public class Dictionary {
    private String mName;
    private String mSourceKeyLang;
    private String mTargetKeyLang;

    public Dictionary() {
    }

    public Dictionary(String name, String sourceKeyLang, String targetKeyLang) {
        mName = name;
        mSourceKeyLang = sourceKeyLang;
        mTargetKeyLang = targetKeyLang;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSourceKeyLang() {
        return mSourceKeyLang;
    }

    public void setSourceKeyLang(String sourceKeyLang) {
        mSourceKeyLang = sourceKeyLang;
    }

    public String getTargetKeyLang() {
        return mTargetKeyLang;
    }

    public void setTargetKeyLang(String targetKeyLang) {
        mTargetKeyLang = targetKeyLang;
    }
}
