/*
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
package com.monits.volleyrequests.restsupport;

import android.support.annotation.NonNull;

import com.android.volley.JSONArrayRequestDecorator;
import com.android.volley.Request;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A representation of a collection of element in a REST API.
 *
 * @param <T> The type of objects both submitted and contained in the collection at the current
 *           endpoint in the API.
 */
@SuppressFBWarnings(value = "CD_CIRCULAR_DEPENDENCY",
		justification = "Tight coupling is intended, we just use different interfaces to direct"
				+ " the method calls flow")
public class RestCollection<T> extends RestBase<T, List<T>>
		implements UrlBuilder.CollectionUrlBuilder {

	/*package*/ RestCollection(final String url, final Gson gson, final Class<T> clazz) {
		super(url, gson, clazz);
	}

	@Override
	protected Type getDecodeType(final Class<T> type) {
		return new TypeToken<List<T>>() { }
				.where(new TypeParameter<T>() {
				}, TypeToken.of(type)).getType();
	}

	@NonNull
	@Override
	protected Request<List<T>> decorateRequest(@NonNull final Request<List<T>> request) {
		final String elementsKey = Rest.getElementsKey();
		if (elementsKey == null) {
			return request;
		}
		return new JSONArrayRequestDecorator<>(request, elementsKey);
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	public <U> RequestBuilder<U, List<U>> get(@NonNull final Class<U> clazz) {
		return (RequestBuilder<U, List<U>>) method(Request.Method.GET, clazz);
	}

	@NonNull
	@Override
	public ElementUrlBuilder get(final int id) {
		return get(Integer.toString(id));
	}

	@NonNull
	@Override
	public ElementUrlBuilder get(@NonNull final String id) {
		return one(id);
	}
}
