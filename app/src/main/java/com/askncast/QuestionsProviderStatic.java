package com.askncast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Benjamin Piouffle on 18-Feb-16.
 */
public class QuestionsProviderStatic implements IQuestionsProvider {
    /**
     * This class aims to provide a very basic implementation for IQuestionsProvider
     *
     * Questions are stored in questions list that is used by QuestionsProviderDB to
     * generate the basic DB
     */
    private static List<Question> questions = new ArrayList<>(Arrays.asList(
        new Question("Static question 1 ?"),
        new Question("Static question 2 ?"),
        new Question("Static question 3 ?")
    ));

    @Override
    public List<Question> getRandomQuestions(int nbQuestions) {
        List<Question> randomQuestions = new ArrayList<>();
        while (randomQuestions.size() < nbQuestions) {
            randomQuestions.add(questions.get(new Random().nextInt(questions.size())));
        }
        return randomQuestions;
    }

    @Override
    public boolean storeQuestion(final String text) {
        return questions.add(new Question(text));
    }
}
