/*
 * </summary>
 * Source File	: ContactActivity.java
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

package com.openarc.nirmal.mytrack;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openarc.nirmal.mytrack.adapter.ContactAdapter;
import com.openarc.nirmal.mytrack.event.ContactRetrievedEvent;
import com.openarc.nirmal.mytrack.model.Contact;
import com.openarc.nirmal.mytrack.tasks.OSMContactTask;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.ContactView;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    ContactView contactView;
    ContactAdapter mContactAdapter;
    OSMContactTask osmContactTask;

    ContactAdapter.OnItemClickListener contactOnClick = new ContactAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Contact contact) {
            if (getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            showContactDialog(contact);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initialize();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!MyTrackConfig.getInstance().submitAuthorized)
            hideLeaveSubmit(navigationView);
        Picasso.with(ContactActivity.this).load(MyTrackConfig.getInstance().imageUrl).into((CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivUser));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLoggedUser)).setText(MyTrackConfig.getInstance().loggedUser);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initialize() {
        contactView = new ContactView();
        contactView.rvContact = (RecyclerView) findViewById(R.id.rvContact);
        contactView.rlLoadingLayout = (RelativeLayout) findViewById(R.id.rlLoadingLayout);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            setupSearchMenu(searchItem);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                AboutDialog();
                break;
            case R.id.menu_logout:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                finish();
                break;
            case R.id.nav_view_attendance:
                startActivity(new Intent(this, AttendanceActivity.class));
                finish();
                break;
            case R.id.nav_leave_requests:
                startActivity(new Intent(this, LeaveRequestActivity.class));
                finish();
                break;
            case R.id.nav_leave_utilization:
                startActivity(new Intent(this, LeaveUtilizationActivity.class));
                finish();
                break;
            case R.id.nav_gate_pass_request:
                startActivity(new Intent(this, GatePassRequestActivity.class));
                finish();
                break;
            case R.id.nav_cir_contact:
                startActivity(new Intent(this, CIRContactActivity.class));
                finish();
                break;
            case R.id.nav_openarc_customers:
                startActivity(new Intent(this, CustomerActivity.class));
                finish();
                break;
            case R.id.nav_meeting_rooms:
                startActivity(new Intent(this, MeetingRoomActivity.class));
                finish();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showContactDialog(final Contact contact) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContactActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_contact, null);
        alertDialogBuilder.setView(dialogView);
        ImageView ivClose = (ImageView) dialogView.findViewById(R.id.ivClose);
        ImageView ivBackground = (ImageView) dialogView.findViewById(R.id.ivBackground);
        ImageView civContactImage = (CircleImageView) dialogView.findViewById(R.id.civContactImage);
        TextView tvContactName = (TextView) dialogView.findViewById(R.id.tvContactName);
        TextView tvCallNumber = (TextView) dialogView.findViewById(R.id.tvCallNumber);
        TextView tvMessageNumber = (TextView) dialogView.findViewById(R.id.tvMessageNumber);

        CardView callCardView = (CardView) dialogView.findViewById(R.id.cvCall);
        CardView smsCardView = (CardView) dialogView.findViewById(R.id.cvSMS);
        CardView emailCardView = (CardView) dialogView.findViewById(R.id.cvEmail);

        TextView tvEmail = (TextView) dialogView.findViewById(R.id.tvEmail);
        Picasso.with(ContactActivity.this).load(contact.image).into(ivBackground);
        Picasso.with(ContactActivity.this).load(contact.image).into(civContactImage);
        tvContactName.setText(contact.name);
        tvCallNumber.setText(contact.mobile);
        tvMessageNumber.setText(contact.mobile);
        tvEmail.setText(contact.email);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        ivClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    alertDialog.dismiss();
                }
                return false;
            }
        });

        callCardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!contact.mobile.contentEquals("")) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + contact.mobile));
                        if (ActivityCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            showToast("Cannot Make a Call");
                        } else {
                            startActivity(callIntent);
                            alertDialog.dismiss();
                        }
                    } else {
                        showToast("Not a valid mobile");
                    }
                }
                return false;
            }
        });

        smsCardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!contact.mobile.contentEquals("")) {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setData(Uri.parse("sms:" + contact.mobile));
                        startActivity(sendIntent);
                        alertDialog.dismiss();
                    } else {
                        showToast("Not a valid mobile");
                    }
                }
                return false;
            }
        });

        emailCardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!contact.email.contentEquals("")) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{contact.email});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(emailIntent);
                        alertDialog.dismiss();
                    } else {
                        showToast("Not a valid email");
                    }
                }
                return false;
            }
        });
        alertDialog.show();
    }

    private void setupSearchMenu(MenuItem searchItem) {
        showWaiting(ContactActivity.this);
        contactView.searchView = (SearchView) searchItem.getActionView();
        osmContactTask = new OSMContactTask(ContactActivity.this, contactView);
        osmContactTask.execute();
        contactView.searchView.setOnQueryTextListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(ContactRetrievedEvent event) {
        if (event.contacts == null) {
            return;
        }
        mContactAdapter = new ContactAdapter(event.contacts, ContactActivity.this, contactOnClick);
        contactView.rvContact.setHasFixedSize(true);
        contactView.rvContact.setLayoutManager(new LinearLayoutManager(ContactActivity.this));
        contactView.rvContact.setAdapter(mContactAdapter);
        dismissWaiting();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("--ContactActivity-->", query);
        mContactAdapter.getFilter().filter(query);
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("--ContactActivity-->", "On change " + newText);
        mContactAdapter.getFilter().filter(newText);
        return false;
    }
}
