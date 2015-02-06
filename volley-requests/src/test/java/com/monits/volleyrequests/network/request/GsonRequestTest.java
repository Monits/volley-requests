package com.monits.volleyrequests.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.google.gson.Gson;

import org.apache.http.protocol.HTTP;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class GsonRequestTest extends AbstractRfcCompliantListenableRequestTest<SampleData, GsonRequest<SampleData>> {
	private static final String CHARSET = "UTF-8";
	private static final String STRING_DATA = "my string data";

	@Override
	protected GsonRequest<SampleData> newRequest(final int method,
				final Response.Listener<SampleData> listener) {
		return new GsonRequest<>(method, "http://www.google.com/", new Gson(), SampleData.class,
				listener, null, null);
	}

	protected SampleData newValidResponse() {
		return new SampleData(STRING_DATA);
	}

	@Test
	public void testParseNetworkResponse() {
		// This method is not delegated, but overridden by the decorator
		final SampleData data = newValidResponse();
		final String json = new Gson().toJson(data);
		final Map<String, String> headers = new HashMap<>();
		headers.put(HTTP.CONTENT_TYPE, "application/javascript; charset=" + CHARSET);

		try {
			final NetworkResponse networkResponse = new NetworkResponse(
					json.getBytes(CHARSET), headers);
			final Response<SampleData> response = request.parseNetworkResponse(networkResponse);

			assertEquals(data, response.result);
		} catch (final UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseNetworkResponseWithBadEncoding() {
		// This method is not delegated, but overridden by the decorator
		final SampleData data = newValidResponse();
		final String json = new Gson().toJson(data);
		final Map<String, String> headers = new HashMap<>();
		headers.put(HTTP.CONTENT_TYPE, "application/javascript; charset=nonexistingcharset");

		try {
			final NetworkResponse networkResponse = new NetworkResponse(
					json.getBytes(CHARSET), headers);
			final Response<SampleData> response = request.parseNetworkResponse(networkResponse);

			assertFalse(response.isSuccess());
		} catch (final UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseNetworkResponseWithBadJson() {
		// This method is not delegated, but overridden by the decorator
		final String json = "{data: null";	// Malformed json
		final Map<String, String> headers = new HashMap<>();
		headers.put(HTTP.CONTENT_TYPE, "application/javascript; charset=" + CHARSET);

		try {
			final NetworkResponse networkResponse = new NetworkResponse(
					json.getBytes(CHARSET), headers);
			final Response<SampleData> response = request.parseNetworkResponse(networkResponse);

			assertFalse(response.isSuccess());
		} catch (final UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}
}
