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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

public class htmlTemplate {

    /**
     * The Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ChatServer.class);


    public void htmlUI(PrintWriter pw, ArrayList<ChatMessage> messagesInChatRoom) {
        pw.println("HTTP/1.1 200 OK");
        pw.println("Server: DSM v0.0.1");
        pw.println("Date: " + new Date());
        pw.println("Content-Type: text/html; charset=UTF-8");
        // pw.println("Content-Type: text/plain; charset=UTF-8");
        pw.println("<html><body>The Date: <strong>" + new Date() + "</strong><body></html>");



        pw.flush();
    }

}
