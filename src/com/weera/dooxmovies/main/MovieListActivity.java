package com.weera.dooxmovies.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import buzzcity.android.sdk.BCAdsClientBanner;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.weera.dooxmovies.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.weera.dooxmovies.uil.AbsListViewBaseActivity;
import com.weera.dooxmovies.uil.BaseActivity;

public class MovieListActivity extends AbsListViewBaseActivity {

	public static ArrayList<MovieObject> movies;
	
	DisplayImageOptions options;

	String[] imageUrls;
	String[] titles;
	String[] urls;
	String[] descs;
	String[] vcodes;
 	
//	DatabaseManager myDb;
	ItemAdapter adapter;
	
	boolean clickVideo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_list);
		
		BaseActivity.imageLoader.init(ImageLoaderConfiguration.createDefault(this));

		imageUrls = new String[movies.size()];
		titles = new String[movies.size()];
		descs = new String[movies.size()];
		urls = new String[movies.size()];
		vcodes = new String[movies.size()];
		
		for(int i=0; i<movies.size(); i++){
			MovieObject movieObject = movies.get(i);
			titles[i] = movieObject.getTitle();
			descs[i] = "";//movieObject.getCreatetime();
			imageUrls[i] = movieObject.getImage();
			urls[i] = movieObject.getUrl();
			vcodes[i] = movieObject.getVcode();
			System.out.println("title = "+movieObject.getTitle());
			
		}

		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.displayer(new /*RoundedBitmapDisplayer(20)*/FadeInBitmapDisplayer(1000))
			.build();

		listView = (ListView) findViewById(android.R.id.list);
		adapter = new ItemAdapter();
		((ListView) listView).setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//open player
				clickVideo = true;
				if(vcodes[position]!=null){
					//open youtube
					System.out.println("vcode = " + vcodes[position]);
					
					//don't forget to random apikey
					
//					Intent intent = YouTubeStandalonePlayer.createVideoIntent(MovieListActivity.this, "AIzaSyBuJweYhLFmX3fvlKhWkv6lnZqNlItSlcs", vcodes[position]);
//					startActivity(intent);
					
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + vcodes[position]));
					startActivity(intent);
				}else{
					
					//open player
					System.out.println("url = " + urls[position]);
					Intent intent = new Intent(Intent.ACTION_VIEW); 
					intent.setDataAndType(Uri.parse(urls[position]), "video/mp4");
					startActivity(intent);
					
				}
			}
		});
		
//		myDb = new DatabaseManager(this);
//		HomeActivity.showTipBookmark(this);
		
		BCAdsClientBanner graphicAdClient = new BCAdsClientBanner(106400,
				BCAdsClientBanner.ADTYPE_MWEB,
				BCAdsClientBanner.IMGSIZE_MWEB_216x36, this);
		ImageView graphicalAds = (ImageView) findViewById(R.id.ads1);
		graphicAdClient.getGraphicalAd(graphicalAds);
		
		BCAdsClientBanner graphicAdClient2 = new BCAdsClientBanner(106896,
				BCAdsClientBanner.ADTYPE_MWEB,
				BCAdsClientBanner.IMGSIZE_MWEB_216x36, this);
		ImageView graphicalAds2 = (ImageView) findViewById(R.id.ads2);
		graphicAdClient2.getGraphicalAd(graphicalAds2);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.notifyDataSetChanged();
		if(clickVideo){
			clickVideo = false;
			//interestial ads
			
			Intent i = new Intent(getApplicationContext(), AdsWrapper.class);
	        i.putExtra("partnerId", "106896");
	        i.putExtra("appId", "com.weera.dooxmovies");
	        i.putExtra("showAt", "start");
	        i.putExtra("skipEarly", "true");
	        i.putExtra("adsTimeout", "10");
	        startActivity(i);
		}
	}

	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
	}

	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView text;
			public TextView desc;
			public ImageView image;
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.desc = (TextView) view.findViewById(R.id.desc);
				holder.image = (ImageView) view.findViewById(R.id.image);				
				
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.text.setText(titles[position]);
			//holder.desc.setText(descs[position]);
			
//			if(imageLoader!=null){
			imageLoader.displayImage(imageUrls[position], holder.image, options, animateFirstListener);
//			}

			return view;
		}
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
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
}
