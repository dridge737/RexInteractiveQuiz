package com.example.eldridge.rexinteractivequiz;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ㅇ on 2017-07-05.
 */

public class QuizManager extends Activity {
    public ArrayList<Integer> listAnswer = new ArrayList<Integer>();
    public JSONObject jsonQuestion = new JSONObject();
    public JSONObject jsonChoice = new JSONObject();

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_window);

        Intent intent = getIntent();
        String qrData = intent.getStringExtra("qrData");
        TextView textScannedQr = (TextView) findViewById(R.id.textScannedQr);
        textScannedQr.setText(qrData);
        int quizId = parseQrData(qrData);

        // Query for tbl_question
        String dbQueryQuestion =
                "SELECT tbl_question.f_id as QuestionID, tbl_question.f_question as Question, tbl_question.f_answer as Answer, tbl_question.f_choiceCnt as ChoiceCnt " +
                        "FROM tbl_question " +
                        "INNER JOIN tbl_quiz ON tbl_quiz.f_id = tbl_question.f_quiz_id " +
                        "WHERE tbl_quiz.f_id = " + quizId + " " +
                        "ORDER BY tbl_question.f_id asc;";

        // Query for tbl_choice
        String dbQueryChoice =
                "SELECT tbl_choice.f_question_id as QuestionID, tbl_choice.f_choiceNo as ChoiceNo, tbl_choice.f_choice as Choice " +
                        "FROM tbl_choice " +
                        "INNER JOIN tbl_question ON tbl_question.f_id = tbl_choice.f_question_id " +
                        "WHERE tbl_question.f_quiz_id = " + quizId + " " +
                        "ORDER BY tbl_choice.f_question_id AND tbl_choice.f_choiceNo asc;";

        DBManager dbManager = DBManager.getInstance(this);
        dbManager.open();
        // Retrieved data from tbl_question( RexQuiz.db)
        Cursor cursorQuestion = dbManager.getData(dbQueryQuestion);
        cursorQuestion.moveToFirst();

        // Retrieved data from tbl_choice (RexQuiz.db)
        Cursor cursorChoice = dbManager.getData(dbQueryChoice);
        cursorChoice.moveToFirst();

        formatData(cursorQuestion, cursorChoice);

        cursorQuestion.close();
        cursorChoice.close();
        dbManager.close();

        Log.w("jsonQuestion", jsonQuestion.toString());
        Log.w("jsonChoice", jsonChoice.toString());
        Log.w("answers", listAnswer.toString());

        TextView textQuestion = (TextView) findViewById(R.id.textQuestion);
        textQuestion.setText(jsonQuestion.toString());
        TextView textChoice = (TextView) findViewById(R.id.textChoice);
        textChoice.setText(jsonChoice.toString());
        TextView textAnswer = (TextView) findViewById(R.id.textAnswer);
        textAnswer.setText(listAnswer.toString());
    }

        @Override
        protected void onDestroy () {
            super.onDestroy();
        }

        private int parseQrData(String qrDate) {
            int quizId = -1;

            try {
                JSONObject jsonObj = new JSONObject(qrDate);
                quizId = jsonObj.getInt("QuizId");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return quizId;
        }

        private void formatData(Cursor cursorQuestion, Cursor cursorChoice) {
            int questionNo = 1;

            while (!cursorQuestion.isAfterLast()) {
                try {
                    JSONObject tmp = new JSONObject();
                    jsonQuestion.put(String.valueOf(questionNo), cursorQuestion.getString(1)); // question
                    listAnswer.add(cursorQuestion.getInt(2)); // answer
/*
                    Log.w("Sup","Question: "+ cursorQuestion.getInt(0) + " " + cursorQuestion.getString(1) + " " + cursorQuestion.getInt(2) + " " + cursorQuestion.getInt(3));
                    Log.w("Sup","Choice: "+ cursorChoice.getInt(0) + " " + cursorChoice.getInt(1) + " " + cursorChoice.getString(2));
*/
                    if( !cursorChoice.isAfterLast() ) {
                        // number of choices
                        for (int i = 1; i <= cursorQuestion.getInt(3); i++) {
                            if (cursorQuestion.getInt(0) == cursorChoice.getInt(0)) {
                                tmp.put(String.valueOf(cursorChoice.getInt(1)), cursorChoice.getString(2)); // (choiceNo, choice)
                            }
                            cursorChoice.moveToNext();
                        }

                        jsonChoice.put(String.valueOf(questionNo), new JSONArray().put(tmp));
                    }

                    cursorQuestion.moveToNext();
                    questionNo++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
   }
