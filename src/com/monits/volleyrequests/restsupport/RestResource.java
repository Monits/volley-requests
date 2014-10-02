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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
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
	private static final String PARAMETERS_REGEX = "/(:([^/]+))";
	private static final String REMOVE_MULTIPLE_SLASH = "\\/{2,}";
	private static final String REMOVE_FINAL_SLASH = "\\/$";
	private static final Pattern PATTERN = Pattern.compile(PARAMETERS_REGEX);
	private final String resource;
	private final Class<T> clazz;
	private final String hostAndPort;
	private final Gson gson;
	private String elementsKey = "elements";

	/**
	 * Create a new instance of RestResource
	 *
	 * @param resource
	 *            The resourcer that you want to access
	 * @param clazz
	 *            The type of the object that the json represents
	 * @throws MalformedURLException
	 */
	public RestResource(final String resource, final Class<T> clazz,
			final Gson gson) throws MalformedURLException {
		final URL url = new URL(resource);
		this.resource = url.getFile();
		this.clazz = clazz;
		this.hostAndPort = prepareHostURL(url);
		this.gson = gson;
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
	 * 			  A Map with the parameters that must be in the query string
	 * @param listener
	 *            The listener for success.
	 * @param errListener
	 *            The listener for errors.
	 * @return The GsonRequest with the created request
	 */
	public GsonRequest<T> getObject(final Map<String, String> resourceParams,
			final Map<String, String> queryParams, final Listener<T> listener, 
			final ErrorListener errListener) {
		final String url = generateFullUrl(resourceParams, queryParams);
		final GsonRequest<T> gsonRequest = new GsonRequest<T>(Method.GET,
				this.hostAndPort + url, this.gson, this.clazz, listener, errListener,
				null);

		return gsonRequest;
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
	public GsonRequest<T> getObject(final Map<String, String> resourceParams,
			final Listener<T> listener, final ErrorListener errListener) {
		return getObject(resourceParams, null, listener, errListener);
	}
	

	/**
	 * Create the GsonRequest for a GET request and give a request for a
	 * collection. Example: If your resource is /user/:userId this method create
	 * the url /user or if your resource is /user/:userId/card/:cardId, the new
	 * url is /user/123/card. We need to use guava TypeToken because
	 * Gson.TypeToken cannot accept TypeToken<List<T>>. Until Gson update this
	 * we will use guava
	 *
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @param queryParams
	 * 			  A Map with the parameters that must be in the query string
	 * @param listener
	 *            The listener for success.getAllResource
	 * @param errListener
	 *            The listener for errors.
	 * @return The JSONArrayGsonRequest with the created request
	 */
	public GsonRequest<List<T>> getAll(final Map<String, String> resourceParams,
			final Map<String, String> queryParams, final Listener<List<T>> listener, 
			final ErrorListener errListener) {

		final String url = generateFullUrl(resourceParams, queryParams);

		if (elementsKey != null) {
			return new JSONArrayGsonRequest<List<T>>(
					Method.GET, this.hostAndPort + url, this.gson, createListTypeToken().getType(),
					listener, errListener, null, elementsKey);
		}

		return new GsonRequest<List<T>>(
				Method.GET, this.hostAndPort + url, this.gson, createListTypeToken().getType(),
				listener, errListener, null);
	}
	
	/**
	 * Create the GsonRequest for a GET request and give a request for a
	 * collection. Example: If your resource is /user/:userId this method create
	 * the url /user or if your resource is /user/:userId/card/:cardId, the new
	 * url is /user/123/card. We need to use guava TypeToken because
	 * Gson.TypeToken cannot accept TypeToken<List<T>>. Until Gson update this
	 * we will use guava
	 *
	 * @param resourceParams
	 *            A Map with the value of the parameters that must be replaced
	 *            in the url resource. The key of the map must be the name of
	 *            the parameter in the url. Example, url = "/user/:userId" and
	 *            the map must contains {"userId", "123}
	 * @param listener
	 *            The listener for success.getAllResource
	 * @param errListener
	 *            The listener for errors.
	 * @return The JSONArrayGsonRequest with the created request
	 */
	public GsonRequest<List<T>> getAll(final Map<String, String> resourceParams,
			final Listener<List<T>> listener, final ErrorListener errListener) {
		return getAll(resourceParams, null, listener, errListener);
	}

	/**
	 * Create the GsonRequest for a POST or PUT request from the resource.
	 *
	 * @param method
	 *            Supported method Method.PUT or Method.POST
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
	public GsonRequest<T> saveObject(final int method,
			final Map<String, String> resourceParams,
			final Listener<T> listener, final ErrorListener errListener,
			final T object) throws IllegalArgumentException {
		return saveObject(method, resourceParams, null, listener, errListener, object);
	}

	/**
	 *
	 * @param method
	 *            Supported method Method.PUT or Method.POST
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
	public GsonRequest<T> saveObject(final int method,
			final Map<String, String> resourceParams,
			final Map<String, String> queryParams, final Listener<T> listener,
			final ErrorListener errListener, final T object) throws IllegalArgumentException {
		if (method != Method.POST && method != Method.PUT) {
			throw new IllegalArgumentException(
					"Save object can only be used with POST or PUT method");
		}
		final String url = generateFullUrl(resourceParams, queryParams);
		final MaybeGsonRequest<T> gsonRequest = new MaybeGsonRequest<T>(method,
				this.hostAndPort + url, this.gson, this.clazz, listener,
				errListener, object);

		return gsonRequest;
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
	public GsonRequest<T> deleteObject(final Listener<T> listener,
			final ErrorListener errListener) {
		return new GsonRequest<T>(Method.DELETE, this.hostAndPort + resource,
				this.gson, clazz, listener, errListener, null);
	}
	
	/**
	 * Replace the resource parameters in the url and if the queryParams is
	 * not null and not empty, add the query string to the URL
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
	private String generateFullUrl(
			final Map<String, String> resourceParams,
			final Map<String, String> queryParams) {
		final Map<String, String> map = resourceParams == null 
				? Collections.<String, String>emptyMap() : resourceParams;
		String url = replaceValuesInResource(map);
		if (queryParams != null && !queryParams.isEmpty()) {
			final StringBuilder builder = new StringBuilder();
			builder.append(url).append("?");
			int elementsLeft = queryParams.size();
			for (final Entry<String, String> entry : queryParams.entrySet()) {
				builder.append(entry.getKey()).append("=")
						.append(entry.getValue());
				if (elementsLeft-- > 0) {
					builder.append("&");
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
	private String replaceValuesInResource(final Map<String, String> resourceParams) {
		String url = resource;
		final Matcher matcher = PATTERN.matcher(url);
		while (matcher.find()) {
			final String mapValue = resourceParams.get(matcher.group(2));
			final String value = mapValue == null ? "" : mapValue;
			url = url.replace(matcher.group(1), value);
		}
		url = url.replaceAll(REMOVE_MULTIPLE_SLASH, "/");
		url = url.replaceAll(REMOVE_FINAL_SLASH, "");
		return url;
	}

	/**
	 * Prepare the authority with the corresponding port of the url.
	 *
	 * @param url The url for the request
	 * @return {protocol}://{authority}
	 */
	private String prepareHostURL(final URL url) {
		final StringBuilder builder = new StringBuilder();
		builder.append(url.getProtocol()).append("://")
				.append(url.getAuthority());
		return builder.toString();
	}

	/**
	 * Create the TypeToken for a list of generic, using guava TypeToken
	 * @return The TypeToken using where method
	 */
	private TypeToken<List<T>> createListTypeToken() {
		return new TypeToken<List<T>>() {}.where(new TypeParameter<T>() {
		}, TypeToken.of(clazz));
	}

	/**
	 * Sets the key for the JSON that retrieves all items of a collection.
	 * @param elementsKey the key used in the JSON response. If null, it is assumed that the JSON array comes alone
	 * without being wrapped by an object.
	 */
	public void setElementsKey(final String elementsKey) {
		this.elementsKey = elementsKey;
	}


	/**
	 * Class to parse a list of all items in a collection from a NetworkResponse depending on a key.
	 * @author fpredassi
	 *
	 * @param <T>
	 */
	private static class JSONArrayGsonRequest<T> extends GsonRequest<T> {
		private final String elementsKey;

		public JSONArrayGsonRequest(final int method, final String url, final Gson gson,
				final Type clazz, final Listener<T> listener, final ErrorListener errListener,
				final String jsonBody, final String elementsKey) {
			super(method, url, gson, clazz, listener, errListener, jsonBody);
			this.elementsKey = elementsKey;
		}

		@Override
		protected Response<T> parseNetworkResponse(final NetworkResponse response) {
			try {
				final String headersCharset = HttpHeaderParser.parseCharset(response.headers);
				final String json = new String(response.data, headersCharset);
				final JSONObject object = new JSONObject(json);
				final JSONArray array = object.getJSONArray(elementsKey);
				final byte[] data = array.toString().getBytes(headersCharset);
				final NetworkResponse responseJsonArray = new NetworkResponse(data, response.headers);
				return super.parseNetworkResponse(responseJsonArray);
			} catch (final UnsupportedEncodingException e) {
				return Response.error(new ParseError(e));
			} catch (final JSONException e) {
				return Response.error(new ParseError(e));
			}
		}
	}


	/**
	 * Class to manage status code 201 when saving an object.
	 * @author fpredassi
	 *
	 * @param <T>
	 */
	private static class MaybeGsonRequest<T> extends GsonRequest<T> {
		private final T object;

		public MaybeGsonRequest(final int method, final String url, final Gson gson,
				final Type clazz, final Listener<T> listener, final ErrorListener errListener,
				final T object) {
			super(method, url, gson, clazz, listener, errListener, gson.toJson(object, clazz));
			this.object = object;
		}

		@Override
		protected Response<T> parseNetworkResponse(final NetworkResponse response) {
			if (response.statusCode == HttpStatus.SC_CREATED) {
				return Response.success(object, HttpHeaderParser.parseCacheHeaders(response));
			}
			return super.parseNetworkResponse(response);
		}
	}

}
