package com.ToTwo.ToTwo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.ToTwo.ToTwo.Repo.RoomMemberRepo;
import com.ToTwo.ToTwo.Repo.UserRepo;

import java.util.Map;

@Controller
public class WebRTCSignalingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoomMemberRepo roomMemberRepo;

    
    @MessageMapping("/signal")
    public void handleSignaling(
            @Payload Map<String, Object> payload,
            java.security.Principal principal
    ) {
        Long toUserId = Long.valueOf(String.valueOf(payload.get("to")));

        messagingTemplate.convertAndSend(
                "/topic/signal/" + toUserId,
                payload
        );
    }

 
}
