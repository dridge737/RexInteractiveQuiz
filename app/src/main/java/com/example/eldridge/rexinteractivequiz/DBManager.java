package com.example.eldridge.rexinteractivequiz;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ㅇ on 2017-07-05.
 */

public class DBManager {

    private SQLiteOpenHelper dbConnection;
    private SQLiteDatabase database;
    private static DBManager instance;

    private DBManager (Context context) {
        this.dbConnection = new DBConnection(context);
    }

    /**
     * Return a singleton instance of database manager.
     *
     * @param context the Context
     * @return the instance of DBManager
     */
    public static DBManager getInstance(Context context) {
        if(instance == null) {
            instance = new DBManager (context);
        }

        return instance;
    }

    public void open() {
        this.database = dbConnection.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public Cursor getData(String query) {
        return database.rawQuery(query, null);
    }

    public List<String> getChoice(int quizId) {
        List<String> list = new ArrayList<>();

        Cursor cursor = database.rawQuery(
            "SELECT tbl_choice.f_question_id as QuestionID, tbl_choice.f_id as ChoiceID, tbl_choice.f_choice as Choice " +
            "FROM tbl_choice " +
            "INNER JOIN tbl_question ON tbl_question.f_quiz_id = tbl_choice.f_quiz_id AND tbl_question.f_id = tbl_choice.f_question_id " +
            "WHERE tbl_question.f_quiz_id = "+quizId+";", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Log.w("DB:tbl_choice", cursor.getString(0) +"  "+ cursor.getString(1) +"  "+cursor.getString(2));

            list.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }
}
