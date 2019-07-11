package com.studyco.LinearTracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.studyco.Assignments.CreateAssignmentActivity;
import com.studyco.Dashboard.DashboardActivity;
import com.studyco.Databases.ExamScheduleDatabase;
import com.studyco.Databases.LinearTrackerDatabase;
import com.studyco.Databases.SubjectDatabase;
import com.studyco.DatePickerWithHeader;
import com.studyco.Exams.ExamPlannerActivity;
import com.studyco.R;
import com.studyco.UserMonitor.InformServer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LinearTrackerActivity extends AppCompatActivity implements RecyclerAdapterLinearTracker.ItemClickListener{

    InformServer informServer;
    String ActivityTag = "Linear Tracker Activity";

    RecyclerAdapterLinearTracker adapter;
    ArrayList<String> subjectList = new ArrayList<>();
    ArrayList<Integer> entryList = new ArrayList<>();
    ArrayList<Long> startDateList = new ArrayList<>();
    ArrayList<Long> endDateList = new ArrayList<>();
    ArrayList<Long> difficultyList = new ArrayList<>();
    private DateRangeCalendarView calendar;
    long totalDaysOnCalendar;
    List<Date> dates;
    RadioGroup ll;
    Long startTime;
    Long endTime;
    Long diffLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_tracker);

        getSupportActionBar();
        getActionBar();

        //  ' Inform server ' initialization

        informServer = new InformServer(getApplicationContext());


        //Database fetch

        LinearTrackerDatabase db  = new LinearTrackerDatabase(this);
        Cursor cursor = db.allData();
        if(cursor.moveToFirst()){
            cursor.moveToFirst();

            for(int i =0;i<cursor.getCount();i++)
            {
                entryList.add(i+1);
                subjectList.add(cursor.getString(0));
                startDateList.add(cursor.getLong(1));
                endDateList.add(cursor.getLong(2));
                difficultyList.add((cursor.getLong(3)));
                cursor.moveToNext();

            }
        }
        else
        {
            RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_subject);

        }


        // Display the contents of the recycler view adapter on LinearTrackerActivity ( Recycler view )

        RecyclerView recyclerView = findViewById(R.id.recycler_subject);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapterLinearTracker(this, entryList,subjectList,startDateList,
                endDateList, difficultyList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


        // floating button
        FloatingActionButton floatingActionButton =(FloatingActionButton)findViewById(R.id.floating_button_linear_tracking);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog();
            }
        });

    }
    @Override
    public void onItemClick(View view, int position) {
        LinearTrackerDatabase db = new LinearTrackerDatabase(this);
        Cursor cursor = db.allData();
        if(view.getId() == R.id.row_linear_tracking_delete)
        {
            if( cursor.moveToPosition(position))
            {
                cursor.moveToPosition(position);
                db.deleteData(cursor.getString(0));

                // update recycler
                subjectList.remove(position);
                entryList.remove(position);
                startDateList.remove(position);
                endDateList.remove(position);
                difficultyList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyDataSetChanged();
            }
        }
        else
        {

        }


    }

    @Override
    public void onLongItemClick(View v, int position) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        overridePendingTransition(R.animator.enter,R.animator.exit);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.linear_tracker, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.instruction_linear_track) {
            // show a dialog with instructions
            AlertDialog dialog = new AlertDialog.Builder(this).setMessage(getText(R.string.linear_tracker_instruction))
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
    protected void showCalendarDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(LinearTrackerActivity.this);
        View promptView = layoutInflater.inflate(R.layout.linear_tracking_calendar_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LinearTrackerActivity.this);
        alertDialogBuilder.setView(promptView);

        final AlertDialog alert = alertDialogBuilder.create();
        alertDialogBuilder.setCancelable(true);


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

                startTime = startDate.getTimeInMillis();
                endTime = endDate.getTimeInMillis();
                diffLong = endTime - startTime;
                System.out.println(diffLong);


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

                    Toast.makeText(getApplicationContext(),"No schedule range chosen",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    // show a dialog to pick a subject
                    alert.dismiss();
                    selectAnySubjectDialog();

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
    protected void selectAnySubjectDialog() {

        // set up the dialog

        LayoutInflater layoutInflater = LayoutInflater.from(LinearTrackerActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.linear_tracking_radio_subject, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LinearTrackerActivity.this);

        Button calculate = (Button)promptView.findViewById(R.id.exam_planner_subject_list_proceed);
        final RadioGroup radioGroup = (RadioGroup)promptView.findViewById(R.id.radiogroup);

        alertDialogBuilder.setView(promptView);

        final AlertDialog alert = alertDialogBuilder.create();
        alertDialogBuilder.setCancelable(true);

        // Extract all subjects name from database

        SubjectDatabase subjectDatabase = new SubjectDatabase(this);
        Cursor cursor = subjectDatabase.allData();

        // set the radiobutton

        if(cursor.moveToFirst())
        {
            addRadioButtons(promptView,cursor.getCount(),cursor);
        }




        // calculate button

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alert.dismiss();

                SubjectDatabase subjectDatabase = new SubjectDatabase(LinearTrackerActivity.this);
                Cursor cursor = subjectDatabase.allData();



                if(cursor.moveToFirst()) {

                    // User monitor
                    informServer.informServerMethod(ActivityTag, "Subject track added ");

                    // get the selected radio button
                    Integer id  = ll.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton)promptView.findViewById(id);

                    String chosenSubject =null;

                    try {
                        chosenSubject = radioButton.getText().toString();
                        // Save the data

                        if(!chosenSubject.isEmpty() ) {

                            // check if the subject already exists

                            boolean duplicate = false;
                            LinearTrackerDatabase lndb = new LinearTrackerDatabase(LinearTrackerActivity.this);
                            Cursor duplicateCursor = lndb.allData();
                            duplicateCursor.moveToFirst();
                            for(int i=0;i<duplicateCursor.getCount();i++)
                            {
                                if(chosenSubject.equals(duplicateCursor.getString(0)))
                                    duplicate =true;
                                duplicateCursor.moveToNext();

                            }
                            if(!duplicate)
                            {
                                // save the data into database

                                lndb.insertData(chosenSubject,startTime,endTime,diffLong);

                                // update the recycler

                                subjectList.add(chosenSubject);
                                entryList.add(duplicateCursor.getCount()+ 1 );
                                startDateList.add(startTime);
                                endDateList.add(endTime);
                                difficultyList.add(diffLong);
                                adapter.notifyItemInserted(duplicateCursor.getCount());
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Snackbar.make(findViewById(android.R.id.content),
                                        " This subject already exists   ", Snackbar.LENGTH_LONG).show();
                            }

                        } else {

                            Snackbar.make(findViewById(android.R.id.content),
                                    " You haven't selected any subject  ", Snackbar.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e)
                    {
                        Snackbar.make(findViewById(android.R.id.content),
                                " You haven't selected any subject  ", Snackbar.LENGTH_LONG).show();
                    }

                }

            }
        });


        alert.show();

    }
    public void addRadioButtons(View prompt , int number , Cursor cursor) {

        for (int row = 0; row < 1; row++) {
            ll  = new RadioGroup(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            cursor.moveToFirst();
            Typeface typeface = Typeface.createFromAsset(getAssets(), "lato_regular.ttf");
            for (int i = 1; i <= number; i++) {
                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setId( 200+i);
                rdbtn.setText(cursor.getString(0));
                ll.addView(rdbtn);
                rdbtn.setTypeface(typeface);
                cursor.moveToNext();
            }
            ((ViewGroup) prompt.findViewById(R.id.radiogroup)).addView(ll);
        }
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
}
