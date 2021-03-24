package com.hw06;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button btnDoItAgain;
    EditText txtBox;
    TextView txtPercent;
    int progress = 0, step = 0, inputVal = 0, globalVar = 0;
    long startingMills = System.currentTimeMillis();
    final int MAX_PROGRESS = 100;
    Handler myHandler = new Handler();
    Random ran = new Random();
    boolean isRunning = false;
    ArrayList<View> viewsToFadeIn = new ArrayList<View>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtBox = findViewById(R.id.txtBox);
        progressBar = findViewById(R.id.progressBar);
        txtPercent = findViewById(R.id.txtPercent);
        btnDoItAgain = findViewById(R.id.btnDoItAgain);
        btnDoItAgain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnDoItAgain.setEnabled(false);
                        inputVal = Integer.parseInt(txtBox.getText().toString());
                        isRunning = true;
                        //Toast.makeText(MainActivity.this, ""+isRunning.getText().toString(),1).show();
                        if(isRunning == true){
                            onStart();}

                    }

                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewsToFadeIn.add(findViewById(R.id.btnDoItAgain));
        for (View vz : viewsToFadeIn)
        {
            vz.setAlpha(0); // make invisible to start
        }
        txtBox.setHint("Input a number");
        txtBox.setText("");
        progress=0;
        txtPercent.setText(progress+"%");
        progressBar.setMax(MAX_PROGRESS);
        progressBar.setProgress(0);

//        Thread incProgress =
        Thread backgroundThread = new Thread(bgTask, "bgTask");
        backgroundThread.start();
    }

    private Runnable incProgress = new Runnable() {
        @Override
        public void run() {
            try{
                progressBar.incrementProgressBy(inputVal);
                //progress += (inputVal <= 0) ? 1 : ran.nextInt(inputVal - 1); // Tang dan
                progress += 1; // basic

                txtPercent.setText(progress+"%");
                for (View v : viewsToFadeIn)
                {
                    // 3 second fade in time
                    v.animate().alpha(progress%100).start();
                }
                if (progress >= progressBar.getMax()) {
                    progressBar.setProgress(MAX_PROGRESS);
                    btnDoItAgain.setEnabled(true);
                    txtPercent.setText(MAX_PROGRESS+"%");
                    for (View v : viewsToFadeIn)
                    {
                        // 3 second fade in time
                        v.setAlpha(1);
                    }
                }
            }
            catch (Exception e){Log.e("<<foregroundTask>>",e.getMessage());}
        }
    };

    private Runnable bgTask = new Runnable() {
        @Override
        public void run() {
            try {
                for (int i = 0; i < MAX_PROGRESS; i++) {
                    Thread.sleep(1000);
                    globalVar++;
                    myHandler.post(incProgress);
                }
            } catch (InterruptedException e) {
                Log.e("<<Foreground Task>>", e.getMessage());
            }
        }
    };
}