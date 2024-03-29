/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.edit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;

import java.util.ArrayList;

/**
 * @author  jfcartier
 * @since   4/06/2015
 */
public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

    protected ArrayList<MenuItem> mData;

    public MenuItemAdapter(ArrayList<MenuItem> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_edit_tool_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MenuItem item = mData.get(position);
        holder.setup(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.onSelected();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(ArrayList<MenuItem> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ImageView mImage;

        public ViewHolder(View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.fragment_edit_tool_name);
            mImage = (ImageView) view.findViewById(R.id.fragment_edit_tool_image);
        }

        public void setup(MenuItem item) {
            mName.setText(item.getName());
            if (item.isSelected()) {
                mName.setTextColor(SnapziApplication.getContext().getResources().getColor(R.color.primary));
            } else {
                mName.setTextColor(SnapziApplication.getContext().getResources().getColor(android.R.color.white));
            }
            mImage.setImageResource(item.getImageResource());
        }
    }
}
