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

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Julian Jupiter
 */
final class HttpRequestFactory {
    private HttpRequestFactory() {
    }

    public static HttpRequest create(BufferedReader reader) {
        try {
            var sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    break;
                }

                sb.append(line).append("\n");
            }

            var request = sb.toString();
            var httpRequestLine = HttpRequestLineFactory.create(request);
            var httpHeaders = HttpHeadersFactory.create(request);
            var httpCookies = HttpCookiesFactory.create(httpHeaders);
            var httpBody = HttpBodyFactory.create(request);

            return new DefaultHttpRequest(httpRequestLine, httpHeaders, httpCookies, httpBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
