package com.monits.volleyrequests.network.request;

import com.android.volley.AuthFailureError;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@Ignore("Abstract test class for abstract class, don't run stand alone")
@SuppressWarnings("PMD.TooManyStaticImports")
public abstract class AbstractJsonRfcCompliantListenableRequestTest<S, T extends JsonRfcCompliantListenableRequest<S>>
		extends AbstractRfcCompliantListenableRequestTest<S, T> {
	private static final String CUSTOM_HEADER = "X-Custom";
	private static final String CUSTOM_HEADER_VALUE = "custom";

	@Test
	public void testAddHeader() throws AuthFailureError {
		assertThat(request.getHeaders(), not(Matchers.<String, String>hasKey(CUSTOM_HEADER)));

		request.addHeader(CUSTOM_HEADER, CUSTOM_HEADER_VALUE);

		assertThat(request.getHeaders(), Matchers.<String, String>hasKey(CUSTOM_HEADER));
	}

	@Test
	public void testBodyContentType() {
		assertEquals("application/json", request.getBodyContentType());
	}
}
