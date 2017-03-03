package com.example.kimcj.speechtotext;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity_inbi extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

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

        setContentView(R.layout.activity_main);

        kcj();

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
                message = new String(responseBody);

                message = message.replaceAll("[^ㄱ-ㅢ가-힣|0-9]", "");
                Log.e("onSuccess", message);

                tts_GO(message);

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
            charaterView.setBackgroundResource(R.drawable.no);

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

            Toast.makeText(getApplicationContext(), rs[0], Toast.LENGTH_SHORT).show();
            String rsStr = rs[0].replaceAll(" ", "");

            if(madang.equals("") && !rsStr.contains("나와라")){
                tts_GO("챗봇 접속이 안되어 있어요. 원하는 챗봇에 접속해 주세요.");
            }else if(rsStr.contains("나와라")){
                if(rsStr.contains("피자")){
                    madang = "pizza";
                    String url = "http://104.155.207.154:8080/Judey/EGateJudey?userID=tony@techmaru.com&question=도미노 피자 주문&providerID=facebook&lang=KO";
                    getMessage(url);
                    bottext.setText( madang + " 챗봇 연결 됨");
                }else if(rsStr.contains("건강")){
                    madang = "health";
                    tts_GO(madang + "챗봇에 접속 되었습니다. 원하는 질문을 해 보세요.");
                }else if(rsStr.contains("여행")){
                    bottext.setText( madang + " 챗봇 연결 됨");
                    madang = "travel";
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
                }else{
                    tts_GO("말씀하신 챗봇은 없네요.");
                }

            }else if(rsStr.contains("해지")){
                madang = "";
                tts_GO(madang + "챗봇 접속이 해지 되었습니다.");
                bottext.setText("챗봇 연결 안 됨");
            }else if(!madang.equals("")){

                if(madang.equals("pizza")){

                    // 토니형...
                    Log.e("aaaaa", rs[0]);
                    String url = "http://104.155.207.154:8080/Judey/EGateJudey?userID=tony@techmaru.com&question=" + rs[0] + "&providerID=facebook&lang=KO";
                    getMessage(url);

                }else{
                    Log.e("aaaaa", rs[0]);
                    String url = "http://104.155.207.154:8080/wtf_allai/allbot?text=" + madang + "℆" + rs[0];
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

    public void kcj(){
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                int permissionResult = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
                if (permissionResult == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity_inbi.this);
                        dialog.setTitle("권한이 필요합니다.")
                                .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. 계속하시겠습니까?")
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                                        }
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .create()
                                .show();
                    }
                    else {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                    }
                }
                else {
                }
            }
            else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
