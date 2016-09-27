/*
 * </summary>
 * Source File	: MAWHAdapter.java
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
import com.openarc.nirmal.mytrack.model.MAWHDetail;

import java.util.List;

public class MAWHAdapter extends RecyclerView.Adapter<MAWHAdapter.MAWHViewHolder> {

    List<MAWHDetail> mawhDetails;
    private Context context;

    public MAWHAdapter(List<MAWHDetail> mawhDetails, Context context) {
        this.mawhDetails = mawhDetails;
        this.context = context;
    }

    @Override
    public MAWHAdapter.MAWHViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adpater_row_mawh, parent, false);
        return new MAWHViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MAWHAdapter.MAWHViewHolder holder, int position) {
        MAWHDetail mawhDetailObject = mawhDetails.get(position);
        holder.bind(context, mawhDetailObject);
    }

    @Override
    public int getItemCount() {
        return mawhDetails.size();
    }

    public static class MAWHViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvMAWH;
        private TextView tvCAWH;

        public MAWHViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvMAWH = (TextView) itemView.findViewById(R.id.tvMAWH);
            tvCAWH = (TextView) itemView.findViewById(R.id.tvCAWH);
        }

        public void bind(Context context, final MAWHDetail mawhDetail) {

            if (!mawhDetail.MAWH.contentEquals("") && !(Double.parseDouble(mawhDetail.MAWH) > 9.5))
                tvMAWH.setTextColor(context.getResources().getColor(R.color.red));

            if (!mawhDetail.CAWH.contentEquals("") && !(Double.parseDouble(mawhDetail.CAWH) > 9.5))
                tvCAWH.setTextColor(context.getResources().getColor(R.color.red));

            tvDate.setText(mawhDetail.date);
            tvMAWH.setText(mawhDetail.MAWH);
            tvCAWH.setText(mawhDetail.CAWH);

        }
    }
}
