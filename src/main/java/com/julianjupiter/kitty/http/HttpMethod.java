/*
 * Copyright 2013-2023 Julian Jupiter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.julianjupiter.kitty.http;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Julian Jupiter
 */
public final class HttpMethod {
    public static final HttpMethod DELETE = new HttpMethod("DELETE");
    public static final HttpMethod GET = new HttpMethod("GET");
    public static final HttpMethod HEAD = new HttpMethod("HEAD");
    public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
    public static final HttpMethod PATCH = new HttpMethod("PATCH");
    public static final HttpMethod POST = new HttpMethod("POST");
    public static final HttpMethod PUT = new HttpMethod("PUT");
    public static final HttpMethod QUERY = new HttpMethod("QUERY");
    public static final HttpMethod TRACE = new HttpMethod("TRACE");

    private final String value;
    private static final Map<String, HttpMethod> values = new HashMap<>();

    static {
        var delete = DELETE;
        var get = GET;
        var head = HEAD;
        var options = OPTIONS;
        var patch = PATCH;
        var post = POST;
        var put = PUT;
        var query = QUERY;
        var trace = TRACE;
        values.put(delete.value, delete);
        values.put(get.value, get);
        values.put(head.value, head);
        values.put(options.value, options);
        values.put(patch.value, patch);
        values.put(post.value, post);
        values.put(put.value, put);
        values.put(query.value, query);
        values.put(trace.value, trace);
    }

    private HttpMethod(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static HttpMethod of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("HTTP method cannot be null or blank");
        }

        value = value.toUpperCase(Locale.ROOT);
        if (values.containsKey(value)) {
            return values.get(value);
        } else {
            var newHttpMethod = new HttpMethod(value);
            values.put(newHttpMethod.value, newHttpMethod);
            return newHttpMethod;
        }
    }
}
