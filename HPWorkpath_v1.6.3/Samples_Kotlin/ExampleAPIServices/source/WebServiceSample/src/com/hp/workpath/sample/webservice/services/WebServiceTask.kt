package com.hp.workpath.sample.webservice.services

import android.util.Log
import com.hp.workpath.api.webservices.AbstractWebServices
import com.hp.workpath.api.webservices.HttpRequest
import com.hp.workpath.api.webservices.HttpResponse
import com.hp.workpath.sample.webservice.MainActivity

class WebServiceTask : AbstractWebServices() {
    override fun onStart() {
        Log.d(TAG, "onStart()")
        callback = WebServicesCallback()
    }

    companion object {
        const val TAG = MainActivity.TAG
        const val X_Auth_Value="123456"

        fun setText(method : String, header : String, body : String) {
            MainActivity.setText(method, header, body);
        }
    }

    internal class WebServicesCallback : Callback {
        override fun authenticated(httpRequest: HttpRequest): Boolean {
            Log.d(TAG, "Request for auth:" + httpRequest.getHeader(HttpRequest.HeaderKey.X_AUTH))
            setText("authenticated", httpRequest.headers.toString(), httpRequest.body)
            /* Perform custom logic to validate X-AUTH header value*/
            if(X_Auth_Value.equals(httpRequest.getHeader(HttpRequest.HeaderKey.X_AUTH))){
                return true
            }
            return false
        }

        override fun get(request: HttpRequest): HttpResponse {
            Log.d(TAG, "Get request:" + request.attributes.toString())
            try {
                Thread.sleep(1000)
            } catch (throwable: Throwable) {
            }
            setText("get", request.headers.toString(), request.body)

            Log.d(TAG, "Get request:")
            val response = HttpResponse()
            val attributes = request.attributes
            val keys: Set<String> = attributes.keys
            for (name in keys) {
                Log.d(TAG, name + ", " + request.getAttribute(name))
            }
            response.statusCode = 200
            response.body = MainActivity.getResponse()
            response.statusDescription = "Error Message"
            return response
        }

        override fun post(httpRequest: HttpRequest): HttpResponse {
            Log.d(TAG, "Post request:" + httpRequest.attributes.toString())
            try {
                Thread.sleep(5000)
            } catch (throwable: Throwable) {
            }

            setText("post", httpRequest.headers.toString(), httpRequest.body)

            Log.d(TAG, "Post request:")
            val response = HttpResponse()
            val attributes = httpRequest.attributes
            val keys: Set<String> = attributes.keys
            for (name in keys) {
                Log.d(TAG, name + ", " + httpRequest.getAttribute(name))
            }
            response.body = MainActivity.getResponse()
            return response
        }

        override fun put(httpRequest: HttpRequest): HttpResponse {
            Log.d(TAG, "Put request:" + httpRequest.attributes.toString())
            try {
                Thread.sleep(3000)
            } catch (throwable: Throwable) {
            }
            setText("put", httpRequest.headers.toString(), httpRequest.body)

            Log.d(TAG, "Put request:")
            val response = HttpResponse()
            val attributes = httpRequest.attributes
            val keys: Set<String> = attributes.keys
            for (name in keys) {
                Log.d(TAG, name + ", " + httpRequest.getAttribute(name))
            }
            response.body = MainActivity.getResponse()
            return response
        }

        override fun delete(httpRequest: HttpRequest): HttpResponse {
            Log.d(TAG, "Delete request:" + httpRequest.attributes.toString())
            try {
                Thread.sleep(1000)
            } catch (throwable: Throwable) {
            }

            setText("delete", httpRequest.headers.toString(), httpRequest.body)

            Log.d(TAG, "Delete request:")
            val response = HttpResponse()
            val attributes = httpRequest.attributes
            val keys: Set<String> = attributes.keys
            for (name in keys) {
                Log.d(TAG, name + ", " + httpRequest.getAttribute(name))
            }
            response.body = MainActivity.getResponse()
            return response
        }
    }
}