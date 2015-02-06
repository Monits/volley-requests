package com.monits.volleyrequests.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.MalformedURLException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class NullSafeImageLoaderTest {
	private NullSafeImageLoader imageLoader;

	private RequestQueue queue;
	private ImageLoader.ImageCache cache;

	@Before
	public void setUp() throws MalformedURLException {
		queue = mock(RequestQueue.class);
		cache = mock(ImageLoader.ImageCache.class);

		imageLoader = new NullSafeImageLoader(queue, cache);
	}

	@Test
	public void testGetNull() {
		final ImageLoader.ImageListener listener = mock(ImageLoader.ImageListener.class);

		imageLoader.get(null, listener, 0, 0);

		verify(listener).onResponse(any(ImageLoader.ImageContainer.class), eq(true));
	}

	@Test
	public void testGetNonNull() {
		final ImageLoader.ImageListener listener = mock(ImageLoader.ImageListener.class);

		imageLoader.get("http://i0.kym-cdn.com/photos/images/facebook/000/011/296/success_baby.jpg",
				listener, 0, 0);

		verify(queue).add(any(ImageRequest.class));
	}
}
