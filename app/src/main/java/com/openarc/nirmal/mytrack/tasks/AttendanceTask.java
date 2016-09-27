/*
 * </summary>
 * Source File	: AttendanceTask.java
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
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.openarc.nirmal.mytrack.adapter.AttendanceAdapter;
import com.openarc.nirmal.mytrack.model.Attendance;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.AttendanceView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AttendanceTask extends AsyncTask<Void, Void, String> {

    AttendanceView attendanceView;
    List<Attendance> attendances;
    Context context;
    String mNoPay, mMissingIn, mMissingOut;

    public AttendanceTask(AttendanceView attendanceView, Context context) {
        this.attendanceView = attendanceView;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return requestAttendance();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPreExecute() {
        attendanceView.etFrom.setEnabled(false);
        attendanceView.etTo.setEnabled(false);
        attendanceView.bView.setText("Retrieving Data...");
        attendanceView.bView.setEnabled(false);
        attendanceView.rlLoadingLayout.setVisibility(View.VISIBLE);
        attendanceView.rvAttendance.setVisibility(View.GONE);
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.contentEquals("")) {
            attendanceView.etFrom.setEnabled(true);
            attendanceView.etTo.setEnabled(true);
            attendanceView.bView.setText("View");
            attendanceView.bView.setEnabled(true);
            attendanceView.rlLoadingLayout.setVisibility(View.GONE);
            attendanceView.rvAttendance.setVisibility(View.VISIBLE);
            Snackbar.make(attendanceView.bView, "Data Retrieve Failed", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            getAttendance(s);
            getAdditional(s);
            attendanceView.etFrom.setEnabled(true);
            attendanceView.etTo.setEnabled(true);
            attendanceView.bView.setText("View");
            attendanceView.bView.setEnabled(true);
            attendanceView.tvNoPay.setText(mNoPay);
            attendanceView.tvMissingIn.setText(mMissingIn);
            attendanceView.tvMissingOut.setText(mMissingOut);
            attendanceView.rvAttendance.setHasFixedSize(true);
            attendanceView.rvAttendance.setLayoutManager(new LinearLayoutManager(context));
            attendanceView.rvAttendance.setAdapter(new AttendanceAdapter(attendances, context));
            attendanceView.rlLoadingLayout.setVisibility(View.GONE);
            attendanceView.rvAttendance.setVisibility(View.VISIBLE);
        }
    }

    private String requestAttendance() throws Exception {
        List<String> paramList = new ArrayList<String>();
        paramList.add("EMPNO=" + URLEncoder.encode(MyTrackConfig.getInstance().LoggedUserCode, "UTF-8"));
        paramList.add("DATEFROM=" + URLEncoder.encode(attendanceView.etFrom.getText().toString(), "UTF-8"));
        paramList.add("DATETO=" + URLEncoder.encode(attendanceView.etTo.getText().toString(), "UTF-8"));
        String parameters = setUpParameter(paramList);
        return sendPost(Constant.SERVER_ROOT + "GetAttendance.php", parameters);
    }

    private String setUpParameter(List<String> paramList) {
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }
        return result.toString();
    }

    private String sendPost(String url, String postParams) throws Exception {
        URL obj = new URL(url);
        MyTrackConfig.getInstance().connection = (HttpURLConnection) obj.openConnection();
        MyTrackConfig.getInstance().connection.setUseCaches(false);
        MyTrackConfig.getInstance().connection.setRequestMethod("POST");
        MyTrackConfig.getInstance().connection.setRequestProperty("Host", Constant.HOST_NAME);
        MyTrackConfig.getInstance().connection.setRequestProperty("User-Agent", Constant.USER_AGENT);
        MyTrackConfig.getInstance().connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        MyTrackConfig.getInstance().connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (MyTrackConfig.getInstance().cookies != null) {
            for (String cookie : MyTrackConfig.getInstance().cookies) {
                MyTrackConfig.getInstance().connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        MyTrackConfig.getInstance().connection.setRequestProperty("Connection", "keep-alive");
        MyTrackConfig.getInstance().connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        MyTrackConfig.getInstance().connection.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
        MyTrackConfig.getInstance().connection.setDoOutput(true);
        MyTrackConfig.getInstance().connection.setDoInput(true);
        DataOutputStream wr = new DataOutputStream(MyTrackConfig.getInstance().connection.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = MyTrackConfig.getInstance().connection.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(MyTrackConfig.getInstance().connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private void getAttendance(String body) {
        attendances = new ArrayList<>();
        int startIndex = body.indexOf("Remark");
        startIndex = body.indexOf("<td>", startIndex + 1);
        while (startIndex >= 0 && body.indexOf("Today") > startIndex) {
            Attendance attendance = new Attendance();
            startIndex = startIndex + 4;
            int endIndex = body.indexOf("</td>", startIndex);
            attendance.date = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            attendance.in = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            attendance.out = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            attendance.hours = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td", endIndex + 1);
            startIndex = startIndex + 5;
            endIndex = body.indexOf("</td>", startIndex);
            attendance.remark = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("</tr>", endIndex + 1);
            startIndex = body.indexOf("<td>", startIndex);
            attendances.add(attendance);
        }

    }

    private void getAdditional(String body) {
        int startIndex = body.indexOf("No Pay");
        startIndex = body.indexOf("'>", startIndex + 1);
        startIndex = startIndex + 2;
        int endIndex = body.indexOf("</td>", startIndex);
        mNoPay = body.substring(startIndex, endIndex).trim();
        System.out.println(body.substring(startIndex, endIndex).trim());
        startIndex = body.indexOf("Missing In", endIndex + 1);
        startIndex = body.indexOf("'>", startIndex + 1);
        startIndex = startIndex + 2;
        endIndex = body.indexOf("</td>", startIndex);
        mMissingIn = body.substring(startIndex, endIndex).trim();
        System.out.println(body.substring(startIndex, endIndex).trim());
        startIndex = body.indexOf("Missing Out", endIndex + 1);
        startIndex = body.indexOf("'>", startIndex + 1);
        startIndex = startIndex + 2;
        endIndex = body.indexOf("</td>", startIndex);
        mMissingOut = body.substring(startIndex, endIndex).trim();
        System.out.println(body.substring(startIndex, endIndex).trim());


    }
}
