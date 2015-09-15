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

import com.android.volley.AuthFailureError;

import org.junit.Ignore;
import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;



@Ignore("Abstract test class for abstract class, don't run stand alone")
@SuppressWarnings("PMD.TooManyStaticImports")
public abstract class AbstractJsonRfcCompliantListenableRequestTest<S, T extends JsonRfcCompliantListenableRequest<S>>
		extends AbstractRfcCompliantListenableRequestTest<S, T> {
	private static final String CUSTOM_HEADER = "X-Custom";
	private static final String CUSTOM_HEADER_VALUE = "custom";

	@SuppressFBWarnings(value = "PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS",
		justification = "False positive")
	@Test
	public void testAddHeader() throws AuthFailureError {
		assumeThat(request.getHeaders(), not(hasKey(CUSTOM_HEADER)));

		request.addHeader(CUSTOM_HEADER, CUSTOM_HEADER_VALUE);

		assertThat(request.getHeaders(), hasKey(CUSTOM_HEADER));
	}

	@Test
	public void testBodyContentType() {
		assertEquals("application/json", request.getBodyContentType());
	}

	@Test
	public void testToString() {
		final String defaultToString = request.getClass().getName()
				+ '@' + Integer.toHexString(request.hashCode());

		assertThat(request.toString(), not(equalTo(defaultToString)));
		assertNotNull(request.toString());
	}
}
