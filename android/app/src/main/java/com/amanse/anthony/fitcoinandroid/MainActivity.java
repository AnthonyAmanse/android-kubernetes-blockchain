package com.amanse.anthony.fitcoinandroid;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FITNESS_API";
    private static final String BACKEND_URL = "https://anthony-blockchain.us-south.containers.mybluemix.net";
    public RequestQueue queue;
    Gson gson = new Gson();
    private Fragment currentTab;

    MFPPush push = MFPPush.getInstance();
    MFPPushNotificationListener notificationListener;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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
        push.initialize(getApplicationContext(), "6388599f-912a-4699-b4e9-5a4cb479476a", "33b2fad9-de1a-4756-a786-a6a0266aba1a");
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

        // request queue
        queue = Volley.newRequestQueue(this);

        // check if location is permitted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "access fine location not yet granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        // initialize shared preferences - persistent data
        sharedPreferences = this.getSharedPreferences("shared_preferences_fitcoin", Context.MODE_PRIVATE);

        // Check if user is already enrolled
        if (sharedPreferences.contains("BlockchainUserId")) {
            Log.d(TAG, "User already registered.");
            registerNotification(sharedPreferences.getString("BlockchainUserId","none"));
        } else {
                // register the user
                registerUser();
        }
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
            }

            @Override
            public void onFailure(MFPPushException ex) {
                //handle failure in device registration here
                Log.d(TAG, ex.getErrorMessage());
            }
        });
    }

    public void registerUser() {
        try {
            JSONObject params = new JSONObject("{\"type\":\"enroll\",\"queue\":\"user_queue\",\"params\":{}}");
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


        editor = sharedPreferences.edit();

        editor.putString("BlockchainUserId",resultOfEnroll.result.user);
        editor.apply();

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

        editor = sharedPreferences.edit();

        try {
            JSONObject params = new JSONObject("{\"registereeId\":" + userId + ",\"steps\":0,\"calories\":0,\"device\":\"android\"}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/registerees/add" , params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // save the random name and png assigned to this user
                            editor.putString("UserInfo", response.toString());
                            editor.apply();
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
                    Uri uri = Uri.parse("https://github.com/AnthonyAmanse/fitcoin-android/blob/master/Privacy.md");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}