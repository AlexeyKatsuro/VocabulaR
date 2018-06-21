package com.katsuro.alexey.vocabular.API;

import android.net.Uri;
import android.util.Log;

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


            return parseText(new JSONObject(jsonStr));
        } catch (JSONException e) {
            e.printStackTrace();
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
        Log.d(TAG,url);
        String langs = getUrlString(url);
        Log.d(TAG,langs);
        langs = langs.substring(langs.indexOf("langs")+7);
        langs = langs.substring(0, langs.length()-1);

        String[] splitLangs = langs.split(",");

        Map<String, String> languages = new HashMap<String, String>();
        for (String s : splitLangs) {
            String[] s2 = s.split(":");

            String key = s2[0].substring(1, s2[0].length()-1);
            String value = s2[1].substring(1, s2[1].length()-1);

            languages.put(key, value);
        }
        return languages;
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
}
