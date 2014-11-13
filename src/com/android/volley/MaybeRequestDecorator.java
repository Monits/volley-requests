package com.android.volley;

import org.apache.http.HttpStatus;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

/**
 * Class to parse a list of all items in a collection from a NetworkResponse
 * depending on a key.
 *
 * @author fpredassi
 *
 * @param <T>
 */
public class MaybeRequestDecorator<T> extends RequestDecorator<T> {
	private final T object;

	public MaybeRequestDecorator(final Request<T> request, final Gson gson,
			final int method, final String url, final Listener<T> listener,
			final ErrorListener errListener, final String jsonBody, final T object) {
		super(request, gson, method, url, listener, errListener, jsonBody);
		this.object = object;
	}

	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		if (response.statusCode == HttpStatus.SC_CREATED) {
			return Response.success(object,
					HttpHeaderParser.parseCacheHeaders(response));
		}
		return super.parseNetworkResponse(response);
	}

}
