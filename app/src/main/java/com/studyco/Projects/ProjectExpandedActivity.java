package com.studyco.Projects;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.studyco.Databases.ProjectDatabase;
import com.studyco.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ProjectExpandedActivity extends AppCompatActivity {

    EditText projectName;
    EditText projectDeadline;
    EditText projectHours;
    EditText projectNotes;

    String strings[];

    String project_id =null;
    Button completed_button;

    private RequestQueue mQueue;
    String url = "https://manadoma.com/study_app/project";

    String INCOMP ;
    String COMP ;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_expanded);

        INCOMP =  getResources().getString(R.string.incomplete);
        COMP = getResources().getString(R.string.complete);

        mQueue = Volley.newRequestQueue(this);

        projectName = (EditText)findViewById(R.id.edit_enter_name);
        projectDeadline= (EditText)findViewById(R.id.edit_enter_deadline);
        projectHours= (EditText)findViewById(R.id.edit_enter_hours);
        projectNotes= (EditText)findViewById(R.id.edit_enter_notes);

        // Get intent data

        Intent i = getIntent();
        strings = i.getStringArrayExtra("strings");


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

        // Set intent data

        projectName.setText(" Title : " +strings[0]);
        projectDeadline.setText(" Deadline : "+strings[1]);
        projectHours.setText(" Total hours : "+strings[2]);
        projectNotes.setText(" Notes : "+ strings[3]);

        // disable the edit text

        projectName.setEnabled(false);
        projectDeadline.setEnabled(false);
        projectHours.setEnabled(false);
        projectNotes.setEnabled(false);


        // Edit button

        Button edit_button = (Button)findViewById(R.id.project_edit);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProjectExpandedActivity.this , EditProjectActivity.class)
                        .putExtra("strings",strings));
            }
        });

        // Completed button

        completed_button = (Button)findViewById(R.id.project_completed);

        // set the state of the complete button

        if(strings[4].equals(INCOMP))
            completed_button.setText(COMP);
        else
            completed_button.setText(INCOMP);

        completed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(completed_button.getText().equals(INCOMP))
                    sendProjectInfoToServer(INCOMP);
                else
                    sendProjectInfoToServer(COMP);
            }
        });

        // Delete button

        ImageButton delete_button = (ImageButton)findViewById(R.id.project_delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProjectFromServer();
            }
        });

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ProjectActivity.class));
        overridePendingTransition(R.animator.enter,R.animator.exit);
    }

    public void deleteProjectFromServer() {
        mQueue = Volley.newRequestQueue(this);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("project_id",project_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // sent JSON

        System.out.println(jsonObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);

                        try {
                            String success = response.getString("success");

                            if(success.equals("true"))
                            {

                                ProjectDatabase projectDatabase = new ProjectDatabase(getApplicationContext());
                                Cursor cursor = projectDatabase.allData();
                                boolean flag = false;

                                if(cursor.moveToFirst())
                                {
                                    cursor.moveToFirst();
                                    for( int i=0 ; i<cursor.getCount() ; i++)
                                    {
                                        if(cursor.getString(0).equals(strings[0]))
                                        {
                                            projectDatabase.deleteData(strings[0]);
                                            flag = true;


                                        }
                                        cursor.moveToNext();
                                    }
                                }

                                if(flag)
                                {
                                    startActivity(new Intent(ProjectExpandedActivity.this, ProjectActivity.class));
                                    overridePendingTransition(R.animator.enter,R.animator.exit);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        mQueue.add(request);
    }

    public void sendProjectInfoToServer(final String completed) {

        mQueue = Volley.newRequestQueue(this);

        boolean x ;
        if(completed.equals(COMP))
            x= true;
        else
            x= false;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("project_id",project_id);
            jsonObject.put("completed", x );

            // sent data review

            System.out.println(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(" Server Response -> Project completed toggle " + response);

                        try {
                            String success = response.getString("success");

                            if(success.equals("true"))
                            {
                                // change the completed button

                                if(completed.equals(COMP))
                                    completed_button.setText(INCOMP);
                                else
                                    completed_button.setText(COMP);

                                // save project to database

                                ProjectDatabase projectDatabase = new ProjectDatabase(getApplicationContext());
                                projectDatabase.update_entry(strings[0] ,strings[1], strings[2],strings[3],completed);

                                // redirect the user

                                startActivity(new Intent(ProjectExpandedActivity.this, ProjectActivity.class));
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

}
