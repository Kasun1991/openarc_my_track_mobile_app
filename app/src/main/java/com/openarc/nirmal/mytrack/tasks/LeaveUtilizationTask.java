/*
 * </summary>
 * Source File	: LeaveUtilizationTask.java
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
import android.view.View;

import com.openarc.nirmal.mytrack.LeaveUtilizationActivity;
import com.openarc.nirmal.mytrack.model.LeaveUtilization;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.LeaveUtilizationView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LeaveUtilizationTask extends AsyncTask<Void, Void, String> {

    List<LeaveUtilization> leaveUtilizations;
    Context context;
    LeaveUtilizationView leaveUtilizationView;

    public LeaveUtilizationTask(Context context, LeaveUtilizationView leaveUtilizationView) {
        this.context = context;
        this.leaveUtilizationView = leaveUtilizationView;
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
    protected void onPreExecute() {
        leaveUtilizationView.rlLoadingLayout.setVisibility(View.VISIBLE);
        leaveUtilizationView.rvLeaveUtilization.setVisibility(View.GONE);
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.contentEquals("")) {
            leaveUtilizationView.rlLoadingLayout.setVisibility(View.GONE);
            leaveUtilizationView.rvLeaveUtilization.setVisibility(View.VISIBLE);
            Snackbar.make(leaveUtilizationView.rlLoadingLayout, "Error Occurred", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            getLeave(s);
            ((LeaveUtilizationActivity) context).setupRecyclerView(leaveUtilizations);
            leaveUtilizationView.rvLeaveUtilization.setVisibility(View.VISIBLE);
            leaveUtilizationView.rlLoadingLayout.setVisibility(View.GONE);
        }
    }

    private String GetPageContent() throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "LeaveUtilization.php");
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

    private void getLeave(String body) {
        leaveUtilizations = new ArrayList<>();
        int startIndex = body.indexOf("<tbody>");
        startIndex = body.indexOf("<tr>", startIndex + 1);
        while (startIndex >= 0 && body.indexOf("</tbody>") > startIndex) {
            LeaveUtilization leaveUtilization = new LeaveUtilization();
            startIndex = body.indexOf("<td>", startIndex + 1);
            startIndex = startIndex + 4;
            int endIndex = body.indexOf("</td>", startIndex);
            leaveUtilization.name = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            leaveUtilization.entitle = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            leaveUtilization.utilize = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            leaveUtilization.available = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<tr>", endIndex);
            leaveUtilizations.add(leaveUtilization);
        }
    }


}
