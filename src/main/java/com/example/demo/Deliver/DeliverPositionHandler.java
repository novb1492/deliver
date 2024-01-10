package com.example.demo.Deliver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Service
@Slf4j
@RequiredArgsConstructor
public class DeliverPositionHandler extends TextWebSocketHandler {

    @Override//메세지가오는함수
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }


    @Override//연결이되면 자동으로 작동하는함수
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("배달 웹소켓이 연결되었습니다");
    }



    @Override //연결이끊기면 자동으로 작동하는함수
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //권한에 따라
    }


}