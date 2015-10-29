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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("PMD.TooManyStaticImports")
public class JSONArrayRequestDecoratorTest
		extends AbstractRequestDecoratorTest<JSONArrayRequestDecorator<Object>> {
	private static final String ELEMENTS_KEY = "elements";
	private static final String CHARSET = "UTF-8";

	@NonNull
	@Override
	protected JSONArrayRequestDecorator<Object> newRequestDecorator(final Request<Object> request) {
		return new JSONArrayRequestDecorator<>(request, ELEMENTS_KEY);
	}

	@Override
	public void testParseNetworkResponseDelegate() {
		// This method is not delegated, but overridden by the decorator
		final JSONObject json = new JSONObject();
		final JSONArray jsonArray = new JSONArray();
		jsonArray.put(0);
		jsonArray.put(1);
		jsonArray.put(-1);

		try {
			json.put(ELEMENTS_KEY, jsonArray);
		} catch (final JSONException e) {
			fail(e.getMessage());
		}

		final Map<String, String> headers = new HashMap<>();
		headers.put(CONTENT_TYPE, "application/javascript; charset=" + CHARSET);

		try {
			final ArgumentCaptor<NetworkResponse> capture = ArgumentCaptor.forClass(
					NetworkResponse.class);
			final NetworkResponse response = new NetworkResponse(json.toString().getBytes(CHARSET),
					headers);
			decorator.parseNetworkResponse(response);
			verify(request).parseNetworkResponse(capture.capture());

			assertEquals("Failed to parse jsonArray response", jsonArray,
				new JSONArray(new String(capture.getValue().data, CHARSET)));
		} catch (final UnsupportedEncodingException | JSONException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseNetworkResponseWithBadEncoding() {
		final JSONObject json = new JSONObject();

		final Map<String, String> headers = new HashMap<>();
		headers.put(CONTENT_TYPE, "application/javascript; charset=nonexistingcharset");

		try {
			final NetworkResponse response = new NetworkResponse(json.toString().getBytes(CHARSET),
					headers);
			final Response<Object> parsedResponse = decorator.parseNetworkResponse(response);

			assertFalse("The request has failed", parsedResponse.isSuccess());
		} catch (final UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseNetworkResponseWithBadJson() {
		final Map<String, String> headers = new HashMap<>();
		headers.put(CONTENT_TYPE, "application/javascript; charset=" + CHARSET);

		try {
			final NetworkResponse response = new NetworkResponse("{test: [1,2}".getBytes(CHARSET),
					headers);
			final Response<Object> parsedResponse = decorator.parseNetworkResponse(response);

			assertFalse("The request has failed", parsedResponse.isSuccess());
		} catch (final UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetElementsKey() {
		assertEquals(ELEMENTS_KEY, decorator.getElementsKey());
	}
}