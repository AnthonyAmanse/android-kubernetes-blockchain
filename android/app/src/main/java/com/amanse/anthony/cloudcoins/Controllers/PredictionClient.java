package com.amanse.anthony.cloudcoins.Controllers;

import android.content.Context;
import android.util.Log;

import com.amanse.anthony.cloudcoins.Config.BackendURL;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class PredictionClient {
    Context context;
    RequestQueue queue;

    public PredictionClient(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public void getPrediction(Response.Listener<JSONObject> responseListener) {
        try {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://watsonml.opencloud-cluster.us-south.containers.appdomain.cloud/prediction/cfsummit", null,
                    responseListener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("FITNESS", error.toString());
                }
            });
            queue.add(jsonObjectRequest);
        } catch (Error e) {
            e.printStackTrace();
        }
    }
}
