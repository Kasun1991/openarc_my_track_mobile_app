/*
 * </summary>
 * Source File	: BaseActivity.java
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    public Dialog mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void showToast(String message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void showWaiting(Context context) {

        if (mProgress == null) {
            mProgress = new Dialog(context, R.style.Progressbar);
            mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgress.setContentView(R.layout.custom_progress_spinner);
            mProgress.setCancelable(false);
        }

        if (mProgress.isShowing() == false) {
            mProgress.show();
        }
    }

    public void dismissWaiting() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    public void hideKeyBord() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void AboutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_about, null);
        alertDialogBuilder.setView(dialogView);
        ImageView ivClose = (ImageView) dialogView.findViewById(R.id.ivClose);
        final Button bAboutDeveloper = (Button) dialogView.findViewById(R.id.bAboutDeveloper);
        final AlertDialog parentAlertDialog = alertDialogBuilder.create();
        bAboutDeveloper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BaseActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_about_developer, null);
                    alertDialogBuilder.setView(dialogView);
                    ImageView ivBack = (ImageView) dialogView.findViewById(R.id.ivBack);
                    ImageView ivClose = (ImageView) dialogView.findViewById(R.id.ivClose);
                    TextView tvEmail = (TextView) dialogView.findViewById(R.id.tvEmail);
                    tvEmail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    tvEmail.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setType("plain/text");
                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"developer.kasun.gunathilaka@gmail.com"});
                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Track");
                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                                startActivity(emailIntent);
                                parentAlertDialog.dismiss();
                                alertDialog.dismiss();
                            }
                            return false;
                        }
                    });
                    ivBack.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                alertDialog.dismiss();
                            }
                            return false;
                        }
                    });
                    ivClose.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                parentAlertDialog.dismiss();
                                alertDialog.dismiss();
                            }
                            return false;
                        }
                    });
                    alertDialog.show();
                }
                return false;
            }

        });
        ivClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    parentAlertDialog.dismiss();
                }
                return false;
            }
        });
        parentAlertDialog.show();
    }

    public void hideLeaveSubmit(NavigationView navigationView) {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_submit_missing_attendance).setVisible(false);
    }
}
