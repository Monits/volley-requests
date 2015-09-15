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
package com.monits.volleyrequests.loader;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.monits.volleyrequests.network.request.GsonRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.robolectric.Robolectric.runUiThreadTasksIncludingDelayedTasks;


@RunWith(RobolectricTestRunner.class)
public class RequestLoaderTest {
	private Request<String> request;
	private RequestQueue requestQueue;
	private RequestLoader<String> loader;

	@Before
	public void setUp() {
		request = mock(GsonRequest.class);
		requestQueue = mock(RequestQueue.class);
		final Context context = mock(Context.class);
		loader = new RequestLoader<>(context, request, requestQueue);
	}

	@Test
	public void testUpdateThrottle() throws InterruptedException {
		final int nRepeat = 2;
		loader.setUpdateThrottle(5L);
		loader.onForceLoad();
		runUiThreadTasksIncludingDelayedTasks();
		verify(requestQueue, atLeast(nRepeat)).add(any(Request.class));
	}

	@Test
	public void testSuperCancelNeverCall() {
		final ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

		loader.onForceLoad();
		verify(requestQueue).add(captor.capture());

		final Request capturedRequest = captor.getValue();

		capturedRequest.cancel();
		assertNotEquals(request.isCanceled(), capturedRequest.isCanceled());
		verify(request, never()).cancel();
	}

	@Test
	public void testSuperMarkDeliveredNeverCall() {
		final ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

		// Do it
		loader.onForceLoad();
		verify(requestQueue).add(captor.capture());

		// check captured value
		final Request capturedRequest = captor.getValue();

		// Actually check it's behaviour
		capturedRequest.markDelivered();
		verify(request, never()).markDelivered();
		assertNotEquals(request.hasHadResponseDelivered(),
				captor.getValue().hasHadResponseDelivered());
	}

	@Test
	public void testOnStopLoading() {
		final ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

		loader.onForceLoad();
		verify(requestQueue).add(captor.capture());
		loader.onStopLoading();

		assertTrue(captor.getValue().isCanceled());
	}

	@Test
	public void testOnReset() {
		final ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

		loader.onForceLoad();
		verify(requestQueue).add(captor.capture());
		final Request request = captor.getValue();
		loader.onReset();
		loader.onForceLoad();
		assertFalse(request.isCanceled());
	}

	@Test
	public void testToString() {
		final Request<String> request = mock(GsonRequest.class);
		final RequestQueue requestQueue = mock(RequestQueue.class);
		final Context context = mock(Context.class);
		final RequestLoader<String> loader = new RequestLoader<>(context, request, requestQueue);

		final String defaultToString = loader.getClass().getName()
				+ '@' + Integer.toHexString(loader.hashCode());

		assertThat(loader.toString(), not(equalTo(defaultToString)));
		assertNotNull(loader.toString());
	}

}
