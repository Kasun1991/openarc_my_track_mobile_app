/*
 * </summary>
 * Source File	: CIRContactTask.java
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

import com.openarc.nirmal.mytrack.event.CIRContactRetrievedEvent;
import com.openarc.nirmal.mytrack.model.CIRContact;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class CIRContactTask extends AsyncTask<Void, Void, String> {

    List<CIRContact> contacts;

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
            CIRContactRetrievedEvent event = new CIRContactRetrievedEvent();
            event.contacts = new ArrayList<>();
            EventBus.getDefault().post(event);
//            Snackbar.make(attendanceView.bView, "Data Retrieve Failed", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
        } else {
            getContacts(s);
            CIRContactRetrievedEvent event = new CIRContactRetrievedEvent();
            event.contacts = contacts;
            EventBus.getDefault().post(event);
        }

    }

    private String GetPageContent() throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "cir_contact_details.php");
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
        contacts = new ArrayList<>();
        int startIndex = body.indexOf("<tbody>");
        startIndex = body.indexOf("<tr>", startIndex + 1);
        while (startIndex >= 0 && body.indexOf("</tbody>") > startIndex) {
            CIRContact contact = new CIRContact();
            startIndex = body.indexOf("<td>", startIndex + 1);
            startIndex = startIndex + 4;
            int endIndex = body.indexOf("</td>", startIndex);
            contact.siteName = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            contact.cirName = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            contact.designation = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            contact.mobile = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            contact.email = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            contact.address = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<tr>", endIndex);
            contacts.add(contact);
        }
    }
}
