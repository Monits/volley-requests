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
package com.monits.volleyrequests.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

/**
 * A null-safe implementation of {@link ImageLoader}.
 * 
 * In case the url is null (common when it's unknown), {@link ImageLoader} will throw a NullPointerException,
 * forcing the developer to manually check each time and opting between loading through the ImageLoader, or
 * setting the default placeholder manually. This implementation avoids that by safely showing the placeholder.
 * 
 * For non-null values, loading will be delegated to Volley's {@link ImageLoader}.
 */
public class NullSafeImageLoader extends ImageLoader {

	/**
	 * Constructor
	 *
	 * @param queue The request queue where the request are loaded
	 * @param imageCache The image cache
	 */
	public NullSafeImageLoader(@NonNull final RequestQueue queue,
					@NonNull final ImageCache imageCache) {
		super(queue, imageCache);
	}

	@Override
	public ImageContainer get(@Nullable final String requestUrl,
					@NonNull final ImageListener imageListener,
					final int maxWidth, final int maxHeight) {
		if (requestUrl == null) {
			final ImageContainer container = new ImageContainer(null, null, null, null);
			imageListener.onResponse(container, true);
			return container;
		}
		return super.get(requestUrl, imageListener, maxWidth, maxHeight);
	}
}
