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

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * A JSON request whose response is empty, for which we only care about it's status code.
 */
public class EmptyResponseRequest extends JsonRfcCompliantListenableRequest<Void> {

	/**
	 * Creates a new EmptyResponseRequest instance
	 * 
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param jsonBody The contents of the json to be sent in the request's body.
	 */
	public EmptyResponseRequest(final int method, @NonNull final String url,
					@Nullable final Listener<Void> listener,
					@Nullable final ErrorListener errListener,
					@Nullable final String jsonBody) {
		super(method, url, listener, errListener, jsonBody);
	}

	@Override
	protected Response<Void> parseNetworkResponse(final NetworkResponse response) {
		return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
	}
}
