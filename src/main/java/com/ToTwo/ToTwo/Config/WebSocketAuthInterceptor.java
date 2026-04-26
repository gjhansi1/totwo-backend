package com.ToTwo.ToTwo.Config;

import com.ToTwo.ToTwo.Repo.RoomMemberRepo;
import com.ToTwo.ToTwo.Repo.UserRepo;
import com.ToTwo.ToTwo.Security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoomMemberRepo roomMemberRepo;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing Authorization header");
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            Long userId = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            // OPTIONAL (next step): roomId validation will come later
            accessor.setUser(() -> email);
        }

        return message;
    }
}
