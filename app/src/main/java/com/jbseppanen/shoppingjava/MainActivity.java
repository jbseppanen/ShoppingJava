package com.jbseppanen.shoppingjava;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jbseppanen.shoppingjava.product.ProductListActivity;
import com.jbseppanen.shoppingjava.supplier.SupplierListActivity;

public class MainActivity extends AppCompatActivity {

    public static final String CURRENT_SHOPPER_ID_KEY = "CurrentShopperId";
    public static final int CURRENT_SHOPPER_ID = 1;//TODO get this from user upon login, not hardcode.
    Context context;
    public static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        findViewById(R.id.button_shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_suppliers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SupplierListActivity.class);
                startActivity(intent);
            }
        });
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(CURRENT_SHOPPER_ID_KEY, CURRENT_SHOPPER_ID);
        editor.apply();
    }
}
