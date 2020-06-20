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

public class ConnectionThread extends Thread {

    private int port;
    private ServerSocket serverSocket;
    private Socket socket;
    private ArrayList<ChatMessage> messages;

    /**
     * The Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ChatServer.class);

    public ConnectionThread(int port, ServerSocket serverSocket, Socket socket) {
        this.port = port;
        this.serverSocket = serverSocket;
        this.socket = socket;
        this.messages = new ArrayList<>();
    }

    @Override
    public void run() {

        try {
            // The remote connection address.
            final InetAddress address = socket.getInetAddress();

            log.debug("========================================================================================");
            log.debug("Connection from {} in port {}.", address.getHostAddress(), socket.getPort());

            processConnection(socket);

        } catch (Exception e) {
            log.error("Error not registered on thread: ", e);

        }

        log.debug("thread finalized");
    }


    /**
     * Process the connection.
     *
     * @param socket to use as source of data.
     */
    private void processConnection(final Socket socket) throws IOException {

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
        //pw.println("<html><body>The Date: <strong>" + new Date() + "</strong><body></html>");

        pw.println("<html>");
        //pw.println("<head>");
        //pw.println("    <title> Chat Room </title>");
        //pw.println("</head>");

        pw.println("<body>");
        pw.println("    <h2 style=\"color:Tomato;\"> CHAT ROOM </h2>");
        pw.println("    <p>allMessages:</p>");
        pw.println("    <textarea  readonly cols=\"160\" rows=\"20\" > ");


        pw.println("    </textarea> <form method=\"POST\"> ");
        pw.println("    <form action=\"#\">");
        pw.println("        <br> <hr> <br> ");
        pw.println("        <label for=\"myUserName\"> Enter you Name: </label> ");
        pw.println("        <input type=\"text\" name=\"myUserName\" style=\"margin-right: 20px\"> ");

        pw.println("        <label for=\"myMessage\"> Enter you Message: </label> ");
        pw.println("        <input type=\"text\" name=\"myMessage\" size=\"50\" style=\"margin-right: 20px\"> ");

        pw.println("        <input type=\"submit\" name=\"myButton\" value=\"Send\"> ");
        pw.println("    </form> ");
        pw.println("</body>");
        pw.println("</html>");

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
        //MessagesInChatRoom.add(newMessage);
        //contMessages++;


    }


}
