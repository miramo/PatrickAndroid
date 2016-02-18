package com.askncast;

import java.util.Random;

/**
 * Created by User on 18-Feb-16.
 */
public class QuestionsProviderStatic implements IQuestionsProvider {
    private final String[] questions = {
            "Static question 1 ?",
            "Static question 2 ?",
            "Static question 3 ?"
    };

    @Override
    public String getRandomQuestion() {
        return this.questions[new Random().nextInt(questions.length)];
    }
}
