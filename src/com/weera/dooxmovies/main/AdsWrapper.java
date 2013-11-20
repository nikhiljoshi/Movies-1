package com.weera.dooxmovies.main;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

import com.weera.dooxmovies.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdsWrapper extends Activity {
        private int windowWidth, windowHeight;
        private boolean adSuccesfullyLoaded = false;
        private int defaultAdsTimeout = 20;
        
        private String blockAdsPreferenceName = "blockAds";
        private String blockStartAdFlagName = "blockStartAd";
        private String blockEndAdFlagName = "blockEndAd";
        private String blockAdsFlagValue = "1" ;

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                this.requestWindowFeature(Window.FEATURE_NO_TITLE);

                Bundle extras = getIntent().getExtras();

                int partnerid = 0;
                if (extras.containsKey("partnerId")) {
                        try{
                                partnerid = Integer.parseInt(extras.getString("partnerId"));
                        }
                        catch (NumberFormatException e) {
                                partnerid = 0;
                        }
                }
                if (partnerid <= 0) {
                        finish();
                        return;
                }

                String appid = "";
                if (extras.containsKey("appId")) {
                        appid = extras.getString("appId");
                        if (appid.equals("YOUR APPLICATION ID")) {
                                appid = "";
                        }
                }
                
                String udid = SHA1(getUUID());
                String ua = getUserAgent();
                
                if (!appid.equals("")) {
                        new CallAppconversionTask(this, appid, udid, ua);
                }

                String adsText = "Powered by BuzzCity";
                if (extras.containsKey("loadingText")) {
                        adsText = extras.getString("loadingText");
                }

                String showAt = "start";
                if (extras.containsKey("showAt")) {
                        showAt = extras.getString("showAt");
                        if (!showAt.equalsIgnoreCase("start") && !showAt.equalsIgnoreCase("middle") && !showAt.equalsIgnoreCase("end")) {
                                showAt = "start";
                        }
                }

                boolean skipEarly = false;
                if (extras.containsKey("skipEarly")) {
                        skipEarly = extras.getString("skipEarly").equalsIgnoreCase("true");
                }

                int adsTimeout = defaultAdsTimeout;
                if (extras.containsKey("adsTimeout")) {
                        try{
                                adsTimeout = Integer.parseInt(extras.getString("adsTimeout"));
                        }
                        catch (NumberFormatException e) {
                                adsTimeout = 0;
                        }

                        if (adsTimeout <= 0) {
                                adsTimeout = defaultAdsTimeout;
                        }
                }

                if ((extras.containsKey("blockAds")) && (extras.getString("blockAds").equalsIgnoreCase("true"))) {
                        if (extras.containsKey("blockAdsPreferenceName")) {
                                blockAdsPreferenceName = extras.getString("blockAdsPreferenceName");
                        }

                        if (extras.containsKey("blockStartAdFlagName")) {
                                blockStartAdFlagName = extras.getString("blockStartAdFlagName");
                        }

                        if (extras.containsKey("blockEndAdFlagName")) {
                                blockEndAdFlagName = extras.getString("blockEndAdFlagName");
                        }

                        if (extras.containsKey("blockAdsFlagValue")) {
                                blockAdsFlagValue = extras.getString("blockAdsFlagValue");
                        }

                        if (blockAdsCompareFlag(showAt)) {
                                finish();
                                return;
                        }
                }

                int viewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                windowHeight = metrics.heightPixels - viewTop;
                windowWidth = metrics.widthPixels;

                RelativeLayout waitLayout = new RelativeLayout(this);
                waitLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

                RelativeLayout.LayoutParams loadingLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                loadingLayout.addRule(RelativeLayout.CENTER_IN_PARENT, 1);

                ProgressBar loading = new ProgressBar(this, null, android.R.attr.progressBarStyle);
                loading.setId(1);
                loading.setLayoutParams(loadingLayout);
                loading.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams tvLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                tvLayout.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                tvLayout.addRule(RelativeLayout.BELOW, loading.getId());
                tvLayout.setMargins(0, 20, 0, 20);

                TextView tv = new TextView(this);
                tv.setLayoutParams(tvLayout);
                tv.setText(adsText);
                tv.setTypeface(null, Typeface.BOLD);

                waitLayout.addView(loading);
                waitLayout.addView(tv);

                RelativeLayout adsLayout = new RelativeLayout(this);
                adsLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                adsLayout.setGravity(Gravity.CENTER);
                adsLayout.setVisibility(View.GONE);

                ImageView adsView = new ImageView(this);
                adsView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

                adsLayout.addView(adsView);

                RelativeLayout mainLayout = new RelativeLayout(this);
                mainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                mainLayout.setBackgroundColor(Color.BLACK);

                int maxBtnSize   = Math.min((int)(0.1 * windowHeight), (int)(0.1 * windowWidth));
                int btnSize      = Math.min(maxBtnSize, 50);
                RelativeLayout.LayoutParams closeBtnLayout = new RelativeLayout.LayoutParams(btnSize, btnSize);
                closeBtnLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                closeBtnLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);

                ImageButton closeBtn = new ImageButton(this);
                closeBtn.setLayoutParams(closeBtnLayout);
                closeBtn.setBackgroundResource(R.drawable.close);
                closeBtn.setOnClickListener(new ImageButton.OnClickListener() {
                        public void onClick(View arg0) {
                                finish();
                        }
                });

                if (!skipEarly) {
                        closeBtn.setVisibility(View.GONE);
                }

                mainLayout.addView(waitLayout);
                mainLayout.addView(adsLayout);
                mainLayout.addView(closeBtn);
                setContentView(mainLayout);

                new FetchadsViewTask(partnerid, waitLayout, adsLayout, adsView, 
                                closeBtn, appid, udid, ua);

                Handler timeoutHandler = new Handler();
                timeoutHandler.postDelayed(new Runnable() {
                        public void run() {
                                if (!adSuccesfullyLoaded) {
                                        finish();
                                }
                        }
                }, adsTimeout*1000);
        }

        private boolean blockAdsCompareFlag(String showAt) {
                SharedPreferences bcWrapper = getSharedPreferences(blockAdsPreferenceName, 0);
                if (bcWrapper != null) {
                        if (showAt.equalsIgnoreCase("start")) {
                                if (bcWrapper.contains(blockStartAdFlagName)) {
                                        int tmp = bcWrapper.getInt(blockStartAdFlagName, 0);
                                        if (isInteger(blockAdsFlagValue)) {
                                                if (Integer.parseInt(blockAdsFlagValue) == tmp) {
                                                        return true;
                                                }
                                        }
                                }
                        }
                        else if (showAt.equalsIgnoreCase("end")) {
                                if (bcWrapper.contains(blockEndAdFlagName)) {
                                        int tmp = bcWrapper.getInt(blockEndAdFlagName, 0);
                                        if (isInteger(blockAdsFlagValue)) {
                                                if (Integer.parseInt(blockAdsFlagValue) == tmp) {
                                                        return true;
                                                }
                                        }
                                }
                        }
                }
                return false;
        }

        private boolean isInteger(String str) {
                try {
                        Integer.parseInt(str);
                        return true;
                } catch (NumberFormatException nfe) {
                        return false;
                }
        }

        private class FetchadsViewTask extends AsyncTask<Void,Void,Void> {
                private int partnerid;
                private Bitmap bannerImg = null;
                private RelativeLayout waitLayout;
                private RelativeLayout adsLayout;
                private ImageView adsView;
                private ImageButton closeBtn;
                private String clickUrl = null;
                private String imgUrl = null;
                private String urlParams = null;

                public FetchadsViewTask(int partnerid, RelativeLayout waitLayout,
                                RelativeLayout adsLayout, ImageView adsView,
                                ImageButton closeBtn, String appid, String udid, String ua) {

                        adSuccesfullyLoaded = false;
                        
                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
                        Date currentLocalTime = cal.getTime();
                        int ts = (int) Math.floor(currentLocalTime.getTime() / 1000) ;

                        this.waitLayout = waitLayout;
                        this.adsLayout = adsLayout;
                        this.adsView = adsView;
                        this.closeBtn = closeBtn;
                        this.partnerid = partnerid;
                        this.urlParams = "&browser=app_android";
                        if (null != udid && !udid.equals("")) {
                                this.urlParams += "&udid=" + udid;
                        }
                        if (null != appid && !appid.equals("")) {
                                this.urlParams += "&appid=" + appid;
                        }
                        this.urlParams += "&ua=" + ua;
                        this.urlParams += "&ts=" + ts;
                        execute();
                }

                @Override
                protected Void doInBackground(Void... params) {
                        clickUrl = "http://click.buzzcity.net/click.php?partnerid=" + this.partnerid + urlParams;
                        imgUrl   = "http://show.buzzcity.net/show.php?partnerid=" + this.partnerid + urlParams;
                        
                        URL url;
                        HttpURLConnection connection;
                        int bannerWidth  = 0;
                        int bannerHeight = 0;
                        
                        try {
                                url = new URL(imgUrl + "&imgsize=320x480,300x250");
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                if (connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                                        InputStream input = connection.getInputStream();
                                        bannerImg = BitmapFactory.decodeStream(input);
                                        if (bannerImg != null) {
                                                bannerHeight = bannerImg.getHeight();
                                                bannerWidth  = bannerImg.getWidth();
                                                
                                                if (bannerHeight <= 1 || bannerWidth <= 1) {
                                                        bannerImg = null;
                                                }
                                        }
                                }
                                
                                if (bannerImg == null) {
                                        url = new URL(imgUrl + "&imgsize=320x50,300x50");
                                        connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoInput(true);
                                        connection.connect();
                                        if (connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                                                InputStream input = connection.getInputStream();
                                                bannerImg = BitmapFactory.decodeStream(input);
                                                if (bannerImg != null) {
                                                        bannerHeight = bannerImg.getHeight();
                                                        bannerWidth  = bannerImg.getWidth();
                                                        
                                                        if (bannerHeight <= 1 || bannerWidth <= 1) {
                                                                bannerImg = null;
                                                        }
                                                }
                                        }
                                }
                        } catch (Exception e) {}
                        
                        return null;
                }

                @Override
                protected void onPostExecute(final Void values) {
                        if (bannerImg != null) {
                                adSuccesfullyLoaded = true;

                                int bannerImgWidth = bannerImg.getWidth();
                                int bannerImgHeight = bannerImg.getHeight();

                                double resizeRatio;
                                int adsHeight, adsWidth;
                                resizeRatio = Math.min(windowWidth/bannerImgWidth,windowHeight/bannerImgHeight);

                                if (resizeRatio >= 2.0) {
                                        adsHeight = (int)(2 * bannerImgHeight);
                                        adsWidth  = (int)(2 * bannerImgWidth);
                                }
                                else {
                                        adsHeight = bannerImgHeight;
                                        adsWidth  = bannerImgWidth;
                                }

                                RelativeLayout.LayoutParams layoutParams;
                                layoutParams = new RelativeLayout.LayoutParams(adsWidth, adsHeight);
                                adsView.setLayoutParams(layoutParams);
                                adsView.setImageBitmap(bannerImg);
                                adsView.setOnClickListener(new ImageView.OnClickListener(){
                                        public void onClick(View arg0) {
                                                Intent browse = new Intent(Intent.ACTION_VIEW , Uri.parse(clickUrl));
                                                startActivity(browse);
                                        }
                                });

                                waitLayout.setVisibility(View.GONE);
                                adsLayout.setVisibility(View.VISIBLE);
                                closeBtn.setVisibility(View.VISIBLE);
                        }
                }
        }

        private class CallAppconversionTask extends AsyncTask<Void,Void,Void> {
                private final String SHARED_PREF_KEY = "BuzzCity_AppTracking_BackUp_Prefs_Key";
                private Context context;
                private String bcAppId = null;
                private String udid;
                private String ua;

                public CallAppconversionTask(Context context, String appId, String udid, String ua){
                        this.context = context.getApplicationContext();
                        this.bcAppId = appId;
                        this.udid = udid;
                        this.ua = ua;
                        execute();
                }

                @Override
                protected Void doInBackground(Void... params) {
                        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = prefs.edit();
                        String appTrackingPersistentString = prefs.getString(SHARED_PREF_KEY, null);

                        if(bcAppId != null && appTrackingPersistentString == null) {                
                                editor.putString(SHARED_PREF_KEY, "APP_TRACKED");
                                editor.commit();

                                boolean successful = false;
                                try {
                                        URL url = new URL("http://ads.buzzcity.net/apptracking.php?appid="+bcAppId+"&udid="+udid+"&ua="+ua);
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoInput(true);
                                        connection.connect();
                                        if (connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                                                successful = true;
                                        }
                                } catch (Exception e) {}

                                if (!successful) {
                                        editor.putString(SHARED_PREF_KEY, "");
                                        editor.commit();
                                }
                        }
                        return null;
                }
        }

        private String getUUID() {
                String uuid;
                String androidId = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
                if (!"9774d56d682e549c".equals(androidId)) {
                        uuid = androidId;
                }
                else {
                        String deviceId = ((TelephonyManager) getBaseContext().getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                        uuid = deviceId;
                }
                return uuid;
        }

        private String SHA1(String text) {
                try {
                        MessageDigest md = MessageDigest.getInstance("SHA-1");
                        byte[] sha1hash = new byte[40];
                        md.update(text.getBytes("iso-8859-1"), 0, text.length());
                        sha1hash = md.digest();
                        return convertToHex(sha1hash);
                }
                catch (Exception e){
                        return null;
                }
        }

        private String convertToHex(byte[] data) {
                StringBuilder sb = new StringBuilder(data.length * 2);
                Formatter fmt = new Formatter(sb);
                for (byte b : data) {
                        fmt.format("%02x", b);
                }
                fmt.close();
                return sb.toString();
        }

        private String getUserAgent() {
                String ua = "";
                try {
                        ua = URLEncoder.encode(new WebView(this).getSettings().getUserAgentString(), "UTF-8");
                } 
                catch (Exception e) {}
                return ua;
        }
}