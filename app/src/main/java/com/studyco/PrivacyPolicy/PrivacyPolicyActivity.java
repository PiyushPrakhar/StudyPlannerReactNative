package com.studyco.PrivacyPolicy;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import com.studyco.Dashboard.DashboardActivity;

/**
 * Created by Piyush Prakhar on 07-10-2018.
 */

public class PrivacyPolicyActivity extends AppCompatActivity {
    private ps mAuthTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthTask = new ps();
        mAuthTask.execute();
        Intent i = new Intent(this, DashboardActivity.class);
        startActivity(i);

    }


    private class ps extends AsyncTask<Void, Void, Boolean> {

        Intent intent;

        @Override
        protected Boolean doInBackground(Void... params) {
            Uri uri = Uri.parse("http://www.techknack.in/privacypolicy/");
            intent = new Intent(Intent.ACTION_VIEW, uri);
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            startActivity(intent);

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

