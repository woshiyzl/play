package com.play.proxy;

public class UserImpl implements IUser{
    private String name;


    @Override
    public void setUserName(String name) {
        this.name = name;
    }

    @Override
    public String getUserName() {
        return this.name;
    }
}
