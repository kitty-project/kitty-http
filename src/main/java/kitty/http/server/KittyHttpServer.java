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

import kitty.http.message.HttpContext;
import kitty.http.message.HttpMethod;
import kitty.http.message.HttpResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * @author Julian Jupiter
 */
final class KittyHttpServer implements HttpServer {
    private final System.Logger logger = System.getLogger(KittyHttpServer.class.getName());
    private static final int DEFAULT_PORT = 8080;
    private static final int BUFFER_CAPACITY = 8192;
    private final String serveName;
    private int port = DEFAULT_PORT;
    private Executor executor;
    private final Map<URI, String> routeNames = HashMap.newHashMap(32);
    private final Map<String, HttpHandler> routeHandlers = HashMap.newHashMap(32);
    private final Map<String, Set<HttpMethod>> routeMethods = HashMap.newHashMap(32);
    private HttpContext httpContext;

    public KittyHttpServer(String name) {
        if (name == null || name.isBlank()) {
            this.serveName = "kitty-http-server-" + UUID.randomUUID().toString().substring(0, 8);
        } else {
            this.serveName = name;
        }
    }

    @Override
    public HttpServer map(HttpMethod method, String path, HttpHandler httpHandler) {
        var handlerName = UUID.randomUUID().toString().substring(0, 8);
        this.routeNames.putIfAbsent(URI.create(path), handlerName);
        this.routeHandlers.computeIfAbsent(handlerName, handler -> httpHandler);
        this.routeMethods.computeIfAbsent(handlerName, name -> {
            Set<HttpMethod> value = HashSet.newHashSet(1);
            value.add(method);
            return value;
        });
        this.routeMethods.computeIfPresent(handlerName, (name, value) -> {
            value.add(method);
            return value;
        });
        return this;
    }

    @Override
    public HttpServer executor(Executor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public void start() {
        this.startServer();
    }

    @Override
    public void start(int port) {
        this.port = port;
        this.startServer();
    }

    @Override
    public void start(Runnable runnable) {
        runnable.run();
        this.startServer();
    }

    @Override
    public void start(int port, Runnable runnable) {
        this.port = port;
        runnable.run();
        this.startServer();
    }

    private void startServer() {
        try (var selector = Selector.open();
             var serverSocketChannel = ServerSocketChannel.open()) {
            if (serverSocketChannel.isOpen() && selector.isOpen()) {
                serverSocketChannel.bind(new InetSocketAddress(this.port));
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, BUFFER_CAPACITY * 256);
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                this.logger.log(System.Logger.Level.INFO, "HTTP server name: " + this.serveName);
                this.process(selector);
            } else {
                logger.log(System.Logger.Level.INFO, "The server socket channel or selector cannot be opened!");
            }
        } catch (IOException exception) {
            logger.log(System.Logger.Level.ERROR, exception);
        }
    }

    private void process(Selector selector) throws IOException {
        while (true) {
            selector.select(1000);
            var selectedKeys = selector.selectedKeys();
            var keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                var selectionKey = keyIterator.next();
                keyIterator.remove();

                if (!selectionKey.isValid()) {
                    continue;
                }

                if (selectionKey.isAcceptable()) {
                    this.accept(selector, selectionKey);
                } else if (selectionKey.isReadable()) {
                    if (executor != null) {
                        this.executor.execute(() -> this.read(selectionKey));
                    } else {
                        this.read(selectionKey);
                    }
                } else if (selectionKey.isWritable()) {
                    if (executor != null) {
                        this.executor.execute(() -> this.write(selectionKey));
                    } else {
                        this.write(selectionKey);
                    }
                }
            }
        }
    }

    private void accept(Selector selector, SelectionKey selectionKey) {
        var serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        try {
            var socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException exception) {
            logger.log(System.Logger.Level.ERROR, exception);
        }
    }

    private void read(SelectionKey selectionKey) {
        var socketChannel = (SocketChannel) selectionKey.channel();

        try {
            var clientRequest = this.readRaw(socketChannel);
            var httpRequestLine = HttpRequestLineFactory.create(clientRequest);
            var httpHeaders = HttpHeadersFactory.create(clientRequest);
            var httpCookies = HttpCookiesFactory.create(httpHeaders);
            var httpBody = HttpBodyFactory.create(clientRequest);
            var httpRequest = HttpRequestFactory.create(httpRequestLine, httpHeaders, httpCookies, httpBody);
            this.httpContext = HttpContextFactory.create(httpRequest, new DefaultHttpResponse());
        } catch (IOException exception) {
            logger.log(System.Logger.Level.ERROR, exception);
        }

        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    private String readRaw(ReadableByteChannel channel) throws IOException {
        var readBuffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        var sb = new StringBuilder();
        readBuffer.clear();
        int read;
        while ((read = channel.read(readBuffer)) > 0) {
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.limit()];
            readBuffer.get(bytes);
            sb.append(new String(bytes, StandardCharsets.UTF_8));
            readBuffer.clear();
        }

        if (read < 0) {
            throw new IOException("End of input stream. Connection is closed by the client");
        }

        return sb.toString();
    }

    private void write(SelectionKey selectionKey) {
        var socketChannel = (SocketChannel) selectionKey.channel();
        HttpHandler handler = new NotFoundHttpHandler();
        var requestLine = this.httpContext.request().requestLine();
        var requestMethod = requestLine.method();
        var requestPath = requestLine.target().getPath();
        var nameOptional = this.routeNames.entrySet().stream()
                .filter(entry -> entry.getKey().toString().equals(requestPath))
                .map(Map.Entry::getValue)
                .findFirst();
        if (nameOptional.isPresent()) {
            // Handler path matches to the current request path
            var name = nameOptional.get();
            var methods = routeMethods.get(name);
            if (methods.contains(requestMethod)) {
                // handler path and method match to the current request path and method
                handler = this.routeHandlers.get(name);
            } else {
                // no handler method matches to the current request method
                handler = new MethodNotAllowedHttpHandler();
            }
        }
        var response = handler.handle(this.httpContext);
        this.createResponse(socketChannel, response);
    }

    private void createResponse(SocketChannel socketChannel, HttpResponse response) {
        try {
            var responseBuffer = ByteBuffer.wrap(response.toString().getBytes(StandardCharsets.UTF_8));
            socketChannel.write(responseBuffer);
            socketChannel.close();
        } catch (IOException exception) {
            logger.log(System.Logger.Level.ERROR, exception);
        }
    }
}
