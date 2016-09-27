/*
 * </summary>
 * Source File	: BirthdayAdapter.java
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
import android.widget.ImageView;
import android.widget.TextView;

import com.openarc.nirmal.mytrack.R;
import com.openarc.nirmal.mytrack.model.Birthday;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class BirthdayAdapter extends RecyclerView.Adapter<BirthdayAdapter.BirthdayViewHolder> {

    List<Birthday> birthdays;
    private Context context;

    public BirthdayAdapter(Context context, List<Birthday> birthdays) {
        this.context = context;
        this.birthdays = birthdays;
    }

    @Override
    public BirthdayAdapter.BirthdayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_birthday, parent, false);
        return new BirthdayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BirthdayAdapter.BirthdayViewHolder holder, int position) {
        Birthday birthdayObject = birthdays.get(position);
        holder.bind(context, birthdayObject);
    }

    @Override
    public int getItemCount() {
        return birthdays.size();
    }

    public static class BirthdayViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivBirthday;
        private TextView tvBirthdayDetail;
        private TextView tvToday;
        private View vToday;
        private ImageView ivIcon;

        public BirthdayViewHolder(View itemView) {
            super(itemView);
            ivBirthday = (ImageView) itemView.findViewById(R.id.ivBirthday);
            tvBirthdayDetail = (TextView) itemView.findViewById(R.id.tvBirthdayDetail);
            tvToday = (TextView) itemView.findViewById(R.id.tvToday);
            vToday = itemView.findViewById(R.id.vToday);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
        }

        public void bind(Context context, final Birthday birthday) {
            String date = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
            if (date.contentEquals(birthday.date)) {
                tvToday.setVisibility(View.VISIBLE);
                vToday.setVisibility(View.VISIBLE);
                ivIcon.setVisibility(View.VISIBLE);
            } else {
                tvToday.setVisibility(View.GONE);
                vToday.setVisibility(View.GONE);
                ivIcon.setVisibility(View.GONE);
            }

            Picasso.with(context).load(birthday.image).into(ivBirthday);
            tvBirthdayDetail.setText(birthday.name + " (" + birthday.division +
                    ")\n" + birthday.month + " " + birthday.date + "" + birthday.dateName);
        }
    }
}
