package com.amanse.anthony.cloudcoins;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.amanse.anthony.cloudcoins.Config.BackendURL;
import com.amanse.anthony.cloudcoins.Config.LocalPreferences;
import com.amanse.anthony.cloudcoins.Config.SelectedEventPreferences;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FITNESS_API";
    private static final String BACKEND_URL = BackendURL.DEFAULT_URL;
    public String EVENT_NAME="cfsummit";
    public RequestQueue queue;
    Gson gson = new Gson();
    private Fragment currentTab;

    MFPPush push = MFPPush.getInstance();
    MFPPushNotificationListener notificationListener;

    LocalPreferences localPreferences;
    SelectedEventPreferences selectedEventPreferences;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            if (item.isChecked()) {
                return false;
            }
            switch (item.getItemId()) {
                case R.id.navigation_technology:
                    selectedFragment = TechFragment.newInstance();
                    break;
                case R.id.navigation_user:
                    selectedFragment = UserFragment.newInstance();
                    break;
                case R.id.navigation_shop:
                    selectedFragment = ShopFragment.newInstance();
                    break;
                case R.id.navigation_leaderboards:
                    selectedFragment = LeaderboardsFragment.newInstance();
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // use tech fragment - initial layout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, TechFragment.newInstance());
        transaction.commit();

        // Initialize push notification
        BMSClient.getInstance().initialize(this, BMSClient.REGION_US_SOUTH);
        push.initialize(getApplicationContext(), "47b58b69-f2a4-454d-8037-c6020feedc09", "248d6cbd-ccd3-416a-b063-17001392e66c");
        notificationListener = new MFPPushNotificationListener() {

            @Override
            public void onReceive (final MFPSimplePushNotification message){
                // Handle Push Notification
                Log.d(TAG, message.toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        new android.app.AlertDialog.Builder(MainActivity.this)
                                .setTitle("Kubecoin")
                                .setMessage(message.getAlert())
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                })
                                .show();
                    }
                });
            }
        };

        // initialize request queue
        queue = Volley.newRequestQueue(this);

        // check if location is permitted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "access fine location not yet granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        // initialize shared preferences - persistent data
//        sharedPreferences = this.getSharedPreferences("shared_preferences_fitcoin", Context.MODE_PRIVATE);

        // Check if user is already enrolled
//        if (sharedPreferences.contains("BlockchainUserId")) {
//            Log.d(TAG, "User already registered.");
//            registerNotification(sharedPreferences.getString("BlockchainUserId","none"));
//        } else {
//                // register the user
//                registerUser();
//        }
        localPreferences = new LocalPreferences(this);
        if (localPreferences.getCurrentEventSelected() != null) {
            this.EVENT_NAME = localPreferences.getCurrentEventSelected();
            selectedEventPreferences = new SelectedEventPreferences(this, this.EVENT_NAME);
        }
    }

    protected void onEventSelected() {
        this.EVENT_NAME = localPreferences.getCurrentEventSelected();
        selectedEventPreferences = new SelectedEventPreferences(this, this.EVENT_NAME);
        String userId = selectedEventPreferences.getBlockchainUserId();
        if (userId == null) {
            registerUser();
        } else {
            registerNotification(userId);
        }

        // check if a user id is present in selected event preferences
            // if not - do registerUser
            // else registerNotification
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(push != null) {
            Log.d(TAG, "Listening...");
            push.listen(notificationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (push != null) {
            Log.d(TAG, "Paused...");
            push.hold();
        }
    }



    public void registerNotification(String userId) {
        push.registerDeviceWithUserId(userId, new MFPPushResponseListener<String>() {

            @Override
            public void onSuccess(String response) {
                //handle successful device registration here
                Log.d(TAG, response);
                push.listen(notificationListener);
                subscribe();
            }

            @Override
            public void onFailure(MFPPushException ex) {
                //handle failure in device registration here
                Log.d(TAG, ex.getErrorMessage());
            }
        });
    }

    public void subscribe() {
        final String eventName = this.EVENT_NAME;
        Log.d(TAG, "subscribing to tag: " + this.EVENT_NAME);

        // get all subscribed tags
        push.getSubscriptions(new MFPPushResponseListener<List<String>>() {

            @Override
            public void onSuccess(List<String> tags) {
                System.out.println("Subscribed tags are: "+tags);
                for (final String tag: tags) {

                    // unsubscribe to tags except Push.ALL
                    if (!tag.equals("Push.ALL")) {
                        push.unsubscribe(tag, new MFPPushResponseListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                System.out.println("Successfully unsubscribed from tag . "+ tag);
                            }

                            @Override
                            public void onFailure(MFPPushException e) {
                                System.out.println("Error while unsubscribing from tags. "+ e.getMessage());
                            }
                        });
                    }
                }

                // subscribe to tag with event name
                push.subscribe(eventName, new MFPPushResponseListener<String>() {
                    @Override
                    public void onSuccess(String arg) {
                        System.out.println("Succesfully Subscribed to: "+ arg);
                    }

                    @Override
                    public void onFailure(MFPPushException ex) {
                        System.out.println("Error subscribing to Tag1.." + ex.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(MFPPushException ex) {
                System.out.println("Error getting subscriptions.. " + ex.getMessage());
            }
        });
    }

    public void registerUser() {
        try {
            JSONObject params = new JSONObject("{\"type\":\"enroll\",\"queue\":\"user_queue-" + this.EVENT_NAME + "\",\"params\":{}}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/api/execute", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            InitialResultFromRabbit initialResultFromRabbit = gson.fromJson(response.toString(),InitialResultFromRabbit.class);
                            if (initialResultFromRabbit.status.equals("success")) {
                                getResultFromResultId("enrollment", initialResultFromRabbit.resultId, 0);
                            } else {
                                Log.d(TAG, "Response is: " + response.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "That didn't work!");
                }
            });
            this.queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getResultFromResultId(final String initialRequestType, final String resultId, final int attemptNumber) {
        // Limit to 60 attempts
        if (attemptNumber < 60) {
            if (initialRequestType.equals("enrollment")) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL + "/api/results/" + resultId, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                BackendResult backendResult = gson.fromJson(response.toString(), BackendResult.class);
                                if (backendResult.status.equals("pending")) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getResultFromResultId(initialRequestType,resultId,attemptNumber + 1);
                                        }
                                    },3000);
                                } else if (backendResult.status.equals("done")) {
                                    saveUser(backendResult.result);
                                } else {
                                    Log.d(TAG, "Response is: " + response.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "That didn't work!");
                            }
                        });
                this.queue.add(jsonObjectRequest);
            }
        } else {
            Log.d(TAG, "No results after 60 times...");
        }
    }

    public void saveUser(String result) {
        ResultOfEnroll resultOfEnroll = gson.fromJson(result, ResultOfEnroll.class);
        Log.d(TAG, resultOfEnroll.result.user);

        selectedEventPreferences.setBlockchainUserId(resultOfEnroll.result.user);
        sendToMongo(resultOfEnroll.result.user);
        registerNotification(resultOfEnroll.result.user);

        // send user id to registeree-api

        // save the user name

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Enrollment successful!");
        alertDialog.setMessage("You have been enrolled to the blockchain network. Your User ID is:\n\n" + resultOfEnroll.result.user);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "CONFIRM",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void sendToMongo(String userId) {
        try {
            JSONObject params = new JSONObject("{\"registereeId\":" + userId + ",\"steps\":0,\"calories\":0,\"device\":\"android\"}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/registerees/" + this.EVENT_NAME + "/add" , params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // save the random name and png assigned to this user
                            selectedEventPreferences.setUserInfo(response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "That didn't work!");
                }
            });
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.privacy, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_privacy) {
                    Uri uri = Uri.parse(BackendURL.PRIVACY_POLICY_URL);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    // move this to a new fragment...
    public void testGetEvents() {
        try {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BackendURL.EVENTS_URL , null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG,response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "That didn't work!");
                }
            });
            queue.add(jsonArrayRequest);
        } catch (Error e) {
            e.printStackTrace();
        }
    }
}