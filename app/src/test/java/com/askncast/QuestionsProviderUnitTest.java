package com.askncast;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Piouffle on 18-Feb-16.
 */
@RunWith(Parameterized.class)
public class QuestionsProviderUnitTest {

    private final IQuestionsProvider questionsProvider;

    public QuestionsProviderUnitTest(IQuestionsProvider questionsProvider) {
        this.questionsProvider = questionsProvider;
    }

    @Test
    public void assertNeverNull() {
        /**
         * Method should never return null
         */
        for (int i = 0; i < 500; i++) {
            List<Question> allQuestions = questionsProvider.getRandomQuestions(500);
            assertNotNull(allQuestions);
            assertNotEquals(0, allQuestions.size());
        }
    }

    @Test
    public void assertStoreWorks() {
        /**
         * As we use either an empty DB for tests and static question class should'nt contain too much
         * data, 500 iterations should be more than Ok to get the random question. Unless you're incredibly
         * unlucky
         */
        final String testQuestionText = "Teeeest Question 4242 ???! Jouje ?";
        assertTrue(questionsProvider.storeQuestion(testQuestionText));
        List<Question> allQuestions = questionsProvider.getRandomQuestions(500);
        for (Question question : allQuestions)
            if (question.getText().equals(testQuestionText))
                return;
        throw new AssertionError("Question has not been correctly added");
    }

    @Parameterized.Parameters
    public static Collection<Object[]> instancesToTest() {
        return Arrays.asList(
            new Object[] {new QuestionsProviderStatic()},
            new Object[] {new QuestionsProviderStatic()}
        );
    }
}

