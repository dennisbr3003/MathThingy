package com.dennis_brink.android.mymaththingy;

public class Session {

    private static Session instance = new Session();

    private int userAnswer;
    private int calculatedAnswer;

    // private constructor makes it a singleton
    private Session() {
    }

    // static method can be used without instantiating the class
    public static Session getInstance() {
        return instance;
    }

    public int getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(int userAnswer) {
        this.userAnswer = userAnswer;
    }

    public int getCalculatedAnswer() {
        return calculatedAnswer;
    }

    public void setCalculatedAnswer(int calculatedAnswer) {
        this.calculatedAnswer = calculatedAnswer;
    }
}
