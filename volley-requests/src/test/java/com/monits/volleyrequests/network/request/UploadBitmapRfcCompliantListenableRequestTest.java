package com.monits.volleyrequests.network.request;

import android.graphics.Bitmap;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.apache.http.protocol.HTTP;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class UploadBitmapRfcCompliantListenableRequestTest {
	private final static String CHARSET = "UTF-8";
	private final static String RESPONSE = "response";

	private UploadBitmapRfcCompliantListenableRequest request;

	@Before
	public void setUp() {
		request = new UploadBitmapRfcCompliantListenableRequest(Request.Method.GET,
				"http://www.google.com/", new DummyListener<String>(), null, null,
				Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), "filename");
	}

	@Test
	public void testContentTypeIsMultipart() {
		assertTrue("Content type is not multipart",
				request.getBodyContentType().startsWith("multipart/form-data;"));
	}

	@Test
	public void testParseNetworkResponseIllegalCharset() throws UnsupportedEncodingException {
		final Map<String, String> headers = new HashMap<>();
		headers.put(HTTP.CONTENT_TYPE, "text/plain; charset=nonexistingcharset");

		final NetworkResponse networkResponse = new NetworkResponse(RESPONSE.getBytes(CHARSET), headers);

		final Response<String> response = request.parseNetworkResponse(networkResponse);
		assertFalse("Parsing illegal charset did not fail", response.isSuccess());
	}

	@Test
	public void testParseNetworkResponse() throws UnsupportedEncodingException {
		final Map<String, String> headers = new HashMap<>();
		headers.put(HTTP.CONTENT_TYPE, "text/plain; charset=" + CHARSET);

		final NetworkResponse networkResponse = new NetworkResponse(RESPONSE.getBytes(CHARSET), headers);

		final Response<String> response = request.parseNetworkResponse(networkResponse);
		assertEquals(RESPONSE, response.result);
	}

	// Dummy empty implementation of listener class.
	private static class DummyListener<T> implements Response.Listener<T> {
		@Override
		public void onResponse(final T objects) {
			// Dummy method does nothing
		}
	}
}
