package com.indydev.dooxmovies.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.indydev.dooxmovies.R;
import com.indydev.dooxmovies.main.MovieJsonParser;
import com.indydev.dooxmovies.main.MovieObject;
import com.indydev.dooxmovies.main.MovieJsonParser.MovieJsonParserListenner;
import com.jeremyfeinstein.slidingmenu.example.SampleListFragment.SampleAdapter;
import com.jeremyfeinstein.slidingmenu.example.fragments.ColorFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

@SuppressLint("ValidFragment")
public class MovieListFragment extends ListFragment implements
		MovieJsonParserListenner {

	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	
//	ItemAdapter adapter;

//	SampleAdapter adapter;
	
	ArrayList<MovieObject> movies;
	MovieJsonParser movieJsonParser;
	boolean nowRequest;

	String url_bikini = "http://mazmelllow.zz.mu/movies.php";
	String url_school = "http://mazmelllow.zz.mu/movies.php";
	String url_cute = "http://mazmelllow.zz.mu/movies.php";
	String url_special = "http://mazmelllow.zz.mu/movies.php";

	public MovieListFragment(int page) {
		// TODO Auto-generated constructor stub
		movies = new ArrayList<MovieObject>();
		switch (page) {
		case 0:
			// bikini
			movieJsonParser = new MovieJsonParser(url_bikini,
					MovieJsonParser.PARSE_MOVIE_X, this, getActivity());
			break;

		case 1:
			// school girls
			movieJsonParser = new MovieJsonParser(url_school,
					MovieJsonParser.PARSE_MOVIE_X, this, getActivity());
			break;

		case 2:
			// cute
			movieJsonParser = new MovieJsonParser(url_cute,
					MovieJsonParser.PARSE_MOVIE_X, this, getActivity());
			break;

		case 3:
			// special
			movieJsonParser = new MovieJsonParser(url_special,
					MovieJsonParser.PARSE_MOVIE_X, this, getActivity());
			break;

		default:
			break;
		}

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(20))
				.build();
		
//		adapter = new SampleAdapter(getActivity());
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//
//		adapter = new ItemAdapter();
//		ListView gv = (ListView) inflater.inflate(R.layout.list, null);
//		// gv.setBackgroundResource(android.R.color.black);
//		gv.setAdapter(adapter);
//		gv.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if (getActivity() == null)
//					return;
//
//			}
//		});
//		
//		return gv;
//	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());
		for (int i = 0; i < 20; i++) {
			adapter.add(new SampleItem("Sample List", android.R.drawable.ic_menu_search));
		}
		setListAdapter(adapter);
	}
	
	private class SampleItem {
		public String tag;
		public int iconRes;
		public SampleItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

//		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		
		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
//			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
//			title.setText(getItem(position).tag);
			
			MovieObject mov = movies.get(position);
			title.setText(mov.getTitle());
//			imageLoader.displayImage(mov.getImage(), icon, options,
//					animateFirstListener);
//
//			adapter.notifyDataSetChanged();

			return convertView;
		}

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
			return movies.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getActivity().getLayoutInflater().inflate(
						R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.desc = (TextView) view.findViewById(R.id.desc);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			MovieObject mov = movies.get(position);
			holder.text.setText(mov.getTitle());
			holder.desc.setText(mov.getCreatetime());
			imageLoader.displayImage(mov.getImage(), holder.image, options,
					animateFirstListener);

//			adapter.notifyDataSetChanged();
			
			return view;
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		if (!nowRequest) {
//			System.out.println("--- START REQUEST ---");
//			// request for new movies
//			nowRequest = true;
//			movieJsonParser.start();
//		}
	}

	@Override
	public void onGetMovieList(ArrayList<MovieObject> listMovie) {
		// TODO Auto-generated method stub
		if (listMovie != null && listMovie.size() > 0) {
			// replace to movies
			movies = listMovie;
			nowRequest = false;

			// test
			for (int i = 0; i < movies.size(); i++) {
				MovieObject movieObject = movies.get(i);
				System.out.println("title = " + movieObject.getTitle());
				System.out.println("url = " + movieObject.getUrl());
				System.out.println("image = " + movieObject.getImage());
				System.out.println("createtime = "
						+ movieObject.getCreatetime());
				System.out.println("----------------------------");
			}
		}

	}

}
