package com.studyco.Exams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.studyco.Databases.ExamScheduleDatabase;
import com.studyco.Databases.SubjectDatabase;
import com.studyco.MainViewController;
import com.studyco.R;
import com.studyco.UserMonitor.InformServer;
import com.studyco.billing.BillingManager;
import com.studyco.billing.BillingProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamPlannerActivity extends AppCompatActivity implements ExamPlannerRecyclerAdapter.ItemClickListener,
        BillingProvider {

    private BillingManager mBillingManager;

    InformServer informServer;
    String ActivityTag = "Exam Planner Activity";
    List<Date> dates;

    private DateRangeCalendarView calendar;
    long totalDaysOnCalendar;
    ExamPlannerRecyclerAdapter adapter;
    ArrayList<String> subjectNamesListOne = new ArrayList<>();
    ArrayList<String> subjectNamesListTwo = new ArrayList<>();
    ArrayList<Integer> subjectDifficultyListOne =new ArrayList<>();
    ArrayList<Integer> subjectDifficultyListTwo =new ArrayList<>();
    ArrayList<String> subjectDatesList = new ArrayList<>();
    ArrayList<ExamBotCalculate.Subject> finalList = new ArrayList<>();
    ArrayList<ArrayList<ExamBotCalculate.Subject>> finalListShift = new ArrayList<>();

    RadioButton radioButtonOne ;
    RadioButton radioButtonTwo;
    RadioGroup radioGroup;

    FloatingActionButton create;

    boolean twoShifts ;

    Context context ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_planner_activity);

        getSupportActionBar();

        //  ' Inform server ' initialization

        informServer = new InformServer(getApplicationContext());


        context = this;

        // handle floating create button

        create  = (FloatingActionButton)findViewById(R.id.create_fab);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog();
            }
        });

        // show the previous schedule

        ExamScheduleDatabase db = new ExamScheduleDatabase(this);
        Cursor cursor = db.allData();

        if(cursor.moveToFirst())
        {

            // show the create fab

            //create.setVisibility(View.VISIBLE);

            // Display the contents of the recycler view adapter on ( Recycler view )

            RecyclerView recyclerView = findViewById(R.id.calendar_recycler_view);
            int numberOfColumns = 4;
            recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

            // fill the lists

            ArrayList<String> savedNamesOne = new ArrayList<>();
            ArrayList<String> savedNamesTwo = new ArrayList<>();
            ArrayList<Integer> savedDiffOne = new ArrayList<>();
            ArrayList<Integer> savedDiffTwo = new ArrayList<>();
            ArrayList<String> savedDatesList = new ArrayList<>();

            cursor.moveToFirst();

            for(int i=0;i<cursor.getCount();i++)
            {
                savedNamesOne.add(cursor.getString(0));
                savedNamesTwo.add(cursor.getString(1));
                savedDiffOne.add(cursor.getInt(2));
                savedDiffTwo.add(cursor.getInt(3));
                savedDatesList.add(cursor.getString(4));

                cursor.moveToNext();
            }



            if(adapter!=null)
                adapter=null;
           adapter = new ExamPlannerRecyclerAdapter(context,savedNamesOne,savedNamesTwo,savedDiffOne,
                   savedDiffTwo,
                   savedDatesList);

            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);

            //  Set up the drag and drop

            ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);

        }
    }


    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onLongItemClick(View v, int position) {

    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isLinearTrackSubscribed() {
        return false;
    }

    @Override
    public boolean isPlannerAiSubscribed() {
        return false;
    }

    @Override
    public boolean isSoothingMusicSubscribed() {
        return false;
    }

    private class CallPlannerBot extends AsyncTask<Void, Void, ArrayList<ExamBotCalculate.Subject>>implements ExamPlannerRecyclerAdapter.ItemClickListener {

        ArrayList<String> chosenSubjectsList;

        public CallPlannerBot(ArrayList<String> chosenSubjectList) {
            this.chosenSubjectsList = chosenSubjectList;

        }
        @Override
        protected ArrayList<ExamBotCalculate.Subject> doInBackground(Void... params) {
            MainClassPlannerBot mainClassPlannerBot = new MainClassPlannerBot();
            finalList = mainClassPlannerBot.callBot(chosenSubjectsList,totalDaysOnCalendar,getApplicationContext());
            return finalList;
        }
        @Override
        protected void onPostExecute(ArrayList<ExamBotCalculate.Subject> result) {

            // fill in the data


            for(ExamBotCalculate.Subject subject : finalList) {
                subjectNamesListOne.add(subject.subName);
                subjectNamesListTwo.add(" - ");
            }

            // fill in the diff list

            SubjectDatabase su = new SubjectDatabase(getApplicationContext());
            Cursor cursor = su.allData();
            for (String s :subjectNamesListOne) {
                cursor.moveToFirst();
                for(int i=0;i<cursor.getCount();i++)
                {
                    if(s.equals(cursor.getString(0)))
                    {
                        subjectDifficultyListOne.add(cursor.getInt(1));
                    }
                    cursor.moveToNext();
                }

            }
            for (String s :subjectNamesListTwo) {
                cursor.moveToFirst();
                if(s.equals(" - "))
                {
                    subjectDifficultyListTwo.add(0);
                    cursor.moveToNext();
                }
                else
                {
                    for(int i=0;i<cursor.getCount();i++)
                    {
                        if(s.equals(cursor.getString(0)))
                        {
                            subjectDifficultyListTwo.add(cursor.getInt(1));
                        }
                        cursor.moveToNext();
                    }
                }

            }


            for(Date date : dates){
                String sub;
                sub = date.toString();
                sub = sub.substring(0,10);
                subjectDatesList.add(sub);

            }

            System.out.println(" Subject names list");

            for (String s : subjectNamesListOne)
                System.out.println(s);

            System.out.println(" Subject dates list");

            for (String s : subjectDatesList)
                System.out.println(s);

            // Display the contents of the recycler view

            ViewGroup vg = findViewById (R.id.exam_planner_main_view);
            vg.invalidate();

            RecyclerView recyclerView = findViewById(R.id.calendar_recycler_view);
            int numberOfColumns = 4;
            recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

          /*  System.out.println("Subject names list ");

            for(String s : subjectNamesListOne)
                System.out.println(s);

            System.out.println("Subject dates list ");

            for(String s : subjectDatesList)
                System.out.println(s);

                */

            if(adapter!=null)
                adapter=null;
            adapter = new ExamPlannerRecyclerAdapter(context, subjectNamesListOne,subjectNamesListTwo,
                    subjectDifficultyListOne,subjectDifficultyListTwo,subjectDatesList);

            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);

            // save the data

            saveScheduleState(subjectNamesListOne,subjectNamesListTwo,subjectDifficultyListOne,
                    subjectDifficultyListTwo,subjectDatesList);

            //  Set up the drag and drop

            ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);

            finish();
            startActivity(getIntent());
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        public void onItemClick(View view, final int position) {

            // show dialog which lets the user change the subject

            AlertDialog.Builder builder1 = new AlertDialog.Builder(ExamPlannerActivity.this);
            builder1.setMessage("Change subject for this date ?");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Yes ",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            chooseSubjectDialog(position);
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();



        }

        @Override
        public void onLongItemClick(View v, int position) {

        }
    }

    private class CallPlannerBotShifts extends AsyncTask<Void, Void, ArrayList<ArrayList<ExamBotCalculate.Subject>>>implements ExamPlannerRecyclerAdapter.ItemClickListener {

        ArrayList<String> chosenSubjectsList;

        public CallPlannerBotShifts(ArrayList<String> chosenSubjectList) {
            this.chosenSubjectsList = chosenSubjectList;

        }

        @Override
        protected ArrayList<ArrayList<ExamBotCalculate.Subject>> doInBackground(Void... params) {
            MainClassPlannerBot mainClassPlannerBot = new MainClassPlannerBot();
            finalListShift = mainClassPlannerBot.callBotShift(chosenSubjectsList,totalDaysOnCalendar,getApplicationContext());
            return finalListShift;
        }
        @Override
        protected void onPostExecute(ArrayList<ArrayList<ExamBotCalculate.Subject>> result) {

            // fill in the data

          for(int i=0;i<result.size();i++)
          {
              subjectNamesListOne.add(result.get(i).get(0).subName);
          }

            for(int i=0;i<result.size();i++)
            {
                try
                {
                    subjectNamesListTwo.add(result.get(i).get(1).subName);
                }
                catch (Exception e )
                {

                    subjectNamesListTwo.add( " - ");
                }

            }

            // fill in the diff list

            SubjectDatabase su = new SubjectDatabase(getApplicationContext());
            Cursor cursor = su.allData();
            for (String s :subjectNamesListOne) {
                cursor.moveToFirst();
                for(int i=0;i<cursor.getCount();i++)
                {
                    if(s.equals(cursor.getString(0)))
                    {
                        subjectDifficultyListOne.add(cursor.getInt(1));
                    }
                    cursor.moveToNext();
                }

            }
            for (String s :subjectNamesListTwo) {
                cursor.moveToFirst();
                if(s.equals(" - "))
                {
                    subjectDifficultyListTwo.add(0);
                    cursor.moveToNext();
                }
                else
                {
                    for(int i=0;i<cursor.getCount();i++)
                    {
                        if(s.equals(cursor.getString(0)))
                        {
                            subjectDifficultyListTwo.add(cursor.getInt(1));
                        }
                        cursor.moveToNext();
                    }
                }


            }


            for(Date date : dates){
                String sub;
                sub = date.toString();
                sub = sub.substring(0,10);
                subjectDatesList.add(sub);

            }

            System.out.println(" Subject names list One");

            for (String s : subjectNamesListOne)
                System.out.println(s);

            System.out.println(" Subject names list Two");

            for (String s : subjectNamesListTwo)
                System.out.println(s);

            System.out.println(" Subject dates list");

            for (String s : subjectDatesList)
                System.out.println(s);


            // Display the contents of the recycler view

            RecyclerView recyclerView = findViewById(R.id.calendar_recycler_view);
            int numberOfColumns = 4;
            recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

          /*  System.out.println("Subject names list ");

            for(String s : subjectNamesListOne)
                System.out.println(s);

            System.out.println("Subject dates list ");

            for(String s : subjectDatesList)
                System.out.println(s);

                */
            adapter = new ExamPlannerRecyclerAdapter(context, subjectNamesListOne, subjectNamesListTwo,subjectDifficultyListOne,
                    subjectDifficultyListTwo,subjectDatesList);

            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);

            // save the data

            saveScheduleState(subjectNamesListOne,subjectNamesListTwo,subjectDifficultyListOne
                    ,subjectDifficultyListTwo,subjectDatesList);


            //  Set up the drag and drop

            ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);

            finish();
            startActivity(getIntent());

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        public void onItemClick(View view, final int position) {

            // show dialog which lets the user change the subject

            AlertDialog.Builder builder1 = new AlertDialog.Builder(ExamPlannerActivity.this);
            builder1.setMessage("Change subject for this date ? ");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Yes ",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            chooseSubjectDialog(position);
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }

        @Override
        public void onLongItemClick(View v, int position) {

        }
    }

    public void chooseSubjectDialog(final int position)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(ExamPlannerActivity.this);
        View promptView = layoutInflater.inflate(R.layout.custom_single_choice_header, null);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ExamPlannerActivity.this);
        builderSingle.setCustomTitle(promptView);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ExamPlannerActivity.this, R.layout.single_choice_item);

        SubjectDatabase db = new SubjectDatabase(this);
        Cursor cursor = db.allData();
        if(cursor.moveToFirst())
        {
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++)
            {
                arrayAdapter.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                // update the list

                subjectNamesListOne.set(position,strName);
                adapter.notifyDataSetChanged();

                // update the exam planner db
                ExamScheduleDatabase database = new ExamScheduleDatabase(getApplicationContext());

                database.deleteAll();

                for(int i = 0; i < subjectNamesListOne.size(); i++)
                    database.insertData(subjectNamesListOne.get(i),subjectNamesListTwo.get(i),subjectDifficultyListOne.get(i)
                            ,subjectDifficultyListTwo.get(i),
                            subjectDatesList.get(i));

               dialog.dismiss();
            }
        });
        builderSingle.show();
    }
    protected void showCalendarDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(ExamPlannerActivity.this);
        View promptView = layoutInflater.inflate(R.layout.exam_planner_calendar_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ExamPlannerActivity.this);
        alertDialogBuilder.setView(promptView);

        final AlertDialog alert = alertDialogBuilder.create();
        alertDialogBuilder.setCancelable(true);

        // radio buttons

        radioButtonOne = (RadioButton)promptView.findViewById(R.id.radioButtonOne);
        radioButtonTwo =(RadioButton)promptView.findViewById(R.id.radioButtonTwo);
        radioGroup=(RadioGroup)promptView.findViewById(R.id.numberOfSubjectRadio);


        // calendar test
        calendar = promptView.findViewById(R.id.calendar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.calendar_font));
        calendar.setFonts(typeface);
        calendar.resetAllSelectedViews();

        calendar.setCalendarListener(new DateRangeCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar startDate) {

            }

            @Override
            public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
                long difference  = endDate.getTimeInMillis() - startDate.getTimeInMillis();
                totalDaysOnCalendar= TimeUnit.MILLISECONDS.toDays(difference)+1;
                System.out.println("Total number of days are "+ totalDaysOnCalendar);


                 dates = getDates(startDate.getTime() , endDate.getTime());
                for(Date date:dates)
                    System.out.println( date);
            }

        });

        // calendar.setNavLeftImage(ContextCompat.getDrawable(this,R.drawable.ic_left));
        // calendar.setNavRightImage(ContextCompat.getDrawable(this,R.drawable.ic_right));

        promptView.findViewById(R.id.calendar_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dates == null)
                {
                    // User monitor
                    informServer.informServerMethod(ActivityTag,"PlannerBot fail , No date range ");

                    Toast.makeText(getApplicationContext(),"No date range chosen",Toast.LENGTH_SHORT).show();

                }

                // create a new dialog showing the subjects to be added

                else
                {
                    // Radio buttons

                    int selectedId =0;
                    if(radioButtonOne.isChecked())
                        selectedId=R.id.radioButtonOne;
                    else if(radioButtonTwo.isChecked())
                        selectedId=R.id.radioButtonTwo;

                    if(selectedId==R.id.radioButtonOne) {
                        twoShifts = false;
                        showSubjectsDialog();
                        alert.dismiss();
                    }
                    else if(selectedId==R.id.radioButtonTwo){
                        twoShifts=true;
                        showSubjectsDialog();
                        alert.dismiss();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Choose one or two subject a day",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, -2);
        Calendar later = (Calendar) now.clone();
        later.add(Calendar.MONTH, 5);

        calendar.setVisibleMonthRange(now,later);

        Calendar startSelectionDate = Calendar.getInstance();
        startSelectionDate.add(Calendar.MONTH, 0);
        Calendar endSelectionDate = (Calendar) startSelectionDate.clone();
        endSelectionDate.add(Calendar.DATE, 10);

        // calendar.setSelectedDateRange(startSelectionDate, endSelectionDate);

        Calendar current = Calendar.getInstance();
        calendar.setCurrentMonth(current);

        // calendar test end

        alert.show();
    }

    protected void showSubjectsDialog() {

        // set up the dialog

        LayoutInflater layoutInflater = LayoutInflater.from(ExamPlannerActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.exam_planner_subjects_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ExamPlannerActivity.this);

        Button calculate = (Button)promptView.findViewById(R.id.exam_planner_subject_list_proceed);

        alertDialogBuilder.setView(promptView);

        final AlertDialog alert = alertDialogBuilder.create();
        alertDialogBuilder.setCancelable(true);

        // Extract all subjects name from database

        SubjectDatabase subjectDatabase = new SubjectDatabase(this);
        Cursor cursor = subjectDatabase.allData();

        // set the checkboxes

        LinearLayout linearLayout = (LinearLayout)promptView.findViewById(R.id.exam_planner_subjects_check);

        if(cursor.moveToFirst())
        {
            cursor.moveToFirst();
            for( int i=0; i<cursor.getCount();i++)
            {
                int id =100+i;
                CheckBox checkBox = new CheckBox(ExamPlannerActivity.this);
                checkBox.setText(cursor.getString(0));

                Typeface typeface = Typeface.createFromAsset(getAssets(), "lato_regular.ttf");
                checkBox.setTypeface(typeface);

                linearLayout.addView(checkBox);
                checkBox.setId(id);
                cursor.moveToNext();
            }
        }

        // calculate button

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alert.dismiss();

                SubjectDatabase subjectDatabase = new SubjectDatabase(ExamPlannerActivity.this);
                Cursor cursor = subjectDatabase.allData();



                if(cursor.moveToFirst()) {

                    // User monitor
                    informServer.informServerMethod(ActivityTag, "PlannerBot Pass");

                    // Subject list

                    ArrayList<String> chosenSubjectList = new ArrayList<>();

                    // get the checkbox stats;
                    for (int i = 0; i < cursor.getCount(); i++) {
                        int id = 100 + i;
                        CheckBox checkBox = (CheckBox) promptView.findViewById(id);
                        if (checkBox.isChecked())
                            chosenSubjectList.add(checkBox.getText().toString());

                    }


                    // call the exam planner bot Async class

                    if (!chosenSubjectList.isEmpty()) {

                        if (twoShifts)
                            new CallPlannerBotShifts(chosenSubjectList).execute();
                        else
                            new CallPlannerBot(chosenSubjectList).execute();
                    } else {

                        // User monitor

                        informServer.informServerMethod(ActivityTag, "PlannerBot fail , No subjects ");

                        Snackbar.make(findViewById(android.R.id.content),
                                " You haven't selected any subject  ", Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });


        alert.show();

    }

    private static List<Date> getDates(Date time1, Date time2)
    {
        ArrayList<Date> dates = new ArrayList<Date>();


        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(time1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(time2);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            // show a dialog with instructions
            AlertDialog dialog = new AlertDialog.Builder(this).setMessage(getText(R.string.planner_instruction))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveScheduleState(ArrayList<String> listOne , ArrayList<String> listTwo ,
            ArrayList<Integer> listDiffOne ,ArrayList<Integer> listDiffTwo, ArrayList<String> listDate)
    {
        ExamScheduleDatabase db = new ExamScheduleDatabase(getApplicationContext());
        Cursor c = db.allData();

        if(c.moveToFirst())
            db.deleteAll();


        if(listOne !=null)
        {
            for(int i = 0; i < listOne.size(); i++)
                db.insertData(listOne.get(i),listTwo.get(i),listDiffOne.get(i),listDiffTwo.get(i),listDate.get(i));

            Snackbar.make(findViewById(android.R.id.content), " Your schedule was saved  ", Snackbar.LENGTH_LONG).show();

        }
    }
}
