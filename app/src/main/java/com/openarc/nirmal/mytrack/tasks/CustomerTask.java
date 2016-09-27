/*
 * </summary>
 * Source File	: CustomerTask.java
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

import com.openarc.nirmal.mytrack.event.CustomerRetrievedEvent;
import com.openarc.nirmal.mytrack.model.Customer;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class CustomerTask extends AsyncTask<Void, Void, String> {

    List<Customer> customers;

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
            CustomerRetrievedEvent event = new CustomerRetrievedEvent();
            event.customers = new ArrayList<>();
            EventBus.getDefault().post(event);
        } else {
            getContacts(s);
            CustomerRetrievedEvent event = new CustomerRetrievedEvent();
            event.customers = customers;
            EventBus.getDefault().post(event);
        }

    }

    private String GetPageContent() throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "CustomerList.php");
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
        customers = new ArrayList<>();
        int startIndex = body.indexOf("<tbody>");
        startIndex = body.indexOf("<tr>", startIndex + 1);
        while (startIndex >= 0 && body.indexOf("</tbody>") > startIndex) {
            Customer customer = new Customer();
            startIndex = body.indexOf("<td>", startIndex + 1);
            startIndex = startIndex + 4;
            int endIndex = body.indexOf("</td>", startIndex);
            customer.shortCode = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            customer.name = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex + 1);
            startIndex = startIndex + 4;
            endIndex = body.indexOf("</td>", startIndex);
            extractLocation(customer, body.substring(startIndex, endIndex).trim());
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<tr>", endIndex);
            customers.add(customer);
        }
    }

    private void extractLocation(Customer customer, String body) {
        if (body.contains("View on Map")) {
            int startIndex = body.indexOf("data-longtitude");
            startIndex = startIndex + 17;
            int endIndex = body.indexOf("'", startIndex + 1);
            customer.longtitude = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("data-latitude", endIndex + 1);
            startIndex = startIndex + 15;
            endIndex = body.indexOf("'", startIndex + 1);
            customer.latitude = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
        }
    }
}
