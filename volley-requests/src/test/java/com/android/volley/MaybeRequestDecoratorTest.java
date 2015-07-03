package com.android.volley;

import android.support.annotation.NonNull;

import org.apache.http.HttpStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


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
		final NetworkResponse response = new NetworkResponse(HttpStatus.SC_CREATED, new byte[0],
				Collections.<String, String>emptyMap(), false);
		final Response<Object> r = decorator.parseNetworkResponse(response);
		assertEquals(DEFAULT_OBJECT, r.result);
	}

	@Test
	public void testGetObject() {
		assertEquals(DEFAULT_OBJECT, decorator.getObject());
	}
}