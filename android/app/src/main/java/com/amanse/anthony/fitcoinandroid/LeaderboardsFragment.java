package com.amanse.anthony.fitcoinandroid;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amanse.anthony.fitcoinandroid.Config.BackendURL;
import com.amanse.anthony.fitcoinandroid.Config.LocalPreferences;
import com.amanse.anthony.fitcoinandroid.Config.SelectedEventPreferences;
import com.amanse.anthony.fitcoinandroid.Models.UserPosition;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardsFragment extends Fragment {

    RequestQueue queue;
    Gson gson;
    String TAG = "FITNESS_LEADERBOARDS";
    String BACKEND_URL = BackendURL.DEFAULT_URL;
    public String EVENT_NAME="cfsummit";

    ArrayList<UserInfoModel> userInfoModels;

    RecyclerView recyclerView;
    TextView userStats, userPosition, status;
    Toast loadingToast;

    int numberOfUsersInStanding = 10;
    int totalNumberOfUsers = 0;

    LeaderboardAdapater adapter;

    LocalPreferences localPreferences;
    SelectedEventPreferences selectedEventPreferences;

    public LeaderboardsFragment() {
        // Required empty public constructor
    }

    public static LeaderboardsFragment newInstance() {
        return new LeaderboardsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_leaderboards, container,false);

        final ImageView userImage = rootView.findViewById(R.id.userImage);
        final TextView userName = rootView.findViewById(R.id.userName);
        userStats = rootView.findViewById(R.id.userStats);
        userPosition = rootView.findViewById(R.id.userPosition);
        status = rootView.findViewById(R.id.status);

        userName.setText("-");
        userStats.setText("-");
        userPosition.setText("-");
        status.setText("-");

        userInfoModels = new ArrayList<>();
        loadingToast = Toast.makeText(rootView.getContext(),"Loading...",Toast.LENGTH_SHORT);


        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());

        recyclerView = rootView.findViewById(R.id.leaderboardsList);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (totalNumberOfUsers != 0 && numberOfUsersInStanding < totalNumberOfUsers) {
                        loadingToast.show();
                        numberOfUsersInStanding += 10;
                        getLeaderboardTop(numberOfUsersInStanding);
                    }
                }
            }
        });

        adapter = new LeaderboardAdapater(rootView.getContext(), userInfoModels);
        recyclerView.setAdapter(adapter);

        gson = new Gson();
        queue = Volley.newRequestQueue(rootView.getContext());

        // initialize shared preferences - persistent data
//        SharedPreferences sharedPreferences = ((AppCompatActivity) getActivity()).getSharedPreferences("shared_preferences_fitcoin", Context.MODE_PRIVATE);
        localPreferences = new LocalPreferences(getActivity());


        // get user info from shared prefrences
//        if (sharedPreferences.contains("UserInfo")) {
//            String userInfoJsonString = sharedPreferences.getString("UserInfo","error");
//            if (!userInfoJsonString.equals("error")) {
//                UserInfoModel userInfoModel = gson.fromJson(userInfoJsonString,UserInfoModel.class);
//                userImage.setImageBitmap(userInfoModel.getBitmap());
//                userName.setText(userInfoModel.getName());
//            }
//        }
        if (localPreferences.getCurrentEventSelected() != null) {
            this.EVENT_NAME = localPreferences.getCurrentEventSelected();
            selectedEventPreferences = new SelectedEventPreferences(getActivity(), this.EVENT_NAME);

            UserInfoModel userInfoModel = gson.fromJson(selectedEventPreferences.getUserInfo(), UserInfoModel.class);
            if (userInfoModel != null) {
                userImage.setImageBitmap(userInfoModel.getBitmap());
                userName.setText(userInfoModel.getName());
            } else {
                Log.d(TAG,"no user info...");
                userName.setText("registering...");
                // not really
            }
        }

        // get the users' position from the mongodb
        if (selectedEventPreferences != null) {
            getUserPositionFromMongo(selectedEventPreferences.getBlockchainUserId());
        }

        getLeaderboardTop(numberOfUsersInStanding);

        return rootView;
    }

    public void getUserPositionFromMongo(String userId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL + "/leaderboard/" + this.EVENT_NAME + "/position/user/" + userId , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        UserPosition userPositionModel = gson.fromJson(response.toString(), UserPosition.class);
                        int position = userPositionModel.getUserPosition();
                        int totalUsers = userPositionModel.getCount();
                        totalNumberOfUsers = totalUsers;
                        userStats.setText(String.valueOf(userPositionModel.getSteps()));
                        userPosition.setText(String.valueOf(position));
                        status.setText(String.format("You are position %d of %d", position, totalUsers));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void getLeaderboardTop(int number) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BACKEND_URL + "/leaderboard/" + this.EVENT_NAME + "/top/" + String.valueOf(number) , null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        UserInfoModel[] dataModels = gson.fromJson(response.toString(), UserInfoModel[].class);
                        userInfoModels.clear();
                        userInfoModels.addAll(Arrays.asList(dataModels));
                        adapter.notifyDataSetChanged();
                        loadingToast.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });
        queue.add(jsonArrayRequest);
    }

}
