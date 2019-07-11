package com.studyco.UserMonitor;

import android.content.Context;
import android.database.Cursor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class InfoGenerator {
    Context c;

    public InfoGenerator(Context c )
    {
        this.c = c ;
    }

    public void saveUserString() {
        IdentifierDatabase database = new IdentifierDatabase(c);

        Cursor cursor = database.allData();

        if(!cursor.moveToFirst()){
            database.insertData(userStringGenerator());
        }

    }

    public String getUserString(){
        IdentifierDatabase database = new IdentifierDatabase(c);
        Cursor cursor = database.allData();

        if(cursor.moveToFirst()) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        else
            return null;
    }

    public String userStringGenerator() {

        String availableKeys = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < 32) {
            int index = (int) (rnd.nextFloat() * availableKeys.length());
            stringBuilder.append(availableKeys.charAt(index));
        }
        return stringBuilder.toString();

    }

    public String getApp(){
        return "Study Organizer";
    }

    public String getIpAddress()
    {
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("System IP Address : " +
                (localhost.getHostAddress()).trim());

        return localhost.getHostAddress();
    }

}
