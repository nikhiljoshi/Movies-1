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
package com.weera.dooxmovies2.main;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import buzzcity.android.sdk.BCAdsClientBanner;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.weera.dooxmovies2.R;
import com.weera.dooxmovies2.main.RequestHttpClient.RequestHttpClientListenner;
import com.weera.dooxmovies2.uil.AbsListViewBaseActivity;
import com.weera.dooxmovies2.uil.BaseActivity;

public class AppListActivity extends AbsListViewBaseActivity implements
		RequestHttpClientListenner {

	DisplayImageOptions options;

	String[] titles;
	String[] imageUrls;
	String[] descs;
	String[] appids;

	ItemAdapter adapter;

	// ----
	RequestHttpClient httpClient;
	String urlList = "http://www.mazmellow.com/applist.php";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_list);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		BaseActivity.imageLoader.init(ImageLoaderConfiguration
				.createDefault(this));

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisc(true)
				.displayer(new FadeInBitmapDisplayer(500)/*
														 * RoundedBitmapDisplayer(
														 * 20)
														 */).build();

		listView = (ListView) findViewById(android.R.id.list);
		adapter = new ItemAdapter();
		((ListView) listView).setAdapter(adapter);

		httpClient = new RequestHttpClient(urlList, this, this);
		httpClient.start();

		BCAdsClientBanner graphicAdClient = new BCAdsClientBanner(101021,
				BCAdsClientBanner.ADTYPE_MWEB,
				BCAdsClientBanner.IMGSIZE_MWEB_216x36, this);
		ImageView graphicalAds = (ImageView) findViewById(R.id.ads6);
		graphicAdClient.getGraphicalAd(graphicalAds);

	}

	public void clickList(int position) {
		// TODO Auto-generated method stub
		final String appName = appids[position];
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + appName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ appName)));
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView text;
			public TextView desc;
			public ImageView image;
		}

		public int getCount() {
			if (imageUrls != null) {
				return imageUrls.length;
			}
			return 0;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.applist_item,
						parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				holder.desc = (TextView) view.findViewById(R.id.desc);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.text.setText(titles[position]);
			holder.desc.setText(descs[position]);

			imageLoader.displayImage(imageUrls[position], holder.image,
					options, animateFirstListener);

			view.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					clickList(position);
				}
			});

			return view;
		}
	}

	public void onRequestStringCallback(String response) {
		// TODO Auto-generated method stub
		try {
			JSONArray jsonArray = new JSONArray(response);

			if (jsonArray != null && jsonArray.length() > 0) {

				titles = new String[jsonArray.length()];
				descs = new String[jsonArray.length()];
				imageUrls = new String[jsonArray.length()];
				appids = new String[jsonArray.length()];

				for (int i = jsonArray.length() - 1; i >= 0; i--) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);

					if (jsonObj.has("title"))
						titles[i] = jsonObj.getString("title");
					if (jsonObj.has("desc"))
						descs[i] = jsonObj.getString("desc");
					if (jsonObj.has("img"))
						imageUrls[i] = jsonObj.getString("img");
					if (jsonObj.has("appid"))
						appids[i] = jsonObj.getString("appid");

				}
				adapter.notifyDataSetChanged();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error = " + e.getMessage());
		}
	}

	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
	}

	public static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	@Override
	public void onRequestError(String error) {
		// TODO Auto-generated method stub
		
	}

}