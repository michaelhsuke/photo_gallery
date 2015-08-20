package com.example.xuke.photogallery2;

import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";

    byte[] postUrlBytes(String urlSpec, Map<String, String> parameter) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 输入参数
        StringBuilder sb = new StringBuilder();
        if (parameter != null && !parameter.isEmpty()) {
            for (Map.Entry<String, String> entry : parameter.entrySet()) {
                sb.append(entry.getKey())
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), HTTP.UTF_8))
                        .append('&');
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        Log.i(TAG, "POST PARAMETER IS " + sb.toString());

        try {
            byte[] entitydata = sb.toString().getBytes();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5 * 1000);
            connection.setDoOutput(true);  // 需要输入参数
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(entitydata.length));

            // 猜测真正的连接网络发生在getOutputStream之后，GET请求是发生在getInputStream之后
            OutputStream out = connection.getOutputStream();
            out.write(entitydata);
            out.flush();
            out.close();

            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                byteOut.write(buffer, 0, bytesRead);
            }
            byteOut.close();
            return byteOut.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String postUrl(String urlSpec, Map<String, String> parameter) throws IOException {
        return new String(postUrlBytes(urlSpec, parameter));
    }

    byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}