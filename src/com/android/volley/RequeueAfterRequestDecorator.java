package com.android.volley;

import java.util.Map;

import android.util.Log;

import com.android.volley.Cache.Entry;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class RequeueAfterRequestDecorator<T> extends Request<T> {

	private final Request<T> wrapped;

	private final RequestQueue queue;

	private final RequeuePolicy requeuePolicy;

	private RequeueAfterRequestDecorator(
			final RequestQueue queue, final Request<T> request, final RequeuePolicy requeuePolicy) {
		super(request.getMethod(), request.getUrl(), null);

		wrapped = request;

		this.queue = queue;
		this.requeuePolicy = requeuePolicy;
	}

	/**
	 * Wraps a request to extend it's retry functionality.
	 *
	 * @param queue the queue where this request should be re-queued.
	 * @param request the request to wrap.
	 * @param requeuePolicy the interface to define retry.
	 *
	 * @return the request.
	 */
	public static <T> RequeueAfterRequestDecorator<T> wrap(
			final RequestQueue queue, final Request<T> request, final RequeuePolicy requeuePolicy) {

		return new RequeueAfterRequestDecorator<T>(queue, request, requeuePolicy);
	}

	@Override
	protected void deliverResponse(final T arg0) {
		if (arg0 != null) {
			wrapped.deliverResponse(arg0);
		}
	}

	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse networkResponse) {
		return wrapped.parseNetworkResponse(networkResponse);
	}

	@Override
	protected VolleyError parseNetworkError(final VolleyError volleyError) {
		final NetworkResponse response = volleyError.networkResponse;
		if (requeuePolicy.shouldRequeue(response)) {

			try {
				wrapped.getRetryPolicy().retry(new VolleyError("Retry strategy decided to retry."));
			} catch (final VolleyError e) {
				return e;
			}

			requeuePolicy.executeBeforeRequeueing(
					new Listener<Object>() {

						@Override
						public void onResponse(final Object arg0) {
							queue.add(RequeueAfterRequestDecorator.this);
						}

					},
					new ErrorListener() {

						@Override
						public void onErrorResponse(final VolleyError error) {
							Log.e(VolleyLog.TAG, "Failed to retry", error.getCause());
						}
					});

			return null;
		}

		return super.parseNetworkError(volleyError);
	}

	// Wrapper methods
	@Override
	public void addMarker(final String tag) {
		wrapped.addMarker(tag);
		super.addMarker(tag);
	}

	@Override
	public void cancel() {
		wrapped.cancel();
		super.cancel();
	}

	@Override
	public void deliverError(final VolleyError error) {
		wrapped.deliverError(error);
		super.deliverError(error);
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		return wrapped.getBody();
	}

	@Override
	public String getBodyContentType() {
		return wrapped.getBodyContentType();
	}

	@Override
	public Entry getCacheEntry() {
		return wrapped.getCacheEntry();
	}

	@Override
	public String getCacheKey() {
		return wrapped.getCacheKey();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return wrapped.getHeaders();
	}

	@Override
	public int getMethod() {
		return wrapped.getMethod();
	}

	@Override
	public byte[] getPostBody() throws AuthFailureError {
		return wrapped.getPostBody();
	}

	@Override
	public String getPostBodyContentType() {
		return wrapped.getPostBodyContentType();
	}

	@Override
	public com.android.volley.Request.Priority getPriority() {
		return wrapped.getPriority();
	}

	@Override
	public RetryPolicy getRetryPolicy() {
		return wrapped.getRetryPolicy();
	}

	@Override
	public Object getTag() {
		return wrapped.getTag();
	}

	@Override
	public int getTrafficStatsTag() {
		return wrapped.getTrafficStatsTag();
	}

	@Override
	public String getUrl() {
		return wrapped.getUrl();
	}

	@Override
	public boolean hasHadResponseDelivered() {
		return wrapped.hasHadResponseDelivered();
	}

	@Override
	public boolean isCanceled() {
		return wrapped.isCanceled();
	}

	@Override
	public void markDelivered() {
		wrapped.markDelivered();
		super.markDelivered();
	}

	@Override
	public void setCacheEntry(final Entry entry) {
		wrapped.setCacheEntry(entry);
		super.setCacheEntry(entry);
	}

	@Override
	public void setRequestQueue(final RequestQueue requestQueue) {
		wrapped.setRequestQueue(requestQueue);
		super.setRequestQueue(requestQueue);
	}

	@Override
	public void setRetryPolicy(final RetryPolicy retryPolicy) {
		/*
		 * Request calls to setRetryPolicy on the constructor,
		 * we don't want to override the retry policy set on the wrapped request
		 * with the default one, so we ignore that (and avoid a NPE).
		 */
		if (wrapped != null) {
			wrapped.setRetryPolicy(retryPolicy);
		}

		super.setRetryPolicy(retryPolicy);
	}

	@Override
	public void setTag(final Object tag) {
		wrapped.setTag(tag);
		super.setTag(tag);
	}
}
