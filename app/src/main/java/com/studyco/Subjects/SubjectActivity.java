package com.studyco.Subjects;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.studyco.Dashboard.DashboardActivity;
import com.studyco.Databases.SubjectDatabase;
import com.studyco.R;
import com.studyco.UserMonitor.InformServer;

import java.util.ArrayList;

public class SubjectActivity extends AppCompatActivity implements RecyclerAdapterSubject.ItemClickListener{

    InformServer informServer;
    String ActivityTag = "Subject Main Activity";

    RecyclerAdapterSubject adapter;

    ArrayList<String> subjectList = new ArrayList<>();
    ArrayList<Integer> entryList = new ArrayList<>();
    ArrayList<String> creditsList = new ArrayList<>();
    ArrayList<Integer> difficultyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        //  ' Inform server ' initialization

        informServer = new InformServer(getApplicationContext());


        //Database fetch

        SubjectDatabase subjectDatabase = new SubjectDatabase(this);
        Cursor cursor = subjectDatabase.allData();
        if(cursor.moveToFirst()){
            cursor.moveToFirst();

            for(int i =0;i<cursor.getCount();i++)
            {
                entryList.add(i+1);
                subjectList.add(cursor.getString(0));
                difficultyList.add(Integer.parseInt(cursor.getString(1)));
                creditsList.add(cursor.getString(2));
                
                cursor.moveToNext();

            }
        }
        else
        {
            RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_subject);

        }
        // Display the contents of the recycler view adapter on AssignmentActivity ( Recycler view )

        RecyclerView recyclerView = findViewById(R.id.recycler_subject);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapterSubject(this, subjectList,entryList,creditsList,difficultyList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        // floating button
        FloatingActionButton floatingActionButton =(FloatingActionButton)findViewById(R.id.floating_button_subject);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubjectActivity.this, CreateSubjectActivity.class));
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        SubjectDatabase subjectDatabase = new SubjectDatabase(this);
        Cursor cursor = subjectDatabase.allData();
        if( cursor.moveToPosition(position))
        {
            cursor.moveToPosition(position);
            Intent intent = new Intent(this , SubjectExpandedActivity.class);

            String s1 ;
            String s2 ;
            String s3 ;
            String s4 ;


            s1 = cursor.getString(0);
            s2 = cursor.getString(1);
            s3 = cursor.getString(2);
            s4 = cursor.getString(3);


            String strings[] = {s1 , s2 , s3 , s4};

            intent.putExtra("strings",strings);

            // User monitor
            informServer.informServerMethod(ActivityTag,"Subject Viewed" + " -> "+"[ " +s1 + " ]");

            startActivity(intent);


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
}
