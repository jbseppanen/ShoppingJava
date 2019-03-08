package com.jbseppanen.shoppingjava.shopper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jbseppanen.shoppingjava.DataDao;
import com.jbseppanen.shoppingjava.MainActivity;
import com.jbseppanen.shoppingjava.R;

public class CreateShopper extends AppCompatActivity {

    Switch switchSameAsBilling;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shopper);

        context = this;

        findViewById(R.id.button_save_shopper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shopper shopper = new Shopper();
                shopper.setFirstname(getViewText(R.id.edit_shopper_firstname));
                shopper.setLastname(getViewText(R.id.edit_shopper_lastname));
                shopper.setBillingaddress(getViewText(R.id.edit_shopper_billingaddress));
                shopper.setBillingcity(getViewText(R.id.edit_shopper_billingcity));
                shopper.setBillingstate(getViewText(R.id.edit_shopper_billingstate));
                shopper.setBillingzip(getViewText(R.id.edit_shopper_billingzip));
                shopper.setPhonenumber(getViewText(R.id.edit_shopper_phone));
                shopper.setPaymethod(getViewText(R.id.edit_shopper_paymethod));

                if (switchSameAsBilling.isChecked()) {
                    shopper.setShippingaddress(getViewText(R.id.edit_shopper_billingaddress));
                    shopper.setShippingcity(getViewText(R.id.edit_shopper_billingcity));
                    shopper.setShippingstate(getViewText(R.id.edit_shopper_billingstate));
                    shopper.setShippingzipcode(getViewText(R.id.edit_shopper_billingzip));
                } else {
                    shopper.setShippingaddress(getViewText(R.id.edit_shopper_shippingaddress));
                    shopper.setShippingcity(getViewText(R.id.edit_shopper_shippingcity));
                    shopper.setShippingstate(getViewText(R.id.edit_shopper_shippingstate));
                    shopper.setShippingzipcode(getViewText(R.id.edit_shopper_shippingzip));
                }

                DataDao.createShopperAccount(shopper, new DataDao.ObjectCallback<Long>() {
                    @Override
                    public void returnObjects(Long shopperid) {
                        final String message = shopperid != -1 ? "Account Created!" : "Failed to create account.";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putLong(MainActivity.CURRENT_SHOPPER_ID_KEY, shopperid);
                        editor.putLong(MainActivity.CURRENT_SHOPPER_ID_KEY, 1);//Remove this and uncomment above when figured out.
                        editor.apply();
                        finish();
                    }
                });
            }
        });

        switchSameAsBilling = findViewById(R.id.switch_same_as_billing);

        switchSameAsBilling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.edit_shopper_shippingaddress).setEnabled(!isChecked);
                findViewById(R.id.edit_shopper_shippingcity).setEnabled(!isChecked);
                findViewById(R.id.edit_shopper_shippingstate).setEnabled(!isChecked);
                findViewById(R.id.edit_shopper_shippingzip).setEnabled(!isChecked);
            }
        });


    }

    private String getViewText(int viewId) {
        return ((TextView) findViewById(viewId)).getText().toString();
    }
}
