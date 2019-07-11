package com.studyco.UserMonitor;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class InformServer {
    private RequestQueue mQueue;
    String url = "https://manadoma.com/callbacks/api/monitoring";
    Context c ;

    public InformServer(final Context c)
    {
        this.c = c;
    }

    public void informServerMethod( String s1,String s2 ) {
        mQueue = Volley.newRequestQueue(c);
        InfoGenerator infoGenerator = new InfoGenerator(c);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_identifier", infoGenerator.getUserString());
            jsonObject.put("application_activity",s1 );
            jsonObject.put("user_activity",s2 );
            jsonObject.put("app_name", infoGenerator.getApp());

            // sent data review
            System.out.println(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

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
