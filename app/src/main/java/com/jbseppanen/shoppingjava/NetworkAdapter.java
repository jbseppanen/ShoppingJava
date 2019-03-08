package com.jbseppanen.shoppingjava;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkAdapter {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final int TIMEOUT = 3000;


    public interface NetworkCallback {
        void returnResult(Boolean success, String result);
    }

    public static void httpRequest(final String stringUrl, final String requestType, final String jsonBody, final NetworkCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                boolean success = false;
                InputStream stream = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(stringUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(TIMEOUT);
                    connection.setConnectTimeout(TIMEOUT);
                    connection.setRequestMethod(requestType);
                    connection.setRequestProperty("Content-Type", "application/json");

                    if (requestType.equals(GET) || requestType.equals(DELETE)) {
                        connection.connect();
                    } else if (requestType.equals(POST) || requestType.equals(PUT)) {
                        if (jsonBody != null) {
                            OutputStream outputStream = connection.getOutputStream();
                            outputStream.write(jsonBody.getBytes());
                            outputStream.close();
                        }
                    }

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        stream = connection.getInputStream();
                        if (stream != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            result = builder.toString();
                            success = true;
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    result = e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = e.getMessage();
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
                }
                callback.returnResult(success, result);
            }
        }).start();
    }
}
