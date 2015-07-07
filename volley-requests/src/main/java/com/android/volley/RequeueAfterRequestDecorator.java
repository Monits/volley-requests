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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

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
public final class RequeueAfterRequestDecorator<T> extends RequestDecorator<T> {

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
		super(request);

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

		return new RequeueAfterRequestDecorator<>(request, requeuePolicy);
	}

	@Override
	public Request<?> setRequestQueue(final RequestQueue requestQueue) {
		queue = requestQueue;
		return super.setRequestQueue(requestQueue);
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

		return wrapped.parseNetworkError(volleyError);
	}
}
