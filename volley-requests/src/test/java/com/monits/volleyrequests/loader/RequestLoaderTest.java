package com.monits.volleyrequests.loader;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.monits.volleyrequests.network.request.GsonRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	public void testUpdateThrottle() {
		loader.setUpdateThrottle(2L);
		loader.onForceLoad();
		final CountDownLatch c = new CountDownLatch(2);
		when(requestQueue.add(request)).thenAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable {
				c.countDown();
				return null;
			}
		});
		try {
			c.await(2, TimeUnit.MILLISECONDS);
		} catch (final InterruptedException e) {
		}

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
