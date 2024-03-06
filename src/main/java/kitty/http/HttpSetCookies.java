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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Julian Jupiter
 */
class HttpSetCookies {
    private final Map<String, HttpSetCookie> cookies = new HashMap<>();

    private HttpSetCookies() {
    }

    public static HttpSetCookies create() {
        return new HttpSetCookies();
    }

    public int size() {
        return this.cookies.size();
    }

    public boolean isEmpty() {
        return this.cookies.isEmpty();
    }


    public boolean contains(String name) {
        return this.cookies.containsKey(normalize(name));
    }

    public List<HttpSetCookie> get() {
        return this.cookies.values().stream()
                .toList();
    }

    public Optional<HttpSetCookie> get(String name) {
        return Optional.ofNullable(this.cookies.get(normalize(name)));
    }

    public HttpSetCookie getOrDefault(String name, String defaultValue) {
        return this.cookies.getOrDefault(name, new HttpSetCookie(normalize(name), defaultValue));
    }

    public HttpSetCookies add(String name, String value) {
        checkValue(value);
        this.cookies.put(normalize(name), new HttpSetCookie(normalize(name), value));
        return this;
    }

    public HttpSetCookies add(HttpSetCookie cookie) {
        checkValue(cookie.value());
        this.cookies.put(normalize(cookie.name()), new HttpSetCookie(normalize(cookie.name()), cookie.value()));
        return this;
    }

    public HttpSetCookies addAll(List<HttpSetCookie> cookies) {
        cookies.forEach(this::add);
        return this;
    }

    public HttpSetCookies replace(String name, String newValue) {
        if (contains(name)) {
            checkValue(newValue);
            var cookie = this.cookies.get(name)
                    .value(newValue);
            this.cookies.replace(normalize(name), cookie);
        }

        return this;
    }

    public HttpSetCookies remove(String name) {
        this.cookies.remove(normalize(name));
        return this;
    }

    public HttpSetCookies clear() {
        this.cookies.clear();
        return this;
    }

    @Override
    public String toString() {
        return this.cookies.values().stream()
                .map(cookie -> "Set-Cookie: " + cookie)
                .collect(Collectors.joining("\n"));
    }

    private String normalize(String name) {
        Objects.requireNonNull(name);

        var length = name.length();
        if (length == 0) {
            return name;
        }

        var c = name.toCharArray();
        for (var i = 0; i < length; i++) {
            if (c[i] == '\r' || c[i] == '\n') {
                throw new IllegalArgumentException("Illegal character in name");
            }
        }

        return new String(c);
    }

    private static void checkValue(String value) {
        var length = value.length();
        for (var i = 0; i < length; i++) {
            var c = value.charAt(i);
            if (c == '\r') {
                // is allowed if it is followed by \n and a whitespace char
                if (i >= (length - 2)) {
                    throw new IllegalArgumentException("Illegal CR found in header");
                }

                var c1 = value.charAt(i + 1);
                var c2 = value.charAt(i + 2);
                if (c1 != '\n') {
                    throw new IllegalArgumentException("Illegal character found after CR in header");
                }

                if (c2 != ' ' && c2 != '\t') {
                    throw new IllegalArgumentException("No whitespace found after CRLF in header");
                }

                i += 2;
            } else if (c == '\n') {
                throw new IllegalArgumentException("Illegal LF found in header");
            }
        }
    }
}
