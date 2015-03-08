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

package com.monits.volleyrequests.restsupport;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.JSONArrayRequestDecorator;
import com.android.volley.MaybeRequestDecorator;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.monits.volleyrequests.network.request.GsonRequest;

/**
 *
 * Create the corresponding GsonRequest for a particular resource. For any
 * resource you can get the collection resource executing getAll(...) method and
 * this will create the resource for you.
 *
 * Example of a resource with parameters: /user/:userId/card/:cardId. Example of
 * a resource without parameters: /user/123/card/4 or /user
 *
 *
 */
public class RestResource<T> {
	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ Method.POST, Method.PUT })
	public @interface SaveMethod { }

	private static final String PARAMETERS_REGEX = "/(:([^/]+))";
	private static final String REMOVE_MULTIPLE_SLASH = "/{2,}";
	private static final String REMOVE_FINAL_SLASH = "/$";
	private static final Pattern PATTERN = Pattern.compile(PARAMETERS_REGEX);
	protected final Gson gson;
	private final String resource;
	private final Class<T> clazz;
	private final String hostAndPort;
	private String elementsKey = "elements";
	private final Type listTypeToken;

	/**
	 * Create a new instance of RestResource
	 *
	 * @param resource
	 *            The resourcer that you want to access
	 * @param clazz
	 *            The type of the object that the json represents
	 * @throws MalformedURLException If the given URL is not properly formed.
	 */
	public RestResource(@NonNull final String resource, @NonNull final Class<T> clazz,
					@NonNull final Gson gson) throws MalformedURLException {
		final URL url = new URL(resource);
		this.resource = url.getFile();
		this.clazz = clazz;
		this.hostAndPort = prepareHostURL(url);
		this.gson = gson;
		this.listTypeToken = new TypeToken<List<T>>() {
		} .where(new TypeParameter<T>() {
		}, TypeToken.of(clazz)).getType();
	}

	/**
	 * Create request to use all methods. The default implementation return a
	 * the corresponding GsonRequest
	 *
	 * @param method
	 *            The method for the request
	 * @param url
	 *            The url of the request
	 * @param listener
	 *            The success listener
	 * @param errListener
	 *            The error listener
	 * @param object
	 *            The object you want to save in a POST or PUT method
	 *
	 * @return The request object for the given resource
	 */
	protected <K> Request<K> createRequest(final int method, @NonNull final String url,
					@NonNull final Type type, @Nullable final Listener<K> listener,
					@Nullable final ErrorListener errListener, @Nullable final T object) {
		final String jsonBody  = object == null ? null : gson.toJson(object);
		return new GsonRequest<>(method, url, this.gson,
				type, listener, errListener, jsonBody);
	}

	/**
	 * Create the GsonRequest for a GET request from the resource.
	 *
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @param queryParams
	 *            A Map with the parameters that must be in the query string
	 * @param listener
	 *            The listener for success.
	 * @param errListener
	 *            The listener for errors.
	 * @return The GsonRequest with the created request
	 */
	public Request<T> getObject(@Nullable final Map<String, String> resourceParams,
					@Nullable final Map<String, String> queryParams,
					@Nullable final Listener<T> listener,
					@Nullable final ErrorListener errListener) {
		final String url = generateFullUrl(resourceParams, queryParams);
		final Request<T> request = createRequest(Method.GET, this.hostAndPort + url, this.clazz,
						listener, errListener, null);
		configureRequest(request);
		return request;
	}

	/**
	 * Create the GsonRequest for a GET request from the resource.
	 *
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @param listener
	 *            The listener for success.
	 * @param errListener
	 *            The listener for errors.
	 * @return The GsonRequest with the created request
	 */
	public Request<T> getObject(@NonNull final Map<String, String> resourceParams,
					@Nullable final Listener<T> listener, @Nullable final ErrorListener errListener) {
		return getObject(resourceParams, null, listener, errListener);
	}

	/**
	 * Create the GsonRequest for a GET request and give a request for a collection.
	 *
	 * Example: If your resource is /user/:userId this method create
	 * the url /user or if your resource is /user/:userId/card/:cardId, the new
	 * url is /user/123/card. We need to use guava {@code TypeToken} because
	 * {@code Gson.TypeToken} cannot accept {@code TypeToken<List<T>>}. Until Gson update this
	 * we will use guava.
	 *
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @param queryParams
	 *            A Map with the parameters that must be in the query string
	 * @param listener
	 *            The listener for success.getAllResource
	 * @param errListener
	 *            The listener for errors.
	 * @return The JSONArrayGsonRequest with the created request
	 */
	public Request<List<T>> getAll(@Nullable final Map<String, String> resourceParams,
					@Nullable final Map<String, String> queryParams,
					@Nullable final Listener<List<T>> listener, @Nullable final ErrorListener errListener) {
		final String url = generateFullUrl(resourceParams, queryParams);

		final Request<List<T>> request = createRequest(Method.GET, this.hostAndPort + url,
						listTypeToken, listener, errListener, null);
		final JSONArrayRequestDecorator<List<T>> jsonRequest = new JSONArrayRequestDecorator<>(
				request, elementsKey);
		configureRequest(jsonRequest);
		return jsonRequest;
	}

	/**
	 * Create the GsonRequest for a GET request and give a request for a collection.
	 *
	 * Example: If your resource is /user/:userId this method create
	 * the url {@code /user} or if your resource is {@code /user/:userId/card/:cardId}, the new
	 * url is {@code /user/123/card}. We need to use guava {@code TypeToken} because
	 * {@code Gson.TypeToken} cannot accept {@code TypeToken<List<T>>}. Until Gson update this
	 * we will use guava
	 *
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, {@code url = "/user/:userId"} and
	 *            the map must contains {@code \{"userId", "123"\}}
	 * @param listener
	 *            The listener for success.getAllResource
	 * @param errListener
	 *            The listener for errors.
	 * @return The JSONArrayGsonRequest with the created request
	 */
	public Request<List<T>> getAll(@Nullable final Map<String, String> resourceParams,
					@Nullable final Listener<List<T>> listener, @Nullable final ErrorListener errListener) {
		return getAll(resourceParams, null, listener, errListener);
	}

	/**
	 * Create the GsonRequest for a POST or PUT request from the resource.
	 *
	 * @param method
	 *            Supported method {@link Method#PUT} or {@link Method#POST}
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @param listener
	 *            The listener for success.
	 * @param errListener
	 *            The listener for errors.
	 * @param object
	 *            The object that you want to send
	 * @return The GsonRequest with the created request
	 * @throws IllegalArgumentException
	 *             In case that the method receive is different from Method.POST
	 *             or Method.PUT
	 */
	public Request<T> saveObject(@SaveMethod final int method,
					@Nullable final Map<String, String> resourceParams,
					@Nullable final Listener<T> listener, @Nullable final ErrorListener errListener,
					@NonNull final T object) {
		return saveObject(method, resourceParams, null, listener, errListener,
			object);
	}

	/**
	 *
	 * @param method
	 *            Supported method {@link Method#PUT} or {@link Method#POST}
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @param queryParams
	 *            A Map with the parameters that must be in the query string.
	 * @param listener
	 *            The listener for success.
	 * @param errListener
	 *            The listener for errors.
	 * @param object
	 *            The object that you want to send
	 * @return The GsonRequest with the created request
	 * @throws IllegalArgumentException
	 *             In case that the method receive is different from Method.POST
	 *             or Method.PUT
	 */
	public Request<T> saveObject(@SaveMethod final int method,
					@Nullable final Map<String, String> resourceParams,
					@Nullable final Map<String, String> queryParams,
					@Nullable final Listener<T> listener,
					@Nullable final ErrorListener errListener, @NonNull final T object) {
		if (method != Method.POST && method != Method.PUT) {
			throw new IllegalArgumentException(
					"Save object can only be used with POST or PUT method");
		}
		final String url = generateFullUrl(resourceParams, queryParams);
		final Request<T> request = createRequest(method, this.hostAndPort + url, this.clazz,
						listener, errListener, object);
		final MaybeRequestDecorator<T> maybeRequestDecorator = new MaybeRequestDecorator<>(
				request, object);
		configureRequest(maybeRequestDecorator);
		return maybeRequestDecorator;
	}

	/**
	 * Create the GsonRequest for a DELETE request from the resource.
	 *
	 * @param listener
	 *            The listener for success.
	 * @param errListener
	 *            The listener for errors.
	 * @return The GsonRequest with the created request
	 */
	public Request<T> deleteObject(@Nullable final Listener<T> listener,
					@Nullable final ErrorListener errListener) {
		return deleteObject(null, listener, errListener);
	}

	/**
	 * Create the GsonRequest for a DELETE request from the resource.
	 *
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @param listener
	 *            The listener for success.
	 * @param errListener
	 *            The listener for errors.
	 * @return The GsonRequest with the created request
	 */
	public Request<T> deleteObject(@Nullable final Map<String, String> resourceParams,
					@Nullable final Listener<T> listener, @Nullable final ErrorListener errListener) {
		final String url = generateFullUrl(resourceParams, null);
		final Request<T> request = createRequest(Method.DELETE, this.hostAndPort + url, this.clazz,
						listener, errListener, null);
		configureRequest(request);
		return request;
	}

	/**
	 * Replace the resource parameters in the url and if the queryParams is not
	 * null and not empty, add the query string to the URL
	 *
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @param queryParams
	 *            A Map with the parameters that must be in the query string.
	 * @return The url
	 */
	private String generateFullUrl(@Nullable final Map<String, String> resourceParams,
					@Nullable final Map<String, String> queryParams) {
		final Map<String, String> map = resourceParams == null
						? Collections.<String, String>emptyMap() : resourceParams;
		String url = replaceValuesInResource(map);
		if (queryParams != null && !queryParams.isEmpty()) {
			final StringBuilder builder = new StringBuilder();
			builder.append(url).append('?');
			int elementsLeft = queryParams.size();
			for (final Entry<String, String> entry : queryParams.entrySet()) {
				builder.append(entry.getKey()).append('=')
						.append(entry.getValue());
				if (--elementsLeft > 0) {
					builder.append('&');
				}
			}
			url = builder.toString();
		}
		return url;
	}

	/**
	 *
	 * Replace the params value into the resource
	 *
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @return The url
	 */
	private String replaceValuesInResource(@NonNull final Map<String, String> resourceParams) {
		String url = resource;
		final Matcher matcher = PATTERN.matcher(url);
		while (matcher.find()) {
			final String mapValue = resourceParams.get(matcher.group(2));
			final String value = mapValue == null ? "" : mapValue;
			url = url.replace(matcher.group(1), value);
		}
		url = url.replaceAll(REMOVE_MULTIPLE_SLASH, "/");
		return url.replaceAll(REMOVE_FINAL_SLASH, "");
	}

	/**
	 * Prepare the authority with the corresponding port of the url.
	 *
	 * @param url
	 *            The url for the request
	 * @return {protocol}://{authority}
	 */
	private String prepareHostURL(@NonNull final URL url) {
		return url.getProtocol() + "://" + url.getAuthority();
	}

	/**
	 * Sets the key for the JSON that retrieves all items of a collection.
	 *
	 * @param elementsKey
	 *            the key used in the JSON response. If null, it is assumed that
	 *            the JSON array comes alone without being wrapped by an object.
	 */
	public void setElementsKey(@NonNull final String elementsKey) {
		this.elementsKey = elementsKey;
	}

	/**
	 * The subclasses must override if they want to change attributes
	 * of the request, like retryPolicy
	 *
	 * @param request The request that is going to be modify
	 */
	protected void configureRequest(@NonNull final Request<?> request) {
        // Entry point for extending classes
	}

}
