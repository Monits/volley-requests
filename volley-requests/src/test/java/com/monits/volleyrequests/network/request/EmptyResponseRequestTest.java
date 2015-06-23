package com.monits.volleyrequests.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class EmptyResponseRequestTest
		extends AbstractJsonRfcCompliantListenableRequestTest<Void, EmptyResponseRequest> {

	@Override
	protected EmptyResponseRequest newRequest(final int method,
				final Response.Listener<Void> listener,
				final ListenableRequest.CancelListener cancelListener) {
		return new EmptyResponseRequest(method, "http://www.google.com/", listener, null,
				cancelListener, null);
	}

	protected Void newValidResponse() {
		return null;
	}

	@Test
	public void testParseNetworkResponse() {
		final NetworkResponse networkResponse = new NetworkResponse(null);
		final Response<Void> response = request.parseNetworkResponse(networkResponse);

		assertNull(response.result);
	}
}
