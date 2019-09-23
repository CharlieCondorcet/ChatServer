
/*
 *  Copyright (c) 2019. Charlie Condorcet http://www.charliec.cl .
 *
 *  All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Intellij IDEA
 * which accompanies this distribution, and is available at
 * https://resources.jetbrains.com/storage/products/appcode/docs/AppCode_Classroom_EULA.pdf .
 *
 */


import org.slf4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ChatServer {


    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger( ChatServer.class );


    private static final int PORT = 9000;

    private static final ArrayList<ChatThread> clientesActivos = new ArrayList<>();

    private static final List<ChatMessage> MessagesInChatRoom = new ArrayList<>();



    public static void main(final String[] args) throws IOException {


        log.debug("Starting the Main ..");

        final ServerSocket serverSocket = new ServerSocket(PORT);

        log.debug("Server started in port {}, waiting for connections ..", PORT);

        while (true) {

            try {

                final Socket socket = serverSocket.accept();

                ChatThread newClient = new ChatThread (socket, PORT, MessagesInChatRoom);
                newClient.start();

                clientesActivos.add(newClient);

                MessagesInChatRoom.add(newClient.SaveNewMessage());


            } catch (IOException e) {
                log.error("Error", e);
                throw e;
            }

        }




    }

}
