package com.monits.android_volley.network.request;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * A ListenableRequest that avoids the cache unless using GET (DEPRECATED_GET_OR_POST not supported)
 *
 * Bare in mind this isn't 100% spec compliant. The cache is not checked before performing the request
 * but it's also used to actually store the response, shall it bring
 *
 * @author jmsotuyo
 */
public abstract class RfcCompliantListenableRequest<T> extends ListenableRequest<T> {

	private RequestQueue requestQueue;

	public RfcCompliantListenableRequest(final int method, final String url,
			final Listener<T> listener, final ErrorListener errListener) {
		super(method, url, listener, errListener);

		setShouldCache(method == Method.GET);
	}

	@Override
	public void setRequestQueue(final RequestQueue requestQueue) {
		super.setRequestQueue(requestQueue);

		this.requestQueue = requestQueue;
	}

	@Override
	protected void deliverResponse(final T ret) {
		// Manually store in cache
		if (getCacheEntry() != null) {
			requestQueue.getCache().put(getCacheKey(), getCacheEntry());
		}

		super.deliverResponse(ret);
	}
}
