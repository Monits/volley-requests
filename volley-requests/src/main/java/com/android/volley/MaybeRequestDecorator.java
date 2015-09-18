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

import com.android.volley.toolbox.HttpHeaderParser;

import java.net.HttpURLConnection;

/**
 * Class to parse a list of all items in a collection from a NetworkResponse
 * depending on a key.
 *
 * @author fpredassi
 *
 * @param <T> The type of the request being decorated.
 */
public class MaybeRequestDecorator<T> extends RequestDecorator<T> {
	private final T object;

	/**
	 * Constructor
	 *
	 * @param request The request to be decorated
	 * @param object The object you want to send in the request
	 */
	public MaybeRequestDecorator(@NonNull final Request<T> request, @NonNull final T object) {
		super(request);
		this.object = object;
	}

	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		if (response.statusCode == HttpURLConnection.HTTP_CREATED) {
			return Response.success(object,
					HttpHeaderParser.parseCacheHeaders(response));
		}
		return super.parseNetworkResponse(response);
	}

	@NonNull
	public T getObject() {
		return object;
	}
}
