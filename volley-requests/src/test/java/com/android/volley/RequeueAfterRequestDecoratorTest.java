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
package com.android.volley;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("PMD.TooManyStaticImports")
public class RequeueAfterRequestDecoratorTest
		extends AbstractRequestDecoratorTest<RequeueAfterRequestDecorator<Object>> {
	private RequeuePolicy requeuePolicy;

	@NonNull
	@Override
	protected RequeueAfterRequestDecorator<Object> newRequestDecorator(
			final Request<Object> request) {
		requeuePolicy = mock(RequeuePolicy.class);

		return RequeueAfterRequestDecorator.wrap(request, requeuePolicy);
	}

	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	@Test
	public void testParseNetworkErrorRequeue() throws VolleyError {
		when(requeuePolicy.shouldRequeue(any(NetworkResponse.class))).thenReturn(Boolean.TRUE);
		final RetryPolicy retryPolicy = mock(RetryPolicy.class);
		when(request.getRetryPolicy()).thenReturn(retryPolicy);

		// Use a known queue instance
		final RequestQueue queue = mock(RequestQueue.class);
		decorator.setRequestQueue(queue);

		final VolleyError error = mock(VolleyError.class);
		decorator.parseNetworkError(error);
		verify(retryPolicy).retry(any(VolleyError.class));

		final ArgumentCaptor<Listener> listener = ArgumentCaptor.forClass(Listener.class);
		final ArgumentCaptor<ErrorListener> errorListener = ArgumentCaptor.forClass(ErrorListener.class);
		verify(requeuePolicy).executeBeforeRequeueing(listener.capture(), errorListener.capture());

		// If we go through the error listener, nothing should be requeued...
		errorListener.getValue().onErrorResponse(error);
		verify(queue, never()).add(decorator);

		// ... but if we go through the success one we MUST
		listener.getValue().onResponse(new Object());
		verify(queue).add(decorator);
	}

	@Test
	public void testParseNetworkErrorCantRetry() throws VolleyError {
		when(requeuePolicy.shouldRequeue(any(NetworkResponse.class))).thenReturn(Boolean.TRUE);
		final RetryPolicy retryPolicy = mock(RetryPolicy.class);
		doThrow(VolleyError.class).when(retryPolicy).retry(any(VolleyError.class));
		when(request.getRetryPolicy()).thenReturn(retryPolicy);

		final VolleyError error = mock(VolleyError.class);
		final VolleyError volleyError = decorator.parseNetworkError(error);

		assertNotNull(volleyError);
		verify(requeuePolicy, never()).executeBeforeRequeueing(any(Listener.class),
				any(ErrorListener.class));
	}
}