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
package kitty.http;

import java.util.List;
import java.util.Optional;

/**
 * @author Julian Jupiter
 */
class DefaultHttpRequest extends DefaultHttpMessage<HttpRequest> implements HttpRequest {
    private final HttpRequestLine requestLine;
    private final List<HttpCookie> cookies;

    DefaultHttpRequest(HttpRequestLine requestLine, List<HttpHeader> headers, List<HttpCookie> cookies, HttpBody body) {
        super(headers, body);
        this.requestLine = requestLine;
        this.cookies = cookies;
    }

    @Override
    public HttpRequestLine requestLine() {
        return this.requestLine;
    }

    @Override
    public List<HttpHeader> headers() {
        return super.headers.get();
    }

    @Override
    public List<HttpCookie> cookies() {
        return this.cookies;
    }

    @Override
    public Optional<HttpCookie> cookie(String name) {
        return this.cookies.stream()
                .filter(cookie -> cookie.name().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public HttpBody body() {
        return super.body;
    }

    @Override
    public String toString() {
        return """
                %s
                %s
                
                %s
                """.formatted(this.requestLine, this.headers, this.body);
    }
}
