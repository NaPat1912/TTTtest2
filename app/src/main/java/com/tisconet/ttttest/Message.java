package com.tisconet.ttttest;

public class Message {

    private String text;
    private String name;


    public Message() {
    }

    public Message(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
