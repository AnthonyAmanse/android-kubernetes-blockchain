package com.amanse.anthony.cloudcoins.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amanse.anthony.cloudcoins.Config.LocalPreferences;
import com.amanse.anthony.cloudcoins.Controllers.PredictionClient;
import com.amanse.anthony.cloudcoins.Models.ParticipantPredictionModel;
import com.amanse.anthony.cloudcoins.R;
import com.android.volley.Response;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PredictionFragment extends Fragment {

    LocalPreferences localPreferences;

    public PredictionFragment() {
        // Required empty public constructor
    }

    public static PredictionFragment newInstance() {
        return new PredictionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_prediction, container, false);

        localPreferences = new LocalPreferences(getActivity());
        PredictionClient predictionClient = new PredictionClient(getContext(), localPreferences.getCurrentEventSelected());
        predictionClient.getPrediction(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                ParticipantPredictionModel participantPredictionModel = gson.fromJson(response.toString(), ParticipantPredictionModel.class);

                BarChart barChart = rootView.findViewById(R.id.prediction_chart);


                List<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry(0,participantPredictionModel.getPrediction()));
                entries.add(new BarEntry(1,participantPredictionModel.getCurrentParticipants()));
                BarDataSet barDataSet = new BarDataSet(entries,"Projected versus actual participants");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                barChart.invalidate();

                List<String> labels = new ArrayList<>();
                labels.add("Prediction");
                labels.add("Current Participants");

                // remove description label
                barChart.getDescription().setEnabled(false);

                // remove touch
                barChart.setTouchEnabled(false);

                // add labels
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

                //remove extra labels?
                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setGranularityEnabled(true);

                // set left axis minimum to zero
                barChart.getAxisLeft().setAxisMinimum(0);

                // set right axis minimum to zero
                barChart.getAxisRight().setAxisMinimum(0);
            }
        });

        Button learnMoreButton = rootView.findViewById(R.id.mlLink);
        learnMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://ibm.biz/cloudcoinsml");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }
        });

        return rootView;
    }
}
