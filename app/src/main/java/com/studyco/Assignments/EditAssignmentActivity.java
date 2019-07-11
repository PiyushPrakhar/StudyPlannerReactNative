package com.studyco.Assignments;

import android.annotation.SuppressLint;
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
import com.studyco.Dashboard.DashboardActivity;
import com.studyco.Databases.AssignmentDatabase;
import com.studyco.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditAssignmentActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    String url = "https://manadoma.com/study_app/assignment";

    TextView cancel;
    TextView save;

    EditText assignmentName;
    EditText assignmentDeadline;
    EditText assignmentHours;
    EditText assignmentNotes;

    String s1 = null;
    String s2 = null;
    String s3 = null;
    String s4 = null;
    String s5 = null;

    String assn_id =null;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_assignment);

        mQueue = Volley.newRequestQueue(this);

        // Get intent data

        Intent i = getIntent();
        final String strings[] = i.getStringArrayExtra("strings");

        cancel = (TextView)findViewById(R.id.assignment_cancel);
        save = (TextView)findViewById(R.id.assignment_save);

        assignmentName = (EditText)findViewById(R.id.edit_enter_name);
        assignmentDeadline = (EditText)findViewById(R.id.edit_enter_deadline);
        assignmentHours = (EditText)findViewById(R.id.edit_enter_hours);
        assignmentNotes = (EditText)findViewById(R.id.edit_enter_notes);

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

        assignmentDeadline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditAssignmentActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // disable

        assignmentName.setEnabled(false);

        // Set intent data

        assignmentName.setText( strings[0]);
        assignmentDeadline.setText(strings[1]);
        assignmentHours.setText(strings[2]);
        assignmentNotes.setText(strings[3]);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s1 = assignmentName.getText().toString();
                s2 = assignmentDeadline.getText().toString();
                s3 = assignmentHours.getText().toString();
                s4 = assignmentNotes.getText().toString();
                s5 = strings[4];

                // check if the deadline is empty

                boolean emptyDeadlineFlag =false;
                if(s2.equals(""))
                    emptyDeadlineFlag=true;

                if (emptyDeadlineFlag)
                    Snackbar.make(findViewById(android.R.id.content), "Deadline can't be empty ", Snackbar.LENGTH_LONG).show();
                else
                    sendAssnInfoToServer(assn_id,s1,s2,s3,s4);


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditAssignmentActivity.this , DashboardActivity.class));
                overridePendingTransition(R.animator.enter,R.animator.exit);
            }
        });

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

    }

    public void sendAssnInfoToServer(final String assn_id , String title , String deadline, String hours , String note ) {


        JSONObject jsonObject = new JSONObject();
        try {
            if(hours.equals(""))
                hours="0";
            Integer aid = Integer.parseInt(assn_id);
            Integer ahours = Integer.parseInt(hours);

            jsonObject.put("assn_id",aid);
            jsonObject.put("title", title);
            jsonObject.put("hours", ahours);
            jsonObject.put("deadline",  deadline);
            jsonObject.put("note",  note);

            // sent data review

            System.out.println(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("Server response -> Edit Assignment : " + response);

                        try {
                            String success = response.getString("success");

                            if(success.equals("true"))
                            {
                                AssignmentDatabase assignmentDatabase = new AssignmentDatabase(EditAssignmentActivity.this);
                                assignmentDatabase.update_entry(s1,s2,s3,s4,s5);

                                startActivity(new Intent(EditAssignmentActivity.this , AssignmentActivity.class));
                                overridePendingTransition(R.animator.enter,R.animator.exit);
                            }
                        } catch (JSONException e) {
                            Snackbar.make(findViewById(android.R.id.content), "Unable to reach servers, check your connection", Snackbar.LENGTH_LONG).show();

                            e.printStackTrace();
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

        assignmentDeadline.setText(sdf.format(myCalendar.getTime()));
    }

}
