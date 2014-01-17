package com.monits.android_volley.network.request;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public abstract class ListenableRequest<T> extends Request<T> {

	private final Listener<T> listener;
	
	public ListenableRequest(final int method, final String url,
			final Listener<T> listener, final ErrorListener errListener) {
		super(method, url, errListener);
		
		this.listener = listener;
	}

	@Override
	protected void deliverResponse(T ret) {
		listener.onResponse(ret);
	}
}
