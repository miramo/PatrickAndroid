package com.askncast;

import com.orm.query.Select;
import java.util.List;

/**
 * Created by Benjamin Piouffle on 18-Feb-16.
 */
public class QuestionsProviderDB implements IQuestionsProvider {
    @Override
    public List<Question> getRandomQuestions(int nbQuestions) {
        return Select.from(Question.class).orderBy("RANDOM()").limit(Integer.toString(nbQuestions)).list();
    }

    @Override
    public boolean storeQuestion(final String text) {
        Question question = new Question(text);
        return question.save() > 0;
    }
}
