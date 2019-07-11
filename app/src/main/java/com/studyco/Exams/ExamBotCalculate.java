package com.studyco.Exams;


import java.util.ArrayList;

public class ExamBotCalculate {


    /* Inputs - total number of days
     *        -total number of subjects
     *
     */
    public static class Subject
    {
        public String subName ;

        public Integer subDifficulty ;

        public Subject(String s, Integer d)
        {
            this.subName = s;
            this.subDifficulty = d;
        }
    }
    float  totalDaysOnCalendar;
    private float totalSubjects = 0 ;

    ArrayList<ArrayList<Subject>>  consistentStudyTwoShift(float totalDaysOnCalendar , int subjects , ArrayList<ExamBotCalculate.Subject> allSubjectsList) {

        this.totalDaysOnCalendar = totalDaysOnCalendar;
        this.totalSubjects = subjects;
        ArrayList<Subject> subfinalList = new ArrayList<>();
        ArrayList<ArrayList<Subject>> finalList = new ArrayList<>();

        ArrayList<Integer> consistentSequence = getConsistentSequence(totalDaysOnCalendar, subjects, allSubjectsList);

        System.out.println(consistentSequence);

        for (int i = 0; i < consistentSequence.size(); i++) {
            for (int m = 0; m < allSubjectsList.size(); m++) {
                if (consistentSequence.get(i) == allSubjectsList.get(m).subDifficulty) {
                    subfinalList.add(allSubjectsList.get(m));
                    allSubjectsList.remove(m);
                    break;
                }
            }
        }

        // 2 shifts

        int count = (int)totalSubjects/2;
        int index =0;

        if(totalSubjects%2==0) {
            for( int i=0;i<count;i++)
            {


                ArrayList<Subject> temp = new ArrayList<>();
                temp.add(subfinalList.get(index));
                temp.add(subfinalList.get(index+1));
                finalList.add(temp);

                index = index+2;
            }
        }
        else
        {
            for( int i=0;i<count+1;i++)
            {

                if(index+1 == subfinalList.size())
                {
                    ArrayList<Subject> temp = new ArrayList<>();
                    temp.add(subfinalList.get(index));
                    finalList.add(temp);

                    index = index+2;
                }
                else
                {
                    ArrayList<Subject> temp = new ArrayList<>();
                    temp.add(subfinalList.get(index));
                    temp.add(subfinalList.get(index+1));
                    finalList.add(temp);

                    index = index+2;
                }

            }
        }



        // 2 Shifts end

        ArrayList<ArrayList<Subject>>  finalListSaved = new ArrayList<>(finalList);
        int remainingDays;
        int x;

        if(totalSubjects%2==0)
        {
            remainingDays = (int)totalDaysOnCalendar - (int)(totalSubjects)/2;
            x = (int)(totalSubjects)/2;
        }
        else {
            remainingDays = (int) totalDaysOnCalendar - (int) (totalSubjects + 1) / 2;
            x = (int)(totalSubjects+1)/2;
        }


        int tempRemDays = remainingDays;


        if(tempRemDays >= x) {
            while (tempRemDays >= x) {
                finalList.addAll(finalListSaved);
                tempRemDays = tempRemDays - x;

            }
            for (int i = 0; i < tempRemDays; i++) {
                finalList.add(finalListSaved.get(i));
            }
        }
        else if(remainingDays <(int)x)
        {
            for (int i = 0; i < remainingDays; i++) {
                finalList.add(finalListSaved.get(i));
            }
        }

        // optimizing the shifts

        try {

            for (int i = 0; i < finalList.size()- 1; i = i + 2) {
                ArrayList<Subject> first = finalList.get(i);
                ArrayList<Subject> second = finalList.get(i + 1);

                Subject previous = first.get(1);
                Subject next = second.get(0);

           /* System.out.println(" previous subject is : " + previous.subName + "  - > "+ previous.subDifficulty);
            System.out.println(" next subject is : " + next.subName + "  - > "+ next.subDifficulty); */


                ArrayList<Subject> firstNew = new ArrayList<>();
                firstNew.add(finalList.get(i).get(0));
                firstNew.add(next);

                ArrayList<Subject> secondNew = new ArrayList<>();
                secondNew.add(previous);


                try {
                    if (second.size() == 1) {
                        if (previous.subDifficulty > next.subDifficulty) {
                            finalList.set(i, firstNew);
                            finalList.set(i + 1, secondNew);
                        }
                    }

                } catch (Exception e) {
                    System.out.println(" exception while optimizing " + e);
                }

            }
        }
        catch (Exception e )
        {
            System.out.println(e);
        }

        // printing

        for( int i=0;i<finalList.size();i++)
        {
            ArrayList<Subject> temp = finalList.get(i);
            for (Subject subject :temp) {
                System.out.println(subject.subName +" -> "+subject.subDifficulty);

            }
            System.out.println( "\n next item \n");

        }
        System.out.println("\n size of the array is : " + finalList.size());

        return finalList;
    }


    ArrayList<ExamBotCalculate.Subject> consistentStudy(float totalDaysOnCalendar , int subjects , ArrayList<ExamBotCalculate.Subject> allSubjectsList) {

        this.totalDaysOnCalendar = totalDaysOnCalendar;
        this.totalSubjects = subjects;
        ArrayList<Subject> finalList = new ArrayList<>();

        ArrayList<Integer> consistentSequence = getConsistentSequence(totalDaysOnCalendar, subjects, allSubjectsList);

        System.out.println(consistentSequence);

        for (int i = 0; i < consistentSequence.size(); i++) {
            for (int m = 0; m < allSubjectsList.size(); m++) {
                if (consistentSequence.get(i) == allSubjectsList.get(m).subDifficulty) {
                    finalList.add(allSubjectsList.get(m));
                    allSubjectsList.remove(m);
                    break;
                }
            }
        }

     // Fill the remaining dates


        ArrayList<Subject> finalListSaved = new ArrayList<>(finalList);
        int remainingDays = (int) totalDaysOnCalendar - (int) totalSubjects;
        int tempRemDays = remainingDays;

        if(tempRemDays >= (int)totalSubjects) {
            while (tempRemDays >= (int) totalSubjects) {
                finalList.addAll(finalListSaved);
                tempRemDays = tempRemDays - (int)totalSubjects;

            }
            if (tempRemDays < (int) totalSubjects) {
                for (int i = 0; i < tempRemDays; i++) {
                    finalList.add(finalListSaved.get(i));
                }
            }
        }
        else if(remainingDays <(int)totalSubjects)
        {
            for (int i = 0; i < remainingDays; i++) {
                finalList.add(finalListSaved.get(i));
            }
        }

        // printing

        System.out.println(" Final list , single subject ");
        for( Subject s:finalList)
            System.out.println("Subject name  -> " + s.subName + " Subject difficulty -> " + s.subDifficulty);


        return finalList;
    }

    int[] getMin(ArrayList<Integer> mainList)
    {
        int lowest = 10;
        int pos=10;
        for( int i=0 ; i<mainList.size();i++)
        {
            if(mainList.get(i)<lowest) {
                lowest = mainList.get(i);
                pos=i;
            }
        }
        int arr[] = {lowest,pos};
        return  arr;
    }
    int[] getMax(ArrayList<Integer> mainList)
    {
        int highest =0;
        int pos =10;
        for( int i =0;i<mainList.size();i++)
        {
            if(mainList.get(i)>highest) {
                highest = mainList.get(i);
                pos =i;
            }
        }
        int arr[] ={highest,pos};
        return arr;

    }
    int[] getMedium(ArrayList<Integer> mainList)
    {
        int medium =10;
        int high = getMax(mainList)[0];
        int low =getMin(mainList)[0];
        int pos=10;

        for(int i=0;i<mainList.size();i++)
        {
            if(mainList.get(i) >low )
                if(mainList.get(i)<high) {
                    medium = mainList.get(i);
                    pos =i;
                }


        }
        int arr[] = {medium,pos};
        return arr;

    }

    ArrayList<Integer> getConsistentSequence(float totalDaysOnCalendar , int subjects , ArrayList<ExamBotCalculate.Subject> allSubjectsList)
    {

        ArrayList<Integer> result = new ArrayList<>();

        ArrayList<Integer> mainList = new ArrayList<>();
        for(int i=0 ;i<allSubjectsList.size();i++)
            mainList.add(allSubjectsList.get(i).subDifficulty);

        int mid = getMedium(mainList)[0];
        int midIndex = getMedium(mainList)[1];
        if(mid==10) {
            mid = getMin(mainList)[0];
            midIndex=getMin(mainList)[1];
        }
        result.add(mid);
        mainList.remove(midIndex);


        System.out.println(" Mid is : " + mid);

        int count = mainList.size();
        for(int m=0;m<count;m++)
            if (result.size()!=0) {
                int diff = 0;
                int element = 0;
                int pos=0;
                for (int i = 0; i < mainList.size(); i++) {
                    int temp;
                    temp = Math.abs(result.get(result.size()-1) - mainList.get(i));
                    if (temp > diff) {
                        diff = temp;
                        element = mainList.get(i);
                        pos =i;

                    }

                }
                if(diff==0)
                {
                    while (!mainList.isEmpty())
                    {
                        result.add(mainList.get(0));
                        mainList.remove(0);
                    }
                }
                else
                {
                    result.add(element);
                    mainList.remove(pos);
                }


                System.out.println("Result list is : " + result + " \n");
                System.out.println("Main list is : " + mainList + " \n\n");

            }
            return result;
    }

}