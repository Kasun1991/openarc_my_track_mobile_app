/*
 * </summary>
 * Source File	: ReservationAdapter.java
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
import com.openarc.nirmal.mytrack.model.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    Context context;
    List<Reservation> mReservationList = new ArrayList<Reservation>();

    public ReservationAdapter(Context context, List<Reservation> mReservationList) {
        this.context = context;
        this.mReservationList = mReservationList;
    }

    @Override
    public ReservationAdapter.ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_reservation, parent, false);
        return new ReservationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReservationAdapter.ReservationViewHolder holder, int position) {
        Reservation mReservation = mReservationList.get(position);
        holder.bind(context, mReservation);
    }

    @Override
    public int getItemCount() {
        return mReservationList.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvRoom;
        private TextView tvIn;
        private TextView tvOut;
        private TextView tvReason;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvRoom = (TextView) itemView.findViewById(R.id.tvRoom);
            tvIn = (TextView) itemView.findViewById(R.id.tvIn);
            tvOut = (TextView) itemView.findViewById(R.id.tvOut);
            tvReason = (TextView) itemView.findViewById(R.id.tvReason);
        }

        public void bind(Context context, final Reservation mReservation) {
            tvDate.setText(mReservation.date);
            tvRoom.setText(mReservation.room);
            tvIn.setText(mReservation.inTime);
            tvOut.setText(mReservation.outTime);
            tvReason.setText(mReservation.reason);
        }
    }
}
