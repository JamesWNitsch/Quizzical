package com.jamesn.quizzical;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by James on 1/4/2016.
 */
// Contains a question and an array of possible answers
public class Question implements Parcelable {
    private String question;
    private Choice[] choices;

    public Question(String question, Choice[] choices){
        this.question = question;
        this.choices = choices;
    }

    public String getQuestion() {
        return question;
    }

    public Choice[] getChoices() {
        return choices;
    }

    protected Question(Parcel in) {
        question = in.readString();
    }

    //Methods for implementing Parcelable, so we can store "Question" information
    // through a state change
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}

