/*
 * </summary>
 * Source File	: AttendanceSubmitTask.java
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

import android.os.AsyncTask;

import com.openarc.nirmal.mytrack.event.AttendanceSubmitRetrievedEvent;
import com.openarc.nirmal.mytrack.model.AttendanceSubmit;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class AttendanceSubmitTask extends AsyncTask<Void, Void, String> {

    String mRequestType;
    String mParameter;
    List<AttendanceSubmit> mAttendanceSubmitList;

    public AttendanceSubmitTask(String mRequestType, String mParameter) {
        this.mRequestType = mRequestType;
        this.mParameter = mParameter;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (mRequestType.contentEquals("POST")) {
                if (mParameter.contentEquals("")) {
                    return getPageContent();
                } else {
                    return sendPost(mParameter);
                }
            } else if (mRequestType.contentEquals("GET")) {
                return getPageContent();
            } else {
                postDelete(mParameter);
                return getPageContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.contentEquals("")) {
            AttendanceSubmitRetrievedEvent event = new AttendanceSubmitRetrievedEvent();
            event.attendanceSubmitList = new ArrayList<>();
            EventBus.getDefault().post(event);
        } else {
            getContacts(s);
            AttendanceSubmitRetrievedEvent event = new AttendanceSubmitRetrievedEvent();
            event.attendanceSubmitList = mAttendanceSubmitList;
            EventBus.getDefault().post(event);
        }
    }

    private String getPageContent() throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "MissingAttendance.php");
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

    private String sendPost(String postParams) throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "MissingAttendance.php");
        MyTrackConfig.getInstance().connection = (HttpURLConnection) obj.openConnection();
        MyTrackConfig.getInstance().connection.setUseCaches(false);
        MyTrackConfig.getInstance().connection.setRequestMethod("POST");
        MyTrackConfig.getInstance().connection.setRequestProperty("Host", Constant.HOST_NAME);
        MyTrackConfig.getInstance().connection.setRequestProperty("User-Agent", Constant.USER_AGENT);
        MyTrackConfig.getInstance().connection.setRequestProperty("Accept-Charset", Constant.ACCEPT_CHARSET);
        MyTrackConfig.getInstance().connection.setRequestProperty("Accept-Language", Constant.ACCEPT_LANGUAGE);
        if (MyTrackConfig.getInstance().cookies != null) {
            for (String cookie : MyTrackConfig.getInstance().cookies) {
                MyTrackConfig.getInstance().connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        MyTrackConfig.getInstance().connection.setRequestProperty("Connection", "keep-alive");
        MyTrackConfig.getInstance().connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        MyTrackConfig.getInstance().connection.setDoOutput(true);
        MyTrackConfig.getInstance().connection.setDoInput(true);
        DataOutputStream wr = new DataOutputStream(MyTrackConfig.getInstance().connection.getOutputStream());
        wr.write(postParams.getBytes("UTF-8"));
        wr.flush();
        wr.close();
        MyTrackConfig.getInstance().connection.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(MyTrackConfig.getInstance().connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private String postDelete(String date) throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "DeleteMissingAttendance.php?DELDATE=" + date);
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

    private void getContacts(String body) {
        mAttendanceSubmitList = new ArrayList<>();
        int startIndex = body.indexOf("<tbody>");
        startIndex = body.indexOf("<tr>", startIndex + 1);
        while (startIndex >= 0 && body.indexOf("</tbody>") > startIndex) {
            AttendanceSubmit attendanceSubmit = new AttendanceSubmit();
            startIndex = body.indexOf("<td>", startIndex + 1);
            startIndex = startIndex + 4;
            int endIndex = body.indexOf("</td>", startIndex);
            attendanceSubmit.date = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            attendanceSubmit.timeIn = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            attendanceSubmit.timeOut = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            attendanceSubmit.remark = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            attendanceSubmit.status = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<tr>", endIndex + 1);
            mAttendanceSubmitList.add(attendanceSubmit);
        }
    }
}
