package com.jamesn.quizzical;

import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private static Button beginButton;
    private static TextView questionView;
    private static TextView counterView;
    public static DataHolder dataHolder;
    private RecyclerView recyclerView;
    private static MyAdapter recyclerViewAdapter;
    private static CountDownTimer timer;
    private static boolean isTimeUp;
    private static boolean hasTimeStarted;

    private long millisecondsRemaining;

    public static SFXPlayer sfxPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);

        /* define UI elements */
        questionView = (TextView) findViewById(R.id.questionTextView);
        counterView = (TextView) findViewById(R.id.CountdownTextView);
        beginButton = (Button) findViewById(R.id.button);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerViewAdapter= new MyAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        //If we already have a savedState, load the data from it...
        if (savedInstanceState!=null){
            dataHolder = new DataHolder(this,
                        savedInstanceState.getParcelableArrayList("QuestionsList"),
                        savedInstanceState.getInt("CorrectResponses"),
                        savedInstanceState.getInt("CurrentQuestion"));

            //And display what question we were on in the RecyclerView...
            Question currentQuestion = dataHolder.currentQuestion();
            if (currentQuestion!=null){
                recyclerViewAdapter.nextQuestion(currentQuestion.getChoices());
                questionView.setText(currentQuestion.getQuestion());
            }
            //If we were out of questions...
            else{
                recyclerViewAdapter.nextQuestion(null);
                questionView.setText(dataHolder.showScore());
            }
        }
        //If we don't yet have the questions from the API, request them.
        else{
            beginButton.setEnabled(false);
            dataHolder = new DataHolder(this);
            retrieveData();
        }

        // Load the 'buttons' in either a grid or linear layout depending on the orientation
        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        else{
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Grab the info about the timer from the savedState... if it exists.
        if (savedInstanceState != null){
            isTimeUp = savedInstanceState.getBoolean("isTimeUp");
            millisecondsRemaining = savedInstanceState.getLong("TimeLeft");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if the timer is currently running...
        if(hasTimeStarted && !isTimeUp){
            timer = new CountDownTimer(millisecondsRemaining, 50) {
                int displayTime = 60;
                int currentTime;

                @Override //Display remaining time, and play sound effects
                public void onTick(long millisUntilFinished) {

                    millisecondsRemaining = millisUntilFinished;
                    currentTime = ((int) millisUntilFinished / 1000);

                    if (displayTime != currentTime) {
                        counterView.setText(String.valueOf(currentTime));
                        sfxPlayer.playSFX(SFXPlayer.tick);
                        displayTime = currentTime;
                    }
                }

                @Override
                public void onFinish() {
                    counterView.setText("TIME UP!");
                    questionView.setText("");
                    dataHolder.endQuiz();
                    recyclerViewAdapter.nextQuestion(null);
                    beginButton.setText("Retry?");
                    beginButton.setVisibility(View.VISIBLE);
                }
            };
            timer.start();
            counterView.setText(String.valueOf(millisecondsRemaining / 1000));
            beginButton.setVisibility(View.GONE);
        }
        // if the timer has finished running...
        else if(hasTimeStarted && isTimeUp){
            counterView.setText("Time Up!");
            questionView.setText(dataHolder.showScore());
            beginButton.setText("Retry?");
            beginButton.setVisibility(View.VISIBLE);
            timer = new CountDownTimer(60000, 50) {
                int displayTime = 60;
                int currentTime;

                @Override /* Display remaining time, and play sound effects */
                public void onTick(long millisUntilFinished) {

                    millisecondsRemaining = millisUntilFinished;
                    currentTime = ((int) millisUntilFinished / 1000);

                    if (displayTime != currentTime) {
                        counterView.setText(String.valueOf(currentTime));
                        sfxPlayer.playSFX(SFXPlayer.tick);
                        displayTime = currentTime;
                    }
                }

                @Override
                public void onFinish() {
                    counterView.setText("TIME UP!");
                    questionView.setText("");
                    dataHolder.endQuiz();
                    recyclerViewAdapter.nextQuestion(null);
                    beginButton.setText("Retry?");
                    beginButton.setVisibility(View.VISIBLE);
                }
            };
        }
        // if the timer has not yet started...
        else{
            beginButton.setVisibility(View.VISIBLE);
            //We create the timer on the button click method; Start()
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sfxPlayer=null;
    }

    //Called when the data from the API is finished downloading.
    public static void initDone(){
        beginButton.setEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save information about DataHolder
        outState.putParcelableArrayList("QuestionsList", dataHolder.quizContent);
        outState.putInt("CurrentQuestion", dataHolder.currentQuestion);
        outState.putInt("CorrectResponses", dataHolder.correctResponses);

        // Save information about the clock... and then cancel the current one
        outState.putLong("TimeLeft", millisecondsRemaining);
        outState.putBoolean("isTimeUp", isTimeUp);
        timer.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //New Sound Player
        sfxPlayer = new SFXPlayer(this);
        sfxPlayer.loadResources();
    }

    public void retrieveData(){
        new API_Getter().execute("https://docs.google.com/document/u/0/export?format=txt&id=1MV7GHAvv4tgj98Hj6B_WZdeeEu7CRf1GwOfISjP4GT0");
    }

    //Called when a new question is needed to be displayed
        //Retrieves the next question, passes it to the adapter of the recyclerView
    public static void getNextQuestion(){
        Question nextQuestion = dataHolder.nextQuestion();
        if (nextQuestion!=null) {
            questionView.setText(nextQuestion.getQuestion());
            recyclerViewAdapter.nextQuestion(nextQuestion.getChoices());
        }
        else{
            // Stop Timer, display score, show the "retry" button, and clear the RecyclerView
            isTimeUp=true;
            timer.cancel();

            counterView.setText("Time Up!");
            questionView.setText(dataHolder.showScore());
            beginButton.setText("Retry?");

            beginButton.setVisibility(View.VISIBLE);

            recyclerViewAdapter.nextQuestion(null);
        }
    }

    //Starts or Restarts the game.
    public void start(View view) {
        // Resets the current question and score
        dataHolder.restart();
        // Gets the first Question
        getNextQuestion();
        // Removes the "begin/retry" button, and starts the timer
        beginButton.setVisibility(View.GONE);
        isTimeUp=false;
        hasTimeStarted =true;

        //If the timer hasn't started yet, we'll need to create one here rather than onCreate
        timer = new CountDownTimer(60000, 50) {
            int displayTime = 60;
            int currentTime;

            @Override /* Display remaining time, and play sound effects */
            public void onTick(long millisUntilFinished) {

                millisecondsRemaining = millisUntilFinished;
                currentTime = ((int) millisUntilFinished / 1000);

                if (displayTime != currentTime) {
                    counterView.setText(String.valueOf(currentTime));
                    sfxPlayer.playSFX(SFXPlayer.tick);
                    displayTime = currentTime;
                }
            }

            @Override
            public void onFinish() {
                counterView.setText("TIME UP!");
                questionView.setText("");
                dataHolder.endQuiz();
                recyclerViewAdapter.nextQuestion(null);
                beginButton.setText("Retry?");
                beginButton.setVisibility(View.VISIBLE);
            }
        };
        timer.start();
    }
}
