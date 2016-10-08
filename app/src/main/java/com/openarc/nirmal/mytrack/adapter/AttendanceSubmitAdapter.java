/*
 * </summary>
 * Source File	: AttendanceSubmitAdapter.java
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
import com.openarc.nirmal.mytrack.model.AttendanceSubmit;

import java.util.List;

public class AttendanceSubmitAdapter extends RecyclerView.Adapter<AttendanceSubmitAdapter.AttendanceSubmitViewHolder> {

    public OnItemLongClickListener mOnItemLongClickListener;
    List<AttendanceSubmit> mAttendanceSubmitList;
    Context context;

    public AttendanceSubmitAdapter(Context context, List<AttendanceSubmit> mAttendanceSubmitList, OnItemLongClickListener mOnItemLongClickListener) {
        this.context = context;
        this.mAttendanceSubmitList = mAttendanceSubmitList;
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @Override
    public AttendanceSubmitAdapter.AttendanceSubmitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_attendance_submit, parent, false);
        return new AttendanceSubmitViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AttendanceSubmitAdapter.AttendanceSubmitViewHolder holder, int position) {
        AttendanceSubmit mAttendanceSubmit = mAttendanceSubmitList.get(position);
        holder.bind(context, mAttendanceSubmit, mOnItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mAttendanceSubmitList.size();
    }

    public interface OnItemLongClickListener {
        void OnItemLongClickListener(AttendanceSubmit mAttendanceSubmit);
    }

    public static class AttendanceSubmitViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStatus;
        private TextView tvDate;
        private TextView tvTimeIn;
        private TextView tvTimeOut;
        private TextView tvReason;

        public AttendanceSubmitViewHolder(View itemView) {
            super(itemView);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTimeIn = (TextView) itemView.findViewById(R.id.tvTimeIn);
            tvTimeOut = (TextView) itemView.findViewById(R.id.tvTimeOut);
            tvReason = (TextView) itemView.findViewById(R.id.tvReason);
        }

        public void bind(Context context, final AttendanceSubmit mAttendanceSubmit, final OnItemLongClickListener longClickListener) {
            tvStatus.setText(mAttendanceSubmit.status);
            tvDate.setText(mAttendanceSubmit.date);
            tvTimeIn.setText(mAttendanceSubmit.timeIn);
            tvTimeOut.setText(mAttendanceSubmit.timeOut);
            tvReason.setText(mAttendanceSubmit.remark);

            if (mAttendanceSubmit.status.contentEquals("A")) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            } else if (mAttendanceSubmit.status.contentEquals("P")) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.amber));
            } else {
                tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.OnItemLongClickListener(mAttendanceSubmit);
                    return true;
                }
            });
        }
    }
}
