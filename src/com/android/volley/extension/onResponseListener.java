package com.android.volley.extension;

public interface onResponseListener {

	<T> void onReceiveResponse(HTTP_REQUEST_TAG TAG, T response);
}
