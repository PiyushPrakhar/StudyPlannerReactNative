package com.studyco.FirebaseHandling;

import android.content.Context;
import android.database.Cursor;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.studyco.UserMonitor.IdentifierDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Piyush Prakhar on 07-02-2019.
 */

public class SendRegistrationToServer {
    private RequestQueue mQueue;
    public void sendRegId(Context c , String regId){

        String url = "https://manadoma.com/study_app/firebase_token";
        mQueue = Volley.newRequestQueue(c);
        JSONObject jsonObject = new JSONObject();
        try{
            IdentifierDatabase currentlyLoggedDatabase = new IdentifierDatabase(c);
            Cursor cursor = currentlyLoggedDatabase.allData();
            cursor.moveToFirst();
            jsonObject.put("userid",cursor.getString(0));
            jsonObject.put("firebase_reg_id",regId);



        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,jsonObject ,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(" Send registration id to the server :  "+ response);
                            String success = response.getString("success");
                            if(success.equals("true"))
                            {

                            }
                            else if(success.equals("false"))
                            {

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

