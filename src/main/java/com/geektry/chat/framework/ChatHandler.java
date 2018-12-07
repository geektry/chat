package com.geektry.chat.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geektry.chat.constant.MessageType;
import com.geektry.chat.vo.MessageVO;
import org.springframework.stereotype.Component;
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

@Component
public class ChatHandler extends TextWebSocketHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static Map<String, Map<String, WebSocketSession>> sessionGroups = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        String groupId = getGroupId(session);

        this.putSessionIntoGroup(session, groupId);

        Map<String, WebSocketSession> sessions = sessionGroups.get(groupId);

        this.initRoomInfo(session, sessions);

        this.userEnterRoom(session, sessions);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        String groupId = getGroupId(session);

        Map<String, WebSocketSession> sessions = sessionGroups.get(groupId);

        this.sendMessage(session, message, sessions);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {

        String groupId = getGroupId(session);

        Map<String, WebSocketSession> sessions = sessionGroups.get(groupId);

        this.takeSessionOutOfGroup(session, sessions);

        if (sessions.isEmpty()) {
            sessionGroups.remove(groupId);
            return;
        }

        this.userLeaveRoom(session, sessions);
    }

    private String getGroupId(WebSocketSession session) {

        String queryString = ((StandardWebSocketSession) session).getNativeSession().getQueryString();
        return queryString.split("=")[1];
    }

    private void putSessionIntoGroup(WebSocketSession session, String groupId) {

        if (sessionGroups.containsKey(groupId)) {
            sessionGroups.get(groupId).put(session.getId(), session);
        } else {
            sessionGroups.put(groupId, new HashMap<>() {{
                put(session.getId(), session);
            }});
        }
    }

    private void takeSessionOutOfGroup(WebSocketSession session, Map<String, WebSocketSession> sessions) {

        sessions.remove(session.getId());
    }

    private void initRoomInfo(WebSocketSession session, Map<String, WebSocketSession> sessions) throws IOException {

        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(new HashMap<String, Object>() {{
            put("type", MessageType.EVT_INIT_ROOM_INFO);
            put("myUserId", session.getId());
            put("onlineUserIds", sessions.keySet());
        }})));
    }

    private void userEnterRoom(WebSocketSession session, Map<String, WebSocketSession> sessions) throws IOException {

        for (WebSocketSession sessionItem : sessions.values()) {
            sessionItem.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(new HashMap<String, Object>() {{
                put("type", MessageType.EVT_USER_ENTER_ROOM);
                put("userId", session.getId());
            }})));
        }
    }

    private void userLeaveRoom(WebSocketSession session, Map<String, WebSocketSession> sessions) throws IOException {

        for (WebSocketSession sessionItem : sessions.values()) {
            sessionItem.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(new HashMap<String, Object>() {{
                put("type", MessageType.EVT_USER_LEAVE_ROOM);
                put("userId", session.getId());
            }})));
        }
    }

    private void sendMessage(WebSocketSession session, TextMessage message, Map<String, WebSocketSession> sessions) throws IOException {

        for (WebSocketSession sessionItem : sessions.values()) {
            sessionItem.sendMessage(new TextMessage(this.getUserMessageJsonStr(session.getId(), message.getPayload())));
        }
    }

    private String getUserMessageJsonStr(String userId, String content) throws JsonProcessingException {

        return new ObjectMapper().writeValueAsString(new MessageVO() {{
            setType(MessageType.MSG_USER);
            setDateTime(LocalDateTime.now().format(FORMATTER));
            setUserId(userId);
            setContent(content);
        }});
    }
}
