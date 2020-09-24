package com.genhub.domain.blog.dto;

import java.io.Serializable;


public class BadgesCount implements Serializable {
    private static final long serialVersionUID = 8276459939240769498L;

    private int messages; 

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }
}
