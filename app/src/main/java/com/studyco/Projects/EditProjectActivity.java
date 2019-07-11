package com.studyco.Projects;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.studyco.Assignments.EditAssignmentActivity;
import com.studyco.Dashboard.DashboardActivity;
import com.studyco.Databases.ProjectDatabase;
import com.studyco.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditProjectActivity extends AppCompatActivity {
    TextView cancel;
    TextView save;

    String strings[];

    private RequestQueue mQueue;
    String url = "https://manadoma.com/study_app/project";

    String INCOMP ;
    String COMP ;

    EditText projectName;
    EditText projectDeadline;
    EditText projectHours;
    EditText projectNotes;

    String s1 = null;
    String s2 = null;
    String s3 = null;
    String s4 = null;
    String s5 = null;
    String project_id =null;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_project);

        INCOMP =  getResources().getString(R.string.incomplete);
        COMP = getResources().getString(R.string.complete);

        mQueue = Volley.newRequestQueue(this);

        cancel = (TextView)findViewById(R.id.project_cancel);
        save = (TextView)findViewById(R.id.project_save);

        projectName = (EditText)findViewById(R.id.edit_enter_name);
        projectDeadline= (EditText)findViewById(R.id.edit_enter_deadline);
        projectHours= (EditText)findViewById(R.id.edit_enter_hours);
        projectNotes= (EditText)findViewById(R.id.edit_enter_notes);

        // disable

        projectName.setEnabled(false);

        // Get intent data

        Intent i = getIntent();
        strings = i.getStringArrayExtra("strings");

        // Set intent data

        projectName.setText(strings[0]);
        projectDeadline.setText(strings[1]);
        projectHours.setText(strings[2]);
        projectNotes.setText(strings[3]);


        myCalendar = Calendar.getInstance();

        date= new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        projectDeadline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditProjectActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // get project id

        ProjectDatabase projectDatabase = new ProjectDatabase(this);
        Cursor cursor = projectDatabase.allData();

        if(cursor.moveToFirst())
        {
            cursor.moveToFirst();

            for( int m=0;m<cursor.getCount();m++)
            {
                if( cursor.getString(0).equals(strings[0]))
                {
                    project_id = cursor.getString(5);
                }

                cursor.moveToNext();

            }
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s1 = projectName.getText().toString();
                s2 = projectDeadline.getText().toString();
                s3 = projectHours.getText().toString();
                s4 = projectNotes.getText().toString();
                s5 = strings[4];

                // check if the deadline is empty

                boolean emptyDeadlineFlag =false;
                if(s2.equals(""))
                    emptyDeadlineFlag=true;

                if (emptyDeadlineFlag)
                    Snackbar.make(findViewById(android.R.id.content), "Deadline can't be empty ", Snackbar.LENGTH_LONG).show();
                else
                    sendProjectInfoToServer(project_id,s1,s2,s3,s4);


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProjectActivity.this , DashboardActivity.class));
                overridePendingTransition(R.animator.enter,R.animator.exit);
            }
        });
    }

    public void sendProjectInfoToServer( String project_id , String title ,String deadline,  String hours , String note ) {
        mQueue = Volley.newRequestQueue(this);


        JSONObject jsonObject = new JSONObject();
        try {

            if (hours.equals(""))
                hours ="0";
            Integer pid = Integer.parseInt(project_id);
            Integer phours = Integer.parseInt(hours);

            jsonObject.put("project_id",pid);
            jsonObject.put("title", title);
            jsonObject.put("hours", phours);
            jsonObject.put("deadline",  deadline);
            jsonObject.put("note",  note);

            System.out.println(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("Server Response -> Project edited : " + response);

                        try {
                            String success = response.getString("success");

                            if(success.equals("true"))
                            {

                                // Save project to database

                                ProjectDatabase projectDatabase = new ProjectDatabase(EditProjectActivity.this);
                                projectDatabase.update_entry(s1,s2,s3,s4,s5);

                                startActivity(new Intent(EditProjectActivity.this , ProjectActivity.class));
                                overridePendingTransition(R.animator.enter,R.animator.exit);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(findViewById(android.R.id.content), "Unable to reach servers, check your connection", Snackbar.LENGTH_LONG).show();

                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Unable to reach servers, check your connection", Snackbar.LENGTH_LONG).show();


            }
        });
        mQueue.add(request);
    }
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        projectDeadline.setText(sdf.format(myCalendar.getTime()));
    }

}
