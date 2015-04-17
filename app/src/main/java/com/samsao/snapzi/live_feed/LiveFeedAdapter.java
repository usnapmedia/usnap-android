package com.samsao.snapzi.live_feed;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.samsao.snapzi.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author jingsilu
 * @since 2015-04-17
 */
public class LiveFeedAdapter extends RecyclerView.Adapter<LiveFeedAdapter.LiveFeedViewHolder> {

    private List<ImageLiveFeed> imgLiveFeedList;

    public LiveFeedAdapter(List<ImageLiveFeed> imgLiveFeedList) {
        this.imgLiveFeedList = imgLiveFeedList;
    }

    @Override
    public int getItemCount() {
        return imgLiveFeedList.size();
    }

    @Override
    public void onBindViewHolder(LiveFeedViewHolder liveFeedViewHolder, int position) {
        ImageLiveFeed imgLiveFeed = imgLiveFeedList.get(position);


        if (position < (getItemCount() - 1)) {
            liveFeedViewHolder.itemView.setPadding(0,0,10,0);
        } else {
            liveFeedViewHolder.itemView.setPadding(0,0,0,0);
        }

        Context context = liveFeedViewHolder.imgIcon.getContext();
        Picasso.with(context).load(imgLiveFeed.getPath()).into(liveFeedViewHolder.imgIcon);
    }

    @Override
    public LiveFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View imgView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_livefeed, parent, false);
        return new LiveFeedViewHolder(imgView);
    }

    public static class LiveFeedViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;

        public LiveFeedViewHolder(View v) {
            super(v);
            imgIcon = (ImageView) v.findViewById(R.id.img_view_id);

        }
    }
}

