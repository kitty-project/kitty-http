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

import java.util.List;

/**
 * @author Julian Jupiter
 */
class DefaultHttpMessage<T extends HttpMessage> {
    protected final HttpHeaders headers;
    protected HttpBody body;

    DefaultHttpMessage(List<HttpHeader> headers) {
        this.headers = HttpHeaders.create(headers);
        this.body = new NoContentHttpBody();
    }

    DefaultHttpMessage(List<HttpHeader> headers, HttpBody body) {
        this.headers = HttpHeaders.create(headers);
        this.body = body;
    }

    protected T body(HttpBody body) {
        this.body = body;
        return (T) this;
    }
}
