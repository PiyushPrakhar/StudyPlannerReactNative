package com.studyco.Exams;

import android.content.Context;
import android.database.Cursor;

import com.studyco.Databases.SubjectDatabase;

import java.util.ArrayList;
import java.util.Scanner;

public class MainClassPlannerBot {
    static int totalSubjects;
    static ArrayList<ExamBotCalculate.Subject> allSubjectsList ;
    static  ArrayList<ExamBotCalculate.Subject> dummyList;
    ArrayList<ExamBotCalculate.Subject> finalList ;
    ArrayList<ArrayList<ExamBotCalculate.Subject>> finalListTwo ;

    long totalDaysOnCalendar;


    public ArrayList<ExamBotCalculate.Subject> callBot(ArrayList<String> chosenSubjectsList ,long totalDaysOnCalendar, Context c){
        ExamBotCalculate examBotCalculate = new ExamBotCalculate();

        // fill the all subjects list

        allSubjectsList = new ArrayList<>();
        dummyList = new ArrayList<>();

        // add elements to the subject list

        SubjectDatabase subjectDatabase = new SubjectDatabase(c);
        Cursor cursor = subjectDatabase.allData();

        for (String s : chosenSubjectsList) {
            cursor.moveToFirst();
            for( int i=0;i<cursor.getCount();i++){
                if(cursor.getString(0).equals(s))
                {
                    allSubjectsList.add(new ExamBotCalculate.Subject(s,Integer.parseInt(cursor.getString(1))));
                }
                cursor.moveToNext();
            }
        }

        // Enter the total number of days

        this.totalDaysOnCalendar = totalDaysOnCalendar;

        // Enter the total number of subjects

        totalSubjects = chosenSubjectsList.size();

        // Dummy data

        dummyList.add( new ExamBotCalculate.Subject("A",4));
        dummyList.add( new ExamBotCalculate.Subject("B",3));
        dummyList.add( new ExamBotCalculate.Subject("C",2));
        dummyList.add( new ExamBotCalculate.Subject("D",2));
        dummyList.add( new ExamBotCalculate.Subject("E",3));
        dummyList.add( new ExamBotCalculate.Subject("F",3));

        // Enter the option number

        System.out.println(" total days on calendar is :"+ totalDaysOnCalendar + " total subjects are : "+ totalSubjects);

        finalList = examBotCalculate.consistentStudy(totalDaysOnCalendar, totalSubjects, allSubjectsList);

        return finalList;
    }

    public ArrayList<ArrayList<ExamBotCalculate.Subject>> callBotShift(ArrayList<String> chosenSubjectsList ,long totalDaysOnCalendar, Context c){

        ExamBotCalculate examBotCalculate = new ExamBotCalculate();

        // fill the all subjects list

        allSubjectsList = new ArrayList<>();
        dummyList = new ArrayList<>();

        // add elements to the subject list

        SubjectDatabase subjectDatabase = new SubjectDatabase(c);
        Cursor cursor = subjectDatabase.allData();

        for (String s : chosenSubjectsList) {
            cursor.moveToFirst();
            for( int i=0;i<cursor.getCount();i++){
                if(cursor.getString(0).equals(s))
                {
                    allSubjectsList.add(new ExamBotCalculate.Subject(s,Integer.parseInt(cursor.getString(1))));
                }
                cursor.moveToNext();
            }
        }

        // Enter the total number of days

        this.totalDaysOnCalendar = totalDaysOnCalendar;

        // Enter the total number of subjects

        totalSubjects = chosenSubjectsList.size();

        // Dummy data

        dummyList.add( new ExamBotCalculate.Subject("A",4));
        dummyList.add( new ExamBotCalculate.Subject("B",3));
        dummyList.add( new ExamBotCalculate.Subject("C",2));
        dummyList.add( new ExamBotCalculate.Subject("D",2));
        dummyList.add( new ExamBotCalculate.Subject("E",3));
        dummyList.add( new ExamBotCalculate.Subject("F",3));

        System.out.println(" total days on calendar is :"+ totalDaysOnCalendar + " total subjects are : "+ totalSubjects);

        finalListTwo = examBotCalculate.consistentStudyTwoShift( totalDaysOnCalendar, totalSubjects, allSubjectsList);

        return finalListTwo;
    }



}
