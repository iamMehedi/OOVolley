package com.android.volley.extension;

import java.util.Map;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectRequest<T> extends Request<T> {

	private final Class<T> clazz;
	protected final Map<String, String> params;
	private Map<String, String> headers;
	private final onResponseListener responseListener;
	private final HTTP_REQUEST_TAG REQ_TAG;

	/**
	 * Make a HTTP request and return a parsed object from JSON.
	 * 
	 * @param url
	 *            URL of the request to make
	 * @param responseType
	 *            Relevant class object, for Jackson's reflection
	 * @param params
	 *            Map of request parameters
	 * @param onResponseListener
	 *            Listener to return response to
	 */
	public ObjectRequest(int method, String url, Map<String, String> params,
			Class<T> responseType, HTTP_REQUEST_TAG TAG,
			onResponseListener onResponseListener, ErrorListener listener) {
		super(method, url, listener);
		this.clazz = responseType;
		this.params = params;
		this.responseListener = onResponseListener;
		this.REQ_TAG = TAG;
	}

	/**
	 * set the connection timeout interval
	 */
	public void setTimeoutInterval(int millis) {
		setRetryPolicy(new DefaultRetryPolicy(millis,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	/**
	 * Make a HTTP request and return a parsed object from JSON.
	 * 
	 * @param url
	 *            URL of the request to make
	 * @param responseType
	 *            Relevant class object, for Jackson's reflection
	 * @param params
	 *            Map of request parameters
	 * @param onResponseListener
	 *            Listener to return response to
	 * @param headers
	 *            Map of http headers
	 */
	public ObjectRequest(int method, String url, Map<String, String> params,
			Class<T> responseType, HTTP_REQUEST_TAG TAG,
			onResponseListener onResponseListener, ErrorListener listener,
			Map<String, String> headers) {
		this(method, url, params, responseType, TAG, onResponseListener,
				listener);
		this.headers = headers;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		// TODO Auto-generated method stub
		return params != null ? params : super.getParams();
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		try {
			String json = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			Log.d("JSON", json);
			if (clazz == String.class) {
				return Response.success((T) json,
						HttpHeaderParser.parseCacheHeaders(response));
			}
			// Log.d("TYPE", clazz.getCanonicalName());
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);
			// Log.d("MAPPER",""+
			// mapper.canDeserialize(mapper.getTypeFactory().constructType(clazz)));
			T responseObject = mapper.readValue(json, clazz);

			return Response.success(responseObject,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.d("PARSER", ex.getMessage());
		}
		return null;
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		responseListener.onReceiveResponse(REQ_TAG, response);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		// TODO Auto-generated method stub
		return headers != null ? headers : super.getHeaders();
	}
}
