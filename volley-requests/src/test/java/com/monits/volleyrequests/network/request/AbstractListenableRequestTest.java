package com.monits.volleyrequests.network.request;

import com.android.volley.Request;
import com.android.volley.Response;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Ignore("Abstract test class for abstract class, don't run stand alone")
public abstract class AbstractListenableRequestTest<S, T extends ListenableRequest<S>> {

	protected T request;

	@Before
	public void setUp() {
		request = newRequest(Request.Method.GET);
	}

	protected T newRequest(final int method) {
		return newRequest(method, new DummyListener<S>(), new DummyCancelListener());
	}

	protected abstract T newRequest(final int method, final Response.Listener<S> listener,
						final ListenableRequest.CancelListener cancelListener);

	protected abstract S newValidResponse();

	@Test
	public void testDeliverResponse() throws Exception {
		@SuppressWarnings("unchecked")
		final Response.Listener<S> listener = mock(Response.Listener.class);

		final T r = newRequest(Request.Method.GET, listener, null);
		final S response = newValidResponse();
		r.deliverResponse(response);

		verify(listener).onResponse(response);
	}

	@Test
	public void testCancel() throws Exception {
		@SuppressWarnings("unchecked")
		final ListenableRequest.CancelListener cancelListener = mock(ListenableRequest.CancelListener.class);

		final T r = newRequest(Request.Method.GET, null, cancelListener);
		r.cancel();

		verify(cancelListener).onCancel();
	}

	// Dummy empty implementation of listener class.
	protected static class DummyListener<T> implements Response.Listener<T> {
		@Override
		public void onResponse(final T objects) {
			// Dummy method does nothing
		}
	}

	// Dummy empty implementation of cancel listener class.
	protected static class DummyCancelListener implements ListenableRequest.CancelListener {
		@Override
		public void onCancel() {
			// Dummy method does nothing
		}
	}
}