package com.studyco.RememberCards;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.studyco.Assignments.AssignmentActivity;
import com.studyco.Dashboard.DashboardActivity;
import com.studyco.Databases.RememberCardDatabase;
import com.studyco.R;
import com.studyco.UserMonitor.IdentifierDatabase;
import com.studyco.UserMonitor.InfoGenerator;
import com.studyco.UserMonitor.InformServer;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("ValidFragment")
public class CreateCardFragment extends Fragment {
    TextView cancel;
    TextView save;

    EditText cardName;
    EditText cardSubject;
    EditText cardPriority;
    EditText cardNotes;

    String s1 = null;
    String s2 = null;
    String s3 = null;
    String s4 = null;

    InformServer informServer;
    String ActivityTag = "Remember Card";

    private RequestQueue mQueue;
    String url = "https://manadoma.com/study_app/remember_cards";

    Context c ;

    @SuppressLint("ValidFragment")
    public CreateCardFragment(Context c){
        this.c = c;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mQueue = Volley.newRequestQueue(c);

        // obtain user id from database


        String x = null;

        IdentifierDatabase id = new IdentifierDatabase(c);
        Cursor cursor = id.allData();

        if(cursor.moveToFirst())
        {
            cursor.moveToFirst();

            x = cursor.getString(0);

        }

        final String userid = x ;


        //  ' Inform server ' initialization

        informServer = new InformServer(c);

        View v = inflater.inflate(R.layout.fragment_create_card, container, false);
        cancel = (TextView)v.findViewById(R.id.card_cancel);
        save = (TextView)v.findViewById(R.id.card_save);

        cardName = (EditText)v.findViewById(R.id.card_edit_enter_name);
        cardSubject = (EditText)v.findViewById(R.id.card_edit_enter_subject);
        cardPriority = (EditText)v.findViewById(R.id.card_edit_enter_priority);
        cardNotes = (EditText)v.findViewById(R.id.card_edit_enter_notes);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s1 = cardName.getText().toString();
                s2 = cardSubject.getText().toString();
                s3 = cardPriority.getText().toString();
                s4 = cardNotes.getText().toString();

                // set up a flag to check if the data already exists in database

                boolean flag = false;
                boolean prioriFlag = false;
                boolean emptyFlag = false;

                // check if card name is empty

                if(s1.equals(""))
                    emptyFlag =true;

                // check if the assignment name already exists

                RememberCardDatabase rememberCardDatabase = new RememberCardDatabase(c);
                Cursor cursor = rememberCardDatabase.allData();
                if (cursor.moveToFirst())
                {
                    cursor.moveToFirst();
                    for(int i=0; i<cursor.getCount();i++)
                    {
                        if(cursor.getString(0).equals(s1))
                            flag = true;
                    }
                }

                // check if the priority is an integer


                try {
                    Integer.parseInt(s3);
                }

                catch (Exception e)
                {
                    prioriFlag = true;
                }

                if(!prioriFlag)
                {
                    if( Integer.parseInt(s3) > 4 ||  Integer.parseInt(s3) < 0)
                    {
                        prioriFlag = true;
                    }
                }



                if( flag || prioriFlag || emptyFlag)
                {
                    if(emptyFlag)
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Card title can't be empty ", Snackbar.LENGTH_LONG).show();
                    else if(flag)
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Card title already exists", Snackbar.LENGTH_LONG).show();

                    else if(prioriFlag)
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Wrong priority chosen ", Snackbar.LENGTH_LONG).show();
                }
                else {

                    // User monitor

                    informServer.informServerMethod(ActivityTag, "Card Created" + " -> " + "[ " +s1 + " ]" );


                    // default priority given as 1

                    if(s3.equals(""))
                    {
                        // Register on server , volley call

                        sendCardInfoToServer(userid,s1,s4,s2,1);
                    }

                    // user given priority

                    else {


                        // Register on server , volley call

                        sendCardInfoToServer(userid,s1,s4,s2,Integer.parseInt(s3));
                    }


                    // Swap the tab


                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DashboardActivity.class));
                getActivity().overridePendingTransition(R.animator.enter,R.animator.exit);
            }
        });

        return v;
    }

    public void sendCardInfoToServer(final String userid, final String title , final String content , final String subject, final Integer priority) {
        mQueue = Volley.newRequestQueue(c);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid",userid);
            jsonObject.put("title", title);
            jsonObject.put("content", content);
            jsonObject.put("subject",  subject);
            jsonObject.put("priority", priority );

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
                            String card_id = response.getString("card_id");

                            if(success.equals("true"))
                            {
                                // Insert data into database

                                RememberCardDatabase rememberCardDatabase = new RememberCardDatabase(c);
                                rememberCardDatabase.insertData(title, subject, priority, content,"on",card_id);

                                // direct user to dashboard

                                startActivity(new Intent(c , DashboardActivity.class));
                                getActivity().overridePendingTransition(R.animator.enter,R.animator.exit);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Unable to reach servers, check your connection", Snackbar.LENGTH_LONG).show();

                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Unable to reach servers, check your connection", Snackbar.LENGTH_LONG).show();


            }
        });
        mQueue.add(request);
    }
}