/**
 * Copyright (c) 2012 Conversant Solutions. All rights reserved.
 * <p>
 * Created on 2018/11/13.
 */
package com.geektry.chat.vo;

/**
 * @author Chaohang Fu
 */
public class MessageVO {

    private String datetime;

    private String sender;

    private String content;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageVO{");
        sb.append("datetime='").append(datetime).append('\'');
        sb.append(", sender='").append(sender).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
