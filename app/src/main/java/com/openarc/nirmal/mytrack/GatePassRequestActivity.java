/*
 * </summary>
 * Source File	: GatePassRequestActivity.java
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.openarc.nirmal.mytrack.adapter.GatePassAdapter;
import com.openarc.nirmal.mytrack.event.GatePassRetrievedEvent;
import com.openarc.nirmal.mytrack.model.GatePass;
import com.openarc.nirmal.mytrack.tasks.GatePassRequestTask;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.GatePassRequestDialogView;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class GatePassRequestActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    GatePassRequestDialogView mGatePassRequestDialogView;
    RecyclerView rvGatePass;
    GatePassAdapter mGatePassAdapter;

    View.OnTouchListener inOnClick = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyBord();
                mGatePassRequestDialogView.etIn.setError(null);
                mGatePassRequestDialogView.etIn.setInputType(InputType.TYPE_NULL);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE) + 30;
                if (minute > 59) {
                    minute = minute - 60;
                    hour = hour + 1;
                }
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(GatePassRequestActivity.this, R.style.MyTackDataPicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mGatePassRequestDialogView.etIn.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute) + ":00");
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
                mGatePassRequestDialogView.etOut.setError(null);
                mGatePassRequestDialogView.etOut.setInputType(InputType.TYPE_NULL);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                mcurrentTime.get(Calendar.AM_PM);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(GatePassRequestActivity.this, R.style.MyTackDataPicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mGatePassRequestDialogView.etOut.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute) + ":00");
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
                mGatePassRequestDialogView.etDate.setError(null);
                mGatePassRequestDialogView.etDate.setInputType(InputType.TYPE_NULL);
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(GatePassRequestActivity.this, R.style.MyTackDataPicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mGatePassRequestDialogView.etDate.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + dayOfMonth);
                    }
                }
                        , c.get(Calendar.YEAR)
                        , c.get(Calendar.MONTH) - 1
                        , c.get(Calendar.DATE)).show();
            }
            return false;
        }
    };
    GatePassAdapter.OnItemLongClickListener onItemLongClickListener = new GatePassAdapter.OnItemLongClickListener() {
        @Override
        public void OnItemLongClickListener(final GatePass mGatePass) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GatePassRequestActivity.this);
            alertDialogBuilder.setMessage("Do you want to delete this request?");
            alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new GatePassRequestTask("DELETE", mGatePass.deleteId).execute();
                    dialog.dismiss();
                    showWaiting(GatePassRequestActivity.this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_pass_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvGatePass = (RecyclerView) findViewById(R.id.rvGatePass);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGatePassDialog();
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
        Picasso.with(GatePassRequestActivity.this).load(MyTrackConfig.getInstance().imageUrl).into((CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivUser));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLoggedUser)).setText(MyTrackConfig.getInstance().loggedUser);
        navigationView.setNavigationItemSelectedListener(this);
        showWaiting(this);
        new GatePassRequestTask("GET", null).execute();
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

    private void showGatePassDialog() {
        mGatePassRequestDialogView = new GatePassRequestDialogView();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GatePassRequestActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_request_gate_pass, null);
        alertDialogBuilder.setView(dialogView);
        ImageView ivClose = (ImageView) dialogView.findViewById(R.id.ivClose);
        mGatePassRequestDialogView.tvDialogTitle = (TextView) dialogView.findViewById(R.id.tvDialogTitle);
        mGatePassRequestDialogView.etDate = (EditText) dialogView.findViewById(R.id.etDate);
        mGatePassRequestDialogView.etOut = (EditText) dialogView.findViewById(R.id.etOut);
        mGatePassRequestDialogView.etIn = (EditText) dialogView.findViewById(R.id.etIn);
        mGatePassRequestDialogView.etReason = (EditText) dialogView.findViewById(R.id.etReason);
        mGatePassRequestDialogView.tvDialogTitle.setText("Request Gate Pass");
        alertDialogBuilder.setPositiveButton("Request", new DialogInterface.OnClickListener() {
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
        mGatePassRequestDialogView.etDate.setOnTouchListener(dateOnClick);
        mGatePassRequestDialogView.etIn.setOnTouchListener(inOnClick);
        mGatePassRequestDialogView.etOut.setOnTouchListener(outOnClick);
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
                        String date = mGatePassRequestDialogView.etDate.getText().toString();
                        String out = mGatePassRequestDialogView.etOut.getText().toString();
                        String in = mGatePassRequestDialogView.etIn.getText().toString();
                        String reason = mGatePassRequestDialogView.etReason.getText().toString();
                        String parameter;
                        try {
                            parameter = "DATE=" + date + "&TIMEOUT=" + URLEncoder.encode(out, "UTF-8") + "&TIMEIN=" + URLEncoder.encode(in, "UTF-8") + "&Reason=" + URLEncoder.encode(reason, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            parameter = "";
                        }
                        new GatePassRequestTask("POST", parameter).execute();
                        showWaiting(GatePassRequestActivity.this);
                        mAlertDialog.dismiss();
                    }
                } catch (ParseException e) {
                    showToast("Time range validation error");
                }
            }
        });
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

    public void onEvent(GatePassRetrievedEvent event) {
        if (event.mGatePassList == null) {
            return;
        }
        mGatePassAdapter = new GatePassAdapter(GatePassRequestActivity.this, event.mGatePassList, onItemLongClickListener);
        rvGatePass.setHasFixedSize(true);
        rvGatePass.setLayoutManager(new LinearLayoutManager(GatePassRequestActivity.this));
        rvGatePass.setAdapter(mGatePassAdapter);
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

    private boolean validate() throws ParseException {
        if (mGatePassRequestDialogView.etDate.getText().toString().contentEquals("")) {
            mGatePassRequestDialogView.etDate.setError("Cannot be empty");
            return false;
        } else if (mGatePassRequestDialogView.etOut.getText().toString().contentEquals("")) {
            mGatePassRequestDialogView.etOut.setError("Cannot be empty");
            return false;
        } else if (mGatePassRequestDialogView.etIn.getText().toString().contentEquals("")) {
            mGatePassRequestDialogView.etIn.setError("Cannot be empty");
            return false;
        } else if (mGatePassRequestDialogView.etReason.getText().toString().contentEquals("")) {
            mGatePassRequestDialogView.etReason.setError("Cannot be empty");
            return false;
        } else if (!validateTime()) {
            mGatePassRequestDialogView.etIn.setError("Duration is invalid");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateTime() throws ParseException {
        return (new SimpleDateFormat("HH:mm:ss", Locale.US).parse(mGatePassRequestDialogView.etOut.getText().toString().trim())
                .before(new SimpleDateFormat("HH:mm:ss", Locale.US).parse(mGatePassRequestDialogView.etIn.getText().toString())));
    }

}
