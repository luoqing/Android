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

    private static HappyDayDB happyDayDB; // 因为这是一个私有的单例

    private SQLiteDatabase db;

    private HappyDayDB(Context context){
        HappyDayOpenHelper dbHelper = new HappyDayOpenHelper(context, DB_NAME, null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * @func 将Location实例的数据存储到db---写入
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

    // 从db中进行读取
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

    // 单例模式
    public synchronized static HappyDayDB getInstance(Context context){
        if (happyDayDB == null){
            happyDayDB = new HappyDayDB(context);
        }
        return happyDayDB;
    }

}
