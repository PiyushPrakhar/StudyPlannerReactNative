package com.studyco.Subjects;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.studyco.Dashboard.DashboardActivity;
import com.studyco.Databases.SubjectDatabase;
import com.studyco.R;

public class EditSubjectActivity extends AppCompatActivity {
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_subject);

        cancel = (TextView)findViewById(R.id.subject_cancel);
        save = (TextView)findViewById(R.id.subject_save);

        subjectName = (EditText)findViewById(R.id.edit_enter_name);
        subjectDifficulty = (EditText)findViewById(R.id.edit_enter_difficulty);
        subjectCredits= (EditText)findViewById(R.id.edit_enter_credits);
        subjectNotes= (EditText)findViewById(R.id.edit_enter_notes);


        // Get intent data

        Intent i = getIntent();
        final String strings[] = i.getStringArrayExtra("strings");

        subjectName.setText( strings[0]);
        subjectDifficulty.setText(strings[1]);
        subjectCredits.setText(strings[2]);
        subjectNotes.setText(strings[3]);

        subjectName.setEnabled(false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                s1 = subjectName.getText().toString();
                s2 = subjectDifficulty.getText().toString();
                s3 = subjectCredits.getText().toString();
                s4 = subjectNotes.getText().toString();

                SubjectDatabase subjectDatabase = new SubjectDatabase(EditSubjectActivity.this);
                subjectDatabase.update_entry(s1,s2,s3,s4);

                startActivity(new Intent(EditSubjectActivity.this , SubjectActivity.class));
                overridePendingTransition(R.animator.enter,R.animator.exit);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditSubjectActivity.this , DashboardActivity.class));
                overridePendingTransition(R.animator.enter,R.animator.exit);
            }
        });
    }

}
