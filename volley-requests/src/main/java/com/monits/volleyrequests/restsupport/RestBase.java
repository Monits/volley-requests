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
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.monits.volleyrequests.network.request.GsonRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class RestBase<T, S> implements UrlBuilder, RequestBuilder<T, S> {
	protected final String url;
	protected final Gson gson;
	protected final Class<T> type;

	private final Map<String, String> queryParams = new HashMap<>();
	private final Map<String, String> headers = new HashMap<>();

	protected int method;
	protected T obj;
	private Response.Listener<S> listener;
	private Response.ErrorListener errorListener;

	/*package*/ RestBase(final String url, final Gson gson, final Class<T> type) {
		this.url = url;
		this.gson = gson;
		this.type = type;
	}

	protected abstract Type getDecodeType(Class<T> type);

	@NonNull
	@Override
	public RequestBuilder<T, S> header(@NonNull final String header, @NonNull final String value) {
		headers.put(header, value);
		return this;
	}

	@NonNull
	@Override
	public RequestBuilder<T, S> headers(@NonNull final Map<String, String> headers) {
		this.headers.putAll(headers);
		return this;
	}

	@NonNull
	@Override
	public RequestBuilder<T, S> query(@NonNull final String param, @NonNull final String value) {
		queryParams.put(param, value);
		return this;
	}

	@NonNull
	@Override
	public RequestBuilder<T, S> query(@NonNull final Map<String, String> params) {
		queryParams.putAll(params);
		return this;
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	public RequestBuilder<Void, Void> delete() {
		return (RequestBuilder<Void, Void>) method(Request.Method.DELETE, Void.class);
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	public RequestBuilder<Void, Void> head() {
		return (RequestBuilder<Void, Void>) method(Request.Method.HEAD, Void.class);
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	public RequestBuilder<Void, Void> trace() {
		return (RequestBuilder<Void, Void>) method(Request.Method.TRACE, Void.class);
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	public RequestBuilder<Void, Void> options() {
		return (RequestBuilder<Void, Void>) method(Request.Method.OPTIONS, Void.class);
	}

	@NonNull
	@Override
	public <U> RequestBuilder<U, ?> get(@NonNull final Class<U> clazz) {
		return method(Request.Method.GET, clazz);
	}

	@NonNull
	private <U> RestBase<U, U> createRestElementForUploadMethods(@NonNull final Class<U> clazz,
		@NonNull final int method) {
		@SuppressWarnings("unchecked")
		final RestBase<U, U> rb = new RestElement<>(url, gson, clazz);
		rb.method = method;

		return rb;
	}

	@NonNull
	@Override
	public <U> RequestBuilder<U, U> post(@NonNull final U obj) {
		// Post creates / updates elements, always, so it always retrieves a single element
		final RestBase<U, U> rb = createRestElementForUploadMethods((Class<U>) obj.getClass(),
				Request.Method.POST);
		rb.obj = obj;

		return rb;
	}

	@NonNull
	@Override
	public <U> RequestBuilder<U, U> post(@NonNull final Class<U> clazz) {
		// Post creates / updates elements, always, so it always retrieves a single element
		return createRestElementForUploadMethods(clazz,
				Request.Method.POST);
	}

	@NonNull
	@Override
	public <U> RequestBuilder<U, U> put(@NonNull final U obj) {
		// Even for collections, a put retrieves the same that is being put,
		// so we treat is as an element of the parent object
		@SuppressWarnings("unchecked")
		final RestBase<U, U> rb = createRestElementForUploadMethods((Class<U>) obj.getClass(),
				Request.Method.PUT);
		rb.obj = obj;

		return rb;
	}

	@NonNull
	@Override
	public <U> RequestBuilder<U, U> put(@NonNull final Class<U> clazz) {
		// Even for collections, a put retrieves the same that is being put,
		// so we treat is as an element of the parent object
		return createRestElementForUploadMethods(clazz,
				Request.Method.PUT);
	}

	@NonNull
	@Override
	public <U> RequestBuilder<U, U> patch(@NonNull final U obj) {
		// Even for collections, a patch retrieves the same that is being sent,
		// so we treat is as an element of the parent object
		final RestBase<U, U> rb = createRestElementForUploadMethods((Class<U>) obj.getClass(),
				Request.Method.PATCH);
		rb.obj = obj;

		return rb;
	}

	@NonNull
	@Override
	public <U> RequestBuilder<U, U> patch(@NonNull final Class<U> clazz) {
		// Even for collections, a put retrieves the same that is being put,
		// so we treat is as an element of the parent object
		return createRestElementForUploadMethods(clazz,
				Request.Method.PATCH);
	}

	@SuppressWarnings("PMD.PreserveStackTrace")	// Until API level 21, it can't be done
	@NonNull
	@Override
	public <U> RequestBuilder<U, ?> method(final int method, @NonNull final Class<U> clazz) {
		final RestBase<U, ?> restBase;

		// We want a new instance so we are sure we are not copying any request fields
		try {
			// All subclasses MUST have a constructor with the same fields as ourselves
			//noinspection unchecked
			restBase = getClass().getDeclaredConstructor(String.class, Gson.class, Class.class)
					.newInstance(url, gson, clazz);
		} catch (final InstantiationException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException e) {
			throw new AssertionError("Could not initialize new RestBase element for " + this);
		}
		restBase.method = method;
		return restBase;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{"
				+ "url='" + url + '\''
				+ ", queryParams=" + queryParams
				+ ", headers=" + headers
				+ ", method=" + method
				+ '}';
	}

	@NonNull
	@Override
	public RequestBuilder<T, S> onSuccess(@Nullable final Response.Listener<S> listener) {
		this.listener = listener;
		return this;
	}

	@NonNull
	@Override
	public RequestBuilder<T, S> onError(@Nullable final Response.ErrorListener listener) {
		this.errorListener = listener;
		return this;
	}

	@NonNull
	@Override
	public Request<S> request() {
		final String jsonBody = obj == null ? null : gson.toJson(obj);
		final GsonRequest<S> request = new GsonRequest<>(method, getRequestUrl(), this.gson,
				getDecodeType(type), listener, errorListener, jsonBody);

		// Add headers
		for (final Map.Entry<String, String> entry : headers.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue());
		}
		
		final Request<S> r;

		// Allow client to intercept the request
		if (Rest.interceptor == null) {
			r = request;
		} else {
			r = Rest.interceptor.intercept(request);
		}

		// Add default decorations
		return decorateRequest(r);
	}

	@NonNull
	protected abstract Request<S> decorateRequest(@NonNull Request<S> request);

	// TODO : ¿save?

	@NonNull
	@Override
	public CollectionUrlBuilder several(@NonNull final String route, @NonNull final int... ids) {
		final String[] idsAsString = new String[ids.length];
		for (int i = 0; i < ids.length; i++) {
			idsAsString[i] = Integer.toString(ids[i]);
		}
		return several(route, idsAsString);
	}

	@NonNull
	@Override
	public CollectionUrlBuilder several(@NonNull final String route, @NonNull final String... ids) {
		final StringBuilder sb = new StringBuilder(route).append('/');
		final int lastIndex = ids.length - 1;
		for (int i = 0; i < lastIndex; i++) {
			sb.append(ids[i]).append(',');
		}
		sb.append(ids[lastIndex]);
		return all(sb.toString());
	}

	@NonNull
	@Override
	public CollectionUrlBuilder all(@NonNull final String route) {
		return new RestCollection<>(Rest.formatUrl(url, route), gson, type);
	}

	@NonNull
	@Override
	public ElementUrlBuilder one(@NonNull final String route, final int id) {
		return one(route, Integer.toString(id));
	}

	@NonNull
	@Override
	public ElementUrlBuilder one(@NonNull final String route) {
		return new RestElement<>(Rest.formatUrl(url, route), gson, type);
	}

	@NonNull
	@Override
	public ElementUrlBuilder one(@NonNull final String route, @NonNull final String id) {
		return new RestElement<>(Rest.formatUrl(url, route, id), gson, type);
	}

	@NonNull
	@Override
	public String getUrl() {
		return url;
	}

	@NonNull
	@Override
	public String getRequestUrl() {
		if (queryParams.isEmpty()) {
			return url;
		}

		// Initialize a string builder with a guesstimate of the expected length
		@SuppressWarnings("checkstyle:magicnumber")
		final StringBuilder sb = new StringBuilder(url.length() + 10 * queryParams.size());
		sb.append(url).append('?');
		int elementsLeft = queryParams.size();
		for (final Map.Entry<String, String> entry : queryParams.entrySet()) {
			sb.append(entry.getKey()).append('=')
					.append(entry.getValue());
			if (--elementsLeft > 0) {
				sb.append('&');
			}
		}

		return sb.toString();
	}
}
