package com.geektry.chat.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static Map<String, Map<String, WebSocketSession>> sessionGroupByRoomId = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        String roomId = getRoomId(session);

        if (sessionGroupByRoomId.containsKey(roomId)) {
            sessionGroupByRoomId.get(roomId).put(session.getId(), session);
        } else {
            sessionGroupByRoomId.put(roomId, new HashMap<>() {{
                put(session.getId(), session);
            }});
        }

        Map<String, WebSocketSession> sessionMap = sessionGroupByRoomId.get(roomId);

        String content = "欢迎进入GeekTry聊天室，尊敬的 [ " + this.shortenName(session.getId()) + " ] ~";
        session.sendMessage(new TextMessage(this.getMessageJsonStr("", content)));
        for (WebSocketSession sessionInGroup : sessionMap.values()) {
            sessionInGroup.sendMessage(new TextMessage(this.getMessageJsonStr(this.shortenName(session.getId()), "已加入聊天室")));
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        String roomId = getRoomId(session);
        Map<String, WebSocketSession> sessionMap = sessionGroupByRoomId.get(roomId);

        for (WebSocketSession sessionInGroup : sessionMap.values()) {
            sessionInGroup.sendMessage(new TextMessage(this.getMessageJsonStr(this.shortenName(session.getId()), message.getPayload())));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {

        String roomId = getRoomId(session);
        Map<String, WebSocketSession> sessionMap = sessionGroupByRoomId.get(roomId);

        sessionMap.remove(session.getId());

        if (sessionMap.isEmpty()) {
            sessionGroupByRoomId.remove(roomId);
            return;
        }

        for (WebSocketSession sessionInGroup : sessionMap.values()) {
            sessionInGroup.sendMessage(new TextMessage(this.getMessageJsonStr(this.shortenName(session.getId()), "已离开聊天室")));
        }
    }

    private String getRoomId(WebSocketSession session) {

        String queryString = ((StandardWebSocketSession) session).getNativeSession().getQueryString();
        return queryString.split("=")[1];
    }

    private String shortenName(String name) {

        return name.substring(0, 8);
    }

    private String getMessageJsonStr(String sender, String content) throws JsonProcessingException {

        return new ObjectMapper().writeValueAsString(new MessageVO() {{
            setDatetime(LocalDateTime.now().format(FORMATTER));
            setSender(sender);
            setContent(content);
        }});
    }
}
