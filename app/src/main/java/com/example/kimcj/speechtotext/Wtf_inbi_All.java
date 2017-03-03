package com.example.kimcj.speechtotext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kimcj on 2017. 2. 21..
 */


    public class Wtf_inbi_All extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

        Handler handler = new Handler();
        Intent i;
        TextToSpeech tts;
        SpeechRecognizer mRecognizer;
        AsyncHttpClient client = null;
        ImageView charaterView;
        TextView bottext;
        PersistentCookieStore myCookieStore;

//    ProgressBar progress;

        //
        String madang = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_inbi);

            myCookieStore = new PersistentCookieStore(this);
            client = new AsyncHttpClient();
            client.setCookieStore(myCookieStore);

//        progress= (ProgressBar) findViewById(R.id.progressLoading);

            //stt
            i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

            //tts
            tts = new TextToSpeech(getApplicationContext(), this);

            // 이미지 버튼
            charaterView = (ImageView) findViewById(R.id.charaterView);
            charaterView.setOnClickListener(this);

            // text
            bottext = (TextView) findViewById(R.id.bottext);
            bottext.setText("챗봇 연결 안 됨");

        }

        @Override
        public void onInit(int status) {
            tts_GO("안녕하세요 인공지능 비서 인비 챗 입니다. 먼저 원하는 챗봇에 접속해 보세요.");

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.charaterView:

                    try {
                        if (tts != null) {
                            tts.stop();
                        }
                    } catch (Exception e) {
                        e.toString();
                    }

//                progress.setVisibility(View.VISIBLE);
                    charaterView.setBackgroundResource(R.drawable.yes);

                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(i);

                    charaterView.setClickable(false);
                    break;
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            try {
                if(tts != null){
                    tts.shutdown();
                    tts = null;
                }

                if(mRecognizer != null){
                    mRecognizer.destroy();
                }
            }catch (Exception e){
                e.toString();
            }
        }

        public void tts_GO(String text){
            tts.setLanguage(Locale.KOREA);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

        String message = "";
        public String getMessage(String url)
        {
            String stringAPI = url;
            Log.d("stringAPI",stringAPI);

            client.get(stringAPI, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    message = new String(responseBody).replaceAll("[^ㄱ-ㅢ가-힣|0-9 ]", "");
                    Log.e("onSuccess", message);

                    if(message.contains("항상 저희 원더풀 피자를 이용해 주셔서 대단히 감사합니다") || message.contains("이용해 주셔서 감사합니다")){
                            madang = "";
                            tts_GO(message + "..... 챗봇 접속이 해지 되었습니다.");
                            bottext.setText("챗봇 연결 안 됨");
                    }else {
                        tts_GO(message);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("onFailure",responseBody.toString());

                }

            });

            return message;
        }

        ////
        ////
        ////
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

//            progress.setVisibility(View.GONE);
                tts_GO("말씀 하세요");
                charaterView.setBackgroundResource(R.drawable.no);
                charaterView.setClickable(true);

            }

            @Override
            public void onResults(Bundle results) {
                String key = "";
                key = SpeechRecognizer.RESULTS_RECOGNITION;
                ArrayList<String> mResult = results.getStringArrayList(key);
                String[] rs = new String[mResult.size()];
                mResult.toArray(rs);

//            progress.setVisibility(View.GONE);
                charaterView.setBackgroundResource(R.drawable.no);
                charaterView.setClickable(true);

//                Toast.makeText(getApplicationContext(), rs[0], Toast.LENGTH_SHORT).show();
                String rsStr = rs[0].replaceAll(" ", "");

                if(madang.equals("") && !(rsStr.contains("나와라") || rsStr.contains("연결") || rsStr.contains("접속"))){
                    tts_GO("챗봇 접속이 안되어 있어요. 원하는 챗봇에 접속해 주세요.");
                }else if(rsStr.contains("나와라") || rsStr.contains("연결") || rsStr.contains("접속")){
                    if(rsStr.contains("피자")){
                        madang = "pizza";
                        String url = "http://104.155.207.154:8080/Judey/EGateJudey?userID=tony@techmaru.com&question=원더풀 피자 주문&providerID=facebook&lang=KO";
                        getMessage(url);
                        bottext.setText( madang + " 챗봇 연결 됨");
                    }else if(rsStr.contains("건강")){
                        madang = "health";
                        bottext.setText( madang + " 챗봇 연결 됨");
                        tts_GO(madang + "챗봇에 접속 되었습니다. 원하는 질문을 해 보세요.");
                    }else if(rsStr.contains("여행")){
                        madang = "travel";
                        bottext.setText( madang + " 챗봇 연결 됨");
                        tts_GO(madang + "챗봇에 접속 되었습니다. 원하는 질문을 해 보세요.");
                        bottext.setText( madang + " 챗봇 연결 됨");
                    }else if(rsStr.contains("음식")){
                        madang = "food";
                        tts_GO(madang + "챗봇에 접속 되었습니다. 원하는 질문을 해 보세요.");
                        bottext.setText( madang + " 챗봇 연결 됨");
                    }else if(rsStr.contains("연애") || rsStr.contains("연예")) {
                        madang = "date";
                        tts_GO(madang + "챗봇에 접속 되었습니다. 원하는 질문을 해 보세요.");
                        bottext.setText( madang + " 챗봇 연결 됨");
                    }else if(rsStr.contains("스타일") || rsStr.contains("sytle")) {
                        madang = "beauty";
                        tts_GO(madang + "챗봇에 접속 되었습니다. 원하는 질문을 해 보세요.");
                        bottext.setText( madang + " 챗봇 연결 됨");
                    }else{
                        tts_GO("말씀하신 챗봇은 없네요.");
                    }

                }else if(rsStr.contains("해지") || rsStr.contains("해제") || rsStr.contains("그만")){
                    madang = "";
                    tts_GO("챗봇 접속이 해지 되었습니다.");
                    bottext.setText("챗봇 연결 안 됨");
                }else if(!madang.equals("")){

                    if(madang.equals("pizza")){
                        // 토니형...
                        Log.e("aaaaa", rs[0]);
                        String url = "http://104.155.207.154:8080/Judey/EGateJudey?userID=tony@techmaru.com&question=" + rs[0] + "&providerID=facebook&lang=KO";
                        getMessage(url);

                    }else{
                        Log.e("aaaaa", rs[0]);
                        String url = "http://104.155.207.154:8080/wtf_allai/inbibot?text=" + madang + "℆" + rs[0];
                        getMessage(url);

                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        };
    }