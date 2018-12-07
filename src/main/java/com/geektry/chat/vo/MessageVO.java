package com.geektry.chat.vo;

import com.geektry.chat.constant.MessageType;

/**
 * @author Chaohang Fu
 */
public class MessageVO {

    private MessageType type;

    private String dateTime;

    private String userId;

    private String content;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
