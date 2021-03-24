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
                        if (progress < progressBar.getMax()) {
                            btnDoItAgain.setEnabled(false);
                            String temp = txtBox.getText().toString();
                            if (temp.isEmpty()) {
                                temp = "0";
                            }
                            inputVal = Integer.parseInt(temp);
                            if (inputVal > 100) {
                                inputVal = 100;
                            }
                            isRunning = true;
                        } else {
                            progress = 0;
                            progressBar.setProgress(0);
                            txtBox.setText("");
                        }
                    }

                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        txtBox.setHint("Input a number");
        txtBox.setText("");
        txtPercent.setText(progress+"%");
        progressBar.setMax(MAX_PROGRESS);
        progressBar.setProgress(0);

        Thread incThread = new Thread(incProgress, "incProgress"),
                bgThread = new Thread(bgTask, "bgTask");
        bgThread.start();
    }

    private Runnable incProgress = new Runnable() {
        @Override
        public void run() {
            try{
                if (isRunning) {
                    step = (inputVal <= 0) ? 1 : ran.nextInt(inputVal - 1);
                    progressBar.incrementProgressBy(step);
                    progress += step; // Tang dan
                }

                txtPercent.setText(progress + "%");
                for (View v : viewsToFadeIn) {
                    // 3 second fade in time
                    v.animate().alpha(progress % 100).start();
                }
                if (progress >= progressBar.getMax()) {
                    progressBar.setProgress(MAX_PROGRESS);
                    btnDoItAgain.setEnabled(true);
                    txtPercent.setText(MAX_PROGRESS + "%");
                    isRunning = false;
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