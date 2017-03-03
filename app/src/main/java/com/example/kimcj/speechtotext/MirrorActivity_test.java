package com.example.kimcj.speechtotext;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

///

/**
 * Created by kimcj on 2017. 2. 8..
 */

public class MirrorActivity_test extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private static final int RESULT_SPEECH = 1; // REQUEST_CODE로 쓰임

    private SpeechRecognizer mRecognizer;
    private TextToSpeech myTTS;
    private Intent i;

    public static AsyncHttpClient client = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);

        client = new AsyncHttpClient();

        myTTS = new TextToSpeech(this, this);

    }

    public void tts_go(String text){
        myTTS.setLanguage(Locale.KOREA);
        myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//        myTTS.speak(text, TextToSpeech.QUEUE_ADD, null);

    }

    public void stt_go(){
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(i);

    }


    @SuppressWarnings("deprecation")
    @Override
    public void onInit(int status) {

        try {
            tts_go("a");
            Thread.sleep(1000);
            // stt
            stt_go();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myTTS !=null){
            myTTS.stop();
            myTTS.shutdown();
        }
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            Toast.makeText(getApplicationContext(), rs[0], Toast.LENGTH_SHORT).show();

            if(rs[0].contains("원더풀")){
                try {
                    tts_go("네 말씀하세요.");
                    Thread.sleep(500);
                    stt_go();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                getMessage(rs[0]);
            }

            mRecognizer.startListening(i);

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };



    //////////
    String message = "";
    public String getMessage(String question)
    {
        Log.e("aaaaa", question);

        String stringAPI = "http://104.155.207.154:8080/wtf_allai/allbot?text=health℆" + question;
        Log.d("stringAPI",stringAPI);

        client.get(stringAPI, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                message = new String(responseBody);
                Log.e("onSuccess", message);

                tts_go(message);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("onFailure",responseBody.toString());

            }

        });

        return message;
    }
}
