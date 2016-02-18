package com.askncast;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

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
    public void AssertNeverNull() {
        /**
         * Method should never return null
         */
        for (int i = 0; i < 500; i++) {
            assertNotNull(questionsProvider.getRandomQuestion());
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> instancesToTest() {
        return Arrays.asList(
            new Object[] {new QuestionsProviderStatic()},
            new Object[] {new QuestionsProviderDB()}
        );
    }
}

