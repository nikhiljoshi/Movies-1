package com.indydev.dooxmovies.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class RequestHttpClient{
	
	ProgressDialog progressDialog;
	RequestHttpClientListenner listenner;
	String url;
	Context activity;
	
	public interface RequestHttpClientListenner{
		public void onRequestStringCallback(String response);
	}
	
	public RequestHttpClient(String _url, RequestHttpClientListenner _listenner, Context _activity){
		url = _url;
		listenner = _listenner;
		activity = _activity;
	}
	
	public void start(){
		if(url!=null && !url.equals("")){
			if (activity!=null && progressDialog == null) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(true);
            }   
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(url, new AsyncHttpResponseHandler() {
			    @Override
			    public void onSuccess(String response) {
//			        Log.i("RequestHttpClient", response);
			    	try {
			    		if (progressDialog!=null && progressDialog.isShowing()) {
		                    progressDialog.dismiss();
		                }
			    		
			    		if(listenner!=null){
			    			response = response.trim();
				        	listenner.onRequestStringCallback(response);
				        }
				         
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("ERROR = "+e.getMessage());
					}
			          
			    }
			});
		}
	}
    
}
