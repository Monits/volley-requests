package com.android.volley;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public interface RequeuePolicy {

	/**
	 * Define a condition whether this request should retry or not.
	 *
	 * @param networkResponse the given response of the first request.
	 * @return true if should retry, false otherwise.
	 */
	boolean shouldRequeue(NetworkResponse networkResponse);

	/**
	 * In case {@link RequeuePolicy#shouldRequeue(NetworkResponse)} returns true, this method will be called.
	 * Providing listeners which are in charge of requeueing the request in case of success.
	 *
	 * @param successCallback notify this listener in case of success.
	 * @param errorCallback notify this listener in case of failure.
	 */
	void executeBeforeRequeueing(Listener<?> successCallback, ErrorListener errorCallback);
}
