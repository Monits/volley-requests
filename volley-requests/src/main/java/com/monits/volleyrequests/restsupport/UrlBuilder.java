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

import java.util.List;

/**
 * Defines a builder for url that manages REST concepts.
 */
public interface UrlBuilder {
	/**
	 * A URL builder specific for single elements.
	 */
	interface ElementUrlBuilder extends UrlBuilder {
		/**
		 * Moves up from the single element to the whole collection.
		 *
		 * @return The UrlBuilder, for a fluid programming interface.
		 */
		@NonNull
		CollectionUrlBuilder getList();

		@NonNull
		@Override
		<T> RequestBuilder<T, T> get(@NonNull Class<T> clazz);
	}

	/**
	 * A URL builder specific for collections.
	 */
	interface CollectionUrlBuilder extends UrlBuilder {
		/**
		 * Moves down from the collection to a single element.
		 *
		 * @return The UrlBuilder, for a fluid programming interface.
		 */
		@NonNull
		ElementUrlBuilder get(int id);

		/**
		 * Moves down from the collection to a single element.
		 *
		 * @return The UrlBuilder, for a fluid programming interface.
		 */
		@NonNull
		ElementUrlBuilder get(@NonNull String id);

		@NonNull
		@Override
		<T> RequestBuilder<T, List<T>> get(@NonNull Class<T> clazz);
	}

	/**
	 * Retrieves a builder for a DELETE request on the current url.
	 * @return A RequestBuilder for a DELETE on the current url.
	 */
	@NonNull
	RequestBuilder<Void, Void> delete();

	/**
	 * Retrieves a builder for a HEAD request on the current url.
	 * @return A RequestBuilder for a HEAD on the current url.
	 */
	@NonNull
	RequestBuilder<Void, Void> head();

	/**
	 * Retrieves a builder for a TRACE request on the current url.
	 * @return A RequestBuilder for a TRACE on the current url.
	 */
	@NonNull
	RequestBuilder<Void, Void> trace();

	/**
	 * Retrieves a builder for a OPTIONS request on the current url.
	 * @return A RequestBuilder for a OPTIONS on the current url.
	 */
	@NonNull
	RequestBuilder<Void, Void> options();

	/**
	 * Retrieves a builder for a POST request on the current url.
	 *
	 * @param obj The object to be posted.
	 * @return A RequestBuilder for a POST on the current url.
	 */
	@NonNull
	<T> RequestBuilder<T, T> post(@NonNull T obj);

	/**
	 * Retrieves a builder for a POST request on the current url.
	 *
	 * @param clazz The class of the response object
	 * @return A RequestBuilder for a POST on the current url.
	 */
	@NonNull
	<T> RequestBuilder<T, T> post(@NonNull Class<T> clazz);

	/**
	 * Retrieves a builder for a PUT request on the current url.
	 *
	 * @param obj The object to be put.
	 * @return A RequestBuilder for a PUT on the current url.
	 */
	@NonNull
	<T> RequestBuilder<T, T> put(@NonNull T obj);

	/**
	 * Retrieves a builder for a PUT request on the current url.
	 *
	 * @param clazz The class of the response object
	 * @return A RequestBuilder for a PUT on the current url.
	 */
	@NonNull
	<T> RequestBuilder<T, T> put(@NonNull Class<T> clazz);

	/**
	 * Retrieves a builder for a PATCH request on the current url.
	 *
	 * @param obj The object to be patched.
	 * @return A RequestBuilder for a PATCH on the current url.
	 */
	@NonNull
	<T> RequestBuilder<T, T> patch(@NonNull T obj);

	/**
	 * Retrieves a builder for a PATCH request on the current url.
	 *
	 * @param clazz The class of the response object
	 * @return A RequestBuilder for a PATCH on the current url.
	 */
	@NonNull
	<T> RequestBuilder<T, T> patch(@NonNull Class<T> clazz);

	/**
	 * Retrieves a builder for a GET request on the current url.
	 *
	 * @param clazz The type of objects expected from the server when requesting the current url.
	 * @return A RequestBuilder for a GET on the current url.
	 */
	@NonNull
	<T> RequestBuilder<T, ?> get(@NonNull Class<T> clazz);

	/**
	 * Retrieves a builder for a request using an arbitrary method on the current url.
	 *
	 * @param method The method to be used. {@see com.android.volley.Request.Method}
	 * @param clazz The type of objects expected from the server when requesting the current url.
	 * @return A RequestBuilder for a GET on the current url.
	 */
	@NonNull
	<T> RequestBuilder<T, ?> method(int method, @NonNull Class<T> clazz);

	/**
	 * Moves the pointer on the url to several elements in a child collection from the current point
	 *
	 * @param route The route to the collection in which the desired elements reside.
	 * @param ids The list of ids to be requested.
	 * @return The UrlBuilder, for a fluid programming interface.
	 */
	@NonNull
	CollectionUrlBuilder several(@NonNull String route, @NonNull int... ids);

	/**
	 * Moves the pointer on the url to several elements in a child collection from the current point
	 *
	 * @param route The route to the collection in which the desired elements reside.
	 * @param ids The list of ids to be requested.
	 * @return The UrlBuilder, for a fluid programming interface.
	 */
	@NonNull
	CollectionUrlBuilder several(@NonNull String route, @NonNull String... ids);

	/**
	 * Moves the pointer on the url to all elements in a child collection from the current point.
	 *
	 * @param route The route to the collection to be fetched.
	 * @return The UrlBuilder, for a fluid programming interface.
	 */
	@NonNull
	CollectionUrlBuilder all(@NonNull String route);

	/**
	 * Moves the pointer on the url to one element in a child collection from the current point.
	 *
	 * @param route The route to the collection in which the desired element resides.
	 * @param id The id of the element to be requested.
	 * @return The UrlBuilder, for a fluid programming interface.
	 */
	@NonNull
	ElementUrlBuilder one(@NonNull String route, int id);

	/**
	 * Moves the pointer on the url to a child element from the current point.
	 *
	 * @param route The route to the collection in which the desired element resides.
	 * @return The UrlBuilder, for a fluid programming interface.
	 */
	@NonNull
	ElementUrlBuilder one(@NonNull String route);

	/**
	 * Moves the pointer on the url to one element in a child collection from the current point.
	 *
	 * @param route The route to the collection in which the desired element resides.
	 * @param id The id of the element to be requested.
	 * @return The UrlBuilder, for a fluid programming interface.
	 */
	@NonNull
	ElementUrlBuilder one(@NonNull String route, @NonNull String id);

	/**
	 * Retrieves the url at which the builder is currently pointing.
	 *
	 * @return The url at which the builder is currently pointing.
	 */
	@NonNull
	String getUrl();
}
