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
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * @author Julian Jupiter
 */
final class KittyHttpServer implements HttpServer {
    private final System.Logger logger = System.getLogger(KittyHttpServer.class.getName());
    private final ServerConfiguration serverConfiguration;
    private static volatile boolean running = true;
    private Executor executor;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$s] %5$s %n");
    }

    public KittyHttpServer(HttpHandler handler, String name) {
        this.serverConfiguration = new ServerConfiguration(handler, this.createServerName(name));
    }

    @Override
    public HttpServer executor(Executor executor) {
        if (executor != null) {
            this.executor = executor;
        }
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
        try (var selector = Selector.open();
             var serverSocketChannel = ServerSocketChannel.open()) {
            if (serverSocketChannel.isOpen() && selector.isOpen()) {
                serverSocketChannel.bind(this.inetSocketAddress());
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, this.serverConfiguration.bufferCapacity() * 256);
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                this.logger.log(System.Logger.Level.INFO, "HTTP server name: " + this.serverConfiguration.name());
                var socket = serverSocketChannel.socket();
                this.logger.log(System.Logger.Level.INFO, "HTTP server address: " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
                this.process(selector);
            } else {
                this.logger.log(System.Logger.Level.ERROR, "The server socket channel or selector cannot be opened!");
            }
        } catch (IOException exception) {
            this.logger.log(System.Logger.Level.ERROR, exception);
        }
    }

    private InetSocketAddress inetSocketAddress() {
        String hostname = this.serverConfiguration.hostname();
        if (hostname != null && !hostname.isBlank()) {
            return new InetSocketAddress(hostname, this.serverConfiguration.port());
        }

        return new InetSocketAddress(this.serverConfiguration.port());
    }

    private void process(Selector selector) throws IOException {
        while (running) {
            if (selector.select() == 0) {
                continue;
            }

            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey selectionKey = selectedKeys.next();
                if (!selectionKey.isValid()) {
                    continue;
                }

                if (selectionKey.isAcceptable()) {
                    this.accept(selector, selectionKey);
                } else if (selectionKey.isReadable()) {
                    this.ifReadable(selector, selectionKey);
                } else if (selectionKey.isWritable()) {
                    this.ifWritable(selectionKey);
                }

                // This is necessary to prevent the same key from coming up again the next time around.
                selectedKeys.remove();
            }
        }
    }

    private void ifReadable(Selector selector, SelectionKey selectionKey) throws IOException {
        if (this.executor != null) {
            this.executor.execute(() -> {
                try {
                    this.read(selector, selectionKey);
                } catch (IOException exception) {
                    this.logger.log(System.Logger.Level.ERROR, exception.getMessage());
                }
            });
        } else {
            this.read(selector, selectionKey);
        }
    }

    private void ifWritable(SelectionKey selectionKey) {
        if (this.executor != null) {
            this.executor.execute(() -> this.write(selectionKey));
        } else {
            this.write(selectionKey);
        }
    }

    private void accept(Selector selector, SelectionKey selectionKey) {
        var serverChannel = (ServerSocketChannel) selectionKey.channel();

        try {
            var socketChannel = serverChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            // Register channel with selector for further IO
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException exception) {
            this.logger.log(System.Logger.Level.ERROR, exception);
        }
    }

    private void read(Selector selector, SelectionKey selectionKey) throws IOException {
        var clientChannel = (SocketChannel) selectionKey.channel();
        var byteBuffer = ByteBuffer.allocate(this.serverConfiguration.bufferCapacity());
        int bytesRead = clientChannel.read(byteBuffer);
        if (bytesRead == -1) {
            clientChannel.close();
            selectionKey.cancel();
        } else if (bytesRead > 0) {
            byteBuffer.flip();
            byte[] data = new byte[byteBuffer.remaining()];
            byteBuffer.get(data);
            var request = this.createRequest(new String(data, StandardCharsets.UTF_8));
            clientChannel.register(selector, SelectionKey.OP_WRITE, request);
        }
    }

    private void write(SelectionKey selectionKey) {
        var request = (HttpRequest) selectionKey.attachment();
        var response = new DefaultHttpResponse(HttpHeadersFactory.create());
        var finalResponse = this.serverConfiguration.handler().handle(request, response);
        this.writeResponse(selectionKey, finalResponse);
    }

    private HttpRequest createRequest(String clientRequest) {
        var httpRequestLine = HttpRequestLineFactory.create(clientRequest);
        var httpHeaders = HttpHeadersFactory.create(clientRequest);
        var httpCookies = HttpCookiesFactory.create(httpHeaders);
        var httpBody = HttpBodyFactory.create(clientRequest);

        return HttpRequestFactory.create(httpRequestLine, httpHeaders, httpCookies, httpBody);
    }

    private void writeResponse(SelectionKey selectionKey, HttpResponse response) {
        var clientChannel = (SocketChannel) selectionKey.channel();

        try {
            var responseBuffer = ByteBuffer.wrap(response.toString().getBytes(StandardCharsets.UTF_8));
            clientChannel.write(responseBuffer);
            if (!responseBuffer.hasRemaining()) {
                clientChannel.close();
                selectionKey.cancel();
            }
        } catch (IOException exception) {
            this.logger.log(System.Logger.Level.ERROR, exception);
        }
    }
}
