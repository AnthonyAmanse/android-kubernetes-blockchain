package com.amanse.anthony.cloudcoins;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.amanse.anthony.cloudcoins.Config.EventPreferences;
import com.amanse.anthony.cloudcoins.Config.LocalPreferences;
import com.amanse.anthony.cloudcoins.Controllers.EventList;
import com.amanse.anthony.cloudcoins.Models.EventModel;
import com.amanse.anthony.cloudcoins.Models.RequestPages;
import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

public class ArticlePagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<ArticleModel> articleModels;
    boolean isAnEventSelected;
    private EventModel[] eventsReceived;
    private int numberPickerChoice;
    private BottomSheetDialog bottomSheetDialog;
    RequestPages requestPages;

    public ArticlePagerAdapter(Context context, ArrayList<ArticleModel> articleModels, boolean isAnEventSelected, RequestPages requestPages) {
        this.context = context;
        this.articleModels = articleModels;
        this.isAnEventSelected = isAnEventSelected;
        this.requestPages = requestPages;
    }

    public ArticlePagerAdapter(ArticlePagerAdapter articlePagerAdapter) {
        this.context = articlePagerAdapter.context;
        this.articleModels = articlePagerAdapter.articleModels;
        this.isAnEventSelected = articlePagerAdapter.isAnEventSelected;
        this.requestPages = articlePagerAdapter.requestPages;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_item, container, false);
        final ArticleModel articleModel = articleModels.get(position);

        final TextView articleTitle = view.findViewById(R.id.articleTitle);
        TextView articleSubTitle = view.findViewById(R.id.articleSubtitle);
        TextView articleSubtext = view.findViewById(R.id.articleSubtext);
        TextView articleDescription = view.findViewById(R.id.articleStatement);
        ImageView articleImage = view.findViewById(R.id.articleImage);
        Button articleLink = view.findViewById(R.id.articleLink);

        articleSubTitle.setText(articleModel.getSubtitle());

        if (position == 0) {
            articleSubTitle.setClickable(true);
            articleSubTitle.setText("other CloudCoins events >");
            articleSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            articleSubTitle.setBackgroundResource(outValue.resourceId);
            bottomSheetDialog = new BottomSheetDialog((Activity) view.getContext());
            final View sheetView = ((Activity) view.getContext()).getLayoutInflater().inflate(R.layout.event_picker,null);
            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            final NumberPicker picker = sheetView.findViewById(R.id.listOfEvents);
            articleSubTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    picker.setWrapSelectorWheel(false);

                    EventList eventList = new EventList(context);
                    eventList.getAvailableEvents(new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Gson gson = new Gson();
                            EventModel[] events = gson.fromJson(response.toString(), EventModel[].class);
                            eventsReceived = events;
                            ArrayList<String> data = new ArrayList<>();
                            for (EventModel eventModel: events) {
                                data.add(eventModel.getName());
                            }
                            picker.setMinValue(0);
                            picker.setMaxValue(events.length - 1);
                            picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                @Override
                                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                                    numberPickerChoice = i1;
                                }
                            });

                            picker.setDisplayedValues(data.toArray(new String[0]));
                            bottomSheetDialog.show();
                        }
                    });
                    Button chooseEventButton = sheetView.findViewById(R.id.confirmButton);
                    chooseEventButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String eventId = eventsReceived[numberPickerChoice].getEventId();
                            FragmentActivity fragmentActivity = (FragmentActivity) view.getContext();

                            // set selected event in local preferences
                            LocalPreferences localPreferences = new LocalPreferences(fragmentActivity);
                            localPreferences.setCurrentEventSelected(eventId);

                            // enter the event
                            EventPreferences eventPreferences = new EventPreferences(fragmentActivity);
                            String[] eventsRegistered = eventPreferences.getEventsRegistered();
                            if (!Arrays.asList(eventsRegistered).contains(eventId)) {
                                eventPreferences.enterNewEvent(eventId);
                            }
                            ((MainActivity) fragmentActivity).onEventSelected();
                            bottomSheetDialog.dismiss();
                            requestPages.requestPages(eventId);
                        }
                    });
                }
            });

            if (!isAnEventSelected) {
                articleSubTitle.performClick();
            }
        }

        articleTitle.setText(articleModel.getTitle());
        articleSubtext.setText(articleModel.getSubtext());
        articleDescription.setText(articleModel.getDescription());
        if (articleModel.getBitmap() != null) {
            articleImage.setImageBitmap(articleModel.getBitmap());
        }

        articleLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(articleModel.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return articleModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
