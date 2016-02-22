package com.askncast;

import com.orm.SugarRecord;

/**
 * Created by Benjamin Piouffle on 20-Feb-16.
 */
public class Question extends SugarRecord {
    private String text;
    private boolean adultOnly;
    //TODO: private int categoryId;
    //TODO: private int authorId;

    public Question() {}

    public Question(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public boolean isAdultOnly() {
        return adultOnly;
    }
}
