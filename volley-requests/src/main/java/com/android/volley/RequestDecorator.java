/**
 * Copyright 2010 - 2015 Monits
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

import com.android.volley.Cache.Entry;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class RequestDecorator<T> extends Request<T> {
	protected final Request<T> wrapped;

	public RequestDecorator(@NonNull final Request<T> request) {
		super(request.getMethod(), request.getUrl(), null);
		this.wrapped = request;
	}

	public Request<T> getRequest() {
		return wrapped;
	}

	@Override
	public int getMethod() {
		return wrapped.getMethod();
	}

	@Override
	public Request<?> setTag(final Object tag) {
		return wrapped.setTag(tag);
	}

	@Override
	public Object getTag() {
		return wrapped.getTag();
	}

	@Override
	public int getTrafficStatsTag() {
		return wrapped.getTrafficStatsTag();
	}

	@SuppressFBWarnings(value = "UR_UNINIT_READ_CALLED_FROM_SUPER_CONSTRUCTOR",
					justification = "The read is done to avoid a NPE, and is properly documented.")
	@Override
	public Request<?> setRetryPolicy(final RetryPolicy retryPolicy) {
		//noinspection ConstantConditions
		if (wrapped != null) {
            // Method is called from constructor, before we initialize wrapped
			wrapped.setRetryPolicy(retryPolicy);
		}
		return super.setRetryPolicy(retryPolicy);
	}

	@Override
	protected VolleyError parseNetworkError(final VolleyError volleyError) {
		return wrapped.parseNetworkError(volleyError);
	}

	@Override
	public void addMarker(final String tag) {
		super.addMarker(tag);
		wrapped.addMarker(tag);
	}

	@Override
	public Request<?> setRequestQueue(final RequestQueue requestQueue) {
		super.setRequestQueue(requestQueue);
		return wrapped.setRequestQueue(requestQueue);
	}

	@Override
	public String getUrl() {
		return wrapped.getUrl();
	}

	@Override
	public String getCacheKey() {
		return wrapped.getCacheKey();
	}

	@Override
	public Request<?> setCacheEntry(final Entry entry) {
		return wrapped.setCacheEntry(entry);
	}

	@Override
	public Entry getCacheEntry() {
		return wrapped.getCacheEntry();
	}

	@Override
	public void cancel() {
		wrapped.cancel();
	}

	@Override
	public boolean isCanceled() {
		return wrapped.isCanceled();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return wrapped.getHeaders();
	}

	@Override
	public String getPostBodyContentType() {
		return wrapped.getPostBodyContentType();
	}

	@Override
	public byte[] getPostBody() throws AuthFailureError {
		return wrapped.getPostBody();
	}

	@Override
	public String getBodyContentType() {
		return wrapped.getBodyContentType();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		return wrapped.getBody();
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
	public void markDelivered() {
		wrapped.markDelivered();
	}

	@Override
	public boolean hasHadResponseDelivered() {
		return wrapped.hasHadResponseDelivered();
	}

	@Override
	public void deliverError(final VolleyError error) {
		wrapped.deliverError(error);
	}

	@Override
	public String toString() {
		return "RequestDecorator for " + wrapped;
	}

	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		return wrapped.parseNetworkResponse(response);
	}

	@Override
	protected void deliverResponse(final T response) {
		wrapped.deliverResponse(response);
	}

	@Override
	public Response.ErrorListener getErrorListener() {
		return wrapped.getErrorListener();
	}
}
