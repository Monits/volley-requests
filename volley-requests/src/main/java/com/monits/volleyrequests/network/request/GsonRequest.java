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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonRequest<T> extends JsonRfcCompliantListenableRequest<T> {
	private final Gson gson;
	private final Type clazz;

	public GsonRequest(final int method, @NonNull final String url, @NonNull final Gson gson,
					@NonNull final Type clazz, @NonNull final Listener<T> listener,
					@Nullable final ErrorListener errListener, @Nullable final String jsonBody) {
		super(method, url, listener, errListener, jsonBody);

		this.gson = gson;
		this.clazz = clazz;
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
}
