package com.example.xuke.photogallery2;

import android.app.AlarmManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xuke on 2015/8/19.
 */
public class PhotoGalleryFragment extends Fragment {
    GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mGridView = (GridView) v.findViewById(R.id.gridView);
        return v;
    }

    public class FetchItemsTask extends AsyncTask<Void, Void, Void> {

        private static final String TAG = "PhotoGalleryFragment";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Map<String, String> parameter = new HashMap<String, String>();

                long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                Date d1 = new Date(time);
                String t1 = format.format(d1);
                String requestURL = "http://route.showapi.com/197-1?showapi_appid=6423&showapi_timestamp=" + t1 + "&num=10&page=1&showapi_sign=85a76ead7c40415b9f6401e9046a6413";
                String result = new FlickrFetchr().getUrl(requestURL);
                Log.i(TAG, "Fetched contents of URL: " + result);
                JSONObject rootObj = new JSONObject(result);
                if (rootObj.getString("showapi_res_code").equals("0")) {
                    JSONObject resBody = new JSONObject(rootObj.getString("showapi_res_body"));
                    try {
                        Iterator it = resBody.keys();
                        while (it.hasNext()) {
                            String key = (String) it.next();
                            String value = resBody.getString(key);
                            if (value.charAt(0) == '{') {
                                Log.i(TAG, "VALUE IS " + value);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Log.e(TAG, "Failed to fetch URL: ", ioe);
            } catch (JSONException jsone) {
                Log.i(TAG, "JSONTokener error");
                jsone.printStackTrace();
            }

            return null;
        }
    }
}

