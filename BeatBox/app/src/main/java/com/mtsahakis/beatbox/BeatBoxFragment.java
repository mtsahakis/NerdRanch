package com.mtsahakis.beatbox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class BeatBoxFragment extends Fragment {

    private class SoundHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Button mButton;
        private Sound mSound;

        public SoundHolder(View itemView) {
            super(itemView);
            mButton = (Button) itemView.findViewById(R.id.list_item_button);
            mButton.setOnClickListener(this);
        }

        public void bindSound(Sound sound) {
            mSound = sound;
            mButton.setText(mSound.getName());
        }

        @Override
        public void onClick(View v) {
            mBeatBox.play(mSound);
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_sound, parent, false);
            return new SoundHolder(view);
        }

        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
            holder.bindSound(mSounds.get(position));
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }

    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    private RecyclerView    mRecyclerView;
    private BeatBox         mBeatBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBeatBox = new BeatBox(getActivity());
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beat_box, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_beat_box_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }
}
