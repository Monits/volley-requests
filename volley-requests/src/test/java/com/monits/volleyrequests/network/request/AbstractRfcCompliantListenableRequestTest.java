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
package com.monits.volleyrequests.network.request;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore("Abstract test class for abstract class, don't run stand alone")
@SuppressWarnings("PMD.TooManyStaticImports")
public abstract class AbstractRfcCompliantListenableRequestTest<S, T extends RfcCompliantListenableRequest<S>>
		extends AbstractListenableRequestTest<S, T> {

	@Test
	public void testShouldCacheGet() {
		assertTrue(newRequest(Request.Method.GET).shouldCache());
	}

	@Test
	public void testShouldCacheHead() {
		assertTrue(newRequest(Request.Method.HEAD).shouldCache());
	}

	@Test
	public void testShouldCachePost() {
		assertFalse(newRequest(Request.Method.POST).shouldCache());
	}

	@Test
	public void testShouldCacheDelete() {
		assertFalse(newRequest(Request.Method.DELETE).shouldCache());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldCacheDeprectedGetOrPost() {
		newRequest(Request.Method.DEPRECATED_GET_OR_POST);
	}

	@Test
	public void testCacheableResponseIsCached() {
		final RequestQueue queue = mock(RequestQueue.class);
		final Cache cache = mock(Cache.class);
		when(queue.getCache()).thenReturn(cache);

		final Cache.Entry entry = new Cache.Entry();
		final T r = newRequest(Request.Method.GET);
		r.setCacheEntry(entry);
		r.setRequestQueue(queue);
		r.deliverResponse(newValidResponse());

		verify(cache).put(r.getCacheKey(), entry);
	}

	@Test
	public void testUncacheableResponseIsNotCached() {
		final RequestQueue queue = mock(RequestQueue.class);
		final Cache cache = mock(Cache.class);
		when(queue.getCache()).thenReturn(cache);

		final T r = newRequest(Request.Method.GET);
		// no cache entry is set
		r.setRequestQueue(queue);
		r.deliverResponse(newValidResponse());

		verify(cache, never()).put(anyString(), any(Cache.Entry.class));
	}
}
