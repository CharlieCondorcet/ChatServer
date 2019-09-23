
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

    private static final ArrayList<ChatMessage> MessagesInChatRoom = new ArrayList<>();
    private static int contMessages = 0;

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

            // One socket by request (try with resources).
            try {

                final Socket socket = serverSocket.accept();

                // The remote connection address.
                final InetAddress address = socket.getInetAddress();

                log.debug("========================================================================================");
                log.debug("Connection from {} in port {}.", address.getHostAddress(), socket.getPort());

                processConnection(socket);

            } catch (IOException e) {
                log.error("Error", e);
                throw e;
            }

        }

    }

    /**
     * Process the connection.
     *
     * @param socket to use as source of data.
     */
    private static void processConnection(final Socket socket) throws IOException {

        // Reading the inputstream
        final List<String> lines = readInputStreamByLines(socket);

        final String request = lines.get(0);
        log.debug("Request: {}", request);

        final PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.println("HTTP/1.1 200 OK");
        pw.println("Server: DSM v0.0.1");
        pw.println("Date: " + new Date());
        pw.println("Content-Type: text/html; charset=UTF-8");
        // pw.println("Content-Type: text/plain; charset=UTF-8");
        pw.println();
        //pw.println("<html><body>The Date: <strong>" + new Date() + "</strong><body></html>");


        pw.println("<html><body> <h2><font color=\"red\"> CHAT ROOM </font> </h2>  <p>allMessages:</p> ");
        pw.println("<textarea  readonly cols=\"160\" rows=\"20\" > ");

        for (int i = 0; i < MessagesInChatRoom.size(); i++) {
            pw.println(MessagesInChatRoom.get(i).PrintCompleteMessage());
        }

        pw.println("</textarea> <form method=\"POST\"> ");
        pw.println("<br> <hr> <br> Enter you Name:<input type=\"text\" name=\"myUserName\" style=\"margin-right: 20px\"> ");
        pw.println("Enter you Message:<input type=\"text\" name=\"myMessage\" size=\"50\" style=\"margin-right: 20px\"> ");
        pw.println("<input type=\"submit\" name=\"myButton\" value=\"Send to Everybody\"> ");
        pw.println("</form> <body></html>");

        pw.flush();

        log.debug("Process ended.");

    }

    /**
     * Read all the input stream.
     *
     * @param socket to use to read.
     * @return all the string readed.
     */
    private static List<String> readInputStreamByLines(final Socket socket) throws IOException {

        final InputStream is = socket.getInputStream();

        // The list of string readed from inputstream.
        final List<String> lines = new ArrayList<>();

        // The Scanner
        final Scanner s = new Scanner(is).useDelimiter("\\A");
        log.debug("Reading the Inputstream ..");

        while (true && s.hasNext()) {

            final String line = s.nextLine();
            // log.debug("Line: [{}].", line);

            if (line.length() == 0) {

                if (lines.get(0).equals("POST / HTTP/1.1")) {
                    String textByUser = s.nextLine();
                    FormatForMessage(textByUser);
                }

                break;
            } else {
                lines.add(line);
            }
        }
        // String result = s.hasNext() ? s.next() : "";

        // final List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
        return lines;

    }


    public static void FormatForMessage(String completeLine) {

        /**
         variables para guardar el nombre del nombre del usuario y mensaje del usuario respectivamente.
         */
        String nameUser = "", messageUser = "";

        nameUser += completeLine.substring(11, completeLine.indexOf("&myMessage="));
        messageUser += completeLine.substring(completeLine.indexOf("&myMessage=") + 11, completeLine.indexOf("&myButton="));

        String withoutAccent = Normalizer.normalize(nameUser, Normalizer.Form.NFD);
        nameUser = withoutAccent.replaceAll("[^a-zA-Z ]", " ");

        withoutAccent = Normalizer.normalize(messageUser, Normalizer.Form.NFD);
        messageUser = withoutAccent.replaceAll("[^a-zA-Z ]", " ");

        LocalDateTime timeStamp = LocalDateTime.now();

        ChatMessage newMessage = new ChatMessage(nameUser, messageUser, timeStamp);
        MessagesInChatRoom.add(newMessage);
        contMessages++;


    }


}
