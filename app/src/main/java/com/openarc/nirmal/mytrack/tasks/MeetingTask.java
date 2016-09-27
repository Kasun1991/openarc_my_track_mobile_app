/*
 * </summary>
 * Source File	: MeetingTask.java
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

import com.openarc.nirmal.mytrack.event.MeetingRetrievedEvent;
import com.openarc.nirmal.mytrack.model.MeetingRoom;
import com.openarc.nirmal.mytrack.model.Reservation;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MeetingTask extends AsyncTask<Void, Void, String> {

    List<MeetingRoom> mMeetingRoomList;
    List<Reservation> mReservationList;
    String mRequestType;
    String mParameter;
    String mReservationStatusSource = "";

    public MeetingTask(String mRequestType, String mParameter) {
        this.mRequestType = mRequestType;
        this.mParameter = mParameter;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (mRequestType.contentEquals("POST")) {
                if (mParameter.contentEquals("")) {
                    return GetPageContent();
                } else {
                    mReservationStatusSource = postReservation(mParameter);
                    return GetPageContent();
                }
            } else {
                return GetPageContent();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.contentEquals("")) {
            MeetingRetrievedEvent event = new MeetingRetrievedEvent();
            event.mReservationStatus = "";
            event.mMeetingRoomList = new ArrayList<>();
            event.mReservationList = new ArrayList<>();
            EventBus.getDefault().post(event);
        } else {
            getMeetingRoom(s);
            getReservation(s);
            MeetingRetrievedEvent event = new MeetingRetrievedEvent();
            if(mReservationStatusSource.contains("ERROR")) {
                event.mReservationStatus = "ERROR";
            } else {
                event.mReservationStatus = "";
            }
            event.mMeetingRoomList = mMeetingRoomList;
            event.mReservationList = mReservationList;
            EventBus.getDefault().post(event);
        }
    }

    private String GetPageContent() throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "MeetingRooms.php");
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

    private String postReservation(String url) throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "CheckMeetingRoomReservation.php?" + url);
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

    private void getMeetingRoom(String body) {
        mMeetingRoomList = new ArrayList<>();
        int startIndex = body.indexOf("<tbody>");
        startIndex = body.indexOf("<tr>", startIndex + 1);
        while (startIndex >= 0 && body.indexOf("</tbody>") > startIndex) {
            MeetingRoom meetingRoom = new MeetingRoom();
            startIndex = body.indexOf("<td>", startIndex + 1);
            startIndex = startIndex + 4;
            int endIndex = body.indexOf("</td>", startIndex);
            meetingRoom.name = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = body.indexOf("\">", startIndex + 1);
            startIndex = startIndex + 2;
            endIndex = body.indexOf("</a>", startIndex);
            meetingRoom.status = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<tr>", endIndex);
            mMeetingRoomList.add(meetingRoom);
        }
    }

    private void getReservation(String body) {
        mReservationList = new ArrayList<>();
        int startIndex = body.indexOf("MY RESERVATION");
        startIndex = body.indexOf("<tbody>", startIndex + 1);
        startIndex = body.indexOf("<tr>", startIndex + 1);
        while (startIndex >= 0 && body.indexOf("\"DetailModalLabel\"") > startIndex) {
            Reservation mReservation = new Reservation();
            startIndex = body.indexOf("<td>", startIndex + 1);
            startIndex = startIndex + 4;
            int endIndex = body.indexOf("</td>", startIndex);
            mReservation.room = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            mReservation.reason = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            mReservation.date = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            mReservation.inTime = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            mReservation.outTime = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<tr>", endIndex);
            mReservationList.add(mReservation);
        }
    }
}
