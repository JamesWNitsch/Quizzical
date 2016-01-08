package com.jamesn.quizzical;

/**
 * Created by James on 1/4/2016.
 */

//Contains one possible answer of a question, and whether it is correct.
public class Choice {
    private Boolean isCorrect;
    private String choiceText;

    public Choice(String choiceText, Boolean isCorrect){
        this.choiceText = choiceText;
        this.isCorrect = isCorrect;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    @Override
    public String toString() {
        return (choiceText+ (isCorrect? " is the right answer.":" is not correct."));
    }
}
