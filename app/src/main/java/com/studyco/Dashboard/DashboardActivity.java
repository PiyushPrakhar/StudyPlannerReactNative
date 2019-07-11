package com.studyco.Dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.studyco.Assignments.AssignmentActivity;
import com.studyco.FirebaseHandling.FirebaseRegIdDatabase;
import com.studyco.FirebaseHandling.SendRegistrationToServer;
import com.studyco.LinearCounter.LinearCounter;
import com.studyco.LinearTracker.LinearTrackerActivity;
import com.studyco.Music.MusicActivity;
import com.studyco.MainActivity;
import com.studyco.PrivacyPolicy.PrivacyPolicyActivity;
import com.studyco.R;
import com.studyco.RememberCards.RememberCardsActivity;
import com.studyco.Subjects.SubjectActivity;
import com.studyco.UserMonitor.InfoGenerator;
import com.studyco.UserMonitor.InformServer;
import com.studyco.billing.BillingConstants;
import com.studyco.billing.BillingManager;
import com.studyco.billing.BillingProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , BillingProvider {

    InformServer informServer;
    String ActivityTag = "Dashboard Activity";
    private RequestQueue mQueue;
    String url = "https://manadoma.com/study_app/verify";

    private BillingManager mBillingManager;
    private MainViewControllerDashboard mViewController;


    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isLinearTrackSubscribed() {
        return mViewController.isLinearTrackSubscribed();
    }

    @Override
    public boolean isPlannerAiSubscribed() {
        return mViewController.isPlannerAiSubscribed();
    }

    @Override
    public boolean isSoothingMusicSubscribed() {
        return mViewController.isSoothingMusicSubscribed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the controller and load game data

        mViewController = new MainViewControllerDashboard(this);

        // Create and initialize BillingManager which talks to BillingLibrary

        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());


        // Specify purchase and drive buttons listeners
        // Note: This couldn't be done inside *.xml for Android TV since TV layout is inflated
        // via AppCompat

        // Billing finish

        getSupportActionBar();

        mQueue = Volley.newRequestQueue(this);

        // check network connection

        if(!isNetworkConnected())
        {
            // show a dialog

            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("No internet connection ." +
                    " You can check your saved documents but you can not add / edit any document ");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        // Firebase registration

        FirebaseRegIdDatabase firebaseRegIdDatabase = new FirebaseRegIdDatabase(this);
        Cursor c = firebaseRegIdDatabase.allData();

        try {
            if (c.moveToFirst()) {
                c.moveToFirst();
                SendRegistrationToServer sendRegistrationToServer = new SendRegistrationToServer();
                sendRegistrationToServer.sendRegId(getApplicationContext(), c.getString(1));
            }
        }
        catch( Exception e )
        {
            System.out.println(e);
        }

        //  ' Inform server ' initialization

        informServer = new InformServer(getApplicationContext());

        // Generate a string for the current user and save it in database

        InfoGenerator infoGenerator = new InfoGenerator(getApplicationContext());
        infoGenerator.saveUserString();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Text View clicks

        CardView music = (CardView) findViewById(R.id.card_1);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sp = getPreferences(MODE_PRIVATE);
                boolean speVal = sp.getBoolean(BillingConstants.SKU_SOOTHING_MUSIC, false);
                speVal=true;
                if(speVal)
                {
                    startActivity(new Intent(DashboardActivity.this, MusicActivity.class));
                }
                else
                {
                    Toast toast=Toast.makeText(getApplicationContext(),"You are not subscribed to music",Toast.LENGTH_SHORT);
                    toast.show();
                }

                // User monitor
                informServer.informServerMethod(ActivityTag,"Music card clicked");
            }
        });
        CardView stats = (CardView) findViewById(R.id.card_2);
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, RememberCardsActivity.class));


                // User monitor
                informServer.informServerMethod(ActivityTag,"Remember card clicked");
            }
        });

        CardView project = (CardView) findViewById(R.id.card_4);
        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(DashboardActivity.this, ProjectActivity.class));
                startActivity(new Intent(DashboardActivity.this, LinearTrackerActivity.class));

                // User monitor
                informServer.informServerMethod(ActivityTag,"Project card clicked");
            }
        });

        CardView assignment = (CardView) findViewById(R.id.card_5);
        assignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, AssignmentActivity.class));

                // User monitor
                informServer.informServerMethod(ActivityTag,"Assignment card clicked");
            }
        });

        CardView subject = (CardView) findViewById(R.id.card_3);
        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, SubjectActivity.class));

                // User monitor
                informServer.informServerMethod(ActivityTag,"Subjects card clicked");
            }
        });

        CardView planner = (CardView) findViewById(R.id.card_6);
        planner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getPreferences(MODE_PRIVATE);
                boolean speVal = sp.getBoolean(BillingConstants.SKU_PLANNER_AI, false);
                speVal=true;
                if(speVal)
                {
                    startActivity(new Intent(DashboardActivity.this,LinearCounter.class));
                }
                else
                {
                    Toast toast=Toast.makeText(getApplicationContext(),"You are not subscribed to AI planner ",Toast.LENGTH_SHORT);
                    toast.show();
                }

                // User monitor
                informServer.informServerMethod(ActivityTag,"AI Planner card clicked");
            }
        });

        // check of app version is valid

        CheckAppStatus("fhYPrDeNHegoOnwVZakjfBpXtOUVLycrntPVDXkNyfMLoNcqctpjdmVfNeQTpNuqGxmgCn");


    }
    /**
     * Update UI to reflect model
     */
    @UiThread
    private void updateUi() {
        Log.d(" Dashboard Activity ", "Updating the UI. Thread: " + Thread.currentThread().getName());

        if (isSoothingMusicSubscribed() || isLinearTrackSubscribed() || isPlannerAiSubscribed()) {
            System.out.println( " Change your UI setting here . ");
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            this.finishAffinity();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // show a dialog with instructions
            AlertDialog dialog = new AlertDialog.Builder(this).setMessage(getText(R.string.cards_instruction))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        }

        if (id == R.id.privacyPolicy) {
            startActivity(new Intent(DashboardActivity.this , PrivacyPolicyActivity.class));

            return  true;
        }

        if (id == R.id.linearCounter) {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            return  false;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void CheckAppStatus( String userid) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token",userid);
            // sent data review

            System.out.println(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("Verification response -> " +  response);

                        try {
                            String success = response.getString("success");

                            if(!success.equals("true"))
                            {
                                // create a dialog

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(DashboardActivity.this);
                                builder1.setMessage("Expired app version !\n\n Please update the app ");
                                builder1.setCancelable(false);
                                AlertDialog alert11 = builder1.create();
                                alert11.show();

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
}
