package com.erik.eduapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

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

        if(savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        runGame();
        runTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
        savedInstanceState.putInt("firstNum", firstNum);
        savedInstanceState.putInt("secondNum", secondNum);
        savedInstanceState.putInt("secretNum", secretNum);
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
        Button guess_1 = findViewById(R.id.guess_1);
        Button guess_2 = findViewById(R.id.guess_2);
        Button guess_3 = findViewById(R.id.guess_3);
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
        } else if (buttonRandomizer == 2) {
            guess_1.setText(String.valueOf(wrongTextOne));
            guess_2.setText(String.valueOf(correctSum));
            guess_3.setText(String.valueOf(wrongTextTwo));
        } else if (buttonRandomizer == 3) {
            guess_1.setText(String.valueOf(wrongTextOne));
            guess_2.setText(String.valueOf(wrongTextTwo));
            guess_3.setText(String.valueOf(correctSum));
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
}
