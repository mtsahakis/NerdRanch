package com.mtsahakis.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends VisibleFragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private List<GalleryItem> mGalleryItems = new ArrayList<>();
    private int mCurrentPage;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    private Handler mHandler;
    private ProgressBar mProgressBar;

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;
        private GalleryItem mgalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindDrawable(Drawable drawable) {
            mImageView.setImageDrawable(drawable);
        }

        public void bindItem(GalleryItem galleryItem) {
            mgalleryItem = galleryItem;
        }

        @Override
        public void onClick(View v) {
            startActivity(PhotoPageActivity.newIntent(getActivity(), mgalleryItem.getPhotoPageUri()));
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        List<GalleryItem> mItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        @Deprecated
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mItems.get(position);
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getURL());

            // add placeholder picture
            Drawable placeholder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                placeholder = getResources().getDrawable(R.drawable.bill_up_close, getActivity().getTheme());
            } else {
                placeholder = getResources().getDrawable(R.drawable.bill_up_close);
            }
            holder.bindDrawable(placeholder);
            holder.bindItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void setItems(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            notifyDataSetChanged();
        }
    }

    private class FetchItemsTask extends AsyncTask<Integer, Void, List<GalleryItem>> {

        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Integer... params) {
            Log.i(TAG, "doInBackground(" + params[0] + ")");

            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos(params[0]);
            } else {
                return new FlickrFetchr().searchPhotos(params[0], mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            Log.i(TAG, "onPostExecute()");
            mGalleryItems.addAll(galleryItems);
            hideProgressBar();
            setUpAdapter();
        }
    }

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Orientation change handling
        setRetainInstance(true);
        // Toolbar Menu
        setHasOptionsMenu(true);
        // init UI Thread Handler
        mHandler = new Handler();
        // init HandlerThread (Message Loop)
        mThumbnailDownloader = new ThumbnailDownloader<>();
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(final PhotoHolder target, final Bitmap bitmap) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                target.bindDrawable(drawable);
                            }
                        });
                    }
                });
        // start FetchItems AsyncTAsk
        updateItems();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.photo_gallery_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setUpAdapter();
        mRecyclerView.setAdapter(mPhotoAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLastItemDisplaying()) {
                    mCurrentPage++;
                    updateItems();
                }
            }
        });
        ViewTreeObserver viewTreeObserver = mRecyclerView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                calculateCellSize();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        final MenuItem menuItemSearch = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit: " + query);
                AppUtils.hideKeyboard(getActivity(), searchView);
                menuItemSearch.collapseActionView();
                showProgressBar();
                submitQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getSearchQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

        final MenuItem menuItemPolling = menu.findItem(R.id.menu_item_polling);
        if (PollService.isAlarmOn(getActivity())) {
            menuItemPolling.setTitle(getString(R.string.stop_polling));
        } else {
            menuItemPolling.setTitle(getString(R.string.start_polling));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_cancel:
                submitQuery(null);
                return true;
            case R.id.menu_item_polling:
                boolean isAlarmOn = PollService.isAlarmOn(getActivity());
                PollService.setAlarmService(getActivity(), !isAlarmOn);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitQuery(String query) {
        QueryPreferences.setSearchQuery(getActivity(), query);
        mCurrentPage = 0;
        mGalleryItems = new ArrayList<>();
        mPhotoAdapter.setItems(mGalleryItems);
        updateItems();
    }

    private void updateItems() {
        String query = QueryPreferences.getSearchQuery(getActivity());
        FetchItemsTask fetchItemsTask = new FetchItemsTask(query);
        fetchItemsTask.execute(mCurrentPage);
    }

    private void hideProgressBar() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showProgressBar() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private static final int sColumnWidth = 120;

    private void calculateCellSize() {
        int spanCount = (int) Math.ceil(mRecyclerView.getWidth() / convertDPToPixels(sColumnWidth));
        ((GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanCount(spanCount);
    }

    private float convertDPToPixels(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        return dp * logicalDensity;
    }

    private void setUpAdapter() {
        if (isAdded()) {
            if (mPhotoAdapter == null) {
                mPhotoAdapter = new PhotoAdapter(mGalleryItems);
            } else {
                mPhotoAdapter.setItems(mGalleryItems);
            }
        }
    }

    private boolean isLastItemDisplaying() {
        RecyclerView.Adapter galleryAdapter = mRecyclerView.getAdapter();
        if (galleryAdapter != null && galleryAdapter.getItemCount() != 0) {
            int lastVisibleItemPosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == galleryAdapter.getItemCount() - 1) {
                return true;
            }
        }
        return false;
    }
}
