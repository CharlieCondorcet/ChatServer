/*
 *  Copyright (c) 2019. Charlie Condorcet http://www.charliec.cl .
 *
 *  All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Intellij IDEA
 * which accompanies this distribution, and is available at
 * https://resources.jetbrains.com/storage/products/appcode/docs/AppCode_Classroom_EULA.pdf .
 *
 */

import java.time.LocalDateTime;

public class ChatMessage {


    private final String username;
    private final String message;
    private final LocalDateTime timeStamp;



    public ChatMessage(String username, String message, LocalDateTime timeStamp) {
        this.username = username;
        this.message = message;
        this.timeStamp = timeStamp;
    }


    public LocalDateTime getTimeStamp() {
        return this.timeStamp;
    }

    public String getUsername() {
        return this.username;
    }

    public String getMessage() {
        return this.message;
    }

    public String PrintCompleteMessage(){
        try{
            return "* "+this.username+ ": "+this.message+" |at: "+this.timeStamp.toString()+" .-";

        }catch (Exception e){

            return " ";
        }
    }

}
