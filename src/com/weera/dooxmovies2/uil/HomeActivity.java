/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.weera.dooxmovies2.uil;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import buzzcity.android.sdk.BCAdsClientBanner;

import com.weera.dooxmovies2.R;
import com.weera.dooxmovies2.main.AppListActivity;
import com.weera.dooxmovies2.main.GPSTracker;
import com.weera.dooxmovies2.main.MovieJsonParser;
import com.weera.dooxmovies2.main.MovieJsonParser.MovieJsonParserListenner;
import com.weera.dooxmovies2.main.MovieListActivity;
import com.weera.dooxmovies2.main.MovieObject;
import com.weera.dooxmovies2.main.RequestHttpClient;
import com.weera.dooxmovies2.main.RequestHttpClient.RequestHttpClientListenner;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HomeActivity extends BaseActivity implements
		MovieJsonParserListenner {

	ArrayList<MovieObject> moviesBikini, moviesSchool, moviesCute,
			moviesSpecial;
	MovieJsonParser movieJsonParser;
	boolean nowRequest;

	boolean openBikini, openSchool, openCute, openSpecial;

	public static final String COUNTRY_CODE = "country_code";
	public static final String SHOW_POPUP_CONFIRM18UP = "show_popup_confirm18up";

	// String url_playlist =
	// "http://gdata.youtube.com/feeds/api/playlists/RD021kz6hNDlEEg?v=1&alt=json";
	// String url_user =
	// "http://gdata.youtube.com/feeds/api/users/UCHmpi5o1Fm2PDGa1izasg8w/uploads?&v=2&max-results=50&alt=jsonc";
	// //UCHmpi5o1Fm2PDGa1izasg8w WishesOnTheEarth

	double longitude;
	double latitude;

	public static String countryCode;

	String url_bikini = "http://gdata.youtube.com/feeds/api/users/awaziavimotihca/uploads?&v=2&max-results=50&alt=jsonc";
	String url_school = "http://gdata.youtube.com/feeds/api/users/xdaoisakuraxd/uploads?&v=2&max-results=50&alt=jsonc";
	String url_cute = "http://gdata.youtube.com/feeds/api/users/AKB48NAME/uploads?&v=2&max-results=50&alt=jsonc";
	String url_special = "http://gdata.youtube.com/feeds/api/users/windiluv5/uploads?&v=2&max-results=50&alt=jsonc";

	// public static DatabaseManager dbMgr;

	SharedPreferences sharedPref;

	Button specialBtn1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		specialBtn1 = (Button) findViewById(R.id.specialBtn1);
		specialBtn1.setVisibility(View.INVISIBLE);
		specialBtn1.setEnabled(false);

		if (sharedPref == null) {
			sharedPref = this.getSharedPreferences(
					getString(R.string.preference_file_key),
					Context.MODE_PRIVATE);
		}
		countryCode = sharedPref.getString(COUNTRY_CODE, null);

		if (countryCode == null) {
			// check if GPS enabled
			GPSTracker gpsTracker = new GPSTracker(this);

			if (gpsTracker.canGetLocation()) {
				String stringLatitude = String.valueOf(gpsTracker.latitude);
				System.out.println("stringLatitude = " + stringLatitude);

				String stringLongitude = String.valueOf(gpsTracker.longitude);
				System.out.println("stringLongitude = " + stringLongitude);

				doRequestCountry(stringLatitude, stringLongitude);
			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gpsTracker.showSettingsAlert();
			}
		} else {
			if (countryCode.equalsIgnoreCase("Thailand")) {
				// open special url
				url_special = "http://www.mazmellow.com/movies.php";
				moviesSpecial = null;
				specialBtn1.setVisibility(View.VISIBLE);
				specialBtn1.setEnabled(true);
			}
		}

		// dbMgr = new DatabaseManager(this);
		// chapterReadList = new ArrayList<String>();
		// ArrayList<HashMap<String, String>> listChapterReadHash = dbMgr
		// .getAllChapterRead();
		// for (int i = 0; i < listChapterReadHash.size(); i++) {
		// HashMap<String, String> hash = listChapterReadHash.get(i);
		// String url = hash.get("url");
		// if (!url.endsWith("/")) {
		// url += "/";
		// }
		// System.out.println("-- url chapter read: " + url);
		// chapterReadList.add(url);
		// }
		
		
		BCAdsClientBanner graphicAdClient1 = new BCAdsClientBanner(101021,
				BCAdsClientBanner.ADTYPE_MWEB,
				BCAdsClientBanner.IMGSIZE_MWEB_216x36, this);
		ImageView graphicalAds1 = (ImageView) findViewById(R.id.ads0);
		graphicAdClient1.getGraphicalAd(graphicalAds1);

		BCAdsClientBanner graphicAdClient0 = new BCAdsClientBanner(106400,
				BCAdsClientBanner.ADTYPE_MWEB,
				BCAdsClientBanner.IMGSIZE_MWEB_216x36, this);
		ImageView graphicalAds0 = (ImageView) findViewById(R.id.ads);
		graphicAdClient0.getGraphicalAd(graphicalAds0);

		

		showCheckBox();
	}

	private void showCheckBox() {
		if (sharedPref == null) {
			sharedPref = this.getSharedPreferences(
					getString(R.string.preference_file_key),
					Context.MODE_PRIVATE);
		}
		boolean isShow = sharedPref.getBoolean(SHOW_POPUP_CONFIRM18UP, false);
		Log.i("isShow", isShow + "");
		if (!isShow) {
			// not save >> show
			View checkBoxView = View.inflate(this, R.layout.checkbox, null);
			CheckBox checkBox = (CheckBox) checkBoxView
					.findViewById(R.id.checkbox);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					// Save to shared preferences
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putBoolean(SHOW_POPUP_CONFIRM18UP, true);
					editor.commit();
				}
			});
			checkBox.setText("ไม่แสดงข้อความนี้อีก");

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("กรุณายืนยันตัวตน");
			builder.setMessage("คุณแน่ใจแล้วหรือว่ามีอายุมากกว่า 18 ปี?")
					.setView(checkBoxView)
					.setCancelable(true)
					.setPositiveButton("ใช่",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							})
					.setNegativeButton("ไม่",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								
									android.os.Process.killProcess(android.os.Process.myPid());
				                    System.exit(1);
								}
					}).show();
		}

	}

	private void doRequestCountry(String lat, String lon) {
		String url = "http://api.worldweatheronline.com/free/v1/search.ashx?q="
				+ lat + "," + lon + "&format=json&key=tc4feefhdfuh75f6pvupfnhz";
		RequestHttpClient locationClient = new RequestHttpClient(url,
				new RequestHttpClientListenner() {
					@Override
					public void onRequestStringCallback(String response) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(response);
							JSONObject search_api = jsonObject
									.getJSONObject("search_api");
							JSONArray results = search_api
									.getJSONArray("result");
							if (results.length() > 0) {
								JSONObject result = results.getJSONObject(0);
								JSONArray country = result
										.getJSONArray("country");
								countryCode = country.getJSONObject(0)
										.getString("value");
							}
							System.out.println("countryCode = " + countryCode);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (countryCode != null) {
							// Save to shared preferences
							SharedPreferences.Editor editor = sharedPref.edit();
							editor.putString(COUNTRY_CODE, countryCode);
							editor.commit();

							if (countryCode.equalsIgnoreCase("Thailand")) {
								// open special url
								url_special = "http://www.mazmellow.com/movies.php";
								moviesSpecial = null;
								specialBtn1.setVisibility(View.VISIBLE);
								specialBtn1.setEnabled(true);
							}

						}

					}

					@Override
					public void onRequestError(String error) {
						// TODO Auto-generated method stub

					}

				}, HomeActivity.this);
		locationClient.start();
	}
	
	public void onClickAppList(View view){
		Intent intent = new Intent(this, AppListActivity.class);
		startActivity(intent);
	}

	public void onClickBikini(View view) {
		openBikini = true;
		openCute = false;
		openSchool = false;
		openSpecial = false;
		if (moviesBikini == null) {
			movieJsonParser = new MovieJsonParser(url_bikini,
					MovieJsonParser.PARSE_YOUTUBE, this, this);
			movieJsonParser.start();
		} else {
			onGetMovieList(moviesBikini);
		}
	}

	public void onClickSchool(View view) {
		openBikini = false;
		openCute = false;
		openSchool = true;
		openSpecial = false;
		if (moviesSchool == null) {
			movieJsonParser = new MovieJsonParser(url_school,
					MovieJsonParser.PARSE_YOUTUBE, this, this);
			movieJsonParser.start();
		} else {
			onGetMovieList(moviesSchool);
		}
	}

	public void onClickCute(View view) {
		openBikini = false;
		openCute = true;
		openSchool = false;
		openSpecial = false;
		if (moviesCute == null) {
			movieJsonParser = new MovieJsonParser(url_cute,
					MovieJsonParser.PARSE_YOUTUBE, this, this);
			movieJsonParser.start();
		} else {
			onGetMovieList(moviesCute);
		}
	}

	public void onClickSpecial(View view) {
		openBikini = false;
		openCute = false;
		openSchool = false;
		openSpecial = true;
		if (moviesSpecial == null) {
			movieJsonParser = new MovieJsonParser(url_special,
					MovieJsonParser.PARSE_MOVIE_X, this, this);
			movieJsonParser.start();
		} else {
			onGetMovieList(moviesSpecial);
		}
	}

	@Override
	public void onBackPressed() {
		imageLoader.stop();
		super.onBackPressed();
	}

	@Override
	public void onGetMovieList(ArrayList<MovieObject> listMovie) {
		// TODO Auto-generated method stub

		if (openBikini) {
			moviesBikini = listMovie;
			MovieListActivity.movies = moviesBikini;
		} else if (openCute) {
			moviesCute = listMovie;
			MovieListActivity.movies = moviesCute;
		} else if (openSchool) {
			moviesSchool = listMovie;
			MovieListActivity.movies = moviesSchool;
		} else if (openSpecial) {
			moviesSpecial = listMovie;
			MovieListActivity.movies = moviesSpecial;
		}
		Intent intent = new Intent(this, MovieListActivity.class);
		startActivity(intent);

		openBikini = false;
		openCute = false;
		openSchool = false;
		openSpecial = false;
	}

}