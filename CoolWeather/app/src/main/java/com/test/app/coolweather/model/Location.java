package com.test.app.coolweather.model;

/**
 * Created by luoqing on 2015/6/17.
 */
public class Location {
    private int id;
    private int type;  // province(1), city(2), country(3)
    private String name;
    private String code;
    private int parentId;

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
