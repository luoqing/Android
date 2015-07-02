package com.example.happyday.model;

/**
 * Created by luoqing on 2015/7/1.
 */
public class Location {
    private int id;
    private int type;  // PROVINCE_TYPE, CITY_TYPE, COUNTRY_TYPE
    private String name;
    private String code;
    private int parentId;

    public static final int PROVINCE_TYPE = 1;
    public static final int CITY_TYPE = 2;
    public static final int COUNTRY_TYPE = 3;

    public int getType(){
        return this.type;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getCode(){
        return this.code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public int getParentId(){
        return this.parentId;
    }

    public void setParentId(int parentId){
        this.parentId = parentId;
    }
}
