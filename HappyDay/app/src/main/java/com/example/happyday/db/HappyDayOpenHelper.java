package com.example.happyday.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by luoqing on 2015/7/1.
 */
public class HappyDayOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_LOCATION = "create table Location ("
            + "id integer primary key autoincrement, "
            + "name text,"
            + "code text,"
            + "type integer,"
            + "parent_id integer)";

    public HappyDayOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
