package com.thirdandloom.storyflow.adapters.holder;

import com.thirdandloom.storyflow.R;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReadStoriesHeaderViewHolder extends RecyclerView.ViewHolder {

    public ReadStoriesHeaderViewHolder(View itemView) {
        super(itemView);
        boldDateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_reading_stories_header_bold_text);
        dateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_reading_stories_header_textview);
    }

    public static ReadStoriesHeaderViewHolder newInstance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_header, parent, false);
        return new ReadStoriesHeaderViewHolder(itemView);
    }

    public TextView dateTextView;
    public TextView boldDateTextView;

    public void setDateRepresentation(String boldText, String formattedDate) {
        dateTextView.setText(formattedDate);
        boldDateTextView.setText(boldText);
    }
}
