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
package com.android.volley;

import android.support.annotation.NonNull;

import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Class to parse a list of all items in a collection from a NetworkResponse
 * depending on a key.
 *
 * @author fpredassi
 *
 * @param <T> The type of the request being decorated.
 */
public class JSONArrayRequestDecorator<T> extends RequestDecorator<T> {
	private final String elementsKey;

	/**
	 * Create a {@link RequestDecorator} for json array
	 *
	 * @param request The request to be decorated
	 * @param elementsKey The key name of the json array
	 */
	public JSONArrayRequestDecorator(@NonNull final Request<T> request, @NonNull final String elementsKey) {
		super(request);
		this.elementsKey = elementsKey;
	}

	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		try {
			final String headersCharset = HttpHeaderParser.parseCharset(response.headers);
			final String json = new String(response.data, headersCharset);
			final JSONObject object = new JSONObject(json);
			final JSONArray array = object.getJSONArray(elementsKey);
			final byte[] data = array.toString().getBytes(headersCharset);
			final NetworkResponse responseJsonArray = new NetworkResponse(data, response.headers);
			return super.parseNetworkResponse(responseJsonArray);
		} catch (final UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (final JSONException e) {
			return Response.error(new ParseError(e));
		}
	}

	@NonNull
	public String getElementsKey() {
		return elementsKey;
	}

	@Override
	public String toString() {
		return "JSONArrayRequestDecorator{"
				+ "elementsKey='" + elementsKey + '\''
				+ "} " + super.toString();
	}
}
