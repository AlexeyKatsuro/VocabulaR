package com.katsuro.alexey.vocabular;

/**
 * Created by alexey on 6/21/18.
 */

public class Word {
    private String mSourseText;
    private String mTargetText;
    private String mAudioFilePath;

    public Word() {
    }

    public Word(String sourseText, String targetText) {
        mSourseText = sourseText;
        mTargetText = targetText;
    }

    public Word(String sourseText, String targetText, String audioFilePath) {
        this(sourseText, targetText);
        mAudioFilePath = audioFilePath;
    }

    public String getSourseText() {
        return mSourseText;
    }

    public void setSourseText(String sourseText) {
        mSourseText = sourseText;
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
}
