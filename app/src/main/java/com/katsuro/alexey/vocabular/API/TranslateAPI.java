package com.katsuro.alexey.vocabular.API;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexey on 6/10/18.
 */

public class TranslateAPI {

    public static final String TAG = TranslateAPI.class.getSimpleName();
    private static final String API_KEY = API_KEYS.TRANSlATE;

    public class Codes {

        public static final int SUCCESS = 200;
        public static final int INVALID_API_KEY = 401;
        public static final int BLOCKED_API_KEY = 402;
        public static final int EXCEEDED_DAILY_LIMIT = 404;
        public static final int EXCEEDED_MAX_TEXT_SIZE = 413;
        public static final int TEXT_FORMAT_ERROR = 422;
        public static final int TRANSLATION_DIRECTION_ERROR = 501;
    }




    private static final Uri ENDPOINT = Uri
            .parse("https://translate.yandex.net/api/v1.5/tr.json/")
            .buildUpon()
            .appendQueryParameter("key", API_KEY)
            .build();

    public String translate(String text, String sourceLang,String targetLang) {

        try {
            String url = ENDPOINT.buildUpon()
                    .appendEncodedPath("translate")
                    .appendQueryParameter("text", text)
                    .appendQueryParameter("lang", sourceLang+"-"+targetLang)
                    .build().toString();

            Log.d(TAG,"URL: " + url);

            String jsonStr = null;

            jsonStr = getUrlString(url);
            Log.d(TAG,"JSON: " + jsonStr);

            TranslateResult result = new Gson().fromJson(jsonStr,TranslateResult.class);

            return result.getTranslation().get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String parseText(JSONObject jsonBody)
            throws IOException, JSONException {

        JSONArray jsonArray = jsonBody.getJSONArray("text");
        String string = jsonArray.getString(0);
        return string;
    }
//
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }



    private String request(String URL) throws IOException {
        URL url = new URL(URL);
        URLConnection urlConn = url.openConnection();
        urlConn.addRequestProperty("User-Agent", "Mozilla");

        InputStream inStream = urlConn.getInputStream();

        String recieved = new BufferedReader(new InputStreamReader(inStream)).readLine();

        inStream.close();
        return recieved;
    }

    public Map<String, String> getLangs(String system_lang) throws IOException {
        if (system_lang==null){
            system_lang = "en";
        }
        String url = ENDPOINT.buildUpon()
                .appendEncodedPath("getLangs")
                .appendQueryParameter("ui", system_lang)
                .build().toString();
        Log.d(TAG,"URL:" + url);
        String langs = getUrlString(url);
        Log.d(TAG,"JSON: " +langs);

        LangsResult result = new Gson().fromJson(langs,LangsResult.class);

        return result.getLangs();
    }


    public String detectLanguage(String text) throws IOException {
        String response = request("https://translate.yandex.net/api/v1.5/tr.json/detect?key=" + API_KEY + "&text=" + text);
        return response.substring(response.indexOf("lang")+7, response.length()-2);
    }

    public String getKey(Map<String, String> map, String value) {
        for (String key : map.keySet()) {
            if (map.get(key).equalsIgnoreCase(value)) {
                return key;
            }
        }
        return null;
    }

    public class TranslateResult {
        @SerializedName("code")
        private int code;
        @SerializedName("lang")
        private String mDeraction;
        @SerializedName("text")
        private List<String> mTranslation;

        public TranslateResult() {

        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDeraction() {
            return mDeraction;
        }

        public void setDeraction(String deraction) {
            mDeraction = deraction;
        }

        public List<String> getTranslation() {
            return mTranslation;
        }

        public void setTranslation(List<String> translation) {
            mTranslation = translation;
        }
    }

    public class LangsResult {
        @SerializedName("dirs")
        List<String> mDiractions;

        @SerializedName("langs")
        Map<String,String> mLangs;

        public LangsResult() {
        }

        public List<String> getDiractions() {
            return mDiractions;
        }

        public void setDiractions(List<String> diractions) {
            mDiractions = diractions;
        }

        public Map<String, String> getLangs() {
            return mLangs;
        }

        public void setLangs(Map<String, String> langs) {
            mLangs = langs;
        }
    }
}
