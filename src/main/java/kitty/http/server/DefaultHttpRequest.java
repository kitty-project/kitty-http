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
package kitty.http.server;

import kitty.http.message.HttpBody;
import kitty.http.message.HttpHeader;
import kitty.http.message.HttpRequest;
import kitty.http.message.HttpRequestLine;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Julian Jupiter
 */
class DefaultHttpRequest extends DefaultHttpMessage<HttpRequest> implements HttpRequest {
    private final HttpRequestLine requestLine;

    DefaultHttpRequest(HttpRequestLine requestLine, List<HttpHeader> headers, HttpBody body) {
        super(headers, body);
        this.requestLine = requestLine;
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
