package com.monits.volleyrequests.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class EmptyResponseRequestTest {

	private EmptyResponseRequest request;

	@Before
	public void setUp() {
		request = new EmptyResponseRequest(Request.Method.GET, "http://www.google.com/",
				new DummyListener<Void>(), null, null);
	}

	@Test
	public void testParseNetworkResponse() {
		final NetworkResponse networkResponse = new NetworkResponse(null);
		final Response<Void> response = request.parseNetworkResponse(networkResponse);

		assertNull(response.result);
	}

	// Dummy empty implementation of listener class.
	private static class DummyListener<T> implements Response.Listener<T> {
		@Override
		public void onResponse(final T objects) {
			// Dummy method does nothing
		}
	}
}
