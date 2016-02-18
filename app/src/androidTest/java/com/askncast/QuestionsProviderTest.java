package com.askncast;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by User on 18-Feb-16.
 */
@RunWith(Parameterized.class)
public class QuestionsProviderTest {
    IQuestionsProvider questionsProvider;

    public QuestionsProviderTest(IQuestionsProvider questionsProvider){
        this.questionsProvider = questionsProvider;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {new QuestionsProviderStatic()}
// TODO         {new QuestionsProviderDB()}
        });
    }

    @Test
    public void testNotNull() {
        TestCase.assertNotNull(questionsProvider.getRandomQuestion());
    }
}

