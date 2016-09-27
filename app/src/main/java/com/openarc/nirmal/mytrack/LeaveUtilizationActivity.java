/*
 * </summary>
 * Source File	: LeaveUtilizationActivity.java
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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openarc.nirmal.mytrack.adapter.LeaveUtilizationAdapter;
import com.openarc.nirmal.mytrack.model.LeaveUtilization;
import com.openarc.nirmal.mytrack.tasks.LeaveTask;
import com.openarc.nirmal.mytrack.tasks.LeaveUtilizationTask;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.LeaveUtilizationView;
import com.openarc.nirmal.mytrack.view.MAWHDialogView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaveUtilizationActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    MAWHDialogView mawhDialogView;
    LeaveUtilizationView leaveUtilizationView;
    LeaveUtilizationAdapter leaveUtilizationAdapter;
    LeaveUtilizationAdapter.OnItemClickListener leaveOnClick = new LeaveUtilizationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(LeaveUtilization leaveUtilization) {
            showLeaveDialog(leaveUtilization);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_utilization);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!MyTrackConfig.getInstance().submitAuthorized)
            hideLeaveSubmit(navigationView);
        Picasso.with(LeaveUtilizationActivity.this).load(MyTrackConfig.getInstance().imageUrl).into((CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivUser));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLoggedUser)).setText(MyTrackConfig.getInstance().loggedUser);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initialize() {
        leaveUtilizationView = new LeaveUtilizationView();
        leaveUtilizationView.rvLeaveUtilization = (RecyclerView) findViewById(R.id.rvLeaveUtilization);
        leaveUtilizationView.rlLoadingLayout = (RelativeLayout) findViewById(R.id.rlLoadingLayout);
        new LeaveUtilizationTask(LeaveUtilizationActivity.this, leaveUtilizationView).execute();
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
            case R.id.nav_gate_pass_request:
                startActivity(new Intent(this, GatePassRequestActivity.class));
                finish();
                break;
            case R.id.nav_osm_contact:
                startActivity(new Intent(this, ContactActivity.class));
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    public void setupRecyclerView(List<LeaveUtilization> leaveUtilizations) {
        leaveUtilizationAdapter = new LeaveUtilizationAdapter(leaveUtilizations, LeaveUtilizationActivity.this, leaveOnClick);
        leaveUtilizationView.rvLeaveUtilization.setHasFixedSize(true);
        leaveUtilizationView.rvLeaveUtilization.setLayoutManager(new LinearLayoutManager(LeaveUtilizationActivity.this));
        leaveUtilizationView.rvLeaveUtilization.setAdapter(leaveUtilizationAdapter);
    }

    private void showLeaveDialog(LeaveUtilization leaveUtilization) {
        mawhDialogView = new MAWHDialogView();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LeaveUtilizationActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_mawh, null);
        alertDialogBuilder.setView(dialogView);
        ImageView ivClose = (ImageView) dialogView.findViewById(R.id.ivClose);
        TextView tvDialogTitle = (TextView) dialogView.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText(leaveUtilization.name);
        mawhDialogView.rvMawhDialog = (RecyclerView) dialogView.findViewById(R.id.rvMawhDialog);
        mawhDialogView.rlLoadingLayout = (RelativeLayout) dialogView.findViewById(R.id.rlLoadingLayout);
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
        new LeaveTask(mawhDialogView, alertDialog, leaveUtilization.name, LeaveUtilizationActivity.this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
}
