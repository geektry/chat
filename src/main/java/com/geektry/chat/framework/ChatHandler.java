package com.geektry.chat.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geektry.chat.constant.MessageTypeEnum;
import com.geektry.chat.vo.MessageVO;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ChatHandler extends TextWebSocketHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static Map<String, Map<String, WebSocketSession>> sessionGroups = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        String roomId = getRoomId(session);

        this.putUserIntoRoom(session, roomId);

        this.bindUser(session);

        Map<String, WebSocketSession> sessions = sessionGroups.get(roomId);

        this.updateOnlineNumber(sessions);

        this.sendEnterMessage(session, sessions);
    }

    private void bindUser(WebSocketSession session) throws IOException {

        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(new HashMap<String, Object>() {{
            put("type", MessageTypeEnum.COMMAND_BIND_USER);
            put("userId", shortenName(session.getId()));
        }})));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        String roomId = getRoomId(session);

        Map<String, WebSocketSession> sessions = sessionGroups.get(roomId);

        this.sendMessage(session, message, sessions);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {

        String roomId = getRoomId(session);

        Map<String, WebSocketSession> sessions = sessionGroups.get(roomId);

        boolean isRoomEmpty = this.takeUserOutOfRoom(session, sessions, roomId);

        if (!isRoomEmpty) {
            this.sendLeaveMessage(session, sessions);
        }

        this.updateOnlineNumber(sessions);
    }

    private String getRoomId(WebSocketSession session) {

        String queryString = ((StandardWebSocketSession) session).getNativeSession().getQueryString();
        return queryString.split("=")[1];
    }

    private void putUserIntoRoom(WebSocketSession session, String roomId) {

        if (sessionGroups.containsKey(roomId)) {
            sessionGroups.get(roomId).put(session.getId(), session);
        } else {
            sessionGroups.put(roomId, new HashMap<>() {{
                put(session.getId(), session);
            }});
        }
    }

    private void sendEnterMessage(WebSocketSession session, Map<String, WebSocketSession> sessions) throws IOException {

        for (WebSocketSession sessionItem : sessions.values()) {
            sessionItem.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(new MessageVO() {{
                setType(MessageTypeEnum.MESSAGE_SYSTEM);
                setContent(shortenName(session.getId()) + "已加入聊天室");
            }})));
        }
    }

    private void sendMessage(WebSocketSession session, TextMessage message, Map<String, WebSocketSession> sessions) throws IOException {

        for (WebSocketSession sessionItem : sessions.values()) {
            sessionItem.sendMessage(new TextMessage(this.getUserMessageJsonStr(this.shortenName(session.getId()), message.getPayload())));
        }
    }

    // return if room is empty
    private boolean takeUserOutOfRoom(WebSocketSession session, Map<String, WebSocketSession> sessions, String roomId) {

        sessions.remove(session.getId());

        if (sessions.isEmpty()) {
            sessionGroups.remove(roomId);
            return true;
        }
        return false;
    }

    private void sendLeaveMessage(WebSocketSession session, Map<String, WebSocketSession> sessions) throws IOException {

        for (WebSocketSession sessionItem : sessions.values()) {
            sessionItem.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(new MessageVO() {{
                setType(MessageTypeEnum.MESSAGE_SYSTEM);
                setContent(shortenName(session.getId()) + "已离开聊天室");
            }})));
        }
    }

    private String shortenName(String name) {

        return name.substring(0, 8);
    }

    private void updateOnlineNumber(Map<String, WebSocketSession> sessions) throws IOException {

        for (WebSocketSession sessionItem : sessions.values()) {
            sessionItem.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(new HashMap<String, Object>() {{
                put("type", MessageTypeEnum.COMMAND_UPDATE_ONLINE_NUMBER);
                put("onlineNumber", sessions.size());
            }})));
        }
    }

    private String getUserMessageJsonStr(String sender, String content) throws JsonProcessingException {

        return new ObjectMapper().writeValueAsString(new MessageVO() {{
            setType(MessageTypeEnum.MESSAGE_USER);
            setDatetime(LocalDateTime.now().format(FORMATTER));
            setSender(sender);
            setContent(content);
        }});
    }
}
