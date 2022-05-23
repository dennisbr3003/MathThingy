package com.dennis_brink.android.mymaththingy;

public interface IGameConstants {

    String FIRST_USE = "First Question";
    String GAME_USE = "Next Question";

    int STREAK_BONUS = 30;
    int NUM_LIVES = 3;
    int STREAK = 10;
    int CORRECT_ANSWER = 10;
    int HIGHSCORE_LISTLENGTH = 10;
    long START_TIMER_IN_MILLIS = 15000;

    String HIGHSCORE_ACTION = "process_highscore_name";
    String HIGHSCORE_KEY = "key";
    String SOFTKEYBOARD_ACTION = "launch_softkeyboard";
    String HIGHSCORE_DELETE_ACTION = "delete_highscore_list";
    String EXIT_GAME_ACTION = "exit_in_game";

    String OPERATOR_ADD = "Add";
    String OPERATOR_SUB = "Sub";
    String OPERATOR_MULTI = "Multi";
    String USER_SCORE = "score";
    String USER_TIME = "time";
    String USER_STREAKS = "streaks";
    String GAME_MODE = "mode";

    String EXT_FIRST = "st";
    String EXT_SECOND = "nd";
    String EXT_THIRD = "rd";
    String EXT_REST = "th";
}
