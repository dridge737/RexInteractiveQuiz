package com.example.eldridge.rexinteractivequiz;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by ㅇ on 2017-07-05.
 */

public class DBConnection extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "RexQuiz.db";
    private static final int DATABASE_VERSION = 1;

    public DBConnection (Context context) {
        // you can use an alternate constructor to specify a database location (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission to write to it
        // super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
