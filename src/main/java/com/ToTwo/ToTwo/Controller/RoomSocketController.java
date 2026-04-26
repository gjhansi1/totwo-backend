/*package com.ToTwo.ToTwo.Controller;

import com.ToTwo.ToTwo.model.Room;
import com.ToTwo.ToTwo.model.VoiceRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RoomSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ Broadcast when a new room is created (optional client trigger)
    @MessageMapping("/notifyRoomCreated")
    @SendTo("/topic/rooms")
    public Room notifyRoomCreated(Room room) {
        return room;
    }

    // ✅ Send updates manually from backend
    public void broadcastRoomUpdate(Room room) {
        messagingTemplate.convertAndSend("/topic/rooms", room);
    }

    // ✅ Broadcast when a voice room starts
    public void broadcastVoiceRoomStarted(VoiceRoom vr) {
        messagingTemplate.convertAndSend("/topic/voice/start", vr);
    }

    // ✅ Broadcast when a voice room ends
    public void broadcastVoiceRoomEnded(VoiceRoom vr) {
        messagingTemplate.convertAndSend("/topic/voice/end", vr);
    }
}*/
package com.ToTwo.ToTwo.Controller;

import com.ToTwo.ToTwo.model.Room;
import com.ToTwo.ToTwo.model.VoiceRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RoomSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ Broadcast when a new room is created (optional client trigger)
    @MessageMapping("/notifyRoomCreated")
    @SendTo("/topic/rooms")
    public Room notifyRoomCreated(Room room) {
        return room;
    }

    // ✅ Send updates manually from backend
    public void broadcastRoomUpdate(Room room) {
        // ✅ Added console log to verify broadcast
        System.out.println("📢 Broadcasting new room to /topic/rooms: " + room.getName());
        messagingTemplate.convertAndSend("/topic/rooms", room);
    }

    // ✅ Broadcast when a voice room starts
    public void broadcastVoiceRoomStarted(VoiceRoom vr) {
        System.out.println("🎙 Voice room started for room ID: " + vr.getRoom().getId());
        messagingTemplate.convertAndSend("/topic/voice/start", vr);
    }

    // ✅ Broadcast when a voice room ends
    public void broadcastVoiceRoomEnded(VoiceRoom vr) {
        System.out.println("🔇 Voice room ended for room ID: " + vr.getRoom().getId());
        messagingTemplate.convertAndSend("/topic/voice/end", vr);
    }
}

