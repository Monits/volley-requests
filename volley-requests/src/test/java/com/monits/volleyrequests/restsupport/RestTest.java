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

import com.android.volley.AuthFailureError;
import com.android.volley.JSONArrayRequestDecorator;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.monits.volleyrequests.network.request.GsonRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("checkstyle:multiplestringliterals")
@RunWith(RobolectricTestRunner.class)
public class RestTest {
	private static final String BASE_URL = "http://test.domain.com";

	private static final String QUERY_PARAM_1 = "PARAM1";
	private static final String QUERY_VALUE_1 = "VALUE1";
	private static final String QUERY_PARAM_2 = "PARAM2";
	private static final String QUERY_VALUE_2 = "VALUE2";

	private static final String HEADER_NAME_1 = "HEADER1";
	private static final String HEADER_VALUE_1 = "VALUE1";
	private static final String HEADER_NAME_2 = "HEADER2";
	private static final String HEADER_VALUE_2 = "VALUE2";

	private static final String RESOURCE_ROUTE_USERS = "users";
	private static final int RESOURCE_VALUE_USER_ID = 123456;
	private static final int RESOURCE_VALUE_OTHER_USER_ID = 654321;
	private static final String RESOURCE_ROUTE_LISTS = "lists";
	private static final String RESOURCE_VALUE_USER_NAME = "John";
	private static final String RESOURCE_ROUTE_USER_NAME = "name";
	private static final String RESOURCE_ROUTE_USER_ID = "id";
	private static final int RESOURCE_VALUE_LIST_ID = 1;

	private static final String GET_ALL_URL = BASE_URL + "/" + RESOURCE_ROUTE_USERS;
	private static final String GET_SEVERAL_URL = BASE_URL + "/" + RESOURCE_ROUTE_LISTS
			+ "/" + RESOURCE_VALUE_LIST_ID + "/" + RESOURCE_ROUTE_USERS + "/"
			+ RESOURCE_VALUE_USER_ID + "," + RESOURCE_VALUE_OTHER_USER_ID;

	@Before
	public void setUp() throws MalformedURLException {
		Rest.setBaseUrl(BASE_URL);
		Rest.setGson(new Gson());
		Rest.setInterceptor(null);
	}

	@Test
	public void testGetUrl() {
		assertEquals(GET_ALL_URL, Rest.all(RESOURCE_ROUTE_USERS).getUrl());
	}

	@Test
	public void testErrorListener() {
		final Response.ErrorListener error = mock(Response.ErrorListener.class);

		final Request<Void> request = Rest.all(RESOURCE_ROUTE_USERS).options()
				.onError(error).request();

		assertSame(error, request.getErrorListener());
	}

	@Test
	public void testHeaders() throws AuthFailureError {
		final Map<String, String> headers = new HashMap<>();
		headers.put(HEADER_NAME_2, HEADER_VALUE_2);

		final Request<Void> request = Rest.all(RESOURCE_ROUTE_USERS).head()
				.header(HEADER_NAME_1, HEADER_VALUE_1)	// Key-value method
				.headers(headers)						// Map based metod
				.request();

		final Map<String, String> actualHeaders = request.getHeaders();
		assertThat(actualHeaders, hasEntry(HEADER_NAME_1, HEADER_VALUE_1));
		assertThat(actualHeaders, hasEntry(HEADER_NAME_2, HEADER_VALUE_2));
	}

	@Test
	public void testGetAll() {
		final Request<List<Object>> request = Rest.all(RESOURCE_ROUTE_USERS)
				.get(Object.class).request();

		assertEquals(GET_ALL_URL, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetAllUrl() {
		final Request<List<Object>> request = Rest.allUrl(RESOURCE_ROUTE_USERS, BASE_URL)
				.get(Object.class).request();

		assertEquals(GET_ALL_URL, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetSeveral() {
		final Request<List<Object>> request = Rest.one(RESOURCE_ROUTE_LISTS, RESOURCE_VALUE_LIST_ID)
				.several(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID, RESOURCE_VALUE_OTHER_USER_ID)
				.get(Object.class).request();

		assertEquals(GET_SEVERAL_URL, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testTrace() {
		final Request<Void> request = Rest.all(RESOURCE_ROUTE_USERS)
				.trace().request();

		assertEquals(GET_ALL_URL, request.getUrl());
		assertEquals(Request.Method.TRACE, request.getMethod());
	}

	@Test
	public void testHead() {
		final Request<Void> request = Rest.all(RESOURCE_ROUTE_USERS)
				.head().request();

		assertEquals(GET_ALL_URL, request.getUrl());
		assertEquals(Request.Method.HEAD, request.getMethod());
	}

	@Test
	public void testOptions() {
		final Request<Void> request = Rest.all(RESOURCE_ROUTE_USERS)
				.options().request();

		assertEquals(GET_ALL_URL, request.getUrl());
		assertEquals(Request.Method.OPTIONS, request.getMethod());
	}

	@Test
	public void testGetAllWithQuery() {
		final Map<String, String> queryParams = new TreeMap<>();
		queryParams.put(QUERY_PARAM_2, QUERY_VALUE_2);

		final Request<List<Object>> request = Rest.all(RESOURCE_ROUTE_USERS).get(Object.class)
				.query(QUERY_PARAM_1, QUERY_VALUE_1)	// Use key-value method
				.query(queryParams)						// Use map method
				.request();

		// Params may be in any order, so we check it's any of the plausible alternatives
		final String param1 = QUERY_PARAM_1 + "=" + QUERY_VALUE_1;
		final String param2 = QUERY_PARAM_2 + "=" + QUERY_VALUE_2;
		assertThat(request.getUrl(), anyOf(equalTo(GET_ALL_URL + "?" + param1 + "&" + param2),
				equalTo(GET_ALL_URL + "?" + param2 + "&" + param1)));
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetMultipleParams() {
		final Request<List<Object>> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.all(RESOURCE_ROUTE_LISTS).get(Object.class).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID + "/" + RESOURCE_ROUTE_LISTS,
				request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetSingle() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.get(Object.class).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetSingleTopLevel() {
		// a single element response...
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS).get(Object.class).request();

		// ... with a top level URL
		assertEquals(GET_ALL_URL, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetSingleUrl() {
		final Request<Object> request =
				Rest.oneUrl(Integer.toString(RESOURCE_VALUE_USER_ID), GET_ALL_URL)
					.get(Object.class).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetSingleWithQuery() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.get(Object.class).query(QUERY_PARAM_1, QUERY_VALUE_1).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID + "?" + QUERY_PARAM_1 + "=" + QUERY_VALUE_1,
				request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testDeleteAll() {
		final Request<Void> request = Rest.all(RESOURCE_ROUTE_USERS).delete().request();

		assertEquals(GET_ALL_URL, request.getUrl());
		assertEquals(Request.Method.DELETE, request.getMethod());
	}

	@Test
	public void testDeleteSingle() {
		final Request<Void> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.delete().request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.DELETE, request.getMethod());
	}

	@Test
	public void testPost() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.post(new Object()).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.POST, request.getMethod());
	}

	@Test
	public void testPatch() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.patch(new Object()).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.PATCH, request.getMethod());
	}

	@Test
	public void testPutWithQuery() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.put(new Object()).query(QUERY_PARAM_1, QUERY_VALUE_1).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID + "?" + QUERY_PARAM_1 + "=" + QUERY_VALUE_1,
				request.getUrl());
		assertEquals(Request.Method.PUT, request.getMethod());
	}

	@Test
	public void testInterceptorGetsCalled() {
		final Rest.RequestInterceptor interceptor = mock(Rest.RequestInterceptor.class);
		when(interceptor.intercept(any(GsonRequest.class))).then(new Answer<Object>() {
			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				return invocationOnMock.getArguments()[0];
			}
		});

		Rest.setInterceptor(interceptor);

		// create a request
		Rest.all(RESOURCE_ROUTE_USERS).trace().request();

		// Verify the interceptor is called
		verify(interceptor).intercept(any(GsonRequest.class));
	}

	@Test
	public void testGetAllFromElement() {
		final Request<List<Object>> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.getList()		// Transverse REST tree up to collection level
				.get(Object.class).request();

		assertEquals(GET_ALL_URL, request.getUrl());
	}

	@Test
	public void testGellAllElementsKey() {
		final RequestBuilder<Object, List<Object>> requestBuilder = Rest.all(RESOURCE_ROUTE_USERS)
				.get(Object.class);

		Rest.setElementsKey("elements");
		assertThat(requestBuilder.request(), instanceOf(JSONArrayRequestDecorator.class));

		Rest.setElementsKey(null);
		assertThat(requestBuilder.request(), instanceOf(GsonRequest.class));
	}

	@Test
	public void testPostWithoutObject() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.post(Object.class).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.POST, request.getMethod());
	}

	@Test
	public void testPatchWithoutObject() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.patch(Object.class).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.PATCH, request.getMethod());
	}

	@Test
	public void testPutWithoutObject() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS, RESOURCE_VALUE_USER_ID)
				.put(Object.class).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.PUT, request.getMethod());
	}

	@Test
	public void testGetSingleChainingWithString() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS)
				.one(RESOURCE_ROUTE_USER_NAME, RESOURCE_VALUE_USER_NAME)
				.get(Object.class).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_ROUTE_USER_NAME
			+ "/" + RESOURCE_VALUE_USER_NAME, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testGetSingleChainingWithInt() {
		final Request<Object> request = Rest.one(RESOURCE_ROUTE_USERS)
				.one(RESOURCE_ROUTE_USER_ID, RESOURCE_VALUE_USER_ID)
				.get(Object.class).request();

		assertEquals(GET_ALL_URL + "/" + RESOURCE_ROUTE_USER_ID
				+ "/" + RESOURCE_VALUE_USER_ID, request.getUrl());
		assertEquals(Request.Method.GET, request.getMethod());
	}

	@Test
	public void testToString() {
		final RestElement restElement = (RestElement) Rest.oneUrl(RESOURCE_ROUTE_USERS, BASE_URL)
				.one(RESOURCE_ROUTE_LISTS);

		final String defaultToString = restElement.getClass().getName()
				+ '@' + Integer.toHexString(restElement.hashCode());

		assertThat(restElement.toString(), not(equalTo(defaultToString)));
		assertNotNull(restElement.toString());
	}
}
