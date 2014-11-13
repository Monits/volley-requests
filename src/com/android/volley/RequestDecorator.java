package com.android.volley;

import java.util.Map;

import com.android.volley.Cache.Entry;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.monits.volleyrequests.network.request.JsonRfcCompliantListenableRequest;

public abstract class RequestDecorator<T> extends JsonRfcCompliantListenableRequest<T> {
	private final Request<T> request;

	public RequestDecorator(final Request<T> request, final Gson gson,
			final int method, final String url, final Listener<T> listener,
			final ErrorListener errListener, final String jsonBody) {
		super(method, url, listener, errListener, jsonBody);
		this.request = request;
	}

	@Override
	public int getMethod() {
		return request.getMethod();
	}

	@Override
	public Request<?> setTag(final Object tag) {
		return request.setTag(tag);
	}

	@Override
	public Object getTag() {
		return request.getTag();
	}

	@Override
	public int getTrafficStatsTag() {
		return request.getTrafficStatsTag();
	}

	@Override
	public Request<?> setRetryPolicy(final RetryPolicy retryPolicy) {
		if (request != null) {
			request.setRetryPolicy(retryPolicy);
		}
		return super.setRetryPolicy(retryPolicy);
	}

	@Override
	public void addMarker(final String tag) {
		request.addMarker(tag);
	}

	@Override
	public Request<?> setRequestQueue(final RequestQueue requestQueue) {
		return request.setRequestQueue(requestQueue);
	}

	@Override
	public String getUrl() {
		return request.getUrl();
	}

	@Override
	public String getCacheKey() {
		return request.getCacheKey();
	}

	@Override
	public Request<?> setCacheEntry(final Entry entry) {
		return request.setCacheEntry(entry);
	}

	@Override
	public Entry getCacheEntry() {
		return request.getCacheEntry();
	}

	@Override
	public void cancel() {
		request.cancel();
	}

	@Override
	public boolean isCanceled() {
		return request.isCanceled();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return request.getHeaders();
	}

	@Override
	public String getPostBodyContentType() {
		return request.getPostBodyContentType();
	}

	@Override
	public byte[] getPostBody() throws AuthFailureError {
		return request.getPostBody();
	}

	@Override
	public String getBodyContentType() {
		return request.getBodyContentType();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		return request.getBody();
	}

	@Override
	public com.android.volley.Request.Priority getPriority() {
		return request.getPriority();
	}

	@Override
	public RetryPolicy getRetryPolicy() {
		return request.getRetryPolicy();
	}

	@Override
	public void markDelivered() {
		request.markDelivered();
	}

	@Override
	public boolean hasHadResponseDelivered() {
		return request.hasHadResponseDelivered();
	}

	@Override
	public void deliverError(final VolleyError error) {
		request.deliverError(error);
	}

	@Override
	public int compareTo(final Request<T> other) {
		return request.compareTo(other);
	}

	@Override
	public String toString() {
		return request.toString();
	}


	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		return request.parseNetworkResponse(response);
	}


}
