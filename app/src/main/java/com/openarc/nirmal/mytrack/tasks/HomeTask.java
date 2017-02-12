/*
 * </summary>
 * Source File	: HomeTask.java
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
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.openarc.nirmal.mytrack.R;
import com.openarc.nirmal.mytrack.adapter.BirthdayAdapter;
import com.openarc.nirmal.mytrack.adapter.NewsfeedAdapter;
import com.openarc.nirmal.mytrack.model.Birthday;
import com.openarc.nirmal.mytrack.model.KeyPerformanceIndicator;
import com.openarc.nirmal.mytrack.model.Newsfeed;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.HomeView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeTask extends AsyncTask<Void, Void, Void> {

    private HomeView homeView;
    private List<Birthday> birthdays;
    private List<Newsfeed> newsfeeds;
    private KeyPerformanceIndicator kpi;
    private Context context;

    public HomeTask(Context context, HomeView homeView) {
        this.context = context;
        this.homeView = homeView;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            extractData(MyTrackConfig.getInstance().homeSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        homeView.rlLoading.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        homeView.rvBirthday.setHasFixedSize(true);
        homeView.rvBirthday.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        homeView.rvBirthday.setAdapter(new BirthdayAdapter(context, birthdays));
        if (!(Double.parseDouble(kpi.cawh) > 9.5))
            homeView.tvCAWH.setTextColor(context.getResources().getColor(R.color.red));

        if (!(Double.parseDouble(kpi.mawh) > 9.5))
            homeView.tvMAWH.setTextColor(context.getResources().getColor(R.color.red));

//        if (!(Double.parseDouble(kpi.sui.replace("%", "")) > 92))
//            homeView.tvSUI.setTextColor(context.getResources().getColor(R.color.red));

        homeView.tvCAWH.setText(kpi.cawh);
        homeView.tvMAWH.setText(kpi.mawh);
        homeView.tvSUI.setText(kpi.sui);
        homeView.tvUserName.setText(MyTrackConfig.getInstance().loggedUser);
        homeView.rvNewsfeed.setHasFixedSize(true);
        homeView.rvNewsfeed.setLayoutManager(new LinearLayoutManager(context));
        homeView.rvNewsfeed.setAdapter(new NewsfeedAdapter(newsfeeds));
        Picasso.with(context).load(MyTrackConfig.getInstance().imageUrl).into(homeView.ivUser);
        homeView.rlLoading.setVisibility(View.GONE);
    }

    private void extractData(String body) {
        getSubmitAuthorized(body);
        getLoggedUser(body);
        getBirthday(body);
        getKpi(body);
        getNewsFeed(body);
    }

    private void getSubmitAuthorized(String body) {
        MyTrackConfig.getInstance().submitAuthorized = body.indexOf("Submit Missing Attendance") > 0 ? true : false;
    }

    private void getLoggedUser(String body) {
        int startIndex = body.indexOf("navbar-right");
        startIndex = body.indexOf("src", startIndex + 1);
        startIndex = startIndex + 5;
        int endIndex = body.indexOf("style", startIndex + 1);
        endIndex = endIndex - 2;
        MyTrackConfig.getInstance().imageUrl = Constant.SERVER_ROOT + body.substring(startIndex, endIndex).trim();
        System.out.println(body.substring(startIndex, endIndex).trim());
        startIndex = body.indexOf("data-toggle", startIndex + 1);
        startIndex = body.indexOf(">", startIndex + 1);
        startIndex = startIndex + 1;
        endIndex = body.indexOf("<b", startIndex + 1);
        MyTrackConfig.getInstance().loggedUser = body.substring(startIndex, endIndex).trim();
        System.out.println(body.substring(startIndex, endIndex).trim());
    }

    private void getBirthday(String body) {
        birthdays = new ArrayList<>();
        int startIndex = body.indexOf("Birthdays");
        startIndex = body.indexOf("float:left", startIndex + 1);
        while (startIndex >= 0) {
            Birthday birthday = new Birthday();
            startIndex = startIndex + 13;
            int endIndex = body.indexOf("<i", startIndex + 1);
            endIndex = endIndex - 1;
            birthday.name = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("dept", startIndex + 1);
            startIndex = startIndex + 6;
            endIndex = body.indexOf("</i>", startIndex + 1);
            birthday.division = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("nws_date", endIndex + 1);
            startIndex = startIndex + 10;
            endIndex = body.indexOf("<sup>", startIndex + 1);
            birthday.date = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = endIndex + 5;
            endIndex = body.indexOf("</sup>", startIndex + 1);
            birthday.dateName = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = endIndex + 6;
            endIndex = body.indexOf("</span>", startIndex + 1);
            birthday.month = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("src", endIndex + 1);
            startIndex = startIndex + 5;
            endIndex = body.indexOf("class", startIndex + 1);
            endIndex = endIndex - 2;
            birthday.image = Constant.SERVER_ROOT + body.substring(startIndex, endIndex).trim();
            System.out.println(Constant.SERVER_ROOT + body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("float:left", endIndex + 1);
            birthdays.add(birthday);
        }

    }

    private void getKpi(String body) {
        kpi = new KeyPerformanceIndicator();
        int startIndex = body.indexOf("Cumulative AWH");
        startIndex = body.indexOf("</span>", startIndex + 1);
        startIndex = body.indexOf(":", startIndex + 1);
        startIndex = startIndex + 2;
        int endIndex = body.indexOf("</h4>", startIndex + 1);
        kpi.cawh = body.substring(startIndex, endIndex).trim();
        System.out.println(body.substring(startIndex, endIndex).trim());
        startIndex = body.indexOf("Monthly AWH", startIndex + 1);
        startIndex = body.indexOf("</span>", startIndex + 1);
        startIndex = body.indexOf(":", startIndex + 1);
        startIndex = startIndex + 2;
        endIndex = body.indexOf("</h4>", startIndex + 1);
        kpi.mawh = body.substring(startIndex, endIndex).trim();
        System.out.println(body.substring(startIndex, endIndex).trim());
//        startIndex = body.indexOf("SUI", startIndex + 1);
//        startIndex = body.indexOf("</span>", startIndex + 1);
//        startIndex = body.indexOf(":", startIndex + 1);
//        startIndex = startIndex + 2;
//        endIndex = body.indexOf("</h4>", startIndex + 1);
        kpi.sui = "N/A";
//        System.out.println(body.substring(startIndex, endIndex).trim());

    }

    private void getNewsFeed(String body) {
        newsfeeds = new ArrayList<>();
        int startIndex = body.indexOf("News Feed");
        startIndex = body.indexOf("list-group-item-text", startIndex + 1);
        while (startIndex >= 0) {
            Newsfeed newsfeed = new Newsfeed();
            startIndex = startIndex + 22;
            int endIndex = body.indexOf("<br>", startIndex + 1);
            newsfeed.newsFeed = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("nws_date", startIndex + 1);
            startIndex = startIndex + 10;
            endIndex = body.indexOf("</span>", startIndex + 1);
            newsfeed.timeStamp = body.substring(startIndex, endIndex).trim();
            System.out.println(body.substring(startIndex, endIndex).trim());
            startIndex = body.indexOf("list-group-item-text", endIndex + 1);
            newsfeeds.add(newsfeed);
        }
    }


}
