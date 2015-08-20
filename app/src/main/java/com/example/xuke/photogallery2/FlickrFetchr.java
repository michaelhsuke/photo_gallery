package com.example.xuke.photogallery2;

import android.net.Uri;
import android.util.Log;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class FlickrFetchr {

    public static final String TAG = "FlickrFetchr";

    private static final String ENDPOINT = "http://route.showapi.com/197-1";
    private static final String APPID = "6423";
    private static final String SECRET = "85a76ead7c40415b9f6401e9046a6413";
    private static final String PAGE = "1";
    private static final String NUM = "100";

    // 创建时间戳
    private String createTimestamp() {
        long time = System.currentTimeMillis(); // long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(time);
        String timestamp = format.format(date);
        return timestamp;
    }

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
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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

    public ArrayList<GalleryItem> fetchItems() {

        ArrayList<GalleryItem> items = new ArrayList<>();
        try {
            String timestamp = createTimestamp();
            String url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("showapi_appid", APPID)
                    .appendQueryParameter("showapi_timestamp", timestamp)
                    .appendQueryParameter("num", NUM)
                    .appendQueryParameter("page", PAGE)
                    .appendQueryParameter("showapi_sign", SECRET)
                    .build().toString();

            String JSONString = getUrl(url);
            parseItems(items, JSONString);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    private void parseItems(ArrayList<GalleryItem> items, String json) throws JSONException {
        JSONObject rootObj = new JSONObject(json);
        if (rootObj.getString("showapi_res_code").equals("0")) {
            JSONObject resBody = new JSONObject(rootObj.getString("showapi_res_body"));
            try {
                Iterator it = resBody.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = resBody.getString(key);
                    Log.i(TAG, "RES BODY VALUE IS " + value);
                    if (value.charAt(0) == '{') {
                        JSONObject jsonPicture = new JSONObject(value);
                        GalleryItem item = new GalleryItem();
                        item.setTitle(jsonPicture.getString("title"));
                        item.setDescription(jsonPicture.getString("description"));
                        item.setPicUrl(jsonPicture.getString("picUrl"));

                        items.add(item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}