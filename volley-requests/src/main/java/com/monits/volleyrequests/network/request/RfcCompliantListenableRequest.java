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

package com.monits.volleyrequests.network.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * A ListenableRequest that complies with RFC 2616, allowing for non GET / HEAD
 * methods to invalidate the cache (notice DEPRECATED_GET_OR_POST is not supported).
 *
 * Volley is awesome, but is too eager to cache stuff, so it does not complies to RFC 2616,
 * in particular with Section 13.10, which states:
 * <pre>Some HTTP methods MUST cause a cache to invalidate an entity.
	This is either the entity referred to by the Request-URI, or by
	the Location or Content-Location headers (if present). These methods are:
      - PUT
      - DELETE
      - POST</pre>
 * Nor with Section 13.11, which states:
 * <pre>All methods that might be expected to cause modifications to the
 	origin server's resources MUST be written through to the origin server.
 	This currently includes all methods except for GET and HEAD.
 	A cache MUST NOT reply to such a request from a client before having
 	transmitted the request to the inbound server, and having received a
 	corresponding response from the inbound server. This does not prevent
 	a proxy cache from sending a 100 (Continue) response before the inbound
 	server has sent its final reply.</pre>
 * In other words, Volley will not hit the origin server for any request (not even POST / PUT /DELETE)
 * if for that very url there is an item in cache.
 * 
 * Bare in mind this isn't 100% spec compliant. The cache is not checked before performing the request
 * but it's also used to actually store the response, shall it bring
 *
 * @author Juan Martín Sotuyo Dodero {@literal <jmsotuyo@monits.com>}
 */
public abstract class RfcCompliantListenableRequest<T> extends ListenableRequest<T> {

	private RequestQueue requestQueue;

	/**
	 * Creates a new RfcCompliantListenableRequest instance
	 * 
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param cancelListener The listener for cancel.
	 */
	public RfcCompliantListenableRequest(final int method, @NonNull final String url,
					@Nullable final Listener<T> listener,
					@Nullable final ErrorListener errListener,
					@Nullable final CancelListener cancelListener) {
		super(method, url, listener, errListener, cancelListener);

		if (method == Method.DEPRECATED_GET_OR_POST) {
			throw new IllegalArgumentException(
				"DEPRECATED_GET_OR_POST is deprecated and not supported, choose wither GET or POST");
		}

		// Should we really hit the server?
		setShouldCache(method == Method.GET || method == Method.HEAD);
	}

	/**
	 * Creates a new RfcCompliantListenableRequest instance with
	 * fewer parameters for backwards compatibility.
	 *
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 */
	public RfcCompliantListenableRequest(final int method, @NonNull final String url,
						@Nullable final Listener<T> listener,
						@Nullable final ErrorListener errListener) {
		this(method, url, listener, errListener, null);
	}

	@Override
	public Request<?> setRequestQueue(final RequestQueue requestQueue) {
		this.requestQueue = requestQueue;

		return super.setRequestQueue(requestQueue);
	}

	@Override
	protected void deliverResponse(final T ret) {
		/*
		 * Manually store in cache if the server replied with caching headers
		 * (valid even for methods that invalidate cache)
		 */
		if (getCacheEntry() != null) {
			requestQueue.getCache().put(getCacheKey(), getCacheEntry());
		}

		super.deliverResponse(ret);
	}
}