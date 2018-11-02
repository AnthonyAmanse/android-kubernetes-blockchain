package com.amanse.anthony.cloudcoins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShopItemsAdapter extends RecyclerView.Adapter<ShopItemsAdapter.ShopItemsViewHolder> implements View.OnClickListener {

    private Context context;
    private ArrayList<ShopItemModel> shopItemModelList;

    public ShopItemsAdapter(Context context, ArrayList<ShopItemModel> shopItemModelList) {
        this.context = context;
        this.shopItemModelList = shopItemModelList;
    }

    @Override
    public ShopItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shop_item, null);
        return new ShopItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShopItemsViewHolder holder, int position) {
        ShopItemModel shopItemModel = shopItemModelList.get(position);

        String quantityLeftConcatenate = String.valueOf(shopItemModel.getQuantityLeft()) + " left";
        String priceConcatenate = String.valueOf(shopItemModel.getPrice()) + " kubecoins each";

        holder.cardView.setTag(shopItemModel);
        holder.cardView.setOnClickListener(this);

        holder.productName.setText(shopItemModel.getProductName());
        holder.productPrice.setText(priceConcatenate);
        holder.productQuantity.setText(quantityLeftConcatenate);


        // Set the images based on the productId
        // images are stored in app (res/drawable*)
        // in the future, backend maybe?
        switch (shopItemModel.getProductId()) {
            case "eye_sticker":
            case "eye-sticker":
                holder.productImage.setImageResource(R.drawable.eye_sticker);
                holder.productImage.setTag(R.drawable.eye_sticker);
                break;
            case "bee_sticker":
            case "bee-sticker":
                holder.productImage.setImageResource(R.drawable.bee_sticker);
                holder.productImage.setTag(R.drawable.bee_sticker);
                break;
            case "em_sticker":
            case "em-sticker":
                holder.productImage.setImageResource(R.drawable.em_sticker);
                holder.productImage.setTag(R.drawable.em_sticker);
                break;
            case "think_bandana":
            case "think-bandana":
                holder.productImage.setImageResource(R.drawable.think_bandana);
                holder.productImage.setTag(R.drawable.think_bandana);
                break;
            case "kubecoin_shirt":
            case "kubecoin-shirt":
                holder.productImage.setImageResource(R.drawable.kubecoin_shirt);
                holder.productImage.setTag(R.drawable.kubecoin_shirt);
                break;
            case "popsocket":
                holder.productImage.setImageResource(R.drawable.popsocket);
                holder.productImage.setTag(R.drawable.popsocket);
                break;
            case "webcam-cover":
            case "webcam_cover":
                holder.productImage.setImageResource(R.drawable.webcam_cover);
                holder.productImage.setTag(R.drawable.webcam_cover);
                break;
            case "ibm-cloud-sticker":
            case "ibm_cloud_sticker":
                holder.productImage.setImageResource(R.drawable.ibm_cloud_sticker);
                holder.productImage.setTag(R.drawable.ibm_cloud_sticker);
                break;
            case "charging-cable":
            case "charging_cable":
                holder.productImage.setImageResource(R.drawable.usb_cable);
                holder.productImage.setTag(R.drawable.usb_cable);
                break;
            case "vr-headset":
            case "vr_headset":
                holder.productImage.setImageResource(R.drawable.vr_headset);
                holder.productImage.setTag(R.drawable.vr_headset);
                break;
            case "thermosbottle":
                holder.productImage.setImageResource(R.drawable.thermosbottle);
                holder.productImage.setTag(R.drawable.thermosbottle);
                break;
            case "notebook":
                holder.productImage.setImageResource(R.drawable.notebook);
                holder.productImage.setTag(R.drawable.notebook);
                break;
            case "laptop-handbag":
            case "laptop_handbag":
                holder.productImage.setImageResource(R.drawable.laptop_handbag);
                holder.productImage.setTag(R.drawable.laptop_handbag);
                break;
            case "scarf":
                holder.productImage.setImageResource(R.drawable.scarf);
                holder.productImage.setTag(R.drawable.scarf);
                break;
            default:
                holder.productImage.setImageResource(R.drawable.ic_footprint);
                holder.productImage.setTag(R.drawable.ic_footprint);
                break;
        }
        setAnimation(holder);
    }

    public void setAnimation(ShopItemsViewHolder holder) {
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return shopItemModelList.size();
    }

    @Override
    public void onClick(View view) {
        TextView pendingChargesView = ((Activity) context).findViewById(R.id.pendingCharges);
        TextView fitcoinsBalanceView = ((Activity) context).findViewById(R.id.fitcoinsBalance);
        RecyclerView recyclerView = ((Activity) context).findViewById(R.id.productList);

        JSONObject response = (JSONObject) recyclerView.getTag();

        if (pendingChargesView.getText().toString().equals("-") || fitcoinsBalanceView.getText().toString().equals("-")) {
            Log.d("FITNESS_ADAPTER_SHOP", "state not yet loaded");
        } else {
            int pendingCharges = Integer.valueOf(pendingChargesView.getText().toString());
            int fitcoinsBalance = Integer.valueOf(fitcoinsBalanceView.getText().toString());

            int availableToSpend = fitcoinsBalance + pendingCharges;



            ShopItemModel shopItemModel = (ShopItemModel) view.getTag();
            Log.d("FITNESS_ADAPTER_SHOP", "clicked on - " + shopItemModel.getProductName());
            ImageView productImage = (ImageView) view.findViewById(R.id.productImage);

            Intent intent = new Intent(view.getContext(), QuantitySelection.class);
            intent.putExtra("PRODUCT_CHOSEN",(Integer) productImage.getTag());
            intent.putExtra("PRODUCT_JSON", new Gson().toJson(shopItemModel, ShopItemModel.class));
            intent.putExtra("AVAILABLE_BALANCE",availableToSpend);

            // get current number of items of user
            if (fitcoinsBalanceView.getTag() instanceof ArrayMap) {
                ArrayMap<String,Integer> listOfItems = (ArrayMap<String,Integer>) fitcoinsBalanceView.getTag();
                intent.putExtra("NUMBER_OF_PRODUCTS_USER_HAS",listOfItems.get(shopItemModel.getProductId()));
                Log.d("FITNESS", "You have " + listOfItems.get(shopItemModel.getProductId()) + " of " + shopItemModel.getProductName());
            }

            // get limits of product
            try {
                int limitOfItem = response.getInt(shopItemModel.getProductId());
                intent.putExtra("PRODUCT_LIMIT",limitOfItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Pair<View, String> pair1 = Pair.create(view.findViewById(R.id.productImage),"productImage");
            Pair<View, String> pair2 = Pair.create(view.findViewById(R.id.productName),"productName");

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), pair1, pair2);


            view.getContext().startActivity(intent,options.toBundle());
        }
    }

    public class ShopItemsViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productQuantity, productPrice;
        TextView quantityQuestion;
        ImageView productImage;
        CardView cardView;

        public ShopItemsViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.shopCard);
            productName = view.findViewById(R.id.productName);
            productPrice = view.findViewById(R.id.productPrice);
            productQuantity = view.findViewById(R.id.productQuantityLeft);
            productImage = view.findViewById(R.id.productImage);
            quantityQuestion = view.findViewById(R.id.quantityQuestion);
        }
    }
}
