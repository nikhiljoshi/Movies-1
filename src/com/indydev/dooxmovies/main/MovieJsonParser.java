package com.indydev.dooxmovies.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.indydev.dooxmovies.main.RequestHttpClient.RequestHttpClientListenner;

public class MovieJsonParser implements RequestHttpClientListenner {

	private int nowParse;
	public static final int PARSE_MOVIE_X = 0;
	public static final int PARSE_YOUTUBE = 1;

	// key hash manga
	public static final String COL_TITLE = "title";
	public static final String COL_URL = "url";
	public static final String COL_IMAGE = "image";
	public static final String COL_CREATETIME = "createtime";
	public static final String COL_VCODE = "vcode";

	RequestHttpClient client;
	MovieJsonParserListenner listenner;
	Context activity;

	public MovieJsonParser(String url, int parseStyle,
			MovieJsonParserListenner _listenner, Context _activity) {
		// TODO Auto-generated constructor stub
		nowParse = parseStyle;
		listenner = _listenner;
		activity = _activity;

		client = new RequestHttpClient(url, this, activity);
	}

	public void start() {
		if (client != null) {
			client.start();
		}
	}

	@Override
	public void onRequestStringCallback(String response) {
		// TODO Auto-generated method stub
		System.out.println("response = "+response);

		if(nowParse == PARSE_MOVIE_X){
			try {
				JSONArray jsonArray = new JSONArray(response);
				
				if(jsonArray!=null && jsonArray.length()>0){
					ArrayList<MovieObject> listMovie = new ArrayList<MovieObject>();
					for(int i=0; i<jsonArray.length(); i++){
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						MovieObject movieObject = new MovieObject();
						if(jsonObj.has(COL_TITLE)) movieObject.setTitle(jsonObj.getString(COL_TITLE));
						if(jsonObj.has(COL_URL)) movieObject.setUrl(jsonObj.getString(COL_URL));
						if(jsonObj.has(COL_IMAGE)) movieObject.setImage(jsonObj.getString(COL_IMAGE));
						if(jsonObj.has(COL_CREATETIME)) movieObject.setCreatetime(jsonObj.getString(COL_CREATETIME));
						listMovie.add(movieObject);
					}
					if(listenner!=null){
						listenner.onGetMovieList(listMovie);
					}
				}
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("error = "+e.getMessage());
			}
		}else if(nowParse == PARSE_YOUTUBE){
			try {
				JSONObject res = new JSONObject(response);
				JSONObject data = res.getJSONObject("data");
				JSONArray items = data.getJSONArray("items");
				ArrayList<MovieObject> listMovie = new ArrayList<MovieObject>();
				for(int i=0; i<items.length(); i++){
					JSONObject item = items.getJSONObject(i);
					MovieObject youtubeObject = new MovieObject();
					
					youtubeObject.setTitle(item.getString("title"));
					youtubeObject.setImage(item.getJSONObject("thumbnail").getString("sqDefault"));
					String mobile = item.getJSONObject("player").getString("mobile"); //"http://m.youtube.com/details?v=EVZYQvWsC54"
					mobile = mobile.substring(mobile.indexOf("v=")+2);
					youtubeObject.setVcode(mobile);
					
					listMovie.add(youtubeObject);
				}
				if(listenner!=null){
					listenner.onGetMovieList(listMovie);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("error = "+e.getMessage());
			}
		}
		

	}


	public interface MovieJsonParserListenner {
		public void onGetMovieList(ArrayList<MovieObject> listMovie);
	}
}
