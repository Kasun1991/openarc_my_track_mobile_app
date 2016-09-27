/*
 * </summary>
 * Source File	: CustomerAdapter.java
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
import com.openarc.nirmal.mytrack.model.Customer;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> mCustomerList;
    private Context context;
    private OnItemClickListener clickListener;

    public CustomerAdapter(List<Customer> mCustomerList, Context context, OnItemClickListener clickListener) {
        this.mCustomerList = mCustomerList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public CustomerAdapter.CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_customer, parent, false);
        return new CustomerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomerAdapter.CustomerViewHolder holder, int position) {
        Customer mCustomerObject = mCustomerList.get(position);
        holder.bind(context, mCustomerObject, clickListener);
    }

    @Override
    public int getItemCount() {
        return mCustomerList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Customer customer);
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCode;
        private TextView tvName;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            tvCode = (TextView) itemView.findViewById(R.id.tvCode);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }

        public void bind(Context context, final Customer customer, final OnItemClickListener clickListener) {
            tvCode.setText(customer.shortCode);
            tvName.setText(customer.name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(customer);
                }
            });
        }
    }
}
