package com.example.kimcj.speechtotext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import com.example.kimcj.db.SPManager;
import com.example.kimcj.httpjson.HTTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener{

    Handler handler = new Handler();

    SPManager spManager;

    public EditText userText;
    public Button userOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if(!spManager.getUser().isEmpty()){
                    goSpeech();
                }
            }
        }, 1000);

        userText = (EditText) findViewById(R.id.userText);

        userOk = (Button) findViewById(R.id.userOk);
        userOk.setOnClickListener(this);

        spManager = new SPManager(this);
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.userOk :

                    if(userText.getText().length() == 0){
                        Toast.makeText(MainActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(), "아이디를 입력해주세요2", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(this, "아이디를 입력해주세요3", Toast.LENGTH_SHORT).show();
                    }else{
                        goSpeech();

                        spManager.setUser("user", userText.getText().toString());

                        Log.e("gdgdsgds", spManager.getUser());
                    }

            }
    }

    public void goSpeech(){
        Intent intent = new Intent(this, SpeechActivity.class);
        startActivity(intent);
        finish();
    }
}
