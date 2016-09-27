/*
 * </summary>
 * Source File	: CIRContactAdapter.java
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.openarc.nirmal.mytrack.R;
import com.openarc.nirmal.mytrack.model.CIRContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CIRContactAdapter extends RecyclerView.Adapter<CIRContactAdapter.CIRContactViewHolder> implements Filterable {

    Context context;
    OnItemClickListener clickListener;
    CIRContactFilter contactFilter = new CIRContactFilter();
    List<CIRContact> mCIRContacts = new ArrayList<CIRContact>();
    List<CIRContact> mFilteredCIRContacts = new ArrayList<CIRContact>();

    public CIRContactAdapter(List<CIRContact> contacts, Context context, OnItemClickListener clickListener) {
        this.context = context;
        this.mCIRContacts = contacts;
        this.mFilteredCIRContacts = contacts;
        this.clickListener = clickListener;
        sortList();
    }

    private void sortList() {
        Collections.sort(mCIRContacts, new Comparator<CIRContact>() {
            @Override
            public int compare(CIRContact contact2, CIRContact contact1) {
                return contact2.cirName.compareTo(contact1.cirName);
            }
        });
    }

    @Override
    public CIRContactAdapter.CIRContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_circontact, parent, false);
        return new CIRContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CIRContactAdapter.CIRContactViewHolder holder, int position) {
        CIRContact contactObject = mFilteredCIRContacts.get(position);
        holder.bind(context, contactObject, clickListener);
    }

    @Override
    public int getItemCount() {
        return mFilteredCIRContacts.size();
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    public interface OnItemClickListener {
        void onItemClick(CIRContact contact);
    }

    public static class CIRContactViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSiteName;
        private TextView CirName;
        private TextView tvDesignation;
        private TextView tvMobile;
        private TextView tvEmail;
        private TextView tvAddress;

        public CIRContactViewHolder(View itemView) {
            super(itemView);
            tvSiteName = (TextView) itemView.findViewById(R.id.tvSiteName);
            CirName = (TextView) itemView.findViewById(R.id.CirName);
            tvDesignation = (TextView) itemView.findViewById(R.id.tvDesignation);
            tvMobile = (TextView) itemView.findViewById(R.id.tvMobile);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
        }

        public void bind(Context context, final CIRContact contact, final OnItemClickListener clickListener) {
            tvSiteName.setText(contact.siteName);
            CirName.setText(contact.cirName);
            tvDesignation.setText(contact.designation);
            tvMobile.setText(contact.mobile);
            tvEmail.setText(contact.email);
            tvAddress.setText(contact.address);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(contact);
                }
            });
        }
    }

    private class CIRContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults result = new FilterResults();
            List<CIRContact> filteredCIRContacts = new ArrayList<CIRContact>();

            constraint = constraint.toString().toLowerCase();
            Log.d("--CIRContactAdapter-->", "Constraint : " + constraint);

            for (CIRContact contact : mCIRContacts) {
                if (contact.siteName.toLowerCase().contains(constraint)
                        || contact.cirName.toLowerCase().contains(constraint)
                        || contact.designation.toLowerCase().contains(constraint)
                        || contact.mobile.toLowerCase().contains(constraint)
                        || contact.email.toLowerCase().contains(constraint)
                        || contact.address.toLowerCase().contains(constraint)) {
                    Log.d("--CIRContactAdapter-->", "CIRContact found : " + contact.siteName);
                    filteredCIRContacts.add(contact);
                }
            }
            result.values = filteredCIRContacts;
            result.count = filteredCIRContacts.size();

            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredCIRContacts = (List<CIRContact>) results.values;
            notifyDataSetChanged();
        }

    }
}
