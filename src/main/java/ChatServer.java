
/*
 * Copyright (c) 2020 Charlie Condorcet - Engineering Student.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class ChatServer {

    /**
     * The Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ChatServer.class);

    /**
     * The Port
     */
    private static final int PORT = 9000;

    private static ArrayList<ConnectionThread> hilos = new ArrayList<>();

    /**
     * The Ppal.
     */
    public static void main(final String[] args) throws IOException {

        log.debug("Starting the Main ..");

        // The Server Socket
        final ServerSocket serverSocket = new ServerSocket(PORT);

        // serverSocket.setReuseAddress(true);
        log.debug("Server started in port {}, waiting for connections ..", PORT);

        // Forever serve.
        while (true) {

            try {

                // Process to listen connections.
                final Socket socket = serverSocket.accept();

                // Threads to print web UI.
                ConnectionThread connectionThread = new ConnectionThread(PORT, serverSocket, socket);
                connectionThread.start();

                hilos.add(connectionThread);

                log.debug("estado hilo: "+ connectionThread.getState().toString());

            } catch (Exception e) {
                log.error("Error not registered: ", e);
                throw e;
            }

            log.debug("One thread more created.");

        }
    }

}