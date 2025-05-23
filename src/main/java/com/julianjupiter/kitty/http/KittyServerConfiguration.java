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

import java.util.concurrent.ExecutorService;

/**
 * @author Julian Jupiter
 */
class KittyServerConfiguration implements ServerConfiguration {
    private final String name;
    private String hostname;
    private int port = DEFAULT_PORT;
    private final HttpHandler handler;
    private ExecutorService executorService = DEFAULT_EXECUTOR_SERVICE;
    private int bufferCapacity = DEFAULT_BUFFER_CAPACITY;

    public KittyServerConfiguration(HttpHandler handler, String name) {
        this.handler = handler;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public String hostname() {
        return hostname;
    }

    public KittyServerConfiguration hostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    @Override
    public int defaultPort() {
        return DEFAULT_PORT;
    }

    public int port() {
        return port;
    }

    public KittyServerConfiguration port(int port) {
        this.port = port;
        return this;
    }

    public HttpHandler handler() {
        return handler;
    }

    public ExecutorService executorService() {
        return executorService;
    }

    public KittyServerConfiguration executorService(ExecutorService executorService) {
        if (executorService != null) {
            this.executorService = executorService;
        }

        return this;
    }

    public int bufferCapacity() {
        return bufferCapacity;
    }

    public KittyServerConfiguration bufferCapacity(int bufferCapacity) {
        this.bufferCapacity = bufferCapacity;
        return this;
    }
}
