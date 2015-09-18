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

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class GsonRequest<T> extends JsonRfcCompliantListenableRequest<T> {
	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
		justification = "No need to use in the toString")
	private final Gson gson;
	private final Type clazz;

	/**
	 * Creates a new GsonRequest instance
	 *
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param gson The gson instance to parse the response
	 * @param clazz The {@link Type} of the class T
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param cancelListener The listener for errors.
	 * @param jsonBody The contents of the json to be sent in the request's body.
	 */

	public GsonRequest(final int method, @NonNull final String url, @NonNull final Gson gson,
					@NonNull final Type clazz, @Nullable final Listener<T> listener,
					@Nullable final ErrorListener errListener,
					@Nullable final CancelListener cancelListener,
					@Nullable final String jsonBody) {

		super(method, url, listener, errListener, cancelListener, jsonBody);
		this.gson = gson;
		this.clazz = clazz;
	}

	/**
	 * Creates a new GsonRequest instance, with less parameters for
	 * backwards compatibility.
	 *
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param gson The gson instance to parse the response
	 * @param clazz The {@link Type} of the class T
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param jsonBody The contents of the json to be sent in the request's body.
	 */

	public GsonRequest(final int method, @NonNull final String url, @NonNull final Gson gson,
					@NonNull final Type clazz, @Nullable final Listener<T> listener,
					@Nullable final ErrorListener errListener,
					@Nullable final String jsonBody) {
		this(method, url, gson, clazz, listener, errListener, null, jsonBody);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		try {
			final String json = new String(response.data,
							HttpHeaderParser.parseCharset(response.headers));
			return Response.success((T) gson.fromJson(json, clazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (final UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (final JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public String toString() {
		return "GsonRequest{ clazz="
			+ clazz
			+ " }";
	}
}
