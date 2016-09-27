/*
 * </summary>
 * Source File	: LeaveAdapter.java
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
import com.openarc.nirmal.mytrack.model.Leave;

import java.util.List;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.LeaveViewHolder> {

    List<Leave> leaves;
    Context context;

    public LeaveAdapter(List<Leave> leaves, Context context) {
        this.leaves = leaves;
        this.context = context;
    }

    @Override
    public LeaveAdapter.LeaveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adpater_row_leave_taken, parent, false);
        return new LeaveViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LeaveAdapter.LeaveViewHolder holder, int position) {
        Leave leave = leaves.get(position);
        holder.bind(context, leave);
    }

    @Override
    public int getItemCount() {
        return leaves.size();
    }

    public static class LeaveViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDuration;
        private TextView tvFrom;
        private TextView tvTo;
        private TextView tvAppliedDate;
        private TextView tvReason;

        public LeaveViewHolder(View itemView) {
            super(itemView);
            tvDuration = (TextView) itemView.findViewById(R.id.tvDuration);
            tvFrom = (TextView) itemView.findViewById(R.id.tvFrom);
            tvTo = (TextView) itemView.findViewById(R.id.tvTo);
            tvAppliedDate = (TextView) itemView.findViewById(R.id.tvAppliedDate);
            tvReason = (TextView) itemView.findViewById(R.id.tvReason);
        }

        public void bind(Context context, final Leave leave) {
            tvDuration.setText(leave.duration);
            tvFrom.setText(leave.fromDate);
            tvTo.setText(leave.toDate);
            tvAppliedDate.setText(leave.dateApplied);
            tvReason.setText(leave.reason);
        }
    }
}
