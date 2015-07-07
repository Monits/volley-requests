package com.android.volley;

import android.support.annotation.NonNull;

import org.apache.http.HttpStatus;

import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Class to parse a list of all items in a collection from a NetworkResponse
 * depending on a key.
 *
 * @author fpredassi
 *
 * @param <T> The type of the request being decorated.
 */
public class MaybeRequestDecorator<T> extends RequestDecorator<T> {
	private final T object;

	public MaybeRequestDecorator(@NonNull final Request<T> request, @NonNull final T object) {
		super(request);
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

	@NonNull
	public T getObject() {
		return object;
	}
}
