package com.katsuro.alexey.vocabular;

import java.util.Date;

/**
 * Created by alexey on 6/21/18.
 */

public class Word {
    private String mSourceText;
    private String mTargetText;
    private String mAudioFilePath;
    private Date mDate;

    public Word() {
    }

    public Word(String sourceText, String targetText) {
        mSourceText = sourceText;
        mTargetText = targetText;
    }

    public Word(String sourceText, String targetText, String audioFilePath) {
        this(sourceText, targetText);
        mAudioFilePath = audioFilePath;
    }

    public Word(String sourceText, String targetText, String audioFilePath, Date date) {
        this(sourceText, targetText,audioFilePath);
        mDate = date;
    }

    public String getSourceText() {
        return mSourceText;
    }

    public void setSourceText(String sourceText) {
        mSourceText = sourceText;
    }

    public String getTargetText() {
        return mTargetText;
    }

    public void setTargetText(String targetText) {
        mTargetText = targetText;
    }

    public String getAudioFilePath() {
        return mAudioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        mAudioFilePath = audioFilePath;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
}
