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
import com.monits.volleyrequests.network.request.ListenableRequest.CancelListener;

import java.util.Map;

/**
 * Defines a builder for requests,that can be used to configure several parameters.
 *
 * @param <T> The type of the object sent in the request's body.
 * @param <S> The type of the objects returned by the API.
 */
public interface RequestBuilder<T, S> {
	/**
	 * Adds / overwrites the given header.
	 *
	 * @param header The name of the header.
	 * @param value The value to be associated with the header.
	 * @return The RequestBuilder, for a fluid programming interface.
	 */
	@NonNull
	RequestBuilder<T, S> header(@NonNull String header, @NonNull String value);

	/**
	 * Adds / overwrites headers.
	 *
	 * @param headers The collection of headers to be added. Keys are header names, values their
	 *                corresponding values.
	 * @return The RequestBuilder, for a fluid programming interface.
	 */
	@NonNull
	RequestBuilder<T, S> headers(@NonNull Map<String, String> headers);

	/**
	 * Adds / overwrites the given query parameter.
	 *
	 * @param param The name of the query parameter.
	 * @param value The value to be associated with the parameter.
	 * @return The RequestBuilder, for a fluid programming interface.
	 */
	@NonNull
	RequestBuilder<T, S> query(@NonNull String param, @NonNull String value);

	/**
	 * Adds / overwrites query parameters.
	 *
	 * @param queryParams The collection of query params to be added. Keys are param names,
	 *                    and values their corresponding values.
	 * @return The RequestBuilder, for a fluid programming interface.
	 */
	@NonNull
	RequestBuilder<T, S> query(@NonNull Map<String, String> queryParams);

	/**
	 * Sets a success listener for the request.
	 *
	 * There can be only one. Calling this method more than once overwrites the value.
	 * Passing null simply deletes any already set listener.
	 *
	 * @param listener The listener to be used.
	 * @return The RequestBuilder, for a fluid programming interface.
	 */
	@NonNull
	RequestBuilder<T, S> onSuccess(@Nullable Response.Listener<S> listener);

	/**
	 * Sets an error listener for the request.
	 *
	 * There can be only one. Calling this method more than once overwrites the value.
	 * Passing null simply deletes any already set listener.
	 *
	 * @param listener The listener to be used.
	 * @return The RequestBuilder, for a fluid programming interface.
	 */
	@NonNull
	RequestBuilder<T, S> onError(@Nullable Response.ErrorListener listener);

	/**
	 * Sets a cancel listener for the request.
	 *
	 * There can be only one. Calling this method more than once overwrites the value.
	 * Passing null simply deletes any already set listener.
	 *
	 * @param listener The listener to be used.
	 * @return The RequestBuilder, for a fluid programming interface.
	 */
	@NonNull
	RequestBuilder<T, S> onCancel(@Nullable CancelListener listener);

	/**
	 * Retrieves the url to be requested. This includes not only the path, but also query parameters
	 *
	 * @return The url to be requested.
	 */
	@NonNull
	String getRequestUrl();

	/**
	 * Create the configured request.
	 *
	 * @return The generated request.
	 */
	@NonNull
	Request<S> request();
}
