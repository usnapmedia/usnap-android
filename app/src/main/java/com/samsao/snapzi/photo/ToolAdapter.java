/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.photo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.tools.Tool;

import java.util.ArrayList;

/**
 * @author  jfcartier
 * @since   4/06/2015
 */
public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ViewHolder> {

    protected ArrayList<Tool> mData;

    public ToolAdapter(ArrayList<Tool> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_photo_edit_tool_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Tool tool = mData.get(position);
        holder.setup(tool);
        holder.itemView.setOnClickListener(tool.getOnClickListener());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ImageView mImage;

        public ViewHolder(View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.fragment_photo_edit_tool_name);
            mImage = (ImageView) view.findViewById(R.id.fragment_photo_edit_tool_image);
        }

        public void setup(Tool tool) {
            mName.setText(tool.getName());
            mImage.setImageResource(tool.getImageResource());
        }
    }
}
