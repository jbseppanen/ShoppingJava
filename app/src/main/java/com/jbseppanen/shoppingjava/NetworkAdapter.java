package com.jbseppanen.shoppingjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkAdapter {
    private static final int TIMEOUT = 3000;


    public interface NetworkCallback {
        void returnResult(Boolean success, String result);
    }


    public static void httpGetRequest(final String urlString, final AtomicBoolean httpCancel, final NetworkCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (httpCancel.get()) {
                    Log.i("GetRequestCanceled", urlString);
                    return;
                }

                String result = "";
                boolean success = false;
                HttpURLConnection connection = null;
                InputStream stream = null;
                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();

                    if (httpCancel.get()) {
                        Log.i("GetRequestCanceled", urlString);
                        throw new IOException();
                    }

                    connection.connect();

                    int responseCode = connection.getResponseCode();

                    if (httpCancel.get()) {
                        Log.i("GetRequestCanceled", urlString);
                        throw new IOException();
                    }

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        stream = connection.getInputStream();
                        if (stream != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                            StringBuilder builder = new StringBuilder();
                            String line = reader.readLine();
                            while (line != null) {
                                builder.append(line);
                                line = reader.readLine();
                            }
                            result = builder.toString();
                            success = true;
                        }
                    } else {
                        result = String.valueOf(responseCode);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }

                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.returnResult(success, result);
                }
            }
        }).start();
    }
}
