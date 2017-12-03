package com.ikdiabate.distance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ikdiabate.distance.data.Distance;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter to manage real time load of videos from the YouTube API.
 * @Author: Ibrahim Diabate
 * @Version: November 2017
 */

public class VideoAdapter extends ArrayAdapter<Distance> {

    private CircleImageView profileImage;

    public VideoAdapter(Context context, List<Distance> videoStreams) {
        super(context, 0, videoStreams);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.distance_items, parent, false);
        }

        Distance currentStream = getItem(position);

        profileImage = (CircleImageView) listItemView.findViewById(R.id.imageView);
        String imageUrl = currentStream.getThumbnail();

        TextView title = (TextView) listItemView.findViewById(R.id.video_title);
        String textTitle = currentStream.getTitle();
        title.setText(textTitle);

        Glide.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.load)
                .into(profileImage);


        TextView author = (TextView) listItemView.findViewById(R.id.author);
        String authorName = currentStream.getChannelTitle();
        author.setText(authorName);
        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }


}
