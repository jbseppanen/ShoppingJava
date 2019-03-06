package com.jbseppanen.shoppingjava;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jbseppanen.shoppingjava.product.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductsDao {

    public interface ObjectCallback<T> {
        void returnObjects(T object);
    }

    public static void getAllProducts(final AtomicBoolean canceled, final ObjectCallback<ArrayList<Product>> objectCallback) {


        final NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String page) {
                // process page of data
                if (canceled.get()) {
                    Log.i("GetRequestCanceled", page);
                    return;
                }
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Product>>() {
                }.getType();
                ArrayList<Product> products = gson.fromJson(page, type);

                objectCallback.returnObjects(products);

                if (canceled.get()) {
                    Log.i("GetRequestCanceled", "First");
                    return;
                }
            }
        };
        NetworkAdapter.httpGetRequest("http://192.168.0.4:8080/products", canceled, callback);
    }

}
