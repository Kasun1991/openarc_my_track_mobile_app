/*
 * </summary>
 * Source File	: AttendanceActivity.java
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

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openarc.nirmal.mytrack.tasks.AttendanceTask;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.AttendanceView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendanceActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AttendanceView attendanceView;
    View.OnTouchListener fromOnTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyBord();
                attendanceView.etFrom.setError(null);
                attendanceView.etTo.setError(null);
                attendanceView.etFrom.setInputType(InputType.TYPE_NULL);
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(AttendanceActivity.this, R.style.MyTackDataPicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        attendanceView.etFrom.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                    }
                }
                        , c.get(Calendar.YEAR)
                        , c.get(Calendar.MONTH) - 1
                        , c.get(Calendar.DATE)).show();
            }
            return false;
        }
    };
    View.OnTouchListener toOnTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyBord();
                attendanceView.etFrom.setError(null);
                attendanceView.etTo.setError(null);
                attendanceView.etTo.setInputType(InputType.TYPE_NULL);
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(AttendanceActivity.this, R.style.MyTackDataPicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        attendanceView.etTo.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                    }
                }
                        , c.get(Calendar.YEAR)
                        , c.get(Calendar.MONTH)
                        , c.get(Calendar.DATE)).show();

            }
            return false;
        }
    };

    View.OnClickListener viewButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (validate()) {
                    new AttendanceTask(attendanceView, AttendanceActivity.this).execute();
                }
            } catch (ParseException e) {
                showSnackBar(v, "Date range error");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
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
        Picasso.with(AttendanceActivity.this).load(MyTrackConfig.getInstance().imageUrl).into((CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivUser));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLoggedUser)).setText(MyTrackConfig.getInstance().loggedUser);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initialize() {
        attendanceView = new AttendanceView();
        attendanceView.etFrom = (EditText) findViewById(R.id.etFrom);
        attendanceView.etFrom.setInputType(InputType.TYPE_NULL);
        attendanceView.etTo = (EditText) findViewById(R.id.etTo);
        attendanceView.etTo.setInputType(InputType.TYPE_NULL);
        attendanceView.tvNoPay = (TextView) findViewById(R.id.tvNoPay);
        attendanceView.tvMissingIn = (TextView) findViewById(R.id.tvMissingIn);
        attendanceView.tvMissingOut = (TextView) findViewById(R.id.tvMissingOut);
        attendanceView.bView = (Button) findViewById(R.id.bView);
        attendanceView.rvAttendance = (RecyclerView) findViewById(R.id.rvAttendance);
        attendanceView.rlLoadingLayout = (RelativeLayout) findViewById(R.id.rlLoadingLayout);

        attendanceView.etFrom.setOnTouchListener(fromOnTouch);
        attendanceView.etTo.setOnTouchListener(toOnTouch);
        attendanceView.bView.setOnClickListener(viewButtonOnClick);
    }

    private Boolean validate() throws ParseException {
        if (attendanceView.etFrom.getText().toString().trim().contentEquals("")) {
            attendanceView.etFrom.setError("Select a Date");
            return false;
        } else if (attendanceView.etTo.getText().toString().trim().contentEquals("")) {
            attendanceView.etTo.setError("Select a Date");
            return false;
        } else if (!validateDate()) {
            attendanceView.etTo.setError("Date rage is no valid");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateDate() throws ParseException {
        return (new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(attendanceView.etFrom.getText().toString().trim())
                .before(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(attendanceView.etTo.getText().toString())));
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
