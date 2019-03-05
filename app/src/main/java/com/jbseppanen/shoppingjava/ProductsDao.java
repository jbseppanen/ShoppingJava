package com.jbseppanen.shoppingjava;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductsDao {

    public interface ObjectCallback<T> {
        void returnObjects(T object);
    }

    public static void getContacts(final AtomicBoolean canceled, final ObjectCallback<ArrayList<Product>> objectCallback) {


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

                if (canceled.get()) {
                    Log.i("GetRequestCanceled", page);
                    return;
                }
                objectCallback.returnObjects(products);

                if (canceled.get()) {
                    Log.i("GetRequestCanceled", "First");
                    return;
                }
            }
        };
//        NetworkAdapter.httpGetRequest("https://randomuser.me/api/?format=json&inc=name,email,phone,picture&results=1000", canceled, callback);
        NetworkAdapter.httpGetRequest("http://192.168.0.4:8080/products", canceled, callback);
    }


    static void getImageFile(final String url, final Context context, final AtomicBoolean canceled, final ObjectCallback<Bitmap> objectCallback) {

        final NetworkAdapter.NetworkImageCallback callback = new NetworkAdapter.NetworkImageCallback() {

            @Override
            public void returnResult(Bitmap result) {
                File file;
                String searchText = PublicFunctions.getSearchText(url);
                FileOutputStream fileOutputStream = null;
                try {
                    if (canceled.get()) {
                        Log.i("GetRequestCanceled", url);
                        throw new IOException();
                    }

                    file = File.createTempFile(searchText, null, context.getCacheDir());
                    fileOutputStream = new FileOutputStream(file);
                    result.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                objectCallback.returnObjects(result);

            }
        };
        NetworkAdapter.httpImageRequest(url, canceled, callback);
    }
}
