package com.jbseppanen.shoppingjava;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jbseppanen.shoppingjava.product.Product;
import com.jbseppanen.shoppingjava.shopper.Shopper;
import com.jbseppanen.shoppingjava.supplier.Supplier;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataDao {

    private static final String serverAddress = "http://192.168.0.4:8080";
    private static final String emulatorServerAddress = "http://10.0.2.2:8080";

    public interface ObjectCallback<T> {
        void returnObjects(T object);
    }

    public static void getAllProducts(final ObjectCallback<ArrayList<Product>> objectCallback) {
        final NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String page) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Product>>() {
                }.getType();
                ArrayList<Product> products = gson.fromJson(page, type);
                objectCallback.returnObjects(products);
            }
        };
        NetworkAdapter.httpRequest(getServerAddress("/products"), NetworkAdapter.GET, null, callback);
    }

    public static void getAllSuppliers(final ObjectCallback<ArrayList<Supplier>> objectCallback) {
        final NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String page) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Supplier>>() {
                }.getType();
                ArrayList<Supplier> suppliers = gson.fromJson(page, type);
                objectCallback.returnObjects(suppliers);
            }
        };
        NetworkAdapter.httpRequest(getServerAddress("/suppliers"), NetworkAdapter.GET, null, callback);
    }

    public static void addProductToCart(long shopperid, int productId, final ObjectCallback<JsonObject> objectCallback) {
        final NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String page) {
                Gson gson = new Gson();
                Type type = new TypeToken<JsonObject>() {
                }.getType();
                JsonObject json;
                try {
                    json = gson.fromJson(page, type);
                } catch (JsonSyntaxException e) {
                 json = null;
                }
                objectCallback.returnObjects(json);
            }
        };
        String url = getServerAddress("/shopper/" + shopperid + "/product/" + productId);
        NetworkAdapter.httpRequest(url, NetworkAdapter.POST, null, callback);
    }

    public static void addProductToSupplier(final ObjectCallback<Supplier> objectCallback, int supplierId, int productId) {
        final NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String page) {
                Gson gson = new Gson();
                Type type = new TypeToken<Supplier>() {
                }.getType();
                Supplier supplier = gson.fromJson(page, type);
                objectCallback.returnObjects(supplier);
            }
        };
        String url = getServerAddress("/supplier/" + supplierId + "/product/" + productId);
        NetworkAdapter.httpRequest(url, NetworkAdapter.POST, null, callback);
    }

    public static void getCart(long shopperid, final ObjectCallback<ArrayList<Product>> objectCallback) {
        final NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String page) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Product>>() {
                }.getType();
                ArrayList<Product> products = gson.fromJson(page, type);
                objectCallback.returnObjects(products);
            }
        };
        String url = getServerAddress("/shopper/" + shopperid + "/cart");
        NetworkAdapter.httpRequest(url, NetworkAdapter.GET, null, callback);
    }

    public static void orderCart(long shopperid, final ObjectCallback<JsonObject> objectCallback) {
        final NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String page) {
                Gson gson = new Gson();
                Type type = new TypeToken<JsonObject>() {
                }.getType();
                JsonObject json = gson.fromJson(page, type);
                objectCallback.returnObjects(json);
            }
        };
        String url = getServerAddress("/shopper/" + shopperid + "/order");
        NetworkAdapter.httpRequest(url, NetworkAdapter.POST, null, callback);
    }

    public static void addNewProduct(Product product) {
        NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String result) {
                //Nothing to accept a return yet.
            }
        };
        Gson gson = new Gson();
        String jsonString = gson.toJson(product);
        String url = getServerAddress("/product");
        NetworkAdapter.httpRequest(url, NetworkAdapter.POST, jsonString, callback);
    }

    public static void addNewSupplier(Supplier supplier) {
        NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String result) {
                //Nothing to accept a return yet.
            }
        };
        Gson gson = new Gson();
        String jsonString = gson.toJson(supplier);
        String url = getServerAddress("/supplier");
        NetworkAdapter.httpRequest(url, NetworkAdapter.POST, jsonString, callback);
    }

    public static void createShopperAccount(final Shopper shopper, final ObjectCallback<Long> objectCallback) {
        NetworkAdapter.NetworkCallback callback = new NetworkAdapter.NetworkCallback() {
            @Override
            public void returnResult(Boolean success, String result) {
                long shopperid = -1;
                Gson gson = new Gson();
                Type type = new TypeToken<Shopper>() {
                }.getType();
                try {
                    Shopper returnedShopper = gson.fromJson(result, type);
                    shopperid = returnedShopper.getShopperid();
                } catch (JsonParseException e) {
                }
                objectCallback.returnObjects(shopperid);
            }
        };
        Gson gson = new Gson();
        String jsonString = gson.toJson(shopper);
        String url = getServerAddress("/shopper");
        NetworkAdapter.httpRequest(url, NetworkAdapter.POST, jsonString, callback);
    }

    private static String getServerAddress(String suffix) {
        if (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)) {
            return emulatorServerAddress + suffix;
        } else {
            return serverAddress + suffix;
        }
    }

}
