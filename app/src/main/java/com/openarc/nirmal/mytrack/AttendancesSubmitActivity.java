/*
 * </summary>
 * Source File	: AttendancesSubmitActivity.java
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.openarc.nirmal.mytrack.adapter.AttendanceSubmitAdapter;
import com.openarc.nirmal.mytrack.event.AttendanceSubmitRetrievedEvent;
import com.openarc.nirmal.mytrack.model.AttendanceSubmit;
import com.openarc.nirmal.mytrack.tasks.AttendanceSubmitTask;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.AttendancesSubmitDialogView;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class AttendancesSubmitActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView rvAttendanceSubmit;
    AttendanceSubmitAdapter mAttendanceSubmitAdapter;
    AttendancesSubmitDialogView mAttendancesSubmitDialogView;
    AttendanceSubmitAdapter.OnItemLongClickListener onItemLongClickListener = new AttendanceSubmitAdapter.OnItemLongClickListener() {
        @Override
        public void OnItemLongClickListener(final AttendanceSubmit attendanceSubmit) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttendancesSubmitActivity.this);
            alertDialogBuilder.setMessage("Do you want to delete this attendance?");
            alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new AttendanceSubmitTask("DELETE", attendanceSubmit.date).execute();
                    dialog.dismiss();
                    showWaiting(AttendancesSubmitActivity.this);
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    };
    View.OnTouchListener inOnClick = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyBord();
                mAttendancesSubmitDialogView.etIn.setInputType(InputType.TYPE_NULL);
                int hour = 8;
                int minute = 30;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AttendancesSubmitActivity.this, R.style.MyTackDataPicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mAttendancesSubmitDialogView.etIn.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute) + ":00");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
            return false;
        }
    };
    View.OnTouchListener outOnClick = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyBord();
                mAttendancesSubmitDialogView.etOut.setInputType(InputType.TYPE_NULL);
                int hour = 17;
                int minute = 0;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AttendancesSubmitActivity.this, R.style.MyTackDataPicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mAttendancesSubmitDialogView.etOut.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute) + ":00");
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
                mAttendancesSubmitDialogView.etDate.setInputType(InputType.TYPE_NULL);
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(AttendancesSubmitActivity.this, R.style.MyTackDataPicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mAttendancesSubmitDialogView.etDate.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + dayOfMonth);
                    }
                }
                        , c.get(Calendar.YEAR)
                        , c.get(Calendar.MONTH) - 1
                        , c.get(Calendar.DATE)).show();
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendances_submit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvAttendanceSubmit = (RecyclerView) findViewById(R.id.rvAttendanceSubmit);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttendanceSubmitDialog();
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
        Picasso.with(AttendancesSubmitActivity.this).load(MyTrackConfig.getInstance().imageUrl).into((CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivUser));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLoggedUser)).setText(MyTrackConfig.getInstance().loggedUser);
        navigationView.setNavigationItemSelectedListener(this);
        showWaiting(this);
        new AttendanceSubmitTask("GET", null).execute();
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

    public void onEvent(AttendanceSubmitRetrievedEvent event) {
        if (event.attendanceSubmitList == null) {
            return;
        }
        mAttendanceSubmitAdapter = new AttendanceSubmitAdapter(AttendancesSubmitActivity.this, event.attendanceSubmitList, onItemLongClickListener);
        rvAttendanceSubmit.setHasFixedSize(true);
        rvAttendanceSubmit.setLayoutManager(new LinearLayoutManager(AttendancesSubmitActivity.this));
        rvAttendanceSubmit.setAdapter(mAttendanceSubmitAdapter);
        dismissWaiting();
    }

    private void showAttendanceSubmitDialog() {
        mAttendancesSubmitDialogView = new AttendancesSubmitDialogView();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_attendance_submit, null);
        alertDialogBuilder.setView(dialogView);
        ImageView ivClose = (ImageView) dialogView.findViewById(R.id.ivClose);
        mAttendancesSubmitDialogView.tvDialogTitle = (TextView) dialogView.findViewById(R.id.tvDialogTitle);
        mAttendancesSubmitDialogView.etDate = (EditText) dialogView.findViewById(R.id.etDate);
        mAttendancesSubmitDialogView.etOut = (EditText) dialogView.findViewById(R.id.etOut);
        mAttendancesSubmitDialogView.swOverNight = (Switch) dialogView.findViewById(R.id.swOverNight);
        mAttendancesSubmitDialogView.etIn = (EditText) dialogView.findViewById(R.id.etIn);
        mAttendancesSubmitDialogView.etReason = (EditText) dialogView.findViewById(R.id.etReason);
        mAttendancesSubmitDialogView.tvDialogTitle.setText("Attendance Submit");
        alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
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
        mAttendancesSubmitDialogView.etDate.setOnTouchListener(dateOnClick);
        mAttendancesSubmitDialogView.etIn.setOnTouchListener(inOnClick);
        mAttendancesSubmitDialogView.etOut.setOnTouchListener(outOnClick);
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
                if (validate()) {
                    String EEENO = MyTrackConfig.getInstance().LoggedUserCode;
                    String REPORTDATE = mAttendancesSubmitDialogView.etDate.getText().toString();
                    String REPORTTIMEOUT = mAttendancesSubmitDialogView.etOut.getText().toString();
                    String REPORTTIMEIN = mAttendancesSubmitDialogView.etIn.getText().toString();
                    boolean overnight = mAttendancesSubmitDialogView.swOverNight.isChecked() ? true : false;
                    String REMARK = mAttendancesSubmitDialogView.etReason.getText().toString();
                    String parameter;
                    try {
                        if (overnight) {
                            parameter = "EEENO=" + EEENO + "&REPORTDATE=" + URLEncoder.encode(REPORTDATE, "UTF-8") + "&REPORTTIMEIN=" + URLEncoder.encode(REPORTTIMEIN, "UTF-8") + "&REPORTTIMEOUT=" + URLEncoder.encode(REPORTTIMEOUT, "UTF-8") + "&REMARK=" + URLEncoder.encode(REMARK, "UTF-8");
                        } else {
                            parameter = "EEENO=" + EEENO + "&REPORTDATE=" + URLEncoder.encode(REPORTDATE, "UTF-8") + "&REPORTTIMEIN=" + URLEncoder.encode(REPORTTIMEIN, "UTF-8") + "&overnight=" + URLEncoder.encode("on", "UTF-8") + "&REPORTTIMEOUT=" + URLEncoder.encode(REPORTTIMEOUT, "UTF-8") + "&REMARK=" + URLEncoder.encode(REMARK, "UTF-8");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        parameter = "";
                    }
                    new AttendanceSubmitTask("POST", parameter).execute();
                    showWaiting(AttendancesSubmitActivity.this);
                    mAlertDialog.dismiss();
                }
            }
        });
    }

    private boolean validate() {
        if (mAttendancesSubmitDialogView.etDate.getText().toString().contentEquals("")) {
            mAttendancesSubmitDialogView.etDate.setError("Cannot be empty");
            return false;
        } else if (mAttendancesSubmitDialogView.etIn.getText().toString().contentEquals("")) {
            mAttendancesSubmitDialogView.etIn.setError("Cannot be empty");
            return false;
        } else if (mAttendancesSubmitDialogView.etOut.getText().toString().contentEquals("")) {
            mAttendancesSubmitDialogView.etOut.setError("Cannot be empty");
            return false;
        } else if (mAttendancesSubmitDialogView.etReason.getText().toString().contentEquals("")) {
            mAttendancesSubmitDialogView.etReason.setError("Cannot be empty");
            return false;
        } else {
            return true;
        }
    }

}
