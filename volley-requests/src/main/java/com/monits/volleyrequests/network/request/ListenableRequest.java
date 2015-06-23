/*
* Copyright 2010 - 2014 Monits
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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * A generic {@link Request} that adds a listener for success
 * and cancel callbacks.
 */
public abstract class ListenableRequest<T> extends Request<T> {

	private final Listener<T> listener;
	private final CancelListener cancelListener;

	/**
	 * Creates a new ListenableRequest instance
	 *
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param cancelListener The listener for cancel.
	 */
	public ListenableRequest(final int method, @NonNull final String url,
					@Nullable final Listener<T> listener,
					@Nullable final ErrorListener errListener,
					@Nullable final CancelListener cancelListener) {
		super(method, url, errListener);
		this.listener = listener;
		this.cancelListener = cancelListener;

	}

	/**
	 * Creates a new ListenableRequest instance, with fewer
	 * parameters for backwards compatibility.
	 *
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 */
	public ListenableRequest(final int method, @NonNull final String url,
					@Nullable final Listener<T> listener,
					@Nullable final ErrorListener errListener) {
		this(method, url, listener, errListener, null);
	}

	@Override
	protected void deliverResponse(final T ret) {
		if (listener != null) {
			listener.onResponse(ret);
		}
	}

	@Override
	public void cancel() {
		super.cancel();
		if (cancelListener != null) {
			cancelListener.onCancel();
		}
	}

	/**
	 * Interface that defines the cancel listener for {@link ListenableRequest}
	 */
	public interface CancelListener {
		/**
		 *  Performs the desired action on a cancel callback
		 */
		void onCancel();
	}
}
