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
import com.studyco.Assignments.CreateAssignmentActivity;
import com.studyco.Dashboard.DashboardActivity;
import com.studyco.Databases.ProjectDatabase;
import com.studyco.Databases.RememberCardDatabase;
import com.studyco.R;
import com.studyco.RememberCards.RememberCardExpanded;
import com.studyco.UserMonitor.IdentifierDatabase;
import com.studyco.UserMonitor.InformServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateProjectActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    String url = "https://manadoma.com/study_app/project";


    InformServer informServer;
    String ActivityTag = "Create Project Activity";

    TextView cancel;
    TextView save;

    EditText projectName;
    EditText projectDeadline;
    EditText projectHours;
    EditText projectNotes;

    String s1 = null;
    String s2 = null;
    String s3 = null;
    String s4 = null;

    String user_id;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_project);

        mQueue = Volley.newRequestQueue(this);

        // get user id from db

        IdentifierDatabase id = new IdentifierDatabase(this);
        Cursor cursor = id.allData();

        if(cursor.moveToFirst())
        {
            cursor.moveToFirst();
            user_id = cursor.getString(0);
        }

        //  ' Inform server ' initialization

        informServer = new InformServer(getApplicationContext());


        cancel = (TextView)findViewById(R.id.project_cancel);
        save = (TextView)findViewById(R.id.project_save);

        projectName = (EditText)findViewById(R.id.edit_enter_name);
        projectDeadline= (EditText)findViewById(R.id.edit_enter_deadline);
        projectHours= (EditText)findViewById(R.id.edit_enter_hours);
        projectNotes= (EditText)findViewById(R.id.edit_enter_notes);

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
                new DatePickerDialog(CreateProjectActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s1 = projectName.getText().toString();
                s2 = projectDeadline.getText().toString();
                s3 = projectHours.getText().toString();
                s4 = projectNotes.getText().toString();

                // set up a flag to check if the data already exists in database

                boolean emptyTitleFlag = false;
                boolean repeatTitleFlag = false;
                boolean emptyDeadlineFlag = false;

                // check if the title is empty

                if(s1.equals(""))
                    emptyTitleFlag=true;


                // check if the assignment name already exists

                ProjectDatabase projectDatabase = new ProjectDatabase(CreateProjectActivity.this);
                Cursor cursor = projectDatabase.allData();
                if (cursor.moveToFirst())
                {
                    cursor.moveToFirst();
                    for(int i=0; i<cursor.getCount();i++)
                    {
                        if(cursor.getString(0).equals(s1))
                            repeatTitleFlag = true;
                    }
                }
                // check if the deadline is empty

                if(s2.equals(""))
                    emptyDeadlineFlag=true;


                if(emptyTitleFlag || repeatTitleFlag || emptyDeadlineFlag)
                {
                    if(emptyTitleFlag)
                        Snackbar.make(findViewById(android.R.id.content), "Project title can't be empty ", Snackbar.LENGTH_LONG).show();

                    else if(repeatTitleFlag)
                        Snackbar.make(findViewById(android.R.id.content), "Project name already exists ", Snackbar.LENGTH_LONG).show();
                    else if(emptyDeadlineFlag)
                        Snackbar.make(findViewById(android.R.id.content), "Deadline can't be empty", Snackbar.LENGTH_LONG).show();

                }
                else {

                    sendProjectInfoToServer(user_id,s1,s3,s2,s4 , "false");
                }

            }
        });

        // cancel button

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateProjectActivity.this, DashboardActivity.class));
                overridePendingTransition(R.animator.enter,R.animator.exit);
            }
        });
    }
    public void sendProjectInfoToServer( String userid,String title , String hours ,String deadline,String note, String completed) {
        mQueue = Volley.newRequestQueue(this);


        if (hours.equals(""))
            hours = "0";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid",userid);
            jsonObject.put("title", title);
            jsonObject.put("hours", Integer.parseInt(hours));
            jsonObject.put("deadline",  deadline);
            jsonObject.put("note",  note);
            jsonObject.put("completed", Boolean.parseBoolean(completed) );

            // sent data review

            System.out.println(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);

                        try {
                            String success = response.getString("success");

                            if(success.equals("true"))
                            {
                                // Get the project id

                                String project_id = response.getString("project_id");

                                // User monitor

                                informServer.informServerMethod(ActivityTag, "Project Created" + " -> " + "[ " +s1 + " ]" );

                                // save project to database

                                String incom = getResources().getString(R.string.incomplete);

                                ProjectDatabase projectDatabase = new ProjectDatabase(getApplicationContext());
                                projectDatabase.insertData(s1, s2, s3, s4,incom,project_id);

                                // redirect the user

                                startActivity(new Intent(CreateProjectActivity.this, ProjectActivity.class));
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
