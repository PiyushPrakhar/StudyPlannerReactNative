package com.studyco.Assignments;

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
import com.studyco.Databases.AssignmentDatabase;
import com.studyco.Databases.ProjectDatabase;
import com.studyco.Projects.ProjectActivity;
import com.studyco.Projects.ProjectExpandedActivity;
import com.studyco.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AssignmentExpandedActivity extends AppCompatActivity {

    EditText assignmentName;
    EditText assignmentDeadline;
    EditText assignmentHours;
    EditText assignmentNotes;

    String assn_id =null;
    Button completed_button;

    String INCOMP ;
    String COMP ;

    String strings[] =null;


    private RequestQueue mQueue;
    String url = "https://manadoma.com/study_app/assignment";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_expanded);

        INCOMP =  getResources().getString(R.string.incomplete);
        COMP = getResources().getString(R.string.complete);

        mQueue = Volley.newRequestQueue(this);

        assignmentName = (EditText)findViewById(R.id.edit_enter_name);
        assignmentDeadline = (EditText)findViewById(R.id.edit_enter_deadline);
        assignmentHours = (EditText)findViewById(R.id.edit_enter_hours);
        assignmentNotes = (EditText)findViewById(R.id.edit_enter_notes);


        // Get intent data

        Intent i = getIntent();
        strings = i.getStringArrayExtra("strings");

        // get project id

        AssignmentDatabase assignmentDatabase = new AssignmentDatabase(this);
        Cursor cursor = assignmentDatabase.allData();

        if(cursor.moveToFirst())
        {
            cursor.moveToFirst();

            for( int m=0;m<cursor.getCount();m++)
            {
                if( cursor.getString(0).equals(strings[0]))
                {
                    assn_id = cursor.getString(5);
                }

                cursor.moveToNext();

            }
        }

        // Set intent data

        assignmentName.setText(" Title : "+ strings[0]);
        assignmentDeadline.setText(" Deadline : "+strings[1]);
        assignmentHours.setText(" Total hours : "+strings[2]);
        assignmentNotes.setText(strings[3]);

        assignmentName.setEnabled(false);
        assignmentDeadline.setEnabled(false);
        assignmentNotes.setEnabled(false);
        assignmentHours.setEnabled(false);

        // Edit button

        Button edit_button = (Button)findViewById(R.id.assignment_edit);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AssignmentExpandedActivity.this , EditAssignmentActivity.class)
                .putExtra("strings",strings));
            }
        });

        // Completed button

        completed_button = (Button)findViewById(R.id.assignment_completed);

        // set the state of the complete button

        if(strings[4].equals(INCOMP))
            completed_button.setText(COMP);
        else
            completed_button.setText(INCOMP);

        completed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(completed_button.getText().equals(INCOMP))
                {
                    sendAssnInfoToServer(INCOMP);

                }
                else
                {
                    sendAssnInfoToServer(COMP);
                }
            }
        });

        // Delete button

        ImageButton delete_button = (ImageButton)findViewById(R.id.assignment_delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteAssnFromServer();
            }
        });

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AssignmentActivity.class));
        overridePendingTransition(R.animator.enter,R.animator.exit);
    }

    public void deleteAssnFromServer() {
        mQueue = Volley.newRequestQueue(this);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("assn_id",assn_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // sent JSON

        System.out.println(jsonObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String success = response.getString("success");

                            if(success.equals("true"))
                            {

                                AssignmentDatabase assignmentDatabase = new AssignmentDatabase(getApplicationContext());
                                Cursor cursor = assignmentDatabase.allData();
                                boolean flag = false;

                                if(cursor.moveToFirst())
                                {
                                    cursor.moveToFirst();
                                    for( int i=0 ; i<cursor.getCount() ; i++)
                                    {
                                        if(cursor.getString(0).equals(strings[0]))
                                        {
                                            assignmentDatabase.deleteData(strings[0]);
                                            flag = true;

                                        }
                                        cursor.moveToNext();
                                    }
                                }

                                if(flag)
                                {
                                    startActivity(new Intent(AssignmentExpandedActivity.this, AssignmentActivity.class));
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

    public void sendAssnInfoToServer(final String completed) {

        mQueue = Volley.newRequestQueue(this);

        boolean x ;
        if(completed.equals(COMP))
            x= true;
        else
            x= false;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("assn_id",assn_id);
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

                        System.out.println("Server response -> Assignment toggle : " + response);

                        try {
                            String success = response.getString("success");

                            if(success.equals("true"))
                            {
                                if(completed.equals(COMP))
                                    completed_button.setText(INCOMP);
                                else
                                    completed_button.setText(COMP);

                                AssignmentDatabase assignmentDatabase = new AssignmentDatabase(getApplicationContext());
                                assignmentDatabase.update_entry(strings[0] ,strings[1], strings[2],strings[3],completed);

                                startActivity(new Intent(AssignmentExpandedActivity.this, AssignmentActivity.class));
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
