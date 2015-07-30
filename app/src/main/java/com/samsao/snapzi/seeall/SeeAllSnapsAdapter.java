package com.samsao.snapzi.seeall;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.Snap;
import com.samsao.snapzi.fan_page.PhotoDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import timber.log.Timber;


/**
 * @author jfcartier
 * @since 2015-05-05
 */
public class SeeAllSnapsAdapter extends RecyclerView.Adapter<SeeAllSnapsAdapter.SeeAllSnapsViewHolder> {

    private Context mContext;
    private List<Snap> mSnaps;

    public SeeAllSnapsAdapter(Context context) {
        mContext = context;
        mSnaps = null;
    }

    @Override
    public int getItemCount() {
        if (mSnaps == null) {
            return 0;
        } else {
            return mSnaps.size();
        }
    }

    @Override
    public void onBindViewHolder(SeeAllSnapsViewHolder seeAllSnapsViewHolder, int position) {
        Snap snap = mSnaps.get(position);
        seeAllSnapsViewHolder.setup(snap);
    }

    @Override
    public SeeAllSnapsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_see_all_item, parent, false);
        return new SeeAllSnapsViewHolder(view);
    }

    public void setSnaps(List<Snap> snaps) {
        mSnaps = snaps;
        notifyDataSetChanged();
    }

    public void clear() {
        mSnaps.clear();
        mSnaps = null;
    }

    public class SeeAllSnapsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public SeeAllSnapsViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view;

        }

        public void setup(final Snap image) {
            Picasso.with(mContext).setIndicatorsEnabled(true);
            String url = image.getThumbnail(300,300);

            //Timber.d(mImageView.getMeasuredWidth() + " " + mImageView.getMeasuredWidth());
            Picasso.with(mContext).load(url).into(mImageView);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoDetailsActivity.start(image, mContext);
                }
            });
        }
    }
}

