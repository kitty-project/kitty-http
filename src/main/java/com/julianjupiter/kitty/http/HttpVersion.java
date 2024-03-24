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

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Julian Jupiter
 */
public enum HttpVersion {
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2"),
    HTTP_3("HTTP/3");

    public final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static HttpVersion of(String version) {
        return resolve(version)
                .orElseThrow(() -> new IllegalArgumentException("No matching constant for [" + version + "]"));
    }

    public static Optional<HttpVersion> resolve(String version) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.value.equals(version))
                .findFirst();
    }
}
