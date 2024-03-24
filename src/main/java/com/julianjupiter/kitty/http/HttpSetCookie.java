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

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Julian Jupiter
 */
public record HttpSetCookie(
        String name,
        String value,
        String domain,
        String path,
        OffsetDateTime expires,
        long maxAge,
        boolean httpOnly,
        boolean secure,
        SameSite sameSite,
        boolean partitioned) {
    public HttpSetCookie(String name, String value) {
        this(name, value, "localhost", "/", null, 0, false, true, SameSite.NONE, false);
    }

    public HttpSetCookie value(String value) {
        return new HttpSetCookie(this.name, value, this.domain, this.path, this.expires, this.maxAge, this.httpOnly, this.secure, this.sameSite, this.partitioned);
    }

    public HttpSetCookie domain(String domain) {
        return new HttpSetCookie(this.name, this.value, domain, this.path, this.expires, this.maxAge, this.httpOnly, this.secure, this.sameSite, this.partitioned);
    }

    public HttpSetCookie path(String path) {
        return new HttpSetCookie(this.name, this.value, this.domain, path, this.expires, this.maxAge, this.httpOnly, this.secure, this.sameSite, this.partitioned);
    }

    public HttpSetCookie expires(OffsetDateTime expires) {
        return new HttpSetCookie(this.name, this.value, this.domain, this.path, expires, this.maxAge, this.httpOnly, this.secure, this.sameSite, this.partitioned);
    }

    public HttpSetCookie maxAge(long maxAge) {
        return new HttpSetCookie(this.name, this.value, this.domain, this.path, this.expires, maxAge, this.httpOnly, this.secure, this.sameSite, this.partitioned);
    }

    public HttpSetCookie httpOnly(boolean httpOnly) {
        return new HttpSetCookie(this.name, this.value, this.domain, this.path, this.expires, this.maxAge, httpOnly, this.secure, this.sameSite, this.partitioned);
    }

    public HttpSetCookie secure(boolean secure) {
        return new HttpSetCookie(this.name, this.value, this.domain, this.path, this.expires, this.maxAge, this.httpOnly, secure, this.sameSite, this.partitioned);
    }

    public HttpSetCookie sameSite(SameSite sameSite) {
        return new HttpSetCookie(this.name, this.value, this.domain, this.path, this.expires, this.maxAge, this.httpOnly, this.secure, sameSite, this.partitioned);
    }

    public HttpSetCookie partitioned(boolean partitioned) {
        return new HttpSetCookie(this.name, this.value, this.domain, this.path, this.expires, this.maxAge, this.httpOnly, this.secure, this.sameSite, partitioned);
    }

    @Override
    public String toString() {
        var expiresNotNull = this.expires != null;
        return String.format("%s=%s; " +
                        "domain=%s; " +
                        "path=%s; " +
                        "%s" +
                        "maxAge=%d; " +
                        "size=%d; " +
                        "httpOnly=%b; " +
                        "secure=%b; " +
                        "sameSite=%s; " +
                        "partitioned=%b",
                this.name, this.value, this.domain, this.path,
                (expiresNotNull ? "expires=" + this.expires + "; " : ""),
                this.maxAge, this.value.getBytes().length, this.httpOnly,
                this.secure, this.sameSite, this.partitioned
        );
    }

    public enum SameSite {
        LAX("Lax"),
        STRICT("Strict"),
        NONE("None");

        private final String name;

        SameSite(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static SameSite of(String name) {
            return resolve(name)
                    .orElseThrow(() -> new IllegalArgumentException("No matching constant for [" + name + "]"));
        }

        public static Optional<SameSite> resolve(String name) {
            return Arrays.stream(values())
                    .filter(sameSite -> sameSite.name.equalsIgnoreCase(name))
                    .findFirst();
        }
    }
}