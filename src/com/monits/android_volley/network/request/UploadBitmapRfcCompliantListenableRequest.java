package com.monits.android_volley.network.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class UploadBitmapRfcCompliantListenableRequest extends
		RfcCompliantListenableRequest<String> {

	private static final String BOUNDARY = "----------------------------1a4aa85bba8a";
	private static final String CONTENT_TYPE = "multipart/form-data; boundary=" + BOUNDARY;
	private static final String MULTIPART_HEAD = "--" + BOUNDARY + "\r\n"
			+ "Content-Disposition: form-data; name=\"%1$s\"; filename=\"%1$s\"\r\n"
			+ "Content-Type: image/jpeg\r\n\r\n";
	private static final String MULTIPART_TAIL = "\r\n--" + BOUNDARY + "--\r\n";
	
	
	private final Bitmap bitmap;
	private final String filename;
	
	public UploadBitmapRfcCompliantListenableRequest(final int method, final String url,
			final Listener<String> listener, final ErrorListener errListener, final Bitmap bmp, final String filename) {
		super(method, url, listener, errListener);
		
		bitmap = bmp;
		this.filename = filename;
		
		// 30 secs timeout, 1 reattempt, 45 secs timeout for the second attempt
		setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.5f));
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
	
	@Override
	public String getBodyContentType() {
		return CONTENT_TYPE;
	}
	
	@Override
	public byte[] getBody() throws AuthFailureError {
		if (bitmap == null) {
			return null;
		}
		
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bos.write(String.format(MULTIPART_HEAD, filename).getBytes());
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			bos.write(MULTIPART_TAIL.getBytes());
		} catch (final IOException e) {
			Log.wtf("UploadBitmapRfcCompliantListenableRequest", "Unexpected error building multipart body.", e);
		}
		
		return bos.toByteArray();
	}
}
