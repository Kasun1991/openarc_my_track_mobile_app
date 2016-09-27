/*
 * </summary>
 * Source File	: GatePassAdapter.java
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
import com.openarc.nirmal.mytrack.model.GatePass;

import java.util.List;

public class GatePassAdapter extends RecyclerView.Adapter<GatePassAdapter.GatePassViewHolder> {

    public OnItemLongClickListener mOnItemLongClickListener;
    List<GatePass> mGatePassList;
    Context context;

    public GatePassAdapter(Context context, List<GatePass> mGatePassList, OnItemLongClickListener mOnItemLongClickListener) {
        this.context = context;
        this.mGatePassList = mGatePassList;
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @Override
    public GatePassAdapter.GatePassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_gate_pass, parent, false);
        return new GatePassViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GatePassAdapter.GatePassViewHolder holder, int position) {
        GatePass mGatePass = mGatePassList.get(position);
        holder.bind(context, mGatePass, mOnItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mGatePassList.size();
    }

    public interface OnItemLongClickListener {
        void OnItemLongClickListener(GatePass mGatePass);
    }

    public static class GatePassViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStatus;
        private TextView tvDate;
        private TextView tvReqFrom;
        private TextView tvReqTo;
        private TextView tvReason;

        public GatePassViewHolder(View itemView) {
            super(itemView);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvReqFrom = (TextView) itemView.findViewById(R.id.tvReqFrom);
            tvReqTo = (TextView) itemView.findViewById(R.id.tvReqTo);
            tvReason = (TextView) itemView.findViewById(R.id.tvReason);
        }

        public void bind(Context context, final GatePass mGatePass, final OnItemLongClickListener longClickListener) {
            tvStatus.setText(mGatePass.status);
            tvDate.setText(mGatePass.date);
            tvReqFrom.setText(mGatePass.requestFrom);
            tvReqTo.setText(mGatePass.requestTo);
            tvReason.setText(mGatePass.reason);

            if (mGatePass.status.contentEquals("A")) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            } else if (mGatePass.status.contentEquals("P")) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.amber));
            } else {
                tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.OnItemLongClickListener(mGatePass);
                    return true;
                }
            });
        }
    }
}
