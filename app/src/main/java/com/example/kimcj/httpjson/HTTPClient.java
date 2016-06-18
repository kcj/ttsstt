package com.example.kimcj.httpjson;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.kimcj.speechtotext.MainActivity;
import com.example.kimcj.utils.Settings;

import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by kimcj on 2016. 6. 15..
 */
public class HTTPClient extends AsyncTask{

    static String cookies;
    /*
     * HTTP 요청 Method
    *  */
    enum Method { GET, POST, PUT, DELETE };

    @Override
    protected Object doInBackground(Object[] params) {
        try {

            String url = Settings.URL;
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            if (!MainActivity.foodi_session.equals("")) {
                connection.setRequestProperty("Cookie", MainActivity.foodi_session);
            }

            List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("userID", "20160411152332"));
            nameValuePairs.add(new BasicNameValuePair("providerID", "facebook"));
            nameValuePairs.add(new BasicNameValuePair("question", params[0].toString()));

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(getURLQuery(nameValuePairs));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            connection.connect();

            if (MainActivity.foodi_session.equals("")) {
                cookies = connection.getHeaderField("Set-Cookie");
                Map m = connection.getHeaderFields();
                if (m.containsKey("Set-Cookie")) {
                    Collection c = (Collection) m.get("Set-Cookie");
                    for (Iterator i = c.iterator(); i.hasNext(); ) {
                        cookies += (String) i.next();
                    }
                }
                MainActivity.foodi_session = cookies;
            }

            StringBuilder responseStringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for (; ; ) {
                    String stringLine = bufferedReader.readLine();
                    if (stringLine == null) break;
                    responseStringBuilder.append(stringLine + '\n');
                }
                bufferedReader.close();
            }

            String[] response = responseStringBuilder.toString().split("<");
            Log.e("HTTp", response[0].toString());

            MainActivity.tts.setLanguage(Locale.KOREA);
            MainActivity.tts.speak(response[0].toString(), TextToSpeech.QUEUE_FLUSH, null);

            // 파일에 쓰기
            MainActivity.writeStudy_web(response[0].toString().replace("\n", ""));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getURLQuery(List<BasicNameValuePair> params) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for (BasicNameValuePair pair : params) {
            if (first)
                first = false;
            else
                stringBuilder.append("&");

            try {
                stringBuilder.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
}
