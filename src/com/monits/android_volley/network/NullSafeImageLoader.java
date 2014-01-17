package com.monits.android_volley.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

public class NullSafeImageLoader extends ImageLoader {

	public NullSafeImageLoader(final RequestQueue queue, final ImageCache imageCache) {
		super(queue, imageCache);
	}

	@Override
	public ImageContainer get(final String requestUrl, final ImageListener imageListener,
			final int maxWidth, final int maxHeight) {
		
		if (requestUrl == null) {
            final ImageContainer container = new ImageContainer(null, requestUrl, null, null);
            imageListener.onResponse(container, true);
            return container;
		}
		
		return super.get(requestUrl, imageListener, maxWidth, maxHeight);
	}
}
