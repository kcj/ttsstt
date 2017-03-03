package com.example.kimcj.speechtotext;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

public class MirrorActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

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

            /* 사용자의 OS 버전이 마시멜로우 이상인지 체크한다. */
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {

                    /* 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 체크한다.
                    *  int를 쓴 이유? 안드로이드는 C기반이기 때문에, Boolean 이 잘 안쓰인다.
                    */
                int permissionResult = checkSelfPermission(Manifest.permission.RECORD_AUDIO);

                    /* CALL_PHONE의 권한이 없을 때 */
                // 패키지는 안드로이드 어플리케이션의 아이디다.( 어플리케이션 구분자 )
                if (permissionResult == PackageManager.PERMISSION_DENIED) {


                        /* 사용자가 CALL_PHONE 권한을 한번이라도 거부한 적이 있는 지 조사한다.
                        * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
                        */
                    if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(MirrorActivity.this);
                        dialog.setTitle("권한이 필요합니다.")
                                .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. 계속하시겠습니까?")
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);

                                            tts_go("안녕하세요. ");
                                            // stt
                                            Log.e("kcjkcj", "stt go");
                                            stt_go();

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

                    //최초로 권한을 요청할 때
                    else {
                        // CALL_PHONE 권한을 Android OS 에 요청한다.
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                    }

                }
                    /* CALL_PHONE의 권한이 있을 때 */
                else {
                    tts_go("안녕하세요. ");
                    Thread.sleep(1000);
                    // stt
                    Log.e("kcjkcj", "stt go");
                    stt_go();
                }

            }
                /* 사용자의 OS 버전이 마시멜로우 이하일 떄 */
            else {
                tts_go("안녕하세요. ");
                Thread.sleep(1000);
                // stt
                Log.e("kcjkcj", "stt go");
                stt_go();
            }
        } catch (Exception e) {
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

        String stringAPI = "http://104.155.207.154:8080/wtf_allai/allbot?text=talk℆" + question;
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
