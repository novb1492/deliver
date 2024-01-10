package com.example.demo.Deliver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliverPositionHandler extends TextWebSocketHandler {
    Map<String, List<Map<String,Object>>> roomList =new HashMap<>(); //웹소켓 세션을 담아둘 리스트 ---roomListSessions


    @Override//메세지가오는함수
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("받은메세지:{}",message.getPayload());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> payloadMap = new HashMap<>();
        try {
            // payload를 Map으로 파싱합니다.
           payloadMap = objectMapper.readValue(message.getPayload(), new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            // JSON 처리 예외를 처리합니다.
            e.printStackTrace();
            log.error("변환실패");
        }
        Object didObject = payloadMap.get("did");
        String did = didObject.toString();
        String roomKey = "ROOM:" + did;
        for(Map<String,Object>room:roomList.get(roomKey)){
            try {
                //보내기만 하면됨 n번방 세션 들 다꺼내기
                WebSocketSession wss = (WebSocketSession) room.get("session");
                wss.sendMessage(new TextMessage(message.getPayload()));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("배달 위치 전송실패");
            }
        }
    }



    @Override//연결이되면 자동으로 작동하는함수
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("배달 웹소켓이 연결되었습니다");
        URI uri = session.getUri();
        Map<String, String> params = extractQueryParameters(uri.getQuery());
        String did = params.get("did");
        // list에 방 정보 저장
        String roomKey = "ROOM:" + did;
        if (!roomList.containsKey(roomKey)) {
            // 방이 존재하지 않으면 새로운 방 생성
            List<Map<String, Object>> infos = new ArrayList<>();
            Map<String, Object> info = new HashMap<>();
            info.put("role", params.get("role"));
            info.put("did", did);
            info.put("sessionId", session.getId());
            info.put("session", session);
            infos.add(info);

            // 새로운 방 정보를 추가하고 로그 출력
            log.info("새로운 방 생성: {}", roomKey);
            log.info("배달방 입장: {}", infos);

            roomList.put(roomKey, infos);
        } else {
            // 방이 이미 존재하면 기존 방에 입장
            List<Map<String, Object>> infos = roomList.get(roomKey);
            Map<String, Object> info = new HashMap<>();
            info.put("role", params.get("role"));
            info.put("did", did);
            info.put("sessionId", session.getId());
            info.put("session", session);
            infos.add(info);

            // 기존 방에 입장하고 로그 출력
            log.info("기존 방 입장: {}", roomKey);
            log.info("배달방 입장: {}", infos);

            roomList.put(roomKey, infos);
        }

        // 로그에 전체 방 정보 출력
        log.info("전체 배달방 정보: {}", roomList);

    }
    // URI에서 쿼리 파라미터 추출하는 메서드
    private Map<String, String> extractQueryParameters(String queryString) {
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString("?" + queryString).build().getQueryParams();
        // MultiValueMap을 Map으로 변환
        Map<String, String> paramMap = new LinkedHashMap<>();
        queryParams.forEach((key, values) -> paramMap.put(key, values.get(0)));
        return paramMap;
    }


    @Override //연결이끊기면 자동으로 작동하는함수
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //권한에 따라
        log.info("연결종료");
        URI uri = session.getUri();
        Map<String, String> params = extractQueryParameters(uri.getQuery());
        String did = params.get("did");
        // list에 방 정보 저장
        String roomKey = "ROOM:" + did;
        if (roomList.containsKey(roomKey)) {
            List<Map<String, Object>> infos = roomList.get(roomKey);

            // 해당 세션 아이디를 찾아 제거
            for (Iterator<Map<String, Object>> iterator = infos.iterator(); iterator.hasNext();) {
                Map<String, Object> room = iterator.next();
                String sessionId = (String) room.get("sessionId");

                if (sessionId.equals(session.getId())) {
                    // 세션 아이디가 일치하면 해당 세션 제거
                    iterator.remove();
                    log.info("배달방에서 세션 제거: {}", session.getId());
                    break;  // 해당 세션을 찾았으므로 루프 종료
                }
            }

            // 만약 방에 더 이상 세션이 없다면 방도 제거
            if (infos.isEmpty()) {
                roomList.remove(roomKey);
                log.info("배달방 제거: {}", roomKey);
            }

            // 로그에 현재 배달방 정보 출력
            log.info("현재 배달방 정보: {}", roomList);
        } else {
            log.error("배달방이 존재하지 않습니다. 방 정보를 제거할 수 없습니다.");
        }

    }


}