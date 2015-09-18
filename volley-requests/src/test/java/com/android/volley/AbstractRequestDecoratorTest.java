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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Ignore("This is an abstract class to provide base tests, don't run it on it's own!")
@SuppressWarnings("PMD.TooManyStaticImports")
@SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT",
	justification = "We want to make sure calls are delegated, not their effect")
public abstract class AbstractRequestDecoratorTest<T extends RequestDecorator<Object>> {
	protected static final String CONTENT_TYPE = "Content-Type";
	protected T decorator;
	protected Request<Object> request;

	@Before
	public void setUp() {
		//noinspection unchecked
		request = mock(Request.class);
		decorator = newRequestDecorator(request);
	}

	@NonNull
	protected abstract T newRequestDecorator(final Request<Object> request);

	@Test
	public void testDeliverResponseDelegate() {
		final Object response = new Object();
		decorator.deliverResponse(response);
		verify(request).deliverResponse(response);
	}

	@Test
	public void testParseNetworkResponseDelegate() {
		final NetworkResponse response = mock(NetworkResponse.class);
		decorator.parseNetworkResponse(response);
		verify(request).parseNetworkResponse(response);
	}

	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	@Test
	public void testParseNetworkErrorDelegate() {
		final VolleyError error = mock(VolleyError.class);
		decorator.parseNetworkError(error);
		verify(request).parseNetworkError(error);
	}

	@Test
	public void testAddMarkerDelegate() {
		final String tag = "whatever";
		decorator.addMarker(tag);
		verify(request).addMarker(tag);
	}

	@Test
	public void testCancelDelegate() {
		decorator.cancel();
		verify(request).cancel();
	}

	@Test
	public void testDeliverErrorDelegate() {
		final VolleyError error = mock(VolleyError.class);
		decorator.deliverError(error);
		verify(request).deliverError(error);
	}

	@Test
	public void testGetBodyDelegate() throws AuthFailureError {
		decorator.getBody();
		verify(request).getBody();
	}

	@Test
	public void testGetBodyContentTypeDelegate() {
		decorator.getBodyContentType();
		verify(request).getBodyContentType();
	}

	@Test
	public void testGetCacheEntryDelegate() {
		decorator.getCacheEntry();
		verify(request).getCacheEntry();
	}

	@Test
	public void testGetCacheKeyDelegate() {
		decorator.getCacheKey();
		verify(request).getCacheKey();
	}

	@Test
	public void testGetHeadersDelegate() throws AuthFailureError {
		decorator.getHeaders();
		verify(request).getHeaders();
	}

	@Test
	public void testGetMethodDelegate() {
		decorator.getMethod();
		verify(request, atLeastOnce()).getMethod();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetPostBodyDelegate() throws AuthFailureError {
		decorator.getPostBody();
		verify(request).getPostBody();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetPostBodyContentTypeDelegate() {
		decorator.getPostBodyContentType();
		verify(request).getPostBodyContentType();
	}

	@Test
	public void testGetPriorityDelegate() {
		decorator.getPriority();
		verify(request).getPriority();
	}

	@Test
	public void testGetRetryPolicyDelegate() {
		decorator.getRetryPolicy();
		verify(request).getRetryPolicy();
	}

	@Test
	public void testGetTagDelegate() {
		decorator.getTag();
		verify(request).getTag();
	}

	@Test
	public void testGetTrafficStatsTagDelegate() {
		decorator.getTrafficStatsTag();
		verify(request).getTrafficStatsTag();
	}

	@Test
	public void testGetUrlDelegate() {
		decorator.getUrl();
		verify(request, atLeastOnce()).getUrl();
	}

	@Test
	public void testHasHadResponseDeliveredDelegate() {
		decorator.hasHadResponseDelivered();
		verify(request).hasHadResponseDelivered();
	}

	@Test
	public void testIsCancelledDelegate() {
		decorator.isCanceled();
		verify(request).isCanceled();
	}

	@Test
	public void testMarkDeliveredDelegate() {
		decorator.markDelivered();
		verify(request).markDelivered();
	}

	@Test
	public void testSetCacheEntryDelegate() {
		final Cache.Entry entry = mock(Cache.Entry.class);
		decorator.setCacheEntry(entry);
		verify(request).setCacheEntry(entry);
	}

	@Test
	public void testSetRequestQueueDelegate() {
		final RequestQueue requestQueue = mock(RequestQueue.class);
		decorator.setRequestQueue(requestQueue);
		verify(request).setRequestQueue(requestQueue);
	}

	@Test
	public void testSetRetryPolicyDelegate() {
		final RetryPolicy retryPolicy = mock(RetryPolicy.class);
		decorator.setRetryPolicy(retryPolicy);
		verify(request).setRetryPolicy(retryPolicy);
	}

	@Test
	public void testSetTagDelegate() {
		final String tag = "tag";
		decorator.setTag(tag);
		verify(request).setTag(tag);
	}

	@Test
	public void testToString() {
		final String defaultToString = decorator.getClass().getName()
				+ '@' + Integer.toHexString(decorator.hashCode());

		assertThat("toString is not override", decorator.toString(), not(equalTo(defaultToString)));
		assertNotNull("toString return null", decorator.toString());
	}
}
