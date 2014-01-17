package com.monits.android_volley.network.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class EmptyResponseRequest extends RfcCompliantListenableRequest<Void> {

	private final String json;
	
	public EmptyResponseRequest(final int method, final String url,
			final Listener<Void> listener, final ErrorListener errListener, final String json) {
		super(method, url, listener, errListener);
		
		this.json = json;
	}

	@Override
	protected Response<Void> parseNetworkResponse(final NetworkResponse response) {
		return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
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
