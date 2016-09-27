/*
 * </summary>
 * Source File	: LoginActivity.java
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

package com.openarc.nirmal.mytrack;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.openarc.nirmal.mytrack.tasks.LoginTask;
import com.openarc.nirmal.mytrack.view.LoginView;

public class LoginActivity extends BaseActivity {

    AlertDialog mAlertDialog;
    private LoginView loginView;
    View.OnClickListener onLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                validate();
            } else {
                Snackbar.make(view, "Connection Error", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        loginView.bLogin.setOnClickListener(onLoginClick);
    }

    private void initialize() {
        loginView = new LoginView();
        loginView.etUsername = (EditText) findViewById(R.id.etUsername);
        loginView.etPassword = (EditText) findViewById(R.id.etPassword);
        loginView.bLogin = (Button) findViewById(R.id.bLogin);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validate() {
        String username = loginView.etUsername.getText().toString();
        String password = loginView.etPassword.getText().toString();
        if (username.contentEquals("")) {
            loginView.etUsername.setError("Cannot be empty");
        } else if (password.contentEquals("")) {
            loginView.etPassword.setError("Cannot be empty");
        } else {
            new LoginTask(username, password, loginView, LoginActivity.this).execute();
        }
    }

    @Override
    public void onBackPressed() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
            alertDialogBuilder.setMessage("Do you want to exit the application ?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAlertDialog.cancel();
                }
            });
            mAlertDialog = alertDialogBuilder.create();
            mAlertDialog.show();
            mAlertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(R.color.colorAccent));
            mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}
