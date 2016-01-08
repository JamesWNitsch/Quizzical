package com.jamesn.quizzical;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 1/4/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    // List of all possible answers to the given question
    private List<Choice> choiceList;

    // Final values which determine which layout is used for each choice
    private static final int CORRECT=1;
    private static final int INCORRECT=0;

    public MyAdapter(){
        super();
        choiceList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int viewID = (viewType == CORRECT)? R.layout.answer_correct : R.layout.answer_wrong;
        View v = LayoutInflater.from(parent.getContext()).inflate(viewID, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Choice choice = choiceList.get(position);
        holder.choiceTextView.setText(choice.getChoiceText());
    }

    @Override
    public int getItemViewType(int position) {
        final Choice choice = choiceList.get(position);

        /* Lets the recyclerView know which layout to use (answer_wrong, answer_correct) */
        return choice.getIsCorrect() ? CORRECT : INCORRECT;
    }

    @Override
    public int getItemCount() {
        return choiceList.size();
    }

    //Takes in an array of possible answers, and updates the Views with them
    public void nextQuestion(Choice[] answers){
        if (answers==null){
            choiceList.clear();
            notifyDataSetChanged();
        }
        else {
            choiceList.clear();
            for (int i = 0; i < answers.length; i++) {
                choiceList.add(answers[i]);
            }
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final String TAG = this.getClass().getSimpleName();

        // Boolean to keep trigger-happy users at bay...
        static Boolean recentlyClicked;

        TextView choiceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            choiceTextView= (TextView) itemView.findViewById(R.id.answerText);
            recentlyClicked=false;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!recentlyClicked) {
                recentlyClicked=true;
                Log.d(TAG, "clicked");
                final View overlay = v.findViewById(R.id.overlay);

                if (v.getTag().equals("Correct")) {
                    MainActivity.sfxPlayer.playSFX(SFXPlayer.correct);
                    MainActivity.dataHolder.correctResponses++;
                } else {
                    MainActivity.sfxPlayer.playSFX(SFXPlayer.incorrect);
                }

                overlay.setVisibility(View.VISIBLE);

                //Handler that limits the clicks per second to 1, as well as delaying the display
                //  of the next question
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //Call back to refresh the question and answers
                        overlay.setVisibility(View.INVISIBLE);
                        MainActivity.getNextQuestion();
                        recentlyClicked=false;
                    }
                }, 1000);
            }
        }

    }
}
