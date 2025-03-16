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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * @author Julian Jupiter
 */
class DefaultHttpResponse extends DefaultHttpMessage<HttpResponse> implements HttpResponse {
    private static final HttpVersion DEFAULT_HTTP_VERSION = HttpVersion.HTTP_1_1;
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.OK;
    private static final String SET_COOKIE = "Set-Cookie";
    private HttpStatusLine statusLine = new HttpStatusLine(DEFAULT_HTTP_VERSION, DEFAULT_HTTP_STATUS);
    private final HttpSetCookies cookies = HttpSetCookies.create();
    private boolean next;

    public DefaultHttpResponse(List<HttpHeader> headers) {
        super(headers);
    }

    @Override
    public HttpStatusLine statusLine() {
        return this.statusLine;
    }

    @Override
    public HttpResponse status(int status) {
        this.statusLine = this.statusLine.status(HttpStatus.of(status));
        return this;
    }

    @Override
    public HttpResponse status(HttpStatus status) {
        this.statusLine = this.statusLine.status(status);
        return this;
    }

    @Override
    public List<HttpHeader> headers() {
        return super.headers.get();
    }

    @Override
    public HttpResponse header(String name, String value) {
        super.headers.add(new HttpHeader(name, Set.of(value)));
        return this;
    }

    @Override
    public HttpResponse header(HttpHeader httpHeader) {
        super.headers.add(httpHeader);
        return this;
    }

    @Override
    public HttpResponse headers(List<HttpHeader> headers) {
        super.headers.addAll(headers);
        return this;
    }

    @Override
    public List<HttpSetCookie> cookies() {
        return this.cookies.get();
    }

    @Override
    public HttpResponse cookie(String name, String value) {
        this.cookies.add(name, value);
        return this;
    }

    @Override
    public HttpResponse cookie(HttpSetCookie cookie) {
        this.cookies.add(cookie);
        return this;
    }

    @Override
    public HttpResponse cookies(List<HttpSetCookie> cookies) {
        this.cookies.addAll(cookies);
        return this;
    }

    @Override
    public HttpBody body() {
        return super.body;
    }

    @Override
    public HttpResponse body(String body) {
        super.body(new DefaultHttpBody(body));
        return this;
    }

    @Override
    public void next() {
        this.next = true;
    }

    @Override
    public String toString() {
        return switch (this.body) {
            case DefaultHttpBody defaultBody -> {
                if (!this.headers.contains("Content-type")) {
                    this.headers.add(new HttpHeader("Content-Type", "text/plain"));
                }
                this.headers.add(new HttpHeader("Content-Length", String.valueOf(this.contentLengthHeader(defaultBody))));
                if (this.cookies.toString().isBlank()) {
                    yield """
                            %s
                            %s

                            %s
                            """.formatted(this.statusLine, this.headers, defaultBody);
                } else {
                    yield """
                            %s
                            %s
                            %s

                            %s
                            """.formatted(this.statusLine, this.headers, this.cookies, defaultBody);
                }
            }
            case NoContentHttpBody noContentBody -> {
                this.headers.add(new HttpHeader("Content-Length", String.valueOf(0)));
                yield """
                        %s
                        %s
                        """.formatted(this.statusLine, this.headers);
            }
            default -> throw new IllegalArgumentException("Invalid HTTP body");
        };
    }

    private int contentLengthHeader(HttpBody body) {
        return body.toString().getBytes(StandardCharsets.UTF_8).length + 1;
    }
}
