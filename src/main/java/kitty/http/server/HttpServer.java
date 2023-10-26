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

import kitty.http.message.HttpMethod;

import java.util.concurrent.Executor;

/**
 * @author Julian Jupiter
 */
public interface HttpServer {

    static HttpServer create() {
        return new KittyHttpServer(null);
    }

    static HttpServer create(String name) {
        return new KittyHttpServer(name);
    }

    HttpServer map(HttpMethod method, String path, HttpHandler httpHandler);

    HttpServer executor(Executor executor);

    void run();

    void run(int port);

    void run(Runnable runnable);

    void run(int port, Runnable runnable);
}
