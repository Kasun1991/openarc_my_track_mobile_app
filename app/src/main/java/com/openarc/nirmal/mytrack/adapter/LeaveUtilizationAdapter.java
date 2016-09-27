/*
 * </summary>
 * Source File	: LeaveUtilizationAdapter.java
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
import com.openarc.nirmal.mytrack.model.LeaveUtilization;

import java.util.List;

public class LeaveUtilizationAdapter extends RecyclerView.Adapter<LeaveUtilizationAdapter.LeaveUtilizationViewHolder> {

    private List<LeaveUtilization> leaveUtilizations;
    private Context context;
    private OnItemClickListener clickListener;

    public LeaveUtilizationAdapter(List<LeaveUtilization> leaveUtilizations, Context context, OnItemClickListener clickListener) {
        this.leaveUtilizations = leaveUtilizations;
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public LeaveUtilizationAdapter.LeaveUtilizationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_leave_utilization, parent, false);
        return new LeaveUtilizationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LeaveUtilizationAdapter.LeaveUtilizationViewHolder holder, int position) {
        LeaveUtilization leaveUtilizationObject = leaveUtilizations.get(position);
        holder.bind(context, leaveUtilizationObject, clickListener);
    }

    @Override
    public int getItemCount() {
        return leaveUtilizations.size();
    }

    public interface OnItemClickListener {
        void onItemClick(LeaveUtilization leaveUtilization);
    }

    public static class LeaveUtilizationViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLeaveName;
        private TextView tvUtilized;
        private TextView tvAvailable;

        public LeaveUtilizationViewHolder(View itemView) {
            super(itemView);
            tvLeaveName = (TextView) itemView.findViewById(R.id.tvLeaveName);
            tvUtilized = (TextView) itemView.findViewById(R.id.tvUtilized);
            tvAvailable = (TextView) itemView.findViewById(R.id.tvAvailable);
        }

        public void bind(Context context, final LeaveUtilization leaveUtilization, final OnItemClickListener clickListener) {
            tvLeaveName.setText(leaveUtilization.name);
            tvUtilized.setText(leaveUtilization.utilize);
            tvAvailable.setText(leaveUtilization.available);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(leaveUtilization);
                }
            });
        }
    }
}
