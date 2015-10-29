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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.HttpURLConnection;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("PMD.TooManyStaticImports")
public class MaybeRequestDecoratorTest
		extends AbstractRequestDecoratorTest<MaybeRequestDecorator<Object>> {
	private static final Object DEFAULT_OBJECT = new Object();

	@NonNull
	@Override
	protected MaybeRequestDecorator<Object> newRequestDecorator(final Request<Object> request) {
		return new MaybeRequestDecorator<>(request, DEFAULT_OBJECT);
	}

	@Test
	public void testParseNetworkResponseWithCreatedStatus() {
		final NetworkResponse response = new NetworkResponse(HttpURLConnection.HTTP_CREATED, new byte[0],
				Collections.<String, String>emptyMap(), false);
		final Response<Object> r = decorator.parseNetworkResponse(response);
		assertEquals("Failed to object in response", DEFAULT_OBJECT, r.result);
	}

	@Test
	public void testGetObject() {
		assertEquals(DEFAULT_OBJECT, decorator.getObject());
	}
}