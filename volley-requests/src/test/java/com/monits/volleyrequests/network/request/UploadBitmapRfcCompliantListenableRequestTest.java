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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class UploadBitmapRfcCompliantListenableRequestTest {
	private final static String CONTENT_TYPE = "Content-Type";
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
		headers.put(CONTENT_TYPE, "text/plain; charset=nonexistingcharset");

		final NetworkResponse networkResponse = new NetworkResponse(RESPONSE.getBytes(CHARSET), headers);

		final Response<String> response = request.parseNetworkResponse(networkResponse);
		assertFalse("Parsing illegal charset did not fail", response.isSuccess());
	}

	@Test
	public void testParseNetworkResponse() throws UnsupportedEncodingException {
		final Map<String, String> headers = new HashMap<>();
		headers.put(CONTENT_TYPE, "text/plain; charset=" + CHARSET);

		final NetworkResponse networkResponse = new NetworkResponse(RESPONSE.getBytes(CHARSET), headers);

		final Response<String> response = request.parseNetworkResponse(networkResponse);
		assertEquals(RESPONSE, response.result);
	}

	@Test
	public void testGetBody() throws AuthFailureError {
		final Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
		request = new UploadBitmapRfcCompliantListenableRequest(Request.Method.GET,
				"http://www.google.com/", new DummyListener<String>(), null,
				bitmap, "filename");
		final byte[] body = request.getBody();
		final ByteArrayInputStream bais = new ByteArrayInputStream(body);
		final Bitmap decodedBitmap = BitmapFactory.decodeStream(bais);
		assertEquals(bitmap.getWidth(), decodedBitmap.getWidth());
		assertEquals(bitmap.getHeight(), decodedBitmap.getHeight());
	}

	@Test
	public void testToString() {
		final String defaultToString = request.getClass().getName()
				+ '@' + Integer.toHexString(request.hashCode());

		assertThat(request.toString(), not(equalTo(defaultToString)));
		assertNotNull(request.toString());
	}

	// Dummy empty implementation of listener class.
	private static class DummyListener<T> implements Response.Listener<T> {
		@Override
		public void onResponse(final T objects) {
			// Dummy method does nothing
		}
	}
}
