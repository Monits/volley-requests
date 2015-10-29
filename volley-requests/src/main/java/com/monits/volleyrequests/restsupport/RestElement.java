/**
 * Copyright 2010 - 2015 Monits S.A.
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
package com.monits.volleyrequests.restsupport;

import android.support.annotation.NonNull;

import com.android.volley.MaybeRequestDecorator;
import com.android.volley.Request;
import com.google.gson.Gson;

import java.lang.reflect.Type;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A representation of a single element in a REST API.
 *
 * @param <T> The type of objects both submitted and retrieved from the API at the current endpoint.
 */
@SuppressFBWarnings(value = "CD_CIRCULAR_DEPENDENCY",
	justification = "Tight coupling is intended, we just use different interfaces to direct"
		+ " the method calls flow")
public class RestElement<T> extends RestBase<T, T> implements UrlBuilder.ElementUrlBuilder {

	/*package*/ RestElement(@NonNull final String url, @NonNull final Gson gson, @NonNull final Class<T> clazz) {
		super(url, gson, clazz);
	}

	@Override
	protected Type getDecodeType(@NonNull final Class<T> type) {
		return type;
	}

	@NonNull
	@Override
	protected Request<T> decorateRequest(@NonNull final Request<T> request) {
		if (method == Request.Method.POST || method == Request.Method.PUT) {
			return new MaybeRequestDecorator<>(request, obj);
		}

		return request;
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	public <U> RequestBuilder<U, U> get(@NonNull final Class<U> clazz) {
		return (RequestBuilder<U, U>) super.get(clazz);
	}

	@NonNull
	@Override
	public RestCollection<T> getList() {
		return new RestCollection<>(url.substring(0, url.lastIndexOf('/')), gson, type);
	}
}
