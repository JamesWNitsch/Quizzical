package com.jamesn.quizzical;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by James on 1/4/2016.
 */
public class DataHolder {

    int correctResponses;
    int currentQuestion;
    Context mContext;

    public ArrayList<Question> quizContent;


    public DataHolder(Context context){
        mContext = context;
        quizContent = new ArrayList<>();
        correctResponses = 0;
        currentQuestion = 0;
    }

    public DataHolder(Context context, ArrayList content, int numCorrect, int curQuestion){
        mContext = context;
        quizContent = content;
        correctResponses=numCorrect;
        currentQuestion=curQuestion;
    }

    public void parseJSON(String raw) throws JSONException {
        JSONObject temporaryJSON = new JSONObject(raw);
        JSONArray questions = temporaryJSON.getJSONArray("questions");

        for (int i=0; i<questions.length(); i++) {
            temporaryJSON = questions.getJSONObject(i);

            //Get the answers only
            JSONArray answers = temporaryJSON.getJSONArray("multiple_choice");
            //Then for each answer....
            Choice[] answersArray=new Choice[answers.length()];
            String correctChoice= temporaryJSON.getString("answer");
            String question = temporaryJSON.getString("question");
            for (int j=0; j<answers.length(); j++){
                JSONObject choice = answers.getJSONObject(j);
                //Make new Answer objects
                String choiceText = choice.getString("answer");
                String choiceLetter = choice.getString("id");

                Choice newChoice =new Choice(choiceLetter+ ") " +choiceText, (choiceLetter.equalsIgnoreCase(correctChoice)));

                answersArray[j]=newChoice;
            }
            quizContent.add(new Question(question,answersArray));
        }
        MainActivity.initDone();
    }

    // Selects the next question in the list and returns it.
    //  Returns Null if there are no questions left.
    public Question nextQuestion(){
        if (currentQuestion<=quizContent.size()-1) {
            Question chosenQuestion = quizContent.get(currentQuestion);
            currentQuestion++;
            return chosenQuestion;
        }
        else{
            currentQuestion=quizContent.size()+1;
            return null;
        }
    }

    // Returns the current question, or Null if out of range
    public Question currentQuestion(){
        System.out.println("Current Question: "+currentQuestion+"   Size: "+ quizContent.size());
        if (currentQuestion<=quizContent.size()) {
            return quizContent.get(currentQuestion-1);
        }
        else{
            return null;
        }
    }

    // Sets the current question out of range
    public void endQuiz(){
        currentQuestion=quizContent.size()+1;
    }

    // Returns a String with the score, as well as some flavor text
    public String showScore(){
        String response= ("You got "+correctResponses+
                " out of "+quizContent.size()+
                " questions correct.");

        float percentage = ((float)correctResponses/(float)quizContent.size());
        System.out.println(percentage);

        if (percentage>0.90){
            response = response+ " Great job!!!";
        }
        else if(percentage>0.80){
            response = response+ " Good work!";
        }
        else if(percentage>0.66){
            response = response+ " Not bad!";
        }
        else{
            response = response+ " Practice makes perfect!";
        }
        return response;
    }

    // Resets the # of correct responses and the current question counter
    public void restart(){
        correctResponses=0;
        currentQuestion=0;
    }


}
