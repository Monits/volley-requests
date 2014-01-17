package com.monits.android_volley.network.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public abstract class UrlEncodedRfcCompliantListenableRequest<T> extends RfcCompliantListenableRequest<T> {

	private final Map<String, String> params;

	public UrlEncodedRfcCompliantListenableRequest(final int method, final String url,
			final Listener<T> listener, final ErrorListener errListener) {
		super(method, url, listener, errListener);
		this.params = new HashMap<String, String>();
	}

	public void addParameter(final String key, final String value) {
		params.put(key, value);
	}

	public void cleanParameters() {
		params.clear();
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return params;
	}

	@Override
	public String getUrl() {
		final String baseUrl = super.getUrl();
		if (getMethod() == Method.GET && !params.isEmpty()) {
			final StringBuilder url = new StringBuilder()
				.append(baseUrl)
				.append("?");

			for (final Entry<String, String> param : params.entrySet()) {
				url.append("&").append(param.getKey()).append("=").append(param.getValue());
			}

			return url.toString();

		}

		return baseUrl;
	}

	@Override
	public String getBodyContentType() {
	    return "application/x-www-form-urlencoded; charset=UTF-8";
	}
}
