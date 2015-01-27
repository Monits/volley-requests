/*
* Copyright 2010 - 2014 Monits
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.android.volley;

import java.util.Map;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Cache.Entry;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Wrapper for the {@link Request} class, that on failure delegates to a policy to check
 * if the request should be reattempted and perform actions before doing so (unlike {@link RetryPolicy}).
 * 
 * This request is specially useful for things such as detecting credentials to our service expired,
 * and need to revalidate them before we can attempt to retry this request.
 * 
 * By using the decorator pattern, this behavior can be added to any request regardless of it's type
 * and origin.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class RequeueAfterRequestDecorator<T> extends Request<T> {

	private final Request<T> wrapped;
	private final RequeuePolicy requeuePolicy;

	// Can't be final only because we can't instantiate it until the request get's added to a request queue
	private RequestQueue queue;

	/**
	 * Private constructor. Call {@link RequeueAfterRequestDecorator#wrap(Request, RequeuePolicy)} instead.
	 *
	 * @see {@link RequeueAfterRequestDecorator#wrap(Request, RequeuePolicy)}
	 */
	private RequeueAfterRequestDecorator(@NonNull final Request<T> request,
					@NonNull final RequeuePolicy requeuePolicy) {
		super(request.getMethod(), request.getUrl(), null);

		wrapped = request;

		this.requeuePolicy = requeuePolicy;
	}

	/**
	 * Wraps a request to extend it's retry functionality.
	 *
	 * @param request the request to wrap.
	 * @param requeuePolicy the interface to define retry.
	 *
	 * @return the request.
	 */
	public static <T> RequeueAfterRequestDecorator<T> wrap(
					@NonNull final Request<T> request, @NonNull final RequeuePolicy requeuePolicy) {

		return new RequeueAfterRequestDecorator<T>(request, requeuePolicy);
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

	@Nullable
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

	/**
	 * @deprecated Use {@link #getBody()} instead.
	 */
	@Deprecated
	@Override
	public byte[] getPostBody() throws AuthFailureError {
		return wrapped.getPostBody();
	}

	/**
	 * @deprecated Use {@link #getBodyContentType()} instead.
	 */
	@Deprecated
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
	public Request<?> setCacheEntry(final Entry entry) {
		wrapped.setCacheEntry(entry);
		return super.setCacheEntry(entry);
	}

	@Override
	public Request<?> setRequestQueue(final RequestQueue requestQueue) {
		this.queue = requestQueue;
		
		wrapped.setRequestQueue(requestQueue);
		return super.setRequestQueue(requestQueue);
	}

	@SuppressFBWarnings(value = "UR_UNINIT_READ_CALLED_FROM_SUPER_CONSTRUCTOR",
					justification = "The read is done to avoid a NPE, and is properly documented.")
	@Override
	public Request<?> setRetryPolicy(final RetryPolicy retryPolicy) {
		/*
		 * Request calls to setRetryPolicy on the constructor,
		 * we don't want to override the retry policy set on the wrapped request
		 * with the default one, so we ignore that on first call (and avoid a NPE).
		 */
		if (wrapped != null) {
			wrapped.setRetryPolicy(retryPolicy);
		}

		return super.setRetryPolicy(retryPolicy);
	}

	@Override
	public Request<?> setTag(final Object tag) {
		wrapped.setTag(tag);
		return super.setTag(tag);
	}
}
