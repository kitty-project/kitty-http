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

import java.net.URI;

/**
 * @author Julian Jupiter
 */
public record HttpRequestLine(HttpMethod method, URI target, HttpVersion version) {
    private static final String EMPTY_SPACE = " ";

    @Override
    public String toString() {
        return this.method + EMPTY_SPACE + this.target + EMPTY_SPACE + this.version;
    }
}
