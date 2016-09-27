/*
 * </summary>
 * Source File	: LeaveTask.java
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

package com.openarc.nirmal.mytrack.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.openarc.nirmal.mytrack.adapter.LeaveAdapter;
import com.openarc.nirmal.mytrack.model.Leave;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.MAWHDialogView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LeaveTask extends AsyncTask<Void, Void, String> {

    MAWHDialogView mawhDialogView;
    AlertDialog alertDialog;
    private List<Leave> leaves;
    private String leaveType;
    private Context context;

    public LeaveTask(MAWHDialogView mawhDialogView, AlertDialog alertDialog, String leaveType, Context context) {
        this.mawhDialogView = mawhDialogView;
        this.alertDialog = alertDialog;
        this.leaveType = leaveType;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return GetPageContent();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.contentEquals("")) {
            mawhDialogView.rvMawhDialog.setVisibility(View.VISIBLE);
            mawhDialogView.rlLoadingLayout.setVisibility(View.GONE);
            Snackbar.make(mawhDialogView.rlLoadingLayout, "Data Retrieve Failed", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            try {
                getLeaves(s);
                mawhDialogView.rvMawhDialog.setHasFixedSize(true);
                mawhDialogView.rvMawhDialog.setLayoutManager(new LinearLayoutManager(context));
                mawhDialogView.rvMawhDialog.setAdapter(new LeaveAdapter(leaves, context));
                mawhDialogView.rvMawhDialog.setVisibility(View.VISIBLE);
                mawhDialogView.rlLoadingLayout.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
                mawhDialogView.rvMawhDialog.setVisibility(View.VISIBLE);
                mawhDialogView.rlLoadingLayout.setVisibility(View.GONE);
                Snackbar.make(mawhDialogView.rlLoadingLayout, "Error Loading Data!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        mawhDialogView.rlLoadingLayout.setVisibility(View.VISIBLE);
        mawhDialogView.rvMawhDialog.setVisibility(View.GONE);
        alertDialog.show();
    }

    private String GetPageContent() throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "services/getMemberLeaves.php?EEENO=" + MyTrackConfig.getInstance().LoggedUserCode + "&YEAR=" + Calendar.getInstance().get(Calendar.YEAR));
        MyTrackConfig.getInstance().connection = (HttpURLConnection) obj.openConnection();
        MyTrackConfig.getInstance().connection.setRequestMethod("GET");
        MyTrackConfig.getInstance().connection.setUseCaches(false);
        MyTrackConfig.getInstance().connection.setRequestProperty("User-Agent", Constant.USER_AGENT);
        MyTrackConfig.getInstance().connection.setRequestProperty("Accept-Charset", Constant.ACCEPT_CHARSET);
        MyTrackConfig.getInstance().connection.setRequestProperty("Accept-Language", Constant.ACCEPT_LANGUAGE);
        if (MyTrackConfig.getInstance().cookies != null) {
            for (String cookie : MyTrackConfig.getInstance().cookies) {
                MyTrackConfig.getInstance().connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        int responseCode = MyTrackConfig.getInstance().connection.getResponseCode();
        BufferedReader in =
                new BufferedReader(new InputStreamReader(MyTrackConfig.getInstance().connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        setCookies(MyTrackConfig.getInstance().connection.getHeaderFields().get("Set-Cookie"));
        return response.toString();
    }

    public void setCookies(List<String> cookies) {
        MyTrackConfig.getInstance().cookies = cookies;
    }

    private void getLeaves(String body) throws JSONException {
        leaves = new ArrayList<>();
        JSONArray array = new JSONArray(body);
        for (int i = 0; i < array.length(); i++) {
            Leave leave = new Leave();
            JSONObject row = array.getJSONObject(i);
            if (row.getString("LETY_DES").contentEquals(leaveType)) {
                leave.fromDate = row.getString("LVET_FROMDATE").split(" ")[0];
                leave.toDate = row.getString("LVET_TODATE").split(" ")[0];
                leave.duration = row.getString("LVET_NOOFDAYS");
                leave.reason = row.getString("LVET_REASON");
                leave.dateApplied = row.getString("LVET_APPDATE").split(" ")[0];
                leaves.add(leave);
            }
        }
    }
}
