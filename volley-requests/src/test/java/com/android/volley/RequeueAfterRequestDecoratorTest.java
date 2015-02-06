package com.android.volley;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class RequeueAfterRequestDecoratorTest extends AbstractRequestDecoratorTest {
	private RequeuePolicy requeuePolicy;

	@NonNull
	@Override
	protected RequeueAfterRequestDecorator<Object> newRequestDecorator(
			final Request<Object> request) {
		requeuePolicy = mock(RequeuePolicy.class);

		return RequeueAfterRequestDecorator.wrap(request, requeuePolicy);
	}

	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	@Test
	public void testParseNetworkErrorRequeue() throws VolleyError {
		when(requeuePolicy.shouldRequeue(any(NetworkResponse.class))).thenReturn(true);
		final RetryPolicy retryPolicy = mock(RetryPolicy.class);
		when(request.getRetryPolicy()).thenReturn(retryPolicy);

		final VolleyError error = mock(VolleyError.class);
		decorator.parseNetworkError(error);
		verify(retryPolicy).retry(any(VolleyError.class));
		verify(requeuePolicy).executeBeforeRequeueing(any(Listener.class), any(ErrorListener.class));
	}
}