package com.erik.eduapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    MediaPlayer player;
    private SoundPool soundPool;
    private int soundCorrect, soundWrong;

    private boolean isOneCorrect, isTwoCorrect, isThreeCorrect;

    private int seconds = 0;
    private boolean running = true;
    private boolean wasRunning;

    private Random random = new Random();
    private int firstNum;
    private int secondNum;
    private int secretNum;
    private int symbolDecider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ViewGroup gameRows = findViewById(R.id.answer_screen);
        getLayoutInflater().inflate(R.layout.answer_text, gameRows);

        //Checks if variable is empty and puts in background music, good for different song
        if (player == null){
            player = MediaPlayer.create(this, R.raw.bgm_game);
            player.setVolume(0.5f, 0.5f);

            //Restarts loop when music has finished
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    player.start();
                }
            });
        }

        //Max Streams variable to save having to updates twice in OS if statement
        int maxStreams = 1;

        //If statement to decide which variables are needed to play audio
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //Will only run if the OS API 21 or higher
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            //Will only run if the OS API is 20 or lower
            soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }

        //Loads in sounds from raw folder
        soundCorrect = soundPool.load(this, R.raw.correct, 1);
        soundWrong = soundPool.load(this, R.raw.bruh, 1);

        if(savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        player.start();
        runGame();
        runTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    protected void onPause(){
        super.onPause();
        wasRunning = running;
        running = false;
    }

    protected void onResume(){
        super.onResume();
        if(wasRunning){
            running = true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void runGame(){
        TextView question = findViewById(R.id.sumQuestion);
        Button guess_1 = findViewById(R.id.button_one);
        Button guess_2 = findViewById(R.id.button_two);
        Button guess_3 = findViewById(R.id.button_three);
        int correctSum = 0;
        int incorrectSumOne = 0;
        int incorrectSumTwo = 0;
        int wrongTextOne = 0;
        int wrongTextTwo = 0;
        int buttonRandomizer;
        int wrongRandomizer;

        firstNum = generateNum();
        secondNum = generateNum();
        secretNum = generateNum();
        //Decides what symbol will be used in the sum and uses that to define solution
        symbolDecider = random.nextInt(3)+1;
        if (symbolDecider == 1){
            question.setText(firstNum + " + " + secondNum);
            correctSum = addSum(firstNum, secondNum);
            incorrectSumOne = correctSum + secretNum;
            incorrectSumTwo = correctSum - secretNum;
        } else if (symbolDecider == 2){
            question.setText(firstNum + " - " + secondNum);
            correctSum = minusSum(firstNum, secondNum);
            incorrectSumOne = correctSum + secretNum;
            incorrectSumTwo = correctSum - secretNum;
        } else if(symbolDecider == 3){
            question.setText(firstNum + " * " + secondNum);
            correctSum = mulSum(firstNum, secondNum);
            incorrectSumOne = correctSum + secretNum;
            incorrectSumTwo = correctSum - secretNum;
        } else
            System.out.println("Frick");

        wrongRandomizer = random.nextInt(2) + 1;
        if (wrongRandomizer == 1) {
            wrongTextOne = incorrectSumOne;
            wrongTextTwo = incorrectSumTwo;
        } else if (wrongRandomizer == 2) {
            wrongTextOne = incorrectSumTwo;
            wrongTextTwo = incorrectSumOne;
        } else
            System.out.println("Dang");

        //Decides which button has the correct answer
        buttonRandomizer = random.nextInt(3)+1;
        if (buttonRandomizer == 1) {
            guess_1.setText(String.valueOf(correctSum));
            guess_2.setText(String.valueOf(wrongTextOne));
            guess_3.setText(String.valueOf(wrongTextTwo));
            isOneCorrect = true;
            isTwoCorrect = false;
            isThreeCorrect = false;
        } else if (buttonRandomizer == 2) {
            guess_1.setText(String.valueOf(wrongTextOne));
            guess_2.setText(String.valueOf(correctSum));
            guess_3.setText(String.valueOf(wrongTextTwo));
            isOneCorrect = false;
            isTwoCorrect = true;
            isThreeCorrect = false;
        } else if (buttonRandomizer == 3) {
            guess_1.setText(String.valueOf(wrongTextOne));
            guess_2.setText(String.valueOf(wrongTextTwo));
            guess_3.setText(String.valueOf(correctSum));
            isOneCorrect = false;
            isTwoCorrect = false;
            isThreeCorrect = true;
        } else
            System.out.println("Skrrt");
    }

    private void runTimer(){
        final TextView timeView = (TextView)findViewById(R.id.time_view);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds %60;
                String time = String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                if(running){
                    seconds++;
//                    if(seconds == 6){
//                        finish();
//                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private int generateNum(){
        return random.nextInt(20) + 1;
    }

    private int addSum(int numUno, int numDuo){
        int guess;
        guess = numUno + numDuo;
        return guess;
    }

    private int minusSum(int numUno, int numDuo){
        int guess;
        guess = numUno - numDuo;
        return guess;
    }

    private int mulSum(int numUno, int numDuo){
        int guess;
        guess = numUno * numDuo;
        return guess;
    }

    public void fatGuess(View view){
        runGame();
    }

    public void playSound(View view){
        if(isOneCorrect){
            switch (view.getId()){
                case R.id.button_one:
                    soundPool.play(soundCorrect, 1, 1, 0, 0, 1);
                    runGame();
                    break;
                case R.id.button_two:
                case R.id.button_three:
                    soundPool.play(soundWrong, 1, 1, 0, 0, 1);
                    break;
            }
        } else if(isTwoCorrect) {
            switch (view.getId()) {
                case R.id.button_two:
                    soundPool.play(soundCorrect, 1, 1, 0, 0, 1);
                    runGame();
                    break;
                case R.id.button_one:
                case R.id.button_three:
                    soundPool.play(soundWrong, 1, 1, 0, 0, 1);
                    break;
            }
        } else if(isThreeCorrect) {
            switch (view.getId()) {
                case R.id.button_three:
                    soundPool.play(soundCorrect, 1, 1, 0, 0, 1);
                    runGame();
                    break;
                case R.id.button_one:
                case R.id.button_two:
                    soundPool.play(soundWrong, 1, 1, 0, 0, 1);
                    break;
            }
        }
    }

    //Releases SoundPool so it doesn't consume system resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    //Stops music and clears it from memory, use this to input another song
    private void stopPlayer(){
        if (player != null){
            player.release();
            player = null;
        }
    }

    //Stops music from playing when app is closed
    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
}
