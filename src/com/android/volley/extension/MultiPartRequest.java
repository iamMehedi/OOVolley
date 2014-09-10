package com.android.volley.extension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyLog;

public class MultiPartRequest<T> extends ObjectRequest<T>{

	static final int DEFAULT_TIMEOUT_MS=30000;
	
	MultipartEntityBuilder mBuilder;
	HttpEntity httpEntity;
	
	Map<String, String> mfileParams;
	
	public MultiPartRequest(int method, String url, Map<String, String> params,
			Class<T> responseType, HTTP_REQUEST_TAG TAG,
			onResponseListener onResponseListener, ErrorListener listener, Map<String, String> files) {
		super(method, url, params, responseType, TAG, onResponseListener, listener);
		mBuilder=MultipartEntityBuilder.create();
		mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		mfileParams=files;
		setTimeoutInterval(DEFAULT_TIMEOUT_MS);
		buildMultipartBody();
	}

	public MultiPartRequest(int method, String url, Map<String, String> params,
			Class<T> responseType, HTTP_REQUEST_TAG TAG,
			onResponseListener onResponseListener, ErrorListener listener,Map<String, String> headers,  Map<String, String> files) {
		super(method, url, params, responseType, TAG, onResponseListener, listener, headers);
		mBuilder=MultipartEntityBuilder.create();
		mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		mfileParams=files;
		setTimeoutInterval(DEFAULT_TIMEOUT_MS);
		buildMultipartBody();
	}
	
	private void buildMultipartBody()
	{
		if(this.mfileParams!=null)
		{
			for(Map.Entry<String, String> entry:this.mfileParams.entrySet())
			{
				File mFile=new File(entry.getValue());
				mBuilder.addPart(entry.getKey(), new FileBody(mFile));
			}
		}
		//mBuilder.addPart(FILE_PART_NAME, new FileBody(mFilePart));
		
		if(this.params!=null)
		{
			for(Map.Entry<String, String> entry : this.params.entrySet())
			{
				mBuilder.addTextBody(entry.getKey(), entry.getValue());
			}
		}
	}
	
	@Override
	public String getBodyContentType() {
		// TODO Auto-generated method stub
		return httpEntity.getContentType().getValue();
	}
	
	@Override
	public byte[] getBody() throws AuthFailureError {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		httpEntity=mBuilder.build();
		try {
			httpEntity.writeTo(bos);
		} catch (IOException e) {
			e.printStackTrace();
			VolleyLog.e(e.getMessage());
		}
		return bos.toByteArray();
	}
}
