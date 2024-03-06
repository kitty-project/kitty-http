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

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Julian Jupiter
 */
public enum HttpStatusSeries {
    INFORMATIONAL(1),
    SUCCESSFUL(2),
    REDIRECTION(3),
    CLIENT_ERROR(4),
    SERVER_ERROR(5);

    private final int value;

    HttpStatusSeries(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static HttpStatusSeries of(int statusCode) {
        return resolve(statusCode)
                .orElseThrow(() -> new IllegalArgumentException("No matching constant for [" + statusCode + "]"));
    }

    public static Optional<HttpStatusSeries> resolve(int statusCode) {
        int seriesCode = statusCode / 100;

        return Arrays.stream(values())
                .filter(series -> series.value == seriesCode)
                .findFirst();
    }
}
