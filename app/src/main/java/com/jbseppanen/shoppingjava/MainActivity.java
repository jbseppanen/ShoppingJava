package com.jbseppanen.shoppingjava;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jbseppanen.shoppingjava.product.ProductListActivity;
import com.jbseppanen.shoppingjava.shopper.CreateShopper;
import com.jbseppanen.shoppingjava.supplier.SupplierListActivity;

public class MainActivity extends AppCompatActivity {

    public static final String CURRENT_SHOPPER_ID_KEY = "CurrentShopperId";
    public static final int CURRENT_SHOPPER_ID = -1;//TODO get this from user upon login, not hardcode.
    Context context;
    public static SharedPreferences sharedPref;
    Button buttonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);


        findViewById(R.id.button_shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long shopperId = MainActivity.sharedPref.getLong(MainActivity.CURRENT_SHOPPER_ID_KEY, -1);
                if (shopperId != -1) {
                    Intent intent = new Intent(context, ProductListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "You must create an account first.", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.button_suppliers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SupplierListActivity.class);
                startActivity(intent);
            }
        });
        buttonCreateAccount = findViewById(R.id.button_create_shopper);
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, CreateShopper.class));
            }
        });
    }
}
