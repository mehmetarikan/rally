/*
 * Copyright (C) 2014 Zeplin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.zeplin.rally.network;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public abstract class CoreBaseRequest<T> {
    private CoreGsonRequest<T> mCoreGsonRequest;
    protected Map<String, String> mRequestParams = new HashMap<String, String>();
    /**
     * @return base url of the server
     */
    protected abstract String baseUrl();

    /**
     * GET, POST, PUT, DELETE
     * @return integer value of Http Method
     */
    protected abstract int httpMethod();

    /**
     * @return the url path that will be concatenated
     */
    protected abstract String path();

    /**
     * @return response class of the action
     */
    protected abstract Class<T> responseClass();

    /**
     * @return body of the action
     */
    protected String body() {
        return new GsonBuilder().create().toJson(mRequestParams);
    }

    /**
     * @return headers, unless any authorization is needed
     */
    protected abstract Map<String, String> getRequestHeaders();

    /**
     * @return response headers retrieved from {@link io.zeplin.rally.network.CoreGsonRequest}
     */
    public Map<String, String> getResponseHeaders() {
        return mCoreGsonRequest.getResponseHeaders();
    }

    /**
     * callback method for network responses
     */
    protected void onSuccess(T response) {
        // for rent
    }

    /**
     * callback method for network errors
     */
    protected void onError(VolleyError error) {
        // for rent
    }

    /**
     * @return listener back for network responses
     */
    private Response.Listener<T> successListener() {
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                onSuccess(response);
            }
        };
    }

    /**
     * @return listener back for error
     */
    private Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        };
    }

    /**
     * @return whole path of the URL
     */
    private String requestUrl() {

        String requestUrl = baseUrl() + path();

        if(httpMethod() == Request.Method.GET) {
            int cnt =0;
            for(String key:mRequestParams.keySet()) {
                if(cnt++ ==0 ) {
                    requestUrl +="?";
                } else {
                    requestUrl += "&";
                }
                requestUrl = requestUrl.concat(key).concat("=").concat(mRequestParams.get(key));
            }
        }
        return requestUrl;
    }

    /**
     * @return new built of Request class
     */
    public Request<T> create() {
        mCoreGsonRequest = new CoreGsonRequest<T>(
                httpMethod(),
                requestUrl(),
                responseClass(),
                getRequestHeaders(),
                successListener(),
                errorListener(),
                body()
        );

        return mCoreGsonRequest;
    }

    /**
     * Use {@link io.zeplin.rally.toolbox.MFieldNamingStrategy} to have an easier implementation and
     * follow the field naming conventions:
     * https://source.android.com/source/code-style.html#follow-field-naming-conventions
     *
     * @param fieldNamingStrategy custom field naming strategy.
     * @return new built of Request class
     */
    public Request<T> create(FieldNamingStrategy fieldNamingStrategy) {
        mCoreGsonRequest = new CoreGsonRequest<T>(
                httpMethod(),
                requestUrl(),
                responseClass(),
                getRequestHeaders(),
                successListener(),
                errorListener(),
                body(),
                fieldNamingStrategy
        );

        return mCoreGsonRequest;
    }
}
