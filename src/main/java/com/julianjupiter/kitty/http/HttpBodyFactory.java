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

import java.util.ArrayList;

/**
 * @author Julian Jupiter
 */
final class HttpBodyFactory {
    private HttpBodyFactory() {
    }

    public static HttpBody create(String request) {
        var lines = request.lines().toArray(String[]::new);
        var length = lines.length;
        var bodyStarted = false;
        var list = new ArrayList<String>();
        for (var i = 1; i < length; i++) {
            var line = lines[i];
            if (!bodyStarted) {
                if (line.isBlank()) {
                    bodyStarted = true;
                }
                continue;
            }

            list.add(line);
        }

        if (list.isEmpty()) {
            return new EmptyHttpBody();
        }

        return new DefaultHttpBody(String.join("\n", list));
    }
}
