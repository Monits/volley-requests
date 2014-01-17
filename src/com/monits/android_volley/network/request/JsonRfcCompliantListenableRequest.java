package com.monits.android_volley.network.request;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public abstract class JsonRfcCompliantListenableRequest<T> extends
		RfcCompliantListenableRequest<T> {

	private final String json;
	private final Map<String, String> headers;
	
	public JsonRfcCompliantListenableRequest(final int method, final String url,
			final Listener<T> listener, final ErrorListener errListener, final String jsonBody) {
		super(method, url, listener, errListener);
		
		this.json = jsonBody;
		headers = new HashMap<String, String>();
	}
	
	public void addHeader(final String key, final String value) {
		headers.put(key, value);
	}
	
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers;
	}

	@Override
	public String getBodyContentType() {
		return "application/json";
	}
	
	@Override
	public byte[] getBody() throws AuthFailureError {
		return json == null ? null : json.getBytes();
	}
}
