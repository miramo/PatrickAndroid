package com.askncast;

import java.util.List;

/**
 * Created by Benjamin Piouffle on 18-Feb-16.
 */
public interface IQuestionsProvider {
    List<Question> getRandomQuestions(int nbQuestions);
    boolean storeQuestion(final String question);
}
