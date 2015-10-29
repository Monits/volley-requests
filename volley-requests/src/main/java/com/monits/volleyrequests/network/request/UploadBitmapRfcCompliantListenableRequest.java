/**
 * Copyright 2010 - 2015 Monits S.A.
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

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Implementation of {@link RfcCompliantListenableRequest} used to upload
 * JPEG images as Multipart files.
 * 
 * The files are uploaded in the same size and resolution as provided,
 * any scaling / quality processing should be done ahead by the developer.
 */
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

	/**
	 * Creates a new UploadBitmapRfcCompliantListenableRequest request.
	 * 
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param cancelListener The listener for errors
	 * @param bmp The bitmap to be uploaded.
	 * @param filename The filename under which to submit the image.
	 */

	@SuppressWarnings("checkstyle:magicnumber")
	public UploadBitmapRfcCompliantListenableRequest(final int method, @NonNull final String url,
						@Nullable final Listener<String> listener,
						@Nullable final ErrorListener errListener,
						@Nullable final CancelListener cancelListener,
						@NonNull final Bitmap bmp, @NonNull final String filename) {
		super(method, url, listener, errListener, cancelListener);

		bitmap = bmp;
		this.filename = filename;

		/*
		 * Default Volley timeout is too low for most images in slow networks.
		 * 30 secs timeout, 1 reattempt, 45 secs timeout for the second attempt
		 */
		setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.5f));
	}
	/**
	 * Creates a new UploadBitmapRfcCompliantListenableRequest request with
	 * less parameters for backwards compatibility
	 *
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param bmp The bitmap to be uploaded.
	 * @param filename The filename under which to submit the image.
	 */
	public UploadBitmapRfcCompliantListenableRequest(final int method, @NonNull final String url,
					@Nullable final Listener<String> listener,
					@Nullable final ErrorListener errListener,
					@NonNull final Bitmap bmp, @NonNull final String filename) {
		this(method, url, listener, errListener, null, bmp, filename);
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

	@SuppressWarnings("checkstyle:magicnumber")
	@SuppressFBWarnings(value = { "DM_DEFAULT_ENCODING", "MDM_STRING_BYTES_ENCODING",
					"VA_FORMAT_STRING_USES_NEWLINE" },
					justification = "The encoding will be sent with the headers automatically."
					+ " The protocol requires \\r\\n, independently on the platform")
	@Override
	public byte[] getBody() throws AuthFailureError {
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

	@Override
	public String toString() {
		return "UploadBitmapRfcCompliantListenableRequest{ "
				+ "bitmap=" + bitmap
				+ ", filename='" + filename + '\''
				+ " }";
	}
}
