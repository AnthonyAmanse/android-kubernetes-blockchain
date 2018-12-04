package com.amanse.anthony.cloudcoins;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

public class ContractDetails extends AppCompatActivity {

    TextView contractId, productName, quantity, state, totalPrice, details;
    ImageView productImage;


    // override back pressed so that no transition on shared element is performed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_details);

        contractId = findViewById(R.id.contractId);
        productName = findViewById(R.id.productNameInContract);
        quantity = findViewById(R.id.quantityInContract);
        state = findViewById(R.id.stateInContract);
        totalPrice = findViewById(R.id.totalPriceInContract);
        productImage = findViewById(R.id.productImageInContract);
        details = findViewById(R.id.detailsContract);

        ContractModel contractModel = new Gson().fromJson(getIntent().getStringExtra("CONTRACT_JSON"),ContractModel.class);

        // Set the images based on the productId
        // images are stored in app (res/drawable*)
        // in the future, backend maybe?
        switch (contractModel.getProductId()) {
            case "eye_sticker":
            case "eye-sticker":
                productImage.setImageResource(R.drawable.eye_sticker);
                productImage.setTag(R.drawable.eye_sticker);
                break;
            case "bee_sticker":
            case "bee-sticker":
                productImage.setImageResource(R.drawable.bee_sticker);
                productImage.setTag(R.drawable.bee_sticker);
                break;
            case "em_sticker":
            case "em-sticker":
                productImage.setImageResource(R.drawable.em_sticker);
                productImage.setTag(R.drawable.em_sticker);
                break;
            case "think_bandana":
            case "think-bandana":
                productImage.setImageResource(R.drawable.think_bandana);
                productImage.setTag(R.drawable.think_bandana);
                break;
            case "kubecoin_shirt":
            case "kubecoin-shirt":
                productImage.setImageResource(R.drawable.kubecoin_shirt);
                productImage.setTag(R.drawable.kubecoin_shirt);
                break;
            case "popsocket":
                productImage.setImageResource(R.drawable.popsocket);
                productImage.setTag(R.drawable.popsocket);
                break;
            case "webcam-cover":
            case "webcam_cover":
                productImage.setImageResource(R.drawable.webcam_cover);
                productImage.setTag(R.drawable.webcam_cover);
                break;
            case "ibm-cloud-sticker":
            case "ibm_cloud_sticker":
                productImage.setImageResource(R.drawable.ibm_cloud_sticker);
                productImage.setTag(R.drawable.ibm_cloud_sticker);
                break;
            case "charging-cable":
            case "charging_cable":
                productImage.setImageResource(R.drawable.usb_cable);
                productImage.setTag(R.drawable.usb_cable);
                break;
            case "vr-headset":
            case "vr_headset":
                productImage.setImageResource(R.drawable.vr_headset);
                productImage.setTag(R.drawable.vr_headset);
                break;
            case "thermosbottle":
                productImage.setImageResource(R.drawable.thermosbottle);
                productImage.setTag(R.drawable.thermosbottle);
                break;
            case "notebook":
                productImage.setImageResource(R.drawable.notebook);
                productImage.setTag(R.drawable.notebook);
                break;
            case "laptop-handbag":
            case "laptop_handbag":
                productImage.setImageResource(R.drawable.laptop_handbag);
                productImage.setTag(R.drawable.laptop_handbag);
                break;
            case "scarf":
                productImage.setImageResource(R.drawable.scarf);
                productImage.setTag(R.drawable.scarf);
                break;
            case "bonbon":
                productImage.setImageResource(R.drawable.bonbon);
                productImage.setTag(R.drawable.bonbon);
                break;
            case "candy-bag":
            case "candy_bag":
                productImage.setImageResource(R.drawable.candy_bag);
                productImage.setTag(R.drawable.candy_bag);
                break;
            case "ice-scraper":
            case "ice_scraper":
                productImage.setImageResource(R.drawable.ice_scraper);
                productImage.setTag(R.drawable.ice_scraper);
                break;
            case "toblerone":
                productImage.setImageResource(R.drawable.toblerone);
                productImage.setTag(R.drawable.toblerone);
                break;
            case "water-bottle":
            case "water_bottle":
                productImage.setImageResource(R.drawable.water_bottle);
                productImage.setTag(R.drawable.water_bottle);
                break;
            case "beanie":
            case "beanies":
                productImage.setImageResource(R.drawable.beanie);
                productImage.setTag(R.drawable.beanie);
                break;
            default:
                productImage.setImageResource(R.drawable.ic_footprint);
                productImage.setTag(R.drawable.ic_footprint);
                break;
        }

        contractId.setText(contractModel.getContractId());
        productName.setText(contractModel.getProductName());
        quantity.setText(String.valueOf(contractModel.getQuantity()));
        state.setText(contractModel.getState());
        totalPrice.setText(String.valueOf(contractModel.getCost()));

        if (contractModel.state.equals("pending")) {
            details.setText("your swags are waiting in our booth!");
        } else if (contractModel.state.equals("complete")) {
            details.setText("enjoy the swag!");
        } else {
            details.setVisibility(View.GONE);
        }
    }
}
