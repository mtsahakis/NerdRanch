package com.mtsahakis.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {

    private static final String TAG = "NerdLauncherFragment";

    private class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextView;
        private ImageView mImageView;
        private ResolveInfo mResolveInfo;

        public ActivityViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.list_item_nerd_launcher_text);
            mImageView = (ImageView) view.findViewById(R.id.list_item_nerd_launcher_icon);
        }

        public void bindViewHolder(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager packageManager = getActivity().getPackageManager();
            String name = mResolveInfo.loadLabel(packageManager).toString();
            Drawable iconDrawable = mResolveInfo.loadIcon(packageManager);
            mTextView.setText(name);

            int version = Build.VERSION.SDK_INT;
            if (version >= Build.VERSION_CODES.JELLY_BEAN) {
                mImageView.setBackground(iconDrawable);
            } else {
                mImageView.setBackgroundDrawable(iconDrawable);
            }
            mTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.setClassName(activityInfo.packageName, activityInfo.name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityViewHolder> {

        private List<ResolveInfo> mResolveInfoList;

        public ActivityAdapter(List<ResolveInfo> resolveInfoList) {
            mResolveInfoList = resolveInfoList;
        }

        @Override
        public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_nerd_launcher, parent, false);
            return new ActivityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityViewHolder holder, int position) {
            holder.bindViewHolder(mResolveInfoList.get(position));
        }

        @Override
        public int getItemCount() {
            return mResolveInfoList.size();
        }
    }

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_nerd_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setAdapter();
        return view;
    }

    private void setAdapter() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        Log.e(TAG, "Found " + resolveInfoList.size() + " activities");
        Collections.sort(resolveInfoList, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lhs, ResolveInfo rhs) {
                return String.CASE_INSENSITIVE_ORDER.
                        compare(lhs.loadLabel(packageManager).toString(),
                                rhs.loadLabel(packageManager).toString());
            }
        });
        ActivityAdapter activityAdapter = new ActivityAdapter(resolveInfoList);
        mRecyclerView.setAdapter(activityAdapter);
    }
}
