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
package com.indydev.dooxmovies.uil;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import buzzcity.android.sdk.BCAdsClientBanner;

import com.indydev.dooxmovies.R;
import com.indydev.dooxmovies.activity.MovieListActivity;
import com.indydev.dooxmovies.main.MovieJsonParser;
import com.indydev.dooxmovies.main.MovieJsonParser.MovieJsonParserListenner;
import com.indydev.dooxmovies.main.GPSTracker;
import com.indydev.dooxmovies.main.MovieObject;
import com.indydev.dooxmovies.main.RequestHttpClient;
import com.indydev.dooxmovies.main.RequestHttpClient.RequestHttpClientListenner;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HomeActivity extends BaseActivity implements
		MovieJsonParserListenner {

	ArrayList<MovieObject> moviesBikini, moviesSchool, moviesCute, moviesSpecial;
	MovieJsonParser movieJsonParser;
	boolean nowRequest;
	
	boolean openBikini, openSchool, openCute, openSpecial;
	
	public static final String COUNTRY_CODE = "country_code";
	
	
//	String url_playlist = "http://gdata.youtube.com/feeds/api/playlists/RD021kz6hNDlEEg?v=1&alt=json";
//	String url_user = "http://gdata.youtube.com/feeds/api/users/UCHmpi5o1Fm2PDGa1izasg8w/uploads?&v=2&max-results=50&alt=jsonc";  //UCHmpi5o1Fm2PDGa1izasg8w  WishesOnTheEarth
	
	double longitude;
	double latitude;
	
	String countryCode;
	
	String url_bikini = "http://gdata.youtube.com/feeds/api/users/UCHmpi5o1Fm2PDGa1izasg8w/uploads?&v=2&max-results=50&alt=jsonc";
	String url_school = "http://gdata.youtube.com/feeds/api/users/UCHmpi5o1Fm2PDGa1izasg8w/uploads?&v=2&max-results=50&alt=jsonc";
	String url_cute = "http://gdata.youtube.com/feeds/api/users/UCHmpi5o1Fm2PDGa1izasg8w/uploads?&v=2&max-results=50&alt=jsonc";
	String url_special = "http://gdata.youtube.com/feeds/api/users/UCHmpi5o1Fm2PDGa1izasg8w/uploads?&v=2&max-results=50&alt=jsonc";

//	public static DatabaseManager dbMgr;
	
	SharedPreferences sharedPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);

		BCAdsClientBanner graphicAdClient = new BCAdsClientBanner(106400,
				BCAdsClientBanner.ADTYPE_MWEB,
				BCAdsClientBanner.IMGSIZE_MWEB_216x36, this);
		ImageView graphicalAds = (ImageView) findViewById(R.id.ads);
		graphicAdClient.getGraphicalAd(graphicalAds);
		
		
		if (sharedPref == null) {
			sharedPref = this.getSharedPreferences(
					getString(R.string.preference_file_key),
					Context.MODE_PRIVATE);
		}
		countryCode = sharedPref.getString(COUNTRY_CODE, null);
		
		if(countryCode==null){
			// check if GPS enabled
	        GPSTracker gpsTracker = new GPSTracker(this);

	        if (gpsTracker.canGetLocation())
	        {
	            String stringLatitude = String.valueOf(gpsTracker.latitude);
	            System.out.println("stringLatitude = "+stringLatitude);

	            String stringLongitude = String.valueOf(gpsTracker.longitude);
	            System.out.println("stringLongitude = "+stringLongitude);
	            
	            doRequestCountry(stringLatitude,stringLongitude);            
	        }
	        else
	        {
	            // can't get location
	            // GPS or Network is not enabled
	            // Ask user to enable GPS/network in settings
	            gpsTracker.showSettingsAlert();
	        }
		}else{
			if(countryCode.equalsIgnoreCase("Thailand")){
				//open special url
				url_special = "http://mazmelllow.zz.mu/movies.php";
				moviesSpecial = null;
			}
		}
		

//		dbMgr = new DatabaseManager(this);
//		chapterReadList = new ArrayList<String>();
//		ArrayList<HashMap<String, String>> listChapterReadHash = dbMgr
//				.getAllChapterRead();
//		for (int i = 0; i < listChapterReadHash.size(); i++) {
//			HashMap<String, String> hash = listChapterReadHash.get(i);
//			String url = hash.get("url");
//			if (!url.endsWith("/")) {
//				url += "/";
//			}
//			System.out.println("-- url chapter read: " + url);
//			chapterReadList.add(url);
//		}
        
        
        
	}
	
	private void doRequestCountry(String lat, String lon){
		String url = "http://api.worldweatheronline.com/free/v1/search.ashx?q="+lat+","+lon+"&format=json&key=tc4feefhdfuh75f6pvupfnhz";
		RequestHttpClient locationClient = new RequestHttpClient(url, new RequestHttpClientListenner() {
			@Override
			public void onRequestStringCallback(String response) {
				// TODO Auto-generated method stub
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONObject search_api = jsonObject.getJSONObject("search_api");
					JSONArray results = search_api.getJSONArray("result");
					if(results.length()>0){
						JSONObject result = results.getJSONObject(0);
						JSONArray country = result.getJSONArray("country");
						countryCode = country.getJSONObject(0).getString("value");
					}
					System.out.println("countryCode = "+countryCode);
					
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(countryCode!=null){
					// Save to shared preferences
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString(COUNTRY_CODE, countryCode);
					editor.commit();
					
					if(countryCode.equalsIgnoreCase("Thailand")){
						//open special url
						url_special = "http://mazmelllow.zz.mu/movies.php";
						moviesSpecial = null;
					}
					
				}
					
			}
		}, null);
		locationClient.start();
	}

	public void onClickBikini(View view) {
		openBikini = true;
		openCute = false;
		openSchool = false;
		openSpecial = false;
		if(moviesBikini==null){
			movieJsonParser = new MovieJsonParser(url_bikini,
					MovieJsonParser.PARSE_YOUTUBE, this, this);
			movieJsonParser.start();
		}else{
			onGetMovieList(moviesBikini);
		}
	}

	public void onClickSchool(View view) {
		openBikini = false;
		openCute = false;
		openSchool = true;
		openSpecial = false;
		if(moviesSchool==null){
			movieJsonParser = new MovieJsonParser(url_school,
					MovieJsonParser.PARSE_YOUTUBE, this, this);
			movieJsonParser.start();
		}else{
			onGetMovieList(moviesSchool);
		}
	}
	
	public void onClickCute(View view) {
		openBikini = false;
		openCute = true;
		openSchool = false;
		openSpecial = false;
		if(moviesCute==null){
			movieJsonParser = new MovieJsonParser(url_cute,
					MovieJsonParser.PARSE_YOUTUBE, this, this);
			movieJsonParser.start();
		}else{
			onGetMovieList(moviesCute);
		}
	}
	
	public void onClickSpecial(View view) {
		openBikini = false;
		openCute = false;
		openSchool = false;
		openSpecial = true;
		if(moviesSpecial==null){
			movieJsonParser = new MovieJsonParser(url_special,
					MovieJsonParser.PARSE_MOVIE_X, this, this);
			movieJsonParser.start();
		}else{
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
		
		if(openBikini){
			moviesBikini = listMovie;
			MovieListActivity.movies = moviesBikini;
		}else if(openCute){
			moviesCute = listMovie;
			MovieListActivity.movies = moviesCute;
		}else if(openSchool){
			moviesSchool = listMovie;
			MovieListActivity.movies = moviesSchool;
		}else if(openSpecial){
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