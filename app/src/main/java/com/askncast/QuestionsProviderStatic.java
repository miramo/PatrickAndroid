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
            new Question("Is male / female friendship possible ?"),
            new Question("Is death sentence sometimes acceptable ?"),
            new Question("Does euthanasia belong to civilised societies ?"),
            new Question("Is human cloning ok ?"),
            new Question("Should marijuana be legal ?"),
            new Question("Religion does more harm than good "),
            new Question("Is being in love with a family member ok ?"),
            new Question("Is a schizophrenic person who commit suicide a murderer ?"),
            new Question("Have you ever farted and blamed it on a pet ?"),
            new Question("Have you ever been drinking and driving ?"),
            new Question("Have you ever gone in public without a bra ?"),
            new Question("Have you ever thrown up on a perfect stranger ?"),
            new Question("Have you ever peed from a balcony ?"),
            new Question("Is abortion a crime ?"),
            new Question("Have you ever pointed a gun at someone ?"),
            new Question("Would you eat a living cockroach for 1000$ ?"),
            new Question("If you could start your life again with your current memories, would you do it ?"),
            new Question("Is it ok to hit on someone while on a date with someone else ?"),
            new Question("Would you kill to protect your family ?"),
            new Question("Have you ever injected or swallowed hard drugs ?"),
            new Question("Have you ever cross-dressed ?"),
            new Question("Have you ever streaked ?"),
            new Question("Could you be attracted by someone of the same sex ?"),
            new Question("Have you ever gotten bullied ?"),
            new Question("Have you ever done something you could get a prison sentence for ?"),
            new Question("Do you believe in love at first sight ?"),
            new Question("Have you been spanked in the last 6 month ?"),
            new Question("I have or plan to have a tattoo"),
            new Question("Would you hook up with a teacher ?"),
            new Question("Masturbating to anime is a normal thing"),
            new Question("Is there magic in this world ?"),
            new Question("Is it possible to predict the future ?"),
            new Question("Have you ever got / given a footjob ?"),
            new Question("Have you ever had foreplay while driving ?"),
            new Question("Have you ever burped on someone's face ?"),
            new Question("Would you try / Have you tried anal sex ?"),
            new Question("Have you ever hoped to have a chance with one of the players ?"),
            new Question("Is threesome (including your partner cheating ?"),
            new Question("Have you ever thought of an ex-girlfriend / boyfriend during sex ?"),
            new Question("Have you ever masturbated in front of a mirror ?"),
            new Question("Is homosexuality a sickness ?"),
            new Question("Would you accept your child homosexuality ?"),
            new Question("Do you think it’s ok to drive after one spliff ?"),
            new Question("Could you be attracted by someone 20 years older than you ?"),
            new Question("Have you ever had sexual thoughts about your step mother / step father ?"),
            new Question("Do you agree with spanking as an educational tool ?")
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
