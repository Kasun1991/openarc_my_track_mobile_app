/*
 * </summary>
 * Source File	: ContactAdapter.java
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
import com.openarc.nirmal.mytrack.model.Contact;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {

    Context context;
    OnItemClickListener clickListener;
    ContactFilter contactFilter = new ContactFilter();
    List<Contact> mContacts = new ArrayList<Contact>();
    List<Contact> mFilteredContacts = new ArrayList<Contact>();

    public ContactAdapter(List<Contact> contacts, Context context, OnItemClickListener clickListener) {
        this.context = context;
        this.mContacts = contacts;
        this.mFilteredContacts = contacts;
        this.clickListener = clickListener;
        sortList();
    }

    private void sortList() {
        Collections.sort(mContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact2, Contact contact1) {
                return contact2.name.compareTo(contact1.name);
            }
        });
    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row_contact, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, int position) {
        Contact contactObject = mFilteredContacts.get(position);
        holder.bind(context, contactObject, clickListener);
    }

    @Override
    public int getItemCount() {
        return mFilteredContacts.size();
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    public interface OnItemClickListener {
        void onItemClick(Contact contact);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civContact;
        private TextView tvName;
        private TextView tvMobile;
        private TextView tvExt;
        private TextView tvEmail;

        public ContactViewHolder(View itemView) {
            super(itemView);
            civContact = (CircleImageView) itemView.findViewById(R.id.civContact);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvMobile = (TextView) itemView.findViewById(R.id.tvMobile);
            tvExt = (TextView) itemView.findViewById(R.id.tvExt);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
        }

        public void bind(Context context, final Contact contact, final OnItemClickListener clickListener) {
            Picasso.with(context).load(contact.image).into(civContact);
            tvName.setText(contact.name);
            tvMobile.setText(contact.mobile);
            tvExt.setText(contact.extension);
            tvEmail.setText(contact.email);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(contact);
                }
            });
        }
    }

    private class ContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults result = new FilterResults();
            List<Contact> filteredContacts = new ArrayList<Contact>();

            constraint = constraint.toString().toLowerCase();
            Log.d("--ContactAdapter-->", "Constraint : " + constraint);

            for (Contact contact : mContacts) {
                if (contact.name.toLowerCase().contains(constraint)
                        || contact.mobile.toLowerCase().contains(constraint)
                        || contact.email.toLowerCase().contains(constraint)
                        || contact.extension.toLowerCase().contains(constraint)) {
                    Log.d("--ContactAdapter-->", "Contact found : " + contact.name);
                    filteredContacts.add(contact);
                }
            }
            result.values = filteredContacts;
            result.count = filteredContacts.size();

            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredContacts = (List<Contact>) results.values;
            notifyDataSetChanged();
        }

    }

}

