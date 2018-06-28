package com.katsuro.alexey.vocabular;

import java.util.Date;

/**
 * Created by alexey on 6/28/18.
 */

public class Dictionary {
    private String mName;
    private String mSourceLang;
    private String mTargetLang;
    private Date mDate;

    public Dictionary() {
    }

    public Dictionary(String name, String sourceLang, String targetLang, Date date) {
        mName = name;
        mSourceLang = sourceLang;
        mTargetLang = targetLang;
        mDate = date;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSourceLang() {
        return mSourceLang;
    }

    public void setSourceLang(String sourceLang) {
        mSourceLang = sourceLang;
    }

    public String getTargetLang() {
        return mTargetLang;
    }

    public void setTargetLang(String targetLang) {
        mTargetLang = targetLang;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
}
