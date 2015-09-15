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
package com.monits.volleyrequests.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.AuthFailureError;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;


import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class EmptyResponseRequestTest
		extends AbstractJsonRfcCompliantListenableRequestTest<Void, EmptyResponseRequest> {

	@Override
	protected EmptyResponseRequest newRequest(final int method, final Response.Listener<Void> listener) {
		return new EmptyResponseRequest(method, "http://www.google.com/", listener, null,
				null);
	}

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
	public void testGetNullBody() throws AuthFailureError {
		final byte[] body = request.getBody();
		assertNull(body);
	}

	@Test
	public void testParseNetworkResponse() {
		final NetworkResponse networkResponse = new NetworkResponse(null);
		final Response<Void> response = request.parseNetworkResponse(networkResponse);

		assertNull(response.result);
	}
}
