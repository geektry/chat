package com.geektry.chat.vo;

import com.geektry.chat.constant.MessageTypeEnum;

public class ServiceMessageVO {

    private MessageTypeEnum type;

    private Integer onlineNumber;

    public MessageTypeEnum getType() {
        return type;
    }

    public void setType(MessageTypeEnum type) {
        this.type = type;
    }

    public Integer getOnlineNumber() {
        return onlineNumber;
    }

    public void setOnlineNumber(Integer onlineNumber) {
        this.onlineNumber = onlineNumber;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ServiceMessageVO{");
        sb.append("type=").append(type);
        sb.append(", onlineNumber=").append(onlineNumber);
        sb.append('}');
        return sb.toString();
    }
}
