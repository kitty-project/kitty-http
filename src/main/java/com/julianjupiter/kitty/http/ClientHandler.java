package com.julianjupiter.kitty.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Julian Jupiter
 */
class ClientHandler implements Runnable {
    private final System.Logger logger = System.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private final HttpHandler handler;

    ClientHandler(Socket clientSocket, HttpHandler handler) {
        this.clientSocket = clientSocket;
        this.handler = handler;
    }

    @Override
    public void run() {
        try (clientSocket;
             var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             var writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            var response = this.handler.handle(
                    HttpRequestFactory.create(reader),
                    new DefaultHttpResponse(HttpHeadersFactory.create())
            );
            writer.write(response.toString());
        } catch (IOException exception) {
            this.logger.log(System.Logger.Level.ERROR, exception.getMessage());
        }
    }

}
