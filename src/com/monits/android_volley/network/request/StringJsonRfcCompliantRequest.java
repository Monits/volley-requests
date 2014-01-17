package com.monits.android_volley.network.request;

import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class StringJsonRfcCompliantRequest extends JsonRfcCompliantListenableRequest<String> {

	public StringJsonRfcCompliantRequest(final int method, final String url,
			final Listener<String> listener, final ErrorListener errListener, final String jsonBody) {
		super(method, url, listener, errListener, jsonBody);
	}

	@Override
	protected Response<String> parseNetworkResponse(final NetworkResponse response) {
		try {
			final String str = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
		} catch (final UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}
}
