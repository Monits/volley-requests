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
package com.monits.volleyrequests.network.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * A generic {@link Request} that adds a listener for success callback.
 */
public abstract class ListenableRequest<T> extends Request<T> {

	private final Listener<T> listener;
	
	/**
	 * Creates a new ListenableRequest instance
	 * 
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 */
	public ListenableRequest(final int method, @NonNull final String url,
             @NonNull final Listener<T> listener, @Nullable final ErrorListener errListener) {
		super(method, url, errListener);
		
		this.listener = listener;
	}

	@Override
	protected void deliverResponse(final T ret) {
		listener.onResponse(ret);
	}
}
