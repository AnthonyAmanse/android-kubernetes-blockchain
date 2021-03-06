package com.amanse.anthony.cloudcoins.Controllers;

import android.content.Context;
import android.util.Log;

import com.amanse.anthony.cloudcoins.Config.BackendURL;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class EventList {
    Context context;
    RequestQueue queue;

    public EventList(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public void getAvailableEvents(Response.Listener<JSONArray> responseListener) {
        try {

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BackendURL.EVENTS_URL, null,
                    responseListener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("FITNESS", error.toString());
                }
            });
            queue.add(jsonArrayRequest);
        } catch (Error e) {
            e.printStackTrace();
        }
    }
}
