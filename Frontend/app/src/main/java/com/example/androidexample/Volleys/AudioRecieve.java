package com.example.androidexample.Volleys;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom request class for sending text and receiving an audio file from Spring Boot.
 **/
public class AudioRecieve extends Request<byte[]> {

    private final Response.Listener<byte[]> mListener;
    private final Response.ErrorListener mErrorListener;
    private final String mRequestBody;

    public AudioRecieve(int method, String url, String requestBody,
                        Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mRequestBody = requestBody;
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";  // Expecting JSON request body
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
        } catch (Exception e) {
            throw new AuthFailureError("Failed to encode request body", e);
        }
    }

    /**
     * Parse Network Response - Handles audio/mpeg or application/octet-stream responses
     */
    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        try {
            // Log Response Info
            Log.d("Response Code", String.valueOf(response.statusCode));
            String contentType = response.headers.get("Content-Type");
            Log.d("Content-Type", contentType != null ? contentType : "No Content-Type");

            // Check if the response is a file download
            if ("application/octet-stream".equalsIgnoreCase(contentType) ||
                    "audio/mpeg".equalsIgnoreCase(contentType)) {
                return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
            }

            // If unexpected, return a parse error
            return Response.error(new ParseError(new Exception("Unexpected Content-Type: " + contentType)));

        } catch (Exception e) {
            Log.e("Parse Error", e.toString());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);  // Return audio data
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");  // Sending JSON request
        headers.put("Accept", "application/octet-stream, audio/mpeg");  // Expecting audio file
        return headers;
    }

    @Override
    public void deliverError(VolleyError error) {
        if (error.networkResponse != null) {
            String errorMessage = "Status Code: " + error.networkResponse.statusCode +
                    "\nResponse: " + new String(error.networkResponse.data) +
                    "\nHeaders: " + error.networkResponse.headers.toString();
            Log.e("Network Error", errorMessage);
        } else {
            Log.e("Network Error", "No response. Error: " + error.toString());
        }
        mErrorListener.onErrorResponse(error);  // Trigger error listener
    }
}
