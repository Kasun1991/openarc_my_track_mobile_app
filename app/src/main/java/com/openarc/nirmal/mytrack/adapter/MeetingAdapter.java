/*
 * </summary>
 * Source File	: MeetingAdapter.java
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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openarc.nirmal.mytrack.R;
import com.openarc.nirmal.mytrack.model.MeetingRoom;

import java.util.ArrayList;
import java.util.List;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {

    Context context;
    OnItemClickListener clickListener;
    List<MeetingRoom> mMeetingRoomList = new ArrayList<MeetingRoom>();

    public MeetingAdapter(Context context, OnItemClickListener clickListener, List<MeetingRoom> mMeetingRoomList) {
        this.context = context;
        this.clickListener = clickListener;
        this.mMeetingRoomList = mMeetingRoomList;
    }

    @Override
    public MeetingAdapter.MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_meeting_room, parent, false);
        return new MeetingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MeetingAdapter.MeetingViewHolder holder, int position) {
        MeetingRoom mMeetingRoom = mMeetingRoomList.get(position);
        holder.bind(context, mMeetingRoom, clickListener);
    }

    @Override
    public int getItemCount() {
        return mMeetingRoomList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(MeetingRoom mMeetingRoom);
    }

    public static class MeetingViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvStatus;

        public MeetingViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Context context, final MeetingRoom mMeetingRoom, final OnItemClickListener clickListener) {
            tvName.setText(mMeetingRoom.name);
            tvStatus.setText(mMeetingRoom.status);

            if (mMeetingRoom.status.contentEquals("Available")) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(mMeetingRoom);
                }
            });
        }
    }
}
