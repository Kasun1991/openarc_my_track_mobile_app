/*
 * </summary>
 * Source File	: MAWHTask.java
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.openarc.nirmal.mytrack.adapter.MAWHAdapter;
import com.openarc.nirmal.mytrack.model.MAWHDetail;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.MAWHDialogView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MAWHTask extends AsyncTask<Void, Void, String> {

    MAWHDialogView mawhDialogView;
    AlertDialog alertDialog;
    private List<MAWHDetail> mawhDetails;
    private Context context;

    public MAWHTask(MAWHDialogView mawhDialogView, Context context, AlertDialog alertDialog) {
        this.mawhDialogView = mawhDialogView;
        this.context = context;
        this.alertDialog = alertDialog;
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
        mawhDialogView.rlLoadingLayout.setVisibility(View.VISIBLE);
        mawhDialogView.rvMawhDialog.setVisibility(View.GONE);
        alertDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.contentEquals("")) {
            mawhDialogView.rvMawhDialog.setVisibility(View.VISIBLE);
            mawhDialogView.rlLoadingLayout.setVisibility(View.GONE);
        } else {
            getMAWHDetail(s);
            mawhDialogView.rvMawhDialog.setHasFixedSize(true);
            mawhDialogView.rvMawhDialog.setLayoutManager(new LinearLayoutManager(context));
            mawhDialogView.rvMawhDialog.setAdapter(new MAWHAdapter(mawhDetails, context));
            mawhDialogView.rvMawhDialog.setVisibility(View.VISIBLE);
            mawhDialogView.rlLoadingLayout.setVisibility(View.GONE);
        }

    }

    private String GetPageContent() throws Exception {
        URL obj = new URL(Constant.SERVER_ROOT + "services/getMonthlyAWH.php?EEENO=" + MyTrackConfig.getInstance().LoggedUserCode);
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
        System.out.println("\nSending 'GET' request to URL : " + Constant.SERVER_ROOT + "services/getMonthlyAWH.php?EEENO=" + MyTrackConfig.getInstance().LoggedUserCode);
        System.out.println("Response Code : " + responseCode);
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

    private void getMAWHDetail(String body) {
        mawhDetails = new ArrayList<>();
        int startIndex = body.indexOf("<td>");
        while (startIndex >= 0) {
            MAWHDetail mawhDetail = new MAWHDetail();
            startIndex = startIndex + 4;
            int endIndex = body.indexOf("</td>", startIndex);
            mawhDetail.date = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("\">", startIndex);
            startIndex = startIndex + 2;
            endIndex = body.indexOf("</td>", startIndex);
            mawhDetail.MAWH = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("\">", startIndex);
            startIndex = startIndex + 2;
            endIndex = body.indexOf("</td>", startIndex);
            mawhDetail.CAWH = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("<td>", endIndex);
            mawhDetails.add(mawhDetail);
        }
    }
}
