/*
 * </summary>
 * Source File	: CIRContactActivity.java
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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.openarc.nirmal.mytrack.adapter.CIRContactAdapter;
import com.openarc.nirmal.mytrack.event.CIRContactRetrievedEvent;
import com.openarc.nirmal.mytrack.model.CIRContact;
import com.openarc.nirmal.mytrack.tasks.CIRContactTask;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class CIRContactActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    SearchView searchView;
    CIRContactAdapter mCIRContactAdapter;
    RecyclerView rvCirContact;
    CIRContactAdapter.OnItemClickListener contactOnClick = new CIRContactAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(CIRContact contact) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circontact);
        rvCirContact = (RecyclerView) findViewById(R.id.rvCirContact);
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
        Picasso.with(CIRContactActivity.this).load(MyTrackConfig.getInstance().imageUrl).into((CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivUser));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLoggedUser)).setText(MyTrackConfig.getInstance().loggedUser);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                finish();
                break;
            case R.id.nav_contacts:
                startActivity(new Intent(this, ContactActivity.class));
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
            case R.id.nav_osm_contact:
                startActivity(new Intent(this, ContactActivity.class));
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

    private void setupSearchMenu(MenuItem searchItem) {
        showWaiting(CIRContactActivity.this);
        searchView = (SearchView) searchItem.getActionView();
        new CIRContactTask().execute();
        searchView.setOnQueryTextListener(this);
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

    public void onEvent(CIRContactRetrievedEvent event) {
        if (event.contacts == null) {
            return;
        }
        mCIRContactAdapter = new CIRContactAdapter(event.contacts, CIRContactActivity.this, contactOnClick);
        rvCirContact.setHasFixedSize(true);
        rvCirContact.setLayoutManager(new LinearLayoutManager(CIRContactActivity.this));
        rvCirContact.setAdapter(mCIRContactAdapter);
        dismissWaiting();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("--ContactActivity-->", query);
        mCIRContactAdapter.getFilter().filter(query);
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("--ContactActivity-->", "On change " + newText);
        mCIRContactAdapter.getFilter().filter(newText);
        return false;
    }
}
