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

import kitty.http.message.HttpHeader;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Julian Jupiter
 */
final class HttpHeadersFactory {
    private HttpHeadersFactory() {
    }

    public static List<HttpHeader> create(String request) {
        return request.lines()
                .skip(1)
                .takeWhile(Predicate.not(String::isBlank))
                .map(line -> {
                    var headerSegments = line.split(":");
                    var name = headerSegments[0];
                    var values = Arrays.stream(headerSegments[1].split(";"))
                            .collect(Collectors.toSet());
                    return new HttpHeader(name, values);
                })
                .toList();
    }
}
