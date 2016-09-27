/*
 * </summary>
 * Source File	: AttendanceAdapter.java
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openarc.nirmal.mytrack.R;
import com.openarc.nirmal.mytrack.model.Attendance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    List<Attendance> attendances;
    Context context;

    public AttendanceAdapter(List<Attendance> attendances, Context context) {
        this.attendances = attendances;
        this.context = context;
    }

    @Override
    public AttendanceAdapter.AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adpater_row_attendance, parent, false);
        return new AttendanceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AttendanceAdapter.AttendanceViewHolder holder, int position) {
        Attendance attendanceObject = attendances.get(position);
        holder.bind(context, attendanceObject);
    }

    @Override
    public int getItemCount() {
        return attendances.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvTimeIn;
        private TextView tvTimeOut;
        private TextView tvWorkHour;
        private TextView tvWorkMints;
        private TextView tvWorkSec;
        private TextView tvRemark;
        private LinearLayout llRowBackground;

        public AttendanceViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTimeIn = (TextView) itemView.findViewById(R.id.tvTimeIn);
            tvTimeOut = (TextView) itemView.findViewById(R.id.tvTimeOut);
            tvWorkHour = (TextView) itemView.findViewById(R.id.tvWorkHour);
            tvWorkMints = (TextView) itemView.findViewById(R.id.tvWorkMints);
            tvWorkSec = (TextView) itemView.findViewById(R.id.tvWorkSec);
            tvRemark = (TextView) itemView.findViewById(R.id.tvRemark);
            llRowBackground = (LinearLayout) itemView.findViewById(R.id.llRowBackground);
        }

        public void bind(Context context, final Attendance attendance) {
            if (!attendance.hours.contentEquals("")) {
                List<String> list = new ArrayList<String>(Arrays.asList(attendance.hours.split(":")));
                tvWorkHour.setText(list.get(0) + "h");
                tvWorkMints.setText(list.get(1) + "m");
                tvWorkSec.setText(list.get(2) + "s");
            } else {
                tvWorkHour.setText("0h");
                tvWorkMints.setText("0m");
                tvWorkSec.setText("0s");
            }
            if (attendance.remark.trim().contentEquals("")) {
                llRowBackground.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            } else if (attendance.remark.trim().contentEquals("Today")) {
                llRowBackground.setBackgroundColor(context.getResources().getColor(R.color.light_green));
            } else if (attendance.remark.trim().toLowerCase().contains("day")) {
                llRowBackground.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
            } else if (attendance.remark.trim().toLowerCase().contains("leave")) {
                llRowBackground.setBackgroundColor(context.getResources().getColor(R.color.light_green));
            } else if (attendance.remark.trim().contentEquals("No Pay"))
                llRowBackground.setBackgroundColor(context.getResources().getColor(R.color.light_red));

            tvDate.setText(attendance.date);
            tvTimeIn.setText(attendance.in);
            tvTimeOut.setText(attendance.out);
            tvRemark.setText(attendance.remark);
        }
    }
}
