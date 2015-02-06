package com.android.volley;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("PMD.TooManyStaticImports")
@RunWith(RobolectricTestRunner.class)
public class RequeueAfterRequestDecoratorTest {
	private RequeueAfterRequestDecorator<Object> decorator;

	private Request<Object> request;
	private RequeuePolicy requeuePolicy;

	@Before
	public void setUp() {
		//noinspection unchecked
		request = mock(Request.class);
		requeuePolicy = mock(RequeuePolicy.class);

		decorator = RequeueAfterRequestDecorator.wrap(request, requeuePolicy);
	}

	@Test
	public void testDeliverResponseDelegate() {
		final Object response = new Object();
		decorator.deliverResponse(response);
		verify(request).deliverResponse(response);
	}

	@Test
	public void testParseNetworkResponseDelegate() {
		final NetworkResponse response = mock(NetworkResponse.class);
		decorator.parseNetworkResponse(response);
		verify(request).parseNetworkResponse(response);
	}

	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	@Test
	public void testParseNetworkErrorDelegate() {
		when(requeuePolicy.shouldRequeue(any(NetworkResponse.class))).thenReturn(false);

		final VolleyError error = mock(VolleyError.class);
		decorator.parseNetworkError(error);
		verify(request).parseNetworkError(error);
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

	@Test
	public void testAddMarkerDelegate() {
		final String tag = "whatever";
		decorator.addMarker(tag);
		verify(request).addMarker(tag);
	}

	@Test
	public void testCancelDelegate() {
		decorator.cancel();
		verify(request).cancel();
	}

	@Test
	public void testDeliverErrorDelegate() {
		final VolleyError error = mock(VolleyError.class);
		decorator.deliverError(error);
		verify(request).deliverError(error);
	}

	@Test
	public void testGetBodyDelegate() throws AuthFailureError {
		decorator.getBody();
		verify(request).getBody();
	}

	@Test
	public void testGetBodyContentTypeDelegate() {
		decorator.getBodyContentType();
		verify(request).getBodyContentType();
	}

	@Test
	public void testGetCacheEntryDelegate() {
		decorator.getCacheEntry();
		verify(request).getCacheEntry();
	}

	@Test
	public void testGetCacheKeyDelegate() {
		decorator.getCacheKey();
		verify(request).getCacheKey();
	}

	@Test
	public void testGetHeadersDelegate() throws AuthFailureError {
		decorator.getHeaders();
		verify(request).getHeaders();
	}

	@Test
	public void testGetMethodDelegate() {
		decorator.getMethod();
		verify(request, atLeastOnce()).getMethod();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetPostBodyDelegate() throws AuthFailureError {
		decorator.getPostBody();
		verify(request).getPostBody();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetPostBodyContentTypeDelegate() {
		decorator.getPostBodyContentType();
		verify(request).getPostBodyContentType();
	}

	@Test
	public void testGetPriorityDelegate() {
		decorator.getPriority();
		verify(request).getPriority();
	}

	@Test
	public void testGetRetryPolicyDelegate() {
		decorator.getRetryPolicy();
		verify(request).getRetryPolicy();
	}

	@Test
	public void testGetTagDelegate() {
		decorator.getTag();
		verify(request).getTag();
	}

	@Test
	public void testGetTrafficStatsTagDelegate() {
		decorator.getTrafficStatsTag();
		verify(request).getTrafficStatsTag();
	}

	@Test
	public void testGetUrlDelegate() {
		decorator.getUrl();
		verify(request, atLeastOnce()).getUrl();
	}

	@Test
	public void testHasHadResponseDeliveredDelegate() {
		decorator.hasHadResponseDelivered();
		verify(request).hasHadResponseDelivered();
	}

	@Test
	public void testIsCancelledDelegate() {
		decorator.isCanceled();
		verify(request).isCanceled();
	}

	@Test
	public void testMarkDeliveredDelegate() {
		decorator.markDelivered();
		verify(request).markDelivered();
	}

	@Test
	public void testSetCacheEntryDelegate() {
		final Cache.Entry entry = mock(Cache.Entry.class);
		decorator.setCacheEntry(entry);
		verify(request).setCacheEntry(entry);
	}

	@Test
	public void testSetRequestQueueDelegate() {
		final RequestQueue requestQueue = mock(RequestQueue.class);
		decorator.setRequestQueue(requestQueue);
		verify(request).setRequestQueue(requestQueue);
	}

	@Test
	public void testSetRetryPolicyDelegate() {
		final RetryPolicy retryPolicy = mock(RetryPolicy.class);
		decorator.setRetryPolicy(retryPolicy);
		verify(request).setRetryPolicy(retryPolicy);
	}

	@Test
	public void testSetTagDelegate() {
		final String tag = "tag";
		decorator.setTag(tag);
		verify(request).setTag(tag);
	}
}