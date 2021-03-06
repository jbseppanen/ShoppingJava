package com.jbseppanen.shoppingjava;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jbseppanen.shoppingjava.product.Product;

import java.util.ArrayList;

public class CartViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CartListAdapter listAdapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        final long shopperId = MainActivity.sharedPref.getLong(MainActivity.CURRENT_SHOPPER_ID_KEY, -1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_order);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataDao.orderCart(shopperId, new DataDao.ObjectCallback<JsonObject>() {
                    @Override
                    public void returnObjects(JsonObject object) {
                        final String message = (object != null) ? "Order was successful!" : "Order failed.";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                });

                Snackbar.make(view, "Cart has been ordered.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DataDao.getCart(shopperId, new DataDao.ObjectCallback<ArrayList<Product>>() {
            @Override
            public void returnObjects(final ArrayList<Product> products) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView = findViewById(R.id.cart_recycler_view);
                        layoutManager = new LinearLayoutManager(context, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        listAdapter = new CartListAdapter(products, context);
                        recyclerView.setAdapter(listAdapter);
                    }
                });
            }
        });
    }
}
