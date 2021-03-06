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
package com.monits.volleyrequests.restsupport;

import com.android.volley.JSONArrayRequestDecorator;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.monits.volleyrequests.network.request.GsonRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@SuppressWarnings("checkstyle:multiplestringliterals")
@RunWith(RobolectricTestRunner.class)
public class RestResourceTest {

	private static final String GET_ALL_URL = "http://test.domain.com/users";

	private static final String QUERY_PARAM_1 = "PARAM1";
	private static final String QUERY_VALUE_1 = "VALUE1";
	private static final String QUERY_PARAM_2 = "PARAM2";
	private static final String QUERY_VALUE_2 = "VALUE2";

	private static final String RESOURCE_PARAM_USER_ID = "userId";
	private static final String RESOURCE_VALUE_USER_ID = "123456";
	private static final String RESOURCE_PARAM_FIELD = "field";
	private static final String RESOURCE_VALUE_FIELD = "lists";

	private static final String RESOURCE_URL = GET_ALL_URL + "/:" + RESOURCE_PARAM_USER_ID
			+ "/:" + RESOURCE_PARAM_FIELD;

	private RestResource<Object> restResource;

	@Before
	public void setUp() throws MalformedURLException {
		restResource = new RestResource<>(RESOURCE_URL, Object.class, new Gson());
	}

	@Test
	public void testGetAll() {
		final Request<List<Object>> request
				= restResource.getAll(null, new DummyListener<List<Object>>(), null);

		assertEquals(GET_ALL_URL, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetAllWithQuery() {
		final Map<String, String> queryParams = new TreeMap<>();	// We need a known order
		queryParams.put(QUERY_PARAM_1, QUERY_VALUE_1);
		queryParams.put(QUERY_PARAM_2, QUERY_VALUE_2);

		final Request<List<Object>> request
				= restResource.getAll(null, queryParams, new DummyListener<List<Object>>(), null);

		assertEquals(GET_ALL_URL + "?" + QUERY_PARAM_1 + "=" + QUERY_VALUE_1
						+ "&" + QUERY_PARAM_2 + "=" + QUERY_VALUE_2, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetMultipleParams() {
		final Map<String, String> resourceParams = new HashMap<>();
		resourceParams.put(RESOURCE_PARAM_USER_ID, RESOURCE_VALUE_USER_ID);
		resourceParams.put(RESOURCE_PARAM_FIELD, RESOURCE_VALUE_FIELD);

		final Request<Object> request
				= restResource.getObject(resourceParams, new DummyListener<>(), null);

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID + "/" + RESOURCE_VALUE_FIELD,
				request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetSingle() {
		final Map<String, String> resourceParams = new HashMap<>();
		resourceParams.put(RESOURCE_PARAM_USER_ID, RESOURCE_VALUE_USER_ID);

		final Request<Object> request
				= restResource.getObject(resourceParams, new DummyListener<>(), null);

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetSingleWithQuery() {
		final Map<String, String> resourceParams = new HashMap<>();
		resourceParams.put(RESOURCE_PARAM_USER_ID, RESOURCE_VALUE_USER_ID);

		final Map<String, String> queryParams = new HashMap<>();
		queryParams.put(QUERY_PARAM_1, QUERY_VALUE_1);

		final Request<Object> request = restResource.getObject(resourceParams,
				queryParams, new DummyListener<>(), null);

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID + "?" + QUERY_PARAM_1 + "=" + QUERY_VALUE_1,
				request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testDeleteAll() {
		final Request<Object> request
				= restResource.deleteObject(new DummyListener<>(), null);

		assertEquals(GET_ALL_URL, request.getUrl());
		assertEquals(Request.Method.DELETE, request.getMethod());
	}

	@Test
	public void testDeleteSingle() {
		final Map<String, String> resourceParams = new HashMap<>();
		resourceParams.put(RESOURCE_PARAM_USER_ID, RESOURCE_VALUE_USER_ID);

		final Request<Object> request = restResource.deleteObject(resourceParams,
				new DummyListener<>(), null);

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.DELETE, request.getMethod());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSaveIllegalMethod() {
		final Map<String, String> resourceParams = new HashMap<>();
		resourceParams.put(RESOURCE_PARAM_USER_ID, RESOURCE_VALUE_USER_ID);

		// Suppress lint warnings for the issue tested
		//noinspection ResourceType
		restResource.saveObject(Request.Method.DELETE, resourceParams,
				new DummyListener<>(), null, new Object());
	}

	@Test
	public void testSave() {
		final Map<String, String> resourceParams = new HashMap<>();
		resourceParams.put(RESOURCE_PARAM_USER_ID, RESOURCE_VALUE_USER_ID);

		final Request<Object> request
				= restResource.saveObject(Request.Method.POST, resourceParams,
				new DummyListener<>(), null, new Object());

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.POST, request.getMethod());
	}

	@Test
	public void testSaveWithQuery() {
		final Map<String, String> resourceParams = new HashMap<>();
		resourceParams.put(RESOURCE_PARAM_USER_ID, RESOURCE_VALUE_USER_ID);

		final Map<String, String> queryParams = new HashMap<>();
		queryParams.put(QUERY_PARAM_1, QUERY_VALUE_1);

		final Request<Object> request
				= restResource.saveObject(Request.Method.PUT, resourceParams, queryParams,
				new DummyListener<>(), null, new Object());

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID + "?" + QUERY_PARAM_1 + "=" + QUERY_VALUE_1,
				request.getUrl());
		assertEquals(Request.Method.PUT, request.getMethod());
	}

	// Dummy empty implementation of listener class.
	private static class DummyListener<T> implements Response.Listener<T> {
		@Override
		public void onResponse(final T objects) {
			// Dummy method does nothing
		}
	}

	@Test
	public void testGellAllElementsKey() {
		final Request<List<Object>> jsonArrayRequest
				= restResource.getAll(null, new DummyListener<List<Object>>(), null, null);
		assertThat(jsonArrayRequest, instanceOf(JSONArrayRequestDecorator.class));
		restResource.setElementsKey(null);
		final Request<List<Object>> request
				= restResource.getAll(null, new DummyListener<List<Object>>(), null, null);
		assertThat(request, instanceOf(GsonRequest.class));
	}

	@Test
	public void testToString() {

		final String defaultToString = restResource.getClass().getName()
				+ '@' + Integer.toHexString(restResource.hashCode());

		assertThat(restResource.toString(), not(equalTo(defaultToString)));
		assertNotNull(restResource.toString());
	}
}
