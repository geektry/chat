package com.geektry.chat.vo;

import com.geektry.chat.constant.MessageTypeEnum;

/**
 * @author Chaohang Fu
 */
public class MessageVO {

    private MessageTypeEnum type;

    private String dateTime;

    private String userId;

    private String content;

    public MessageTypeEnum getType() {
        return type;
    }

    public void setType(MessageTypeEnum type) {
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
