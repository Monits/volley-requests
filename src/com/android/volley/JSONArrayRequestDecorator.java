package com.android.volley;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class JSONArrayRequestDecorator<T> extends RequestDecorator<T> {
	private final String elementsKey;

	public JSONArrayRequestDecorator(final Request<T> request, final Gson gson,
			final int method, final String url, final Listener<T> listener,
			final ErrorListener errListener, final String jsonBody,
			final String elementsKey) {
		super(request, gson, method, url, listener, errListener, jsonBody);
		this.elementsKey = elementsKey;
	}

	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		try {
			final String headersCharset = HttpHeaderParser
					.parseCharset(response.headers);
			final String json = new String(response.data, headersCharset);
			final JSONObject object = new JSONObject(json);
			final JSONArray array = object.getJSONArray(elementsKey);
			final byte[] data = array.toString().getBytes(headersCharset);
			final NetworkResponse responseJsonArray = new NetworkResponse(data,
					response.headers);
			return super.parseNetworkResponse(responseJsonArray);
		} catch (final UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (final JSONException e) {
			return Response.error(new ParseError(e));
		}
	}

}
