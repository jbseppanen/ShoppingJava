package com.jbseppanen.shoppingjava;

import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jbseppanen.shoppingjava.product.Product;
import com.jbseppanen.shoppingjava.supplier.Supplier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataDao {

   private static final String serverAddress = "http://192.168.0.4:8080";
    private static final String emulatorServerAddress = "http://10.0.2.2:8080";

    public interface ObjectCallback<T> {
        void returnObjects(T object);
    }

    public static void getAllProducts(final AtomicBoolean canceled, final ObjectCallback<ArrayList<Product>> objectCallback) {
        final NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String page) {
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
        String url = getServerAddress() + "/products";
        NetworkAdapter.httpGetRequest(url, canceled, callback);
    }

    public static void getAllSuppliers(final AtomicBoolean canceled, final ObjectCallback<ArrayList<Supplier>> objectCallback) {
        final NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String page) {
                if (canceled.get()) {
                    Log.i("GetRequestCanceled", page);
                    return;
                }
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Supplier>>() {
                }.getType();
                ArrayList<Supplier> suppliers = gson.fromJson(page, type);

                objectCallback.returnObjects(suppliers);

                if (canceled.get()) {
                    Log.i("GetRequestCanceled", "First");
                    return;
                }
            }
        };
        String url = getServerAddress() + "/suppliers";
        NetworkAdapter.httpGetRequest(url, canceled, callback);
    }

    public static String getServerAddress() {
        if(Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)) {
            return emulatorServerAddress;
        } else {
            return serverAddress;
        }
    }

}
