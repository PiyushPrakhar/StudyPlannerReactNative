package com.studyco.Projects;

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
import com.studyco.Databases.ProjectDatabase;
import com.studyco.R;
import com.studyco.UserMonitor.InformServer;

import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity implements RecyclerAdapterProject.ItemClickListener{

    InformServer informServer;
    String ActivityTag = "Project Main Activity";

    RecyclerAdapterProject adapter;

    ArrayList<String> projectList = new ArrayList<>();
    ArrayList<Integer> entryList = new ArrayList<>();
    ArrayList<String> expiryList = new ArrayList<>();
    ArrayList<String> statusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        //  ' Inform server ' initialization

        informServer = new InformServer(getApplicationContext());


        //Database fetch

        ProjectDatabase projectDatabase = new ProjectDatabase(this);
        Cursor cursor = projectDatabase.allData();
        if(cursor.moveToFirst()){
            cursor.moveToFirst();

            for(int i =0;i<cursor.getCount();i++)
            {
                projectList.add(cursor.getString(0));
                entryList.add(i+1);
                expiryList.add(cursor.getString(1));
                statusList.add(cursor.getString(4));

                cursor.moveToNext();

            }
        }
        else
        {
            RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_project);

        }

        // Display the contents of the recycler view adapter on ProjectActivity ( Recycler view )

        RecyclerView recyclerView = findViewById(R.id.recycler_project);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapterProject(this, projectList,entryList,expiryList,statusList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        // floating button
        FloatingActionButton floatingActionButton =(FloatingActionButton)findViewById(R.id.floating_button_project);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProjectActivity.this, CreateProjectActivity.class));
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        ProjectDatabase projectDatabase = new ProjectDatabase(this);
        Cursor cursor = projectDatabase.allData();
        if( cursor.moveToPosition(position))
        {
            cursor.moveToPosition(position);
            Intent intent = new Intent(this , ProjectExpandedActivity.class
            );

            String s1 = null ;
            String s2 = null ;
            String s3 = null ;
            String s4 = null ;
            String s5 = null ;

            s1 = cursor.getString(0);
            s2 = cursor.getString(1);
            s3 = cursor.getString(2);
            s4 = cursor.getString(3);
            s5 = cursor.getString(4);

            String strings[] = {s1 , s2 , s3 , s4 ,s5 };

            intent.putExtra("strings",strings);

            // User monitor
            informServer.informServerMethod(ActivityTag, "Project Viewed" + " -> "+ "[ " +s1 + " ]" );

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
