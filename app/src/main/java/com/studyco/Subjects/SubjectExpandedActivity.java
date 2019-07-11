package com.studyco.Subjects;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.studyco.Databases.SubjectDatabase;
import com.studyco.R;

public class SubjectExpandedActivity extends AppCompatActivity {

    EditText subjectName;
    EditText subjectDifficulty;
    EditText subjectCredits;
    EditText subjectNotes;

    String s1 = null;
    String s2 = null;
    String s3 = null;
    String s4 = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_expanded);


        subjectName = (EditText)findViewById(R.id.edit_enter_name);
        subjectDifficulty = (EditText)findViewById(R.id.edit_enter_difficulty);
        subjectCredits= (EditText)findViewById(R.id.edit_enter_credits);
        subjectNotes= (EditText)findViewById(R.id.edit_enter_notes);


        // Get intent data

        Intent i = getIntent();
        final String strings[] = i.getStringArrayExtra("strings");

        subjectName.setText(" Title : "+ strings[0]);
        subjectDifficulty.setText(" Difficulty level : "+strings[1]);
        subjectCredits.setText(" Credits : "+strings[2]);
        subjectNotes.setText(" Notes : "+strings[3]);

        subjectName.setEnabled(false);
        subjectDifficulty.setEnabled(false);
        subjectCredits.setEnabled(false);
        subjectNotes.setEnabled(false);


        // Edit button

        Button edit_button = (Button)findViewById(R.id.subject_edit);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubjectExpandedActivity.this , EditSubjectActivity.class)
                        .putExtra("strings",strings));
            }
        });

        // Delete button

        Button delete_button = (Button)findViewById(R.id.subject_delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubjectDatabase subjectDatabase = new SubjectDatabase(getApplicationContext());
                Cursor cursor = subjectDatabase.allData();
                boolean flag = false;

                if(cursor.moveToFirst())
                {
                    cursor.moveToFirst();
                    for( int i=0 ; i<cursor.getCount() ; i++)
                    {
                        if(cursor.getString(0).equals(strings[0]))
                        {
                            subjectDatabase.deleteData(strings[0]);
                            flag = true;
                        }
                        cursor.moveToNext();
                    }
                }

                if(flag)
                {
                    startActivity(new Intent(SubjectExpandedActivity.this, SubjectActivity.class));
                    overridePendingTransition(R.animator.enter,R.animator.exit);
                }
            }
        });


    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SubjectActivity.class));
        overridePendingTransition(R.animator.enter,R.animator.exit);
    }
}
