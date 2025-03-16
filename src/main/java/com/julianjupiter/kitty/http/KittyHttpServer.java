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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * @author Julian Jupiter
 */
final class KittyHttpServer implements HttpServer {
    private final System.Logger logger = System.getLogger(KittyHttpServer.class.getName());
    private final ServerConfiguration serverConfiguration;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$s] - %5$s %n");
    }

    public KittyHttpServer(HttpHandler handler, String name) {
        this.serverConfiguration = new ServerConfiguration(handler, this.createServerName(name));
    }

    @Override
    public HttpServer executorService(ExecutorService executorService) {
        this.serverConfiguration.executorService(executorService);
        return this;
    }

    @Override
    public HttpServer hostname(String hostname) {
        this.serverConfiguration.hostname(hostname);
        return this;
    }

    @Override
    public void start() {
        this.startServer();
    }

    @Override
    public void start(int port) {
        this.serverConfiguration.port(port);
        this.startServer();
    }

    @Override
    public void start(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        this.startServer();
    }

    @Override
    public void start(int port, Runnable runnable) {
        this.serverConfiguration.port(port);
        if (runnable != null) {
            runnable.run();
        }
        this.startServer();
    }

    private String createServerName(String name) {
        if (name == null || name.isBlank()) {
            return "kitty-http-server-" + UUID.randomUUID().toString().substring(0, 8);
        }

        return name;
    }

    private void startServer() {
        try (var executorService = this.serverConfiguration.executorService();
             var serverSocket = new ServerSocket()) {
            serverSocket.bind(this.inetSocketAddress());
            while (true) {
                var clientSocket = serverSocket.accept();
                var clientHandler = new ClientHandler(clientSocket, this.serverConfiguration.handler());
                if (executorService != null) {
                    executorService.execute(clientHandler);
                } else {
                    clientHandler.run();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            this.logger.log(System.Logger.Level.ERROR, exception.getMessage());
        }
    }

    private InetSocketAddress inetSocketAddress() {
        String hostname = this.serverConfiguration.hostname();
        if (hostname != null && !hostname.isBlank()) {
            return new InetSocketAddress(hostname, this.serverConfiguration.port());
        }

        return new InetSocketAddress(this.serverConfiguration.port());
    }
}
