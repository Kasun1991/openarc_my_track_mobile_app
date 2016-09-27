/*
 * </summary>
 * Source File	: NewsfeedAdapter.java
 * Project		: MyTrack
 * Module		: app
 * Owner		: nirmal
 * </summary>
 *
 * <license>
 * Copyright 2016 Kasun Gunathilaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </license>
 */

package com.openarc.nirmal.mytrack.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openarc.nirmal.mytrack.R;
import com.openarc.nirmal.mytrack.model.Newsfeed;

import java.util.List;

public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.NewsfeedViewHolder> {

    List<Newsfeed> newsfeed;

    public NewsfeedAdapter(List<Newsfeed> newsfeed) {
        this.newsfeed = newsfeed;
    }

    @Override
    public NewsfeedAdapter.NewsfeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_news_feed, parent, false);
        return new NewsfeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsfeedAdapter.NewsfeedViewHolder holder, int position) {
        Newsfeed newsfeedObject = newsfeed.get(position);
        holder.bind(newsfeedObject);
    }

    @Override
    public int getItemCount() {
        return newsfeed.size();
    }

    public static class NewsfeedViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNewsFeed;
        private TextView tvTimeStamp;

        public NewsfeedViewHolder(View itemView) {
            super(itemView);
            tvNewsFeed = (TextView) itemView.findViewById(R.id.tvNewsFeed);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
        }

        public void bind(final Newsfeed newsfeed) {
            tvNewsFeed.setText(newsfeed.newsFeed);
            tvTimeStamp.setText(newsfeed.timeStamp);

        }
    }
}
