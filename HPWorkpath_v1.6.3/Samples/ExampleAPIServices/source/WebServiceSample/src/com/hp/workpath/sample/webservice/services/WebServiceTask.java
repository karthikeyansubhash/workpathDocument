package com.hp.workpath.sample.webservice.services;

import android.content.Context;
import android.util.Log;

import com.hp.workpath.api.webservices.AbstractWebServices;
import com.hp.workpath.api.webservices.HttpRequest;
import com.hp.workpath.api.webservices.HttpResponse;
import com.hp.workpath.sample.webservice.MainActivity;

import java.util.Map;
import java.util.Set;

public class WebServiceTask extends AbstractWebServices {
    public static final String TAG = MainActivity.TAG;
    public static final String X_Auth_Value = "123456";

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart()");
        callback = new WebServicesCallback();
    }

    static void setText(String method, String header, String body) {
        MainActivity.setRequest(method, header, body);
    }

    static class WebServicesCallback implements Callback {

        @Override
        public boolean authenticated(HttpRequest httpRequest) {
            String msg = "Request for auth : " + httpRequest.getHeader(HttpRequest.HeaderKey.X_AUTH);
            Log.d(TAG, msg);
            setText("authenticated", httpRequest.getHeaders().toString(), httpRequest.getBody());
            /* Perform custom logic to validate X-AUTH header value */
            if(X_Auth_Value.equals(httpRequest.getHeader(HttpRequest.HeaderKey.X_AUTH))){
                return true;
            }
            return false;
        }

        @Override
        public HttpResponse get(HttpRequest request) {
            Log.d(TAG, "Get request:" + request.getAttributes().toString());

            try {
                Thread.sleep(1000);
            } catch (Throwable throwable) {}

            setText("get", request.getHeaders().toString(), request.getBody());

            Log.d(TAG, "Get request:");

            HttpResponse response = new HttpResponse();
            Map<String, Object> attributes = request.getAttributes();
            Set<String> keys = attributes.keySet();
            for(String name: keys) {
                Log.d(TAG, name + ", " + request.getAttribute(name));
            }
            response.setStatusCode(200);
            response.setBody(MainActivity.getResponse());
            response.setStatusDescription("Error Message");
            return response;
        }

        @Override
        public HttpResponse post(HttpRequest httpRequest) {
            Log.d(TAG, "Post request:" + httpRequest.getAttributes().toString());

            try {
                Thread.sleep(5000);
            } catch (Throwable throwable) {}

            setText("post", httpRequest.getHeaders().toString(), httpRequest.getBody());
            Log.d(TAG, "Post request:");

            HttpResponse response = new HttpResponse();
            Map<String, Object> attributes = httpRequest.getAttributes();
            Set<String> keys = attributes.keySet();
            for(String name: keys) {
                Log.d(TAG, name + ", " + httpRequest.getAttribute(name));
            }

            response.setBody(MainActivity.getResponse());
            return response;
        }

        @Override
        public HttpResponse put(HttpRequest httpRequest) {
            Log.d(TAG, "Put request:" + httpRequest.getAttributes().toString());

            try {
                Thread.sleep(3000);
            } catch (Throwable throwable) {}

            setText("put", httpRequest.getHeaders().toString(), httpRequest.getBody());

            Log.d(TAG, "Put request:");

            HttpResponse response = new HttpResponse();
            Map<String, Object> attributes = httpRequest.getAttributes();
            Set<String> keys = attributes.keySet();
            for(String name: keys) {
                Log.d(TAG, name + ", " + httpRequest.getAttribute(name));
            }
            response.setBody(MainActivity.getResponse());
            return response;
        }

        @Override
        public HttpResponse delete(HttpRequest httpRequest) {
            Log.d(TAG, "Delete request:" + httpRequest.getAttributes().toString());

            try {
                Thread.sleep(1000);
            } catch (Throwable throwable) {}

            setText("delete", httpRequest.getHeaders().toString(), httpRequest.getBody());

            Log.d(TAG, "Delete request:");

            HttpResponse response = new HttpResponse();
            Map<String, Object> attributes = httpRequest.getAttributes();
            Set<String> keys = attributes.keySet();
            for(String name: keys) {
                Log.d(TAG, name + ", " + httpRequest.getAttribute(name));
            }

            response.setBody(MainActivity.getResponse());
            return response;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
