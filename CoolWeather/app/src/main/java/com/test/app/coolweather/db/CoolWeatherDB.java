package com.test.app.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.test.app.coolweather.model.City;
import com.test.app.coolweather.model.Country;
import com.test.app.coolweather.model.Location;
import com.test.app.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoqing on 2015/6/17.
 * ��province,city,country������д��db��saveProvince,saveCity,saveCountry����
 * �Լ���db�в�ѯ����ص�����(loadProvinces.loadCities,loadCountries)
 */
public class CoolWeatherDB {

    public static final String DB_NAME = "cool_weather";

    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * @func ��Provinceʵ��������д�뵽db
     * @param province
     */
    public void saveProvince(Province province)
    {
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /**
     * @func ��Cityʵ��������д�뵽db
     * @param city
     */
    public void saveCity(City city)
    {
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /**
     * @func ��Countryʵ�������ݴ洢��db
     * @param country
     */
    public void saveCountry(Country country)
    {
        if (country != null){
            ContentValues values = new ContentValues();
            values.put("country_name", country.getCountryName());
            values.put("country_code", country.getCountryCode());
            values.put("city_id", country.getCityId());
            db.insert("Country", null, values);
        }
    }

    /**
     * @func ��db�ж�ȡȫ����ʡ����Ϣ
     * @return ȫ��ʡ�������б�
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();

        // �˴������ԶԲ�ѯ������������
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if(cursor.moveToFirst())
        {
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor
                        .getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor
                        .getColumnIndex("province_code")));
                list.add(province);
            }while(cursor.moveToNext());

        }

        if (cursor == null)
        {
            cursor.close();
        }

        return list;
    }

    /**
     * @func ��db�ж�ȡĳ��ʡ�ݵĳ�����Ϣ
     * @return ĳ��ʡ�ݵĳ��������б�
     */
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<City>();

        Log.d("DEBUG", "load cities " + provinceId);

        // �˴������ԶԲ�ѯ������������

        Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        Log.d("DEBUG", "load curosr " + provinceId);
        if(cursor.moveToFirst())
        {
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor
                        .getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor
                        .getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while(cursor.moveToNext());
        }

        if (cursor == null)
        {
            cursor.close();
        }

        return list;
    }


    /**
     * @func ��db�ж�ȡĳ�����е�������Ϣ
     * @return ĳ�����е����������б�
     */
    public List<Country> loadCounties(int cityId){
        List<Country> list = new ArrayList<Country>();

        // �˴������ԶԲ�ѯ������������
        Cursor cursor = db.query("Country", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
        if(cursor.moveToFirst())
        {
            do{
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryName(cursor.getString(cursor
                        .getColumnIndex("country_name")));
                country.setCountryCode(cursor.getString(cursor
                        .getColumnIndex("country_code")));
                country.setCityId(cityId);
                list.add(country);
            }while(cursor.moveToNext());
        }

        if (cursor == null)
        {
            cursor.close();
        }

        return list;
    }

    // �����������ʱ��֪����ʲô��˼
    public synchronized static CoolWeatherDB getInstance(Context context){
        if (coolWeatherDB == null){
            coolWeatherDB = new CoolWeatherDB(context);
        }

        return coolWeatherDB;

    }







}
