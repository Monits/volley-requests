package com.monits.android_volley.network.request;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonRequest<T> extends JsonRfcCompliantListenableRequest<T> {

	private final Gson gson;
	private final Type clazz;

	public GsonRequest(final int method, final String url, final Gson gson,
			final Type clazz, final Listener<T> listener,
			final ErrorListener errListener, final String jsonBody) {
		super(method, url, listener, errListener, jsonBody);

		this.gson = gson;
		this.clazz = clazz;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		try {
			final String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success((T) gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
		} catch (final UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (final JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
	}

}
