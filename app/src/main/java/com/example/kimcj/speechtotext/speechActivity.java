package com.example.kimcj.speechtotext;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjc.nlp.cjcNlp;
import com.example.kimcj.db.SPManager;
import com.example.kimcj.httpjson.HTTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kcj on 2016-07-03.
 */
public class SpeechActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    Intent i;
    SpeechRecognizer mRecognizer;
    TextView textView;

    EditText chatEText;
    Button btn;
    Button chatsend;

    public static String foodi_session = "";

    //tts
    public static TextToSpeech tts;
    Button tts_btn;

    public ImageView charaterView;
    static AnimationDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        chatEText = (EditText) findViewById(R.id.chatEText);

        btn = (Button) findViewById(R.id.mic);
        btn.setOnClickListener(this);

        tts_btn = (Button) findViewById(R.id.tts_btn);
        tts_btn.setOnClickListener(this);

        chatsend = (Button) findViewById(R.id.chatsend);
        chatsend.setOnClickListener(this);

//        btn.setVisibility(View.GONE);
//        tts_btn.setVisibility(View.GONE);

        textView = (TextView) findViewById(R.id.textView);

        // 애니매이션
        charaterView = (ImageView) findViewById(R.id.charaterView);
        charaterView.setOnClickListener(this);
        drawable = (AnimationDrawable) charaterView.getBackground();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) charaterView.getLayoutParams();
        layoutParams.width = (int) ((800 * 300) / 720);
        layoutParams.height = (int) ((1024 * 592) / 720);
        charaterView.setLayoutParams(layoutParams);

        final SPManager spManager = new SPManager(this);
        Log.e("fdsfdsfdsfd", "aaaaa : " + spManager.getSession());

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
            textView.setText("" + rs[0]);
            mRecognizer.startListening(i);

            //tts
            tts_GO();

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mic:
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(i);
                break;
            case R.id.tts_btn:
                try {
                    if(tts != null){
                        tts.stop();
                    }
                }catch (Exception e){
                    e.toString();
                }
//                tts_GO();
                new HTTPClient().execute("foodie", "심심해");

                break;
            case R.id.charaterView:

                try {
                    if(tts != null){
                        tts.stop();
                    }
                }catch (Exception e){
                    e.toString();
                }

                mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(i);

                if (drawable.isRunning())
                    drawable.stop();
                break;
            case R.id.chatsend:

                boolean check = new cjcNlp().checkHangul(chatEText.getText().toString());

                if((chatEText.getText().length() != 0) && !check){
                    Log.e("cj", "check " + check);
                    SPManager spManager = new SPManager(this);

                    new HTTPClient().execute("chat", spManager.getUser(), chatEText.getText().toString());
                    chatEText.setText("");
                }else {
                    Log.e("cj", "check " + check);

                    Toast.makeText(getApplicationContext(), "빈 칸을 채워 주세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void tts_GO(){
        tts.setLanguage(Locale.KOREA);
        tts.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

        // 파일에 쓰기
        writeStudy();

        if (!drawable.isRunning())
            drawable.start();
    }

    public void writeStudy(){

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/com.1thefull";
        File file = new File(dirPath);

        try {
            FileOutputStream studyfile = new FileOutputStream(dirPath+"/study.txt", true);

            if( !file.exists() ) { // 원하는 경로에 폴더가 있는지 확인
                file.mkdirs();
            }
            studyfile.write(textView.getText().toString().getBytes());
            studyfile.write(", ".getBytes());
            studyfile.close();
            Log.e("Main","학습 오케이");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeStudy_web(String text){

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/com.1thefull/";
        File file = new File(dirPath);

        try {
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String file_name = dirPath + sdf.format(d) + ".txt";


            if( !file.isDirectory() ) { // 원하는 경로에 폴더가 있는지 확인
                file.mkdirs();
            }
            FileOutputStream studyfile = new FileOutputStream(file_name, true);

            studyfile.write(text.getBytes());
            studyfile.write(", ".getBytes());
            studyfile.close();
            Log.e("Main","학습 오케이");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(tts != null){
                tts.stop();
            }
        }catch (Exception e){
            e.toString();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tts = new TextToSpeech(getApplicationContext(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(tts != null){
                tts.shutdown();
                tts = null;
            }
        }catch (Exception e){
            e.toString();
        }
    }

    @Override
    public void onInit(int status) {

    }
}
