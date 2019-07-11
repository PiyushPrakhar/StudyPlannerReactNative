package com.studyco.Subjects;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.studyco.Dashboard.DashboardActivity;
import com.studyco.Databases.SubjectDatabase;
import com.studyco.R;
import com.studyco.UserMonitor.InformServer;

public class CreateSubjectActivity extends AppCompatActivity {

    InformServer informServer;
    String ActivityTag = "Create Subject Activity";

    TextView save;
    TextView cancel;

    EditText subjectName;
    EditText subjectDifficulty;
    EditText subjectCredits;
    EditText subjectNotes;

    String s1 = null;
    String s2 = null;
    String s3 = null;
    String s4 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_subject);

        //  ' Inform server ' initialization

        informServer = new InformServer(getApplicationContext());

        cancel = (TextView)findViewById(R.id.subject_cancel);
        save = (TextView)findViewById(R.id.subject_save);

        subjectName = (EditText)findViewById(R.id.edit_enter_name);
        subjectDifficulty = (EditText)findViewById(R.id.edit_enter_difficulty);
        subjectCredits= (EditText)findViewById(R.id.edit_enter_credits);
        subjectNotes= (EditText)findViewById(R.id.edit_enter_notes);

        // save button

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s1 = subjectName.getText().toString();
                s2 = subjectDifficulty.getText().toString();
                s3 = subjectCredits.getText().toString();
                s4 = subjectNotes.getText().toString();

                // set up a flag to check if the data already exists in database

                boolean flag = false;
                boolean emptyFlag =false;

                // check if the title is empty

                if(s1.equals(""))
                    emptyFlag=true;

                // check if the subject name already exists

                SubjectDatabase subjectDatabase = new SubjectDatabase(CreateSubjectActivity.this);
                Cursor cursor = subjectDatabase.allData();
                if (cursor.moveToFirst())
                {
                    cursor.moveToFirst();
                    for(int i=0; i<cursor.getCount();i++)
                    {
                        if(cursor.getString(0).equals(s1))
                            flag = true;
                    }
                }

                Boolean numericCheck = false;
                try {
                    Integer.parseInt(s2);
                    numericCheck=true;
                }
                catch (Exception e )
                {
                    numericCheck = false;
                }

                if(emptyFlag || flag) {
                    if (emptyFlag)
                        Snackbar.make(findViewById(android.R.id.content), "Subject title can't be empty ", Snackbar.LENGTH_LONG).show();
                    else if (flag)
                        Snackbar.make(findViewById(android.R.id.content), "Subject already exists", Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    if(numericCheck)
                    {
                        // User monitor

                        informServer.informServerMethod(ActivityTag, "Subject Created" + " -> " + "[ " +s1 + " ]" );

                        subjectDatabase.insertData(s1, s2, s3, s4);
                        startActivity(new Intent(CreateSubjectActivity.this, SubjectActivity.class));
                        overridePendingTransition(R.animator.enter,R.animator.exit);
                    }
                    else
                    {
                        Snackbar.make(findViewById(android.R.id.content), "Please enter the difficulty as a number ", Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });

        // cancel button

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateSubjectActivity.this, DashboardActivity.class));
                overridePendingTransition(R.animator.enter,R.animator.exit);
            }
        });


    }


}
