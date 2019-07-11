package com.studyco.FirebaseHandling;

import android.database.Cursor;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by Piyush Prakhar on 09-02-2019.
 */

public class FirebaseInstanceService extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {


        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        FirebaseRegIdDatabase firebaseRegIdDatabase = new FirebaseRegIdDatabase(getApplicationContext());
        Cursor c = firebaseRegIdDatabase.allData();
        if(c.moveToFirst())
        {
            firebaseRegIdDatabase.deleteAll();
        }
        firebaseRegIdDatabase.insertData(refreshedToken);


    }


}
