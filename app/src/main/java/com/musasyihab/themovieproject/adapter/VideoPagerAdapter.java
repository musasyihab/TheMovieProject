package com.musasyihab.themovieproject.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.musasyihab.themovieproject.R;
import com.musasyihab.themovieproject.model.VideoModel;
import com.musasyihab.themovieproject.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by musasyihab on 8/5/17.
 */

public class VideoPagerAdapter extends PagerAdapter {
    private List<VideoModel> videoList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public VideoPagerAdapter(Activity activity, List<VideoModel> videoList, LayoutInflater mInflater) {
        this.videoList = videoList;
        this.mInflater = mInflater;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final VideoModel video = videoList.get(position);
        View view = mInflater.inflate(R.layout.video_item_layout, container, false);

        ImageView mVideoThumb = (ImageView) view.findViewById(R.id.video_item_thumb);
        ImageView mVideoPlay = (ImageView) view.findViewById(R.id.video_item_play);
        TextView mVideoTitle = (TextView) view.findViewById(R.id.video_item_title);

        mVideoTitle.setText(video.getName());
        String thumbUrl = Constants.YOUTUBE_THUMB_URL + video.getKey() + Constants.YOUTUBE_THUMB_QUALITY;
        Glide.with(activity).load(thumbUrl).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).into(mVideoThumb);

        mVideoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trailerUrl = Constants.YOUTUBE_VIDEO_URL + video.getKey();
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl)));
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
