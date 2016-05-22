package com.mtsahakis.locatr;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class LocatrFragment extends SupportMapFragment {

    public static final String TAG = "LocatrFragment";

    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    private GalleryItem mMapItem;
    private Bitmap mMapImage;
    private Location mCurrentLocation;
    private GoogleMap mGoogleMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(LocationServices.API)
                .build();
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);
        boolean isConnected = mGoogleApiClient != null && mGoogleApiClient.isConnected();
        menu.findItem(R.id.action_search).setEnabled(isConnected);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                findImage();
                return true;
            case R.id.action_settings:
                Log.e(TAG, "action_settings");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findImage() {
        mProgressDialog = ProgressDialog.show(getActivity(), null, "Locating Image", true);

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        new SearchTask().execute(location);
                    }
                });
    }

    private class SearchTask extends AsyncTask<Location, Void, Void> {

        private GalleryItem mGalleryItem;
        private Bitmap mBitmap;
        private Location mLocation;

        @Override
        protected Void doInBackground(Location... params) {
            mLocation = params[0];
            FlickrFetchr flickrFetchr = new FlickrFetchr();
            List<GalleryItem> galleryItems = flickrFetchr.searchPhotos(params[0]);

            if (galleryItems == null || galleryItems.isEmpty()) {
                return null;
            }

            mGalleryItem = galleryItems.get(0);
            try {
                mBitmap = flickrFetchr.getUrlBitmap(mGalleryItem.getURL());
            } catch (IOException e) {
                Log.e("SearchTask", "Unable to download bitmap", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressDialog.dismiss();
            mMapItem = mGalleryItem;
            mMapImage = mBitmap;
            mCurrentLocation = mLocation;

            updateUI();
        }
    }

    private void updateUI() {
        // return if no GoogleMap, Flickr or my location instances are not available
        if (mGoogleMap == null || mMapItem == null || mMapImage == null || mCurrentLocation == null) {
            return;
        }

        // my location and item location LatLng
        LatLng myLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLng itemLatLng = new LatLng(mMapItem.getLat(), mMapItem.getLon());

        // add map markers
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(mMapImage);
        MarkerOptions myMarkerOptions = new MarkerOptions()
                .position(myLatLng);
        MarkerOptions itemMarkerOptions = new MarkerOptions()
                .position(itemLatLng)
                .icon(bitmapDescriptor);
        mGoogleMap.clear();
        mGoogleMap.addMarker(myMarkerOptions);
        mGoogleMap.addMarker(itemMarkerOptions);

        // animate to locations
        LatLngBounds bounds = LatLngBounds
                .builder()
                .include(myLatLng)
                .include(itemLatLng)
                .build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mGoogleMap.animateCamera(cameraUpdate);
    }
}
