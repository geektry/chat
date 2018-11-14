package com.geektry.chat.vo;

import com.geektry.chat.constant.MessageTypeEnum;

/**
 * @author Chaohang Fu
 */
public class MessageVO {

    private MessageTypeEnum type;

    private String datetime;

    private String sender;

    private String content;

    public MessageTypeEnum getType() {
        return type;
    }

    public void setType(MessageTypeEnum type) {
        this.type = type;
    }

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
}
