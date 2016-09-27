/*
 * </summary>
 * Source File	: LoginTask.java
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
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.openarc.nirmal.mytrack.LoginActivity;
import com.openarc.nirmal.mytrack.MainActivity;
import com.openarc.nirmal.mytrack.util.Constant;
import com.openarc.nirmal.mytrack.util.MyTrackConfig;
import com.openarc.nirmal.mytrack.view.LoginView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class LoginTask extends AsyncTask<Void, Void, String> {

    private String username, password;
    private LoginView loginView;
    private Context context;

    public LoginTask(String username, String password, LoginView loginView, Context context) {
        this.username = username;
        this.password = password;
        this.loginView = loginView;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return downloadUrl();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected void onPreExecute() {
        loginView.etUsername.setEnabled(false);
        loginView.etPassword.setEnabled(false);
        loginView.bLogin.setText("Authenticating..");
        loginView.bLogin.setEnabled(false);
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.contains("Invalid EEE")) {
            loginView.etUsername.setEnabled(true);
            loginView.etPassword.setEnabled(true);
            loginView.bLogin.setText("LOGIN");
            loginView.bLogin.setEnabled(true);
            MyTrackConfig.getInstance().connection = null;
            Snackbar.make(loginView.bLogin, "Credential Invalid", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (s.contentEquals("")) {
            loginView.etUsername.setEnabled(true);
            loginView.etPassword.setEnabled(true);
            loginView.bLogin.setText("LOGIN");
            loginView.bLogin.setEnabled(true);
            Snackbar.make(loginView.bLogin, "Error Occurred", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            MyTrackConfig.getInstance().homeSource = s;
            MyTrackConfig.getInstance().LoggedUserCode = loginView.etUsername.getText().toString();
            context.startActivity(new Intent(context, MainActivity.class));
            LoginActivity loginActivity = (LoginActivity) context;
            loginActivity.finish();
        }

    }

    private String downloadUrl() throws Exception {
        CookieHandler.setDefault(new CookieManager());
        String page = GetPageContent(Constant.SERVER_ROOT);
        String postParams = getFormParams(page, this.username, this.password);
        String loginRespond = sendPost(Constant.LOGIN_PAGE, postParams);
        System.out.println(loginRespond);
        return loginRespond;
    }

    private String sendPost(String url, String postParams) throws Exception {
        int responseCode;
        URL obj = new URL(url);
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

        try {
            responseCode = MyTrackConfig.getInstance().connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
            responseCode = 0;
        }
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

    private String GetPageContent(String url) throws Exception {

        URL obj = new URL(url);
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
        System.out.println("\nSending 'GET' request to URL : " + url);
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

    public String getFormParams(String html, String username, String password)
            throws UnsupportedEncodingException {
        System.out.println("Extracting form's data...");
        Document doc = Jsoup.parse(html);
        Elements inputElements = doc.getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("EMPNO"))
                value = username;
            else if (key.equals("PASSWORD"))
                value = password;
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        // build parameters list
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

    public List<String> getCookies() {
        return MyTrackConfig.getInstance().cookies;
    }

    public void setCookies(List<String> cookies) {
        MyTrackConfig.getInstance().cookies = cookies;
    }
}
