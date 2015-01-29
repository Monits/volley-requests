package com.android.volley;

import android.support.annotation.NonNull;

import java.util.Map;

import com.android.volley.Cache.Entry;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class RequestDecorator<T> extends Request<T> {
	private final Request<T> request;

	public RequestDecorator(@NonNull final Request<T> request, final int method,
					@NonNull final String url) {
		super(method, url, null);
		this.request = request;
	}

	public Request<T> getRequest() {
		return request;
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

	@SuppressFBWarnings(value = "UR_UNINIT_READ_CALLED_FROM_SUPER_CONSTRUCTOR",
					justification = "The read is done to avoid a NPE, and is properly documented.")
	@Override
	public Request<?> setRetryPolicy(final RetryPolicy retryPolicy) {
		//noinspection ConstantConditions
		if (request != null) {
            // Method is called from constructor, before we initialize request
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
	public String toString() {
		return "RequestDecorator for " + request;
	}

	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		return request.parseNetworkResponse(response);
	}

	@Override
	protected void deliverResponse(final T response) {
		request.deliverResponse(response);
	}
}
