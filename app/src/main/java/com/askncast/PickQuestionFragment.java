package com.askncast;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.cast.games.GameManagerState;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickQuestionFragment extends StateAwareFragment {

    private static final int NbQuestion = 5;

    public PickQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pick_question, container, false);

        ListView lv = (ListView) view.findViewById(R.id.question_list_view);
        lv.setAdapter(new QuestionAdapter(AskNCastApplication.getInstance().getQuestionProvider().getRandomQuestions(NbQuestion)));

        // Init button binding
        ButterKnife.bind(this, view);

        ((EditText)view.findViewById(R.id.custom_question_edit_text)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onSendClick();
                    handled = true;
                }
                return handled;
            }
        });

        return view;
    }

    @Override
    public void onStateChanged(GameManagerState newState) {

    }

    private class QuestionAdapter extends BaseAdapter {
        private List<Question> mQuestions;

        public QuestionAdapter(List<Question> randomQuestions) {
            mQuestions = randomQuestions;
        }

        @Override
        public int getCount() {
            return mQuestions.size();
        }

        @Override
        public Object getItem(int position) {
            return mQuestions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.question_view, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.question_text_view);

            textView.setText(mQuestions.get(position).getText());

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    questionSelected(mQuestions.get(position).getText());
                }
            });
            return rowView;
        }
    }

    private void questionSelected(String text) {
        AskNCastApplication.getInstance().sendQuestion(text);
    }

    @OnClick(R.id.custom_question_send)
    public void onSendClick() {
        questionSelected(((EditText)getView().findViewById(R.id.custom_question_edit_text)).getText().toString());
    }
}
