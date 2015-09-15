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
package com.monits.volleyrequests.loader;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import com.android.volley.Request;
import com.android.volley.RequestDecorator;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A generic {@link Loader} that wraps a {@link Request}. This loader behaves
 * similarly to {@link android.content.AsyncTaskLoader}, having an {@link #updateThrottle}
 * that determines how often should the data be refreshed, by reinserting the
 * {@link #request} into the {@link #queue}. If the {@link #updateThrottle} is
 * less or equal to 0, the loader will behave as normal {@link Loader}, sending
 * the {@link #request} only when its starts loading and not refreshing the data
 * periodically.
 *
 * @param <D> The result returned when the load is complete.
 */

public class RequestLoader<D> extends Loader<D> {
	private final Request<D> request;
	private final RequestQueue queue;
	private final RequestSender sender;
	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "Doesn't provide particular information")
	private final Handler handler;

	private long updateThrottle;
	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "Doesn't provide particular information")
	private Request<D> dispatchedRequest;

	/**
	 * A {@link RequestDecorator} that preserves the {@link #request} internal
	 * state, so that it remains unspoilt for periodic use.
	 *
	 * @param <T> The {@link #request}'s returned data.
	 */

	@SuppressFBWarnings(value = "MISSING_TO_STRING_OVERRIDE",
			justification = "Unnecessary to implement toString for a private inner class")
	private static class RequestPreservingDecorator<T> extends RequestDecorator<T> {
		private final Response.Listener<T> listener;

		private boolean hadResponseDelivered;
		private boolean cancelled;

		/**
		 * Creates a new RequestPreservingDecorator instance.
		 *
		 * @param request The decorator's wrapped request.
		 * @param listener The listener that receives the request's response.
		 */
		public RequestPreservingDecorator(@NonNull final Request<T> request,
				@NonNull final Response.Listener<T> listener) {
			super(request);
			this.listener = listener;
		}
		/*The decorator does not pass markDelivered(), hasHadResponseDelivered(),
		cancel(), isCanceled() and deliverResponse(T) to the wrapped request,
		to preserve its internal state.	*/

		@Override
		public void markDelivered() {
			hadResponseDelivered = true;
		}

		@Override
		public boolean hasHadResponseDelivered() {
			return hadResponseDelivered;
		}

		@Override
		public void cancel() {
			cancelled = true;
		}

		@Override
		public boolean isCanceled() {
			return cancelled;
		}

		@Override
		protected void deliverResponse(final T response) {
			listener.onResponse(response);
		/*Since using listeners on the Loader is optional, let the
		request listener work too. */
			super.deliverResponse(response);
		}
	}

	/**
	 * Creates a new RequestLoader instance.
	 *
	 * @param context Used to retrieve the application context
	 * @param request The request used by this loader.
	 * @param queue The {@link RequestQueue} that will send the request.
	 */
	public RequestLoader(@NonNull final Context context, @NonNull final Request<D> request,
			@NonNull final RequestQueue queue) {
		super(context);

		this.request = request;
		this.queue = queue;
		this.sender = new RequestSender();
		this.handler = new Handler();
	}

	/**
	 * Sets the {@link #updateThrottle} that determines how often should
	 * the loader refresh the data.
	 *
	 * @param delayMS The {@link #updateThrottle} value. If its less or equal
	 *                to 0, then the loader will not resend the request.
	 */
	public void setUpdateThrottle(final long delayMS) {
		this.updateThrottle = delayMS;
	}

	/**
	 * Creates a new {@link #dispatchedRequest}, sets a schedule and inserts it in the
	 * request queue. It will cancel any previous request and load a new one.
	 */
	private void sendRequest() {
		if (dispatchedRequest != null) {
			dispatchedRequest.cancel();
		}
		dispatchedRequest = new RequestPreservingDecorator<>(request, new Response.Listener<D>() {
			@Override
			public void onResponse(@NonNull final D response) {
				deliverResult(response);
			}
		});
		if (updateThrottle > 0L) {
			handler.postDelayed(sender, updateThrottle);
		}
		queue.add(dispatchedRequest);
	}

	@Override
	protected void onStartLoading() {
		sendRequest();
	}

	/**
	 * {@link Loader} overridden method. Stops the loader and removes all
	 * all pending requests.
	 */
	@Override
	protected void onStopLoading() {
		dispatchedRequest.cancel();
		handler.removeCallbacks(sender);
		super.onStopLoading();
	}

	@Override
	protected void onForceLoad() {
		super.onForceLoad();
		startLoading();
	}

	/**
	 * {@link Loader} overridden method. Resets loader's internal state
	 * and removes any pending request.
	 */
	@Override
	protected void onReset() {
		dispatchedRequest = null;
		handler.removeCallbacks(sender);
		super.onReset();
	}

	/**
	 * {@link Runnable} that periodically sends a {@link #dispatchedRequest}.
	 */
	private final class RequestSender implements Runnable {
		@Override
		public void run() {
			sendRequest();
		}
	}

	@Override
	public String toString() {
		return "RequestLoader for " + request + " with updateThrottle = " + updateThrottle;
	}
}
