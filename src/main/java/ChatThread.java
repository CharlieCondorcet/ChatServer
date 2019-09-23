/*
 *  Copyright (c) 2019. Charlie Condorcet http://www.charliec.cl .
 *
 *  All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Intellij IDEA
 * which accompanies this distribution, and is available at
 * https://resources.jetbrains.com/storage/products/appcode/docs/AppCode_Classroom_EULA.pdf .
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ChatThread extends Thread{


    private final Socket client;
    private final int PORT;
    private final InetAddress IPClient;
    private final Logger log = LoggerFactory.getLogger(ThreadClient.class);


    private ChatMessage newMessage = new ChatMessage("face", "fake", null);

    private List<ChatMessage> MessagesInChatRoom;


    public ChatThread(Socket client, int PORT, List<ChatMessage> MessagesInChatRoom) {
        this.client = client;
        this.PORT = PORT;
        this.IPClient = client.getInetAddress();
        this.MessagesInChatRoom = MessagesInChatRoom;
    }


    @Override
    public void run() {

        log.debug("========================================================================================");
        log.debug("Connection from {} in port {}.", this.IPClient.getHostAddress(), client.getPort());

        try {
            processConnection();

        } catch (IOException e) {
            log.debug("errores de mensajes entrantes 101");
            e.printStackTrace();
        }

        return;

    }


    private void processConnection() throws IOException {

        final List<String> allLinesReceived = readInputStreamByLines();

        if (allLinesReceived.size() > 0) {
            final String request = allLinesReceived.get(0);
            log.debug("Request: {}", request);
        } else {
            log.debug("Request not complete, bad connection with client");
        }

        final PrintWriter pw = new PrintWriter(client.getOutputStream());
        pw.println("HTTP/1.1 200 OK");
        pw.println("Server: DSM v0.0.1");
        pw.println("Date: " + new Date());
        pw.println("Content-Type: text/html; charset=UTF-8");
        pw.println();
        //pw.println("<html><body>The Date: <strong>" + new Date() + "</strong><body></html>");

        pw.println("<html><body> <h2>CHAT ROOM </h2>  <p>allMessages:</p> ");
        pw.println("<textarea  cols=\"160\" rows=\"20\"> ");

        for (int i = 0; i < this.MessagesInChatRoom.size(); i++) {
            pw.println("<p>" + this.MessagesInChatRoom.get(i).PrintCompleteMessage() + "</p>");
            pw.println(" nombre: " + this.MessagesInChatRoom.get(i).getUsername() + " --|");
        }

        pw.println("</textarea> <form method=\"POST\"> ");
        pw.println("<br> <hr> <br> Enter you Name:<input type=\"text\" name=\"myUserName\" style=\"margin-right: 20px\"> ");
        pw.println("Enter you Message:<input type=\"text\" name=\"myMessage\" size=\"50\" style=\"margin-right: 20px\"> ");
        pw.println("<input type=\"submit\" name=\"myButton\" value=\"Send to Everybody\"> ");
        pw.println("</form> <body> </html>");

        pw.flush();

        log.debug("Process ended.");

    }


    private List<String> readInputStreamByLines() throws IOException {

        final InputStream is = client.getInputStream();

        final List<String> allLinesReaded = new ArrayList<>();

        final Scanner s = new Scanner(is).useDelimiter("\\A");
        log.debug("Reading the Inputstream ..");

        while (true && s.hasNext()) {

            final String line = s.nextLine();

            if (line.length() == 0) {

                if (allLinesReaded.get(0).equals("POST / HTTP/1.1")) {
                    String textByUser = s.nextLine();
                    FormatForMessage(textByUser);
                }

                break;
            } else {
                allLinesReaded.add(line);
            }
        }

        return allLinesReaded;

    }

    public ChatMessage SaveNewMessage() {
        return newMessage;
    }


    public void FormatForMessage(String completeLine) {

        String nameUser = "", messageUser = "";

        nameUser += completeLine.substring(11, completeLine.indexOf("&myMessage="));
        messageUser += completeLine.substring(completeLine.indexOf("&myMessage=") + 11, completeLine.indexOf("&myButton="));

        String withoutAccent = Normalizer.normalize(nameUser, Normalizer.Form.NFD);
        nameUser = withoutAccent.replaceAll("[^a-zA-Z ]", " ");

        withoutAccent = Normalizer.normalize(messageUser, Normalizer.Form.NFD);
        messageUser = withoutAccent.replaceAll("[^a-zA-Z ]", " ");

        LocalDateTime timeStamp = LocalDateTime.now();

        System.out.println("datos nuevos " + nameUser + "  " + messageUser + "  " + timeStamp);

        log.debug(nameUser);
        this.newMessage = new ChatMessage(nameUser, messageUser, timeStamp);

    }


}
