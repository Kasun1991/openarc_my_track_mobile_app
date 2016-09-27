/*
 * </summary>
 * Source File	: MeetingRoomActivity.java
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
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.openarc.nirmal.mytrack.adapter.MeetingAdapter;
import com.openarc.nirmal.mytrack.adapter.ReservationAdapter;
import com.openarc.nirmal.mytrack.event.MeetingRetrievedEvent;
import com.openarc.nirmal.mytrack.model.MeetingRoom;
import com.openarc.nirmal.mytrack.tasks.MeetingTask;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.MeetingRoomDialogView;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class MeetingRoomActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    MeetingAdapter mMeetingAdapter;
    ReservationAdapter mReservationAdapter;
    RecyclerView rvMeetingRoom, rvReservation;
    MeetingRoomDialogView mMeetingRoomDialogView;

    View.OnTouchListener toOnClick = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyBord();
                mMeetingRoomDialogView.etTo.setError(null);
                mMeetingRoomDialogView.etFrom.setError(null);
                mMeetingRoomDialogView.etTo.setInputType(InputType.TYPE_NULL);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY) + 1;
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MeetingRoomActivity.this, R.style.MyTackDataPicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mMeetingRoomDialogView.etTo.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute) + ":00");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
            return false;
        }
    };
    View.OnTouchListener fromOnClick = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyBord();
                mMeetingRoomDialogView.etFrom.setError(null);
                mMeetingRoomDialogView.etTo.setError(null);
                mMeetingRoomDialogView.etFrom.setInputType(InputType.TYPE_NULL);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                mcurrentTime.get(Calendar.AM_PM);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MeetingRoomActivity.this, R.style.MyTackDataPicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mMeetingRoomDialogView.etFrom.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute) + ":00");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
            return false;
        }
    };
    View.OnTouchListener dateOnClick = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyBord();
                mMeetingRoomDialogView.etDate.setError(null);
                mMeetingRoomDialogView.etDate.setInputType(InputType.TYPE_NULL);
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(MeetingRoomActivity.this, R.style.MyTackDataPicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mMeetingRoomDialogView.etDate.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + dayOfMonth);
                    }
                }
                        , c.get(Calendar.YEAR)
                        , c.get(Calendar.MONTH) - 1
                        , c.get(Calendar.DATE)).show();
            }
            return false;
        }
    };

    MeetingAdapter.OnItemClickListener meetingOnClick = new MeetingAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(MeetingRoom mMeetingRoom) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMeetingRoomDialog();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!MyTrackConfig.getInstance().submitAuthorized)
            hideLeaveSubmit(navigationView);
        Picasso.with(MeetingRoomActivity.this).load(MyTrackConfig.getInstance().imageUrl).into((CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivUser));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLoggedUser)).setText(MyTrackConfig.getInstance().loggedUser);
        navigationView.setNavigationItemSelectedListener(this);
        rvMeetingRoom = (RecyclerView) findViewById(R.id.rvMeetingRoom);
        rvReservation = (RecyclerView) findViewById(R.id.rvReservation);
        showWaiting(this);
        new MeetingTask("GET", null).execute();
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
            case R.id.nav_cir_contact:
                startActivity(new Intent(this, CIRContactActivity.class));
                finish();
                break;
            case R.id.nav_openarc_customers:
                startActivity(new Intent(this, CustomerActivity.class));
                finish();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void onEvent(MeetingRetrievedEvent event) {
        if (event.mMeetingRoomList == null) {
            return;
        }

        if (event.mReservationStatus.contentEquals("ERROR")) {
            showSnackBar(rvReservation, "Reservation cannot completed between given time");
        }
        mMeetingAdapter = new MeetingAdapter(MeetingRoomActivity.this, meetingOnClick, event.mMeetingRoomList);
        rvMeetingRoom.setHasFixedSize(true);
        rvMeetingRoom.setLayoutManager(new LinearLayoutManager(MeetingRoomActivity.this));
        rvMeetingRoom.setAdapter(mMeetingAdapter);
        mReservationAdapter = new ReservationAdapter(MeetingRoomActivity.this, event.mReservationList);
        rvReservation.setHasFixedSize(true);
        rvReservation.setLayoutManager(new LinearLayoutManager(MeetingRoomActivity.this));
        rvReservation.setAdapter(mReservationAdapter);
        dismissWaiting();
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

    private void showMeetingRoomDialog() {
        mMeetingRoomDialogView = new MeetingRoomDialogView();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeetingRoomActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_meeting_room, null);
        alertDialogBuilder.setView(dialogView);
        ImageView ivClose = (ImageView) dialogView.findViewById(R.id.ivClose);
        mMeetingRoomDialogView.tvDialogTitle = (TextView) dialogView.findViewById(R.id.tvDialogTitle);
        mMeetingRoomDialogView.spLocation = (Spinner) dialogView.findViewById(R.id.spLocation);
        mMeetingRoomDialogView.etDate = (EditText) dialogView.findViewById(R.id.etDate);
        mMeetingRoomDialogView.etFrom = (EditText) dialogView.findViewById(R.id.etFrom);
        mMeetingRoomDialogView.etTo = (EditText) dialogView.findViewById(R.id.etTo);
        mMeetingRoomDialogView.etReason = (EditText) dialogView.findViewById(R.id.etReason);
        mMeetingRoomDialogView.tvDialogTitle.setText("Reserve Meeting Room");
        alertDialogBuilder.setPositiveButton("Reserve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog mAlertDialog = alertDialogBuilder.create();
        mMeetingRoomDialogView.etDate.setOnTouchListener(dateOnClick);
        mMeetingRoomDialogView.etFrom.setOnTouchListener(fromOnClick);
        mMeetingRoomDialogView.etTo.setOnTouchListener(toOnClick);
        ivClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mAlertDialog.dismiss();
                }
                return false;
            }
        });
        mAlertDialog.show();
        Button mPositiveButton = mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        mAlertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.colorAccent));
        mPositiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (validate()) {
                        String ReserveDate = mMeetingRoomDialogView.etDate.getText().toString();
                        String ReserveTimeFrom = mMeetingRoomDialogView.etFrom.getText().toString();
                        String ReserveTimeTo = mMeetingRoomDialogView.etTo.getText().toString();
                        String Reason = mMeetingRoomDialogView.etReason.getText().toString();
                        String roomId = "1";
                        switch (mMeetingRoomDialogView.spLocation.getSelectedItem().toString()) {
                            case "2nd Floor Auditorium":
                                roomId = "1";
                                break;
                            case "3rd Floor Auditorium":
                                roomId = "2";
                                break;
                            case "4th Floor Meeting Room":
                                roomId = "3";
                                break;
                        }
                        String parameter;
                        try {
                            parameter = "roomId=" + roomId
                                    + "&ReserveDate=" + URLEncoder.encode(ReserveDate, "UTF-8")
                                    + "&ReserveTimeFrom=" + URLEncoder.encode(ReserveTimeFrom, "UTF-8")
                                    + "&ReserveTimeTo=" + URLEncoder.encode(ReserveTimeTo, "UTF-8")
                                    + "&Reason=" + URLEncoder.encode(Reason, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            parameter = "";
                        }
                        new MeetingTask("POST", parameter).execute();
                        showWaiting(MeetingRoomActivity.this);
                        mAlertDialog.dismiss();
                    }
                } catch (ParseException e) {
                    showToast("Time range validation error");
                }
            }
        });
    }

    private boolean validate() throws ParseException {
        if (mMeetingRoomDialogView.etDate.getText().toString().contentEquals("")) {
            mMeetingRoomDialogView.etDate.setError("Cannot be empty");
            return false;
        } else if (mMeetingRoomDialogView.etFrom.getText().toString().contentEquals("")) {
            mMeetingRoomDialogView.etFrom.setError("Cannot be empty");
            return false;
        } else if (mMeetingRoomDialogView.etTo.getText().toString().contentEquals("")) {
            mMeetingRoomDialogView.etTo.setError("Cannot be empty");
            return false;
        } else if (mMeetingRoomDialogView.etReason.getText().toString().contentEquals("")) {
            mMeetingRoomDialogView.etReason.setError("Cannot be empty");
            return false;
        } else if (validateTime()) {
            mMeetingRoomDialogView.etTo.setError("Duration is invalid");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateTime() throws ParseException {
        return (new SimpleDateFormat("HH:mm:ss", Locale.US).parse(mMeetingRoomDialogView.etTo.getText().toString().trim())
                .before(new SimpleDateFormat("HH:mm:ss", Locale.US).parse(mMeetingRoomDialogView.etFrom.getText().toString())));
    }
}
