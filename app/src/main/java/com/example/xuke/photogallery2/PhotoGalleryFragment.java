package com.example.xuke.photogallery2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

            new FlickrFetchr().fetchItems();
            return null;
        }
    }
}

