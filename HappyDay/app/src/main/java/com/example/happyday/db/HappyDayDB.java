package com.example.happyday.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.happyday.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoqing on 2015/7/1.
 */
public class HappyDayDB {

    public static final String DB_NAME = "happy_day";

    public static final int VERSION = 1;

    private static HappyDayDB happyDayDB; // ��Ϊ����һ��˽�еĵ���

    private SQLiteDatabase db;

    private HappyDayDB(Context context){
        HappyDayOpenHelper dbHelper = new HappyDayOpenHelper(context, DB_NAME, null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * @func ��Locationʵ�������ݴ洢��db---д��
     * @param location
     */
    public void saveLocation(Location location)
    {
        if (location != null){
            ContentValues values = new ContentValues();
            values.put("name", location.getName());
            values.put("code", location.getCode());
            values.put("type", location.getType());
            values.put("parent_id", location.getParentId());
            db.insert("Location", null, values);
        }
    }

    // ��db�н��ж�ȡ
    public List<Location> loadLocations(int type, int parent_id){
        List<Location> list = new ArrayList<Location>();

        Cursor cursor = db.query("Location", null, "type=? AND parent_id=?", new String[]{String.valueOf(type), String.valueOf(parent_id)}, null, null, null);


        if(cursor.moveToFirst()) {
            do {

                Location location = new Location();
                location.setId(cursor.getInt(cursor.getColumnIndex("id")));
                location.setName(cursor.getString(cursor.getColumnIndex("name")));
                location.setCode(cursor.getString(cursor.getColumnIndex("code")));
                location.setParentId(parent_id);
                location.setType(type);
                list.add(location);

            }while(cursor.moveToNext());

        }

        if (cursor == null)
        {
            cursor.close();
        }

        return list;
    }

    // ����ģʽ
    public synchronized static HappyDayDB getInstance(Context context){
        if (happyDayDB == null){
            happyDayDB = new HappyDayDB(context);
        }
        return happyDayDB;
    }

}
