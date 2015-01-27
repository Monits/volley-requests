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

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * An RFC compliant request that submits jsons in it's body.
 */
public abstract class JsonRfcCompliantListenableRequest<T> extends
				RfcCompliantListenableRequest<T> {

	private final String json;
	private final Map<String, String> headers;
	
	/**
	 * Creates a new RfcCompliantListenableRequest instance
	 * 
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param jsonBody The contents of the json to be sent in the request's body.
	 */
	public JsonRfcCompliantListenableRequest(final int method, @NonNull final String url,
					@NonNull final Listener<T> listener, @Nullable final ErrorListener errListener,
					@Nullable final String jsonBody) {
		super(method, url, listener, errListener);
		
		this.json = jsonBody;
		headers = new HashMap<String, String>();
	}
	
	/**
	 * Manually add a header to this request.
	 * 
	 * @param name The name of the header
	 * @param value The value of the header.
	 */
	public void addHeader(final String name, final String value) {
		headers.put(name, value);
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers;
	}

	@Override
	public String getBodyContentType() {
		return "application/json";
	}

	@SuppressFBWarnings(value = { "DM_DEFAULT_ENCODING", "MDM_STRING_BYTES_ENCODING" },
					justification = "The encoding will be sent with the headers automatically")
	@Nullable
	@Override
	public byte[] getBody() throws AuthFailureError {
		return json == null ? null : json.getBytes();
	}
}
