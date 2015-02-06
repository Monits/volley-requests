package com.monits.volleyrequests.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class EmptyResponseRequestTest extends AbstractRfcCompliantListenableRequestTest<Void, EmptyResponseRequest> {

	protected EmptyResponseRequest newRequestWithMethod(final int method) {
		return new EmptyResponseRequest(method, "http://www.google.com/",
				new DummyListener<Void>(), null, null);
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
