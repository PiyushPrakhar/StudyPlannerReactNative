package com.studyco.RememberCards;

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
import com.studyco.Dashboard.DashboardActivity;
import com.studyco.Databases.AssignmentDatabase;
import com.studyco.Databases.RememberCardDatabase;
import com.studyco.Projects.EditProjectActivity;
import com.studyco.R;
import com.studyco.UserMonitor.IdentifierDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class RememberCardExpanded extends AppCompatActivity {

    private RequestQueue mQueue;
    String url = "https://manadoma.com/study_app/remember_cards";


    EditText cardName;
    EditText cardSubject;
    EditText cardPriority;
    EditText cardNotes;

    String strings[];
    Button completed_button;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remember_card_expanded);


        // obtain user id from database


        String x = null;

        IdentifierDatabase id = new IdentifierDatabase(this);
        Cursor cursor = id.allData();

        if(cursor.moveToFirst())
        {
            cursor.moveToFirst();

            x = cursor.getString(0);

        }

        final String userid = x ;

        mQueue = Volley.newRequestQueue(this);

        cardName = (EditText)findViewById(R.id.card_edit_enter_name);
        cardSubject = (EditText)findViewById(R.id.card_edit_enter_subject);
        cardPriority = (EditText)findViewById(R.id.card_edit_enter_priority);
        cardNotes = (EditText)findViewById(R.id.card_edit_enter_notes);

        // Get intent data

        Intent i = getIntent();
        strings= i.getStringArrayExtra("strings");

        // Set intent data

        cardName.setText("Title : "+ strings[0]);
        cardSubject.setText("Subject : "+strings[1]);
        cardPriority.setText("Priority : "+strings[2]);
        cardNotes.setText(strings[3]);

        cardName.setEnabled(false);
        cardSubject.setEnabled(false);
        cardPriority.setEnabled(false);
        cardNotes.setEnabled(false);


        // Alert button

        completed_button = (Button)findViewById(R.id.remember_card_stop_notifications);

        // set the state of the complete button

        if(strings[4].equals("on"))
            completed_button.setText("Alert Off");
        else
            completed_button.setText("Alert On");

        completed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(completed_button.getText().equals("Alert On"))
                {
                    RememberCardDatabase rememberCardDatabase= new RememberCardDatabase(getApplicationContext());

                    // find the card id

                    String card_id = "";

                    Cursor cursor = rememberCardDatabase.allData();

                    if(cursor.moveToFirst())
                    {
                        cursor.moveToFirst();

                        for( int i =0 ; i<cursor.getCount() ; i++)
                        {
                            if(cursor.getString(0).equals(strings[0]))
                            {
                                card_id = cursor.getString(5);
                                break;
                            }

                            cursor.moveToNext();
                        }

                    }

                    // Register on server , volley call

                    sendCardInfoToServer(userid,strings[0],strings[3],strings[1],Integer.parseInt(strings[2]),card_id);

                }


                else
                {
                    RememberCardDatabase rememberCardDatabase= new RememberCardDatabase(getApplicationContext());


                    // delete the card from server

                    Cursor cursor = rememberCardDatabase.allData();

                    if(cursor.moveToFirst())
                    {
                        cursor.moveToFirst();

                        for( int i =0 ; i<cursor.getCount() ; i++)
                        {
                            if(cursor.getString(0).equals(strings[0]))
                            {
                                String card_id = cursor.getString(5);
                                deleteCardFromServer(card_id , strings[0],false);
                                break;
                            }

                            cursor.moveToNext();
                        }

                    }

                }
            }
        });

        // Delete button

        final Button delete_button = (Button)findViewById(R.id.remember_card_delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RememberCardDatabase rememberCardDatabase = new RememberCardDatabase(getApplicationContext());
                Cursor cursor = rememberCardDatabase.allData();

                if(cursor.moveToFirst())
                {
                    cursor.moveToFirst();

                    for( int i =0 ; i<cursor.getCount() ; i++)
                    {
                        if(cursor.getString(0).equals(strings[0]))
                        {
                            String card_id = cursor.getString(5);
                            deleteCardFromServer(card_id , strings[0], true);
                            break;
                        }

                        cursor.moveToNext();
                    }

                }

            }
        });

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        overridePendingTransition(R.animator.enter,R.animator.exit);
    }

    public void sendCardInfoToServer( String userid,String title , String content ,String subject,Integer priority, String card_id) {
        mQueue = Volley.newRequestQueue(this);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid",userid);
            jsonObject.put("title", title);
            jsonObject.put("content", content);
            jsonObject.put("subject",  subject);
            jsonObject.put("priority", priority );
            jsonObject.put("card_id", Integer.parseInt(card_id) );

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
                            String success  = response.getString("success");

                            if(success.equals("true"))
                            {
                                completed_button.setText("Alert Off");
                                RememberCardDatabase rememberCardDatabase= new RememberCardDatabase(getApplicationContext());
                                rememberCardDatabase.update_entry(strings[0],strings[1],strings[2],strings[3],"on");

                                // direct user to dashboard

                                startActivity(new Intent(RememberCardExpanded.this , RememberCardsActivity.class));
                                overridePendingTransition(R.animator.enter,R.animator.exit);
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

    public void deleteCardFromServer(String card_id , final String card_name, final boolean del ) {
        mQueue = Volley.newRequestQueue(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("card_id",Integer.parseInt(card_id));

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

                                if(del)
                                {
                                    // remove card from database

                                    RememberCardDatabase rem = new RememberCardDatabase(getApplicationContext());
                                    rem.deleteData(card_name);

                                    // direct user to dashboard

                                    startActivity(new Intent(RememberCardExpanded.this , RememberCardsActivity.class));
                                    overridePendingTransition(R.animator.enter,R.animator.exit);
                                }
                                else
                                {
                                    // update the card

                                    completed_button.setText("Alert On");
                                    RememberCardDatabase rememberCardDatabase= new RememberCardDatabase(getApplicationContext());
                                    rememberCardDatabase.update_entry(strings[0],strings[1],strings[2],strings[3],"off");
                                }


                                // direct user to dashboard

                                startActivity(new Intent(RememberCardExpanded.this , RememberCardsActivity.class));
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
