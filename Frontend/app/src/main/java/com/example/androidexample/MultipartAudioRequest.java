package com.example.androidexample;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom request class for handling multipart/form-data requests with Volley,
 * specifically for sending audio files like voice messages.
 **/
public class MultipartAudioRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final Response.ErrorListener mErrorListener;
    private final byte[] mAudioData;
    private final String mBoundary = "apiclient-" + System.currentTimeMillis();
    private final String mLineEnd = "\r\n";
    private final String mTwoHyphens = "--";

    public MultipartAudioRequest(int method, String url, byte[] audioData,
                                 Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mAudioData = audioData;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + mBoundary;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Start with boundary
            dos.writeBytes(mTwoHyphens + mBoundary + mLineEnd);

            // Content-Disposition header for the audio file
            dos.writeBytes("Content-Disposition: form-data; name=\"audio\"; filename=\"" + "audio.m4a" + "\"" + mLineEnd);
            dos.writeBytes("Content-Type: audio/m4a" + mLineEnd);
            dos.writeBytes(mLineEnd);

            // Write audio data
            dos.write(mAudioData);

            // End of the multipart request
            dos.writeBytes(mLineEnd);
            dos.writeBytes(mTwoHyphens + mBoundary + mTwoHyphens + mLineEnd);

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    // Override getHeaders to add any custom headers if needed
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*"); // Ensures server accepts all responses
        return headers;
    }
}
