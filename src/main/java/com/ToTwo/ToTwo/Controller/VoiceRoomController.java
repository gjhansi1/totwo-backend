package com.ToTwo.ToTwo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ToTwo.ToTwo.Repo.RoomRepo;
import com.ToTwo.ToTwo.Repo.VoiceRoomRepo;
import com.ToTwo.ToTwo.model.Room;
import com.ToTwo.ToTwo.model.VoiceRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/voice")
@CrossOrigin
public class VoiceRoomController {

    @Autowired
    private VoiceRoomRepo voiceRoomRepo;

    @Autowired
    private RoomRepo roomRepo;

    // ✅ ADD THIS — to send WebSocket updates
    @Autowired
    private RoomSocketController roomSocketController;

    // ✅ Start a new voice session for a room
    @PostMapping("/start/{roomId}")
    public ResponseEntity<?> startVoiceSession(@PathVariable Long roomId,
                                               @RequestParam String participant1Email,
                                               @RequestParam String participant2Email) {

        Optional<Room> roomOpt = roomRepo.findById(roomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }

        Room room = roomOpt.get();
        if (!room.isActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Room is inactive (void)");
        }

        // end any existing active voice rooms for this room
        List<VoiceRoom> activeSessions = voiceRoomRepo.findByRoomAndActiveTrue(room);
        for (VoiceRoom v : activeSessions) {
            v.setActive(false);
            v.setEndedAt(LocalDateTime.now());
            voiceRoomRepo.save(v);
        }

        // create a new voice room session
        VoiceRoom newSession = new VoiceRoom();
        newSession.setRoom(room);
        newSession.setParticipant1Email(participant1Email);
        newSession.setParticipant2Email(participant2Email);
        newSession.setActive(true);

        voiceRoomRepo.save(newSession);

        // ✅ Send real-time WebSocket update
        roomSocketController.broadcastVoiceRoomStarted(newSession);

        return ResponseEntity.ok(newSession);
    }

    // ✅ End an active voice session
    @PutMapping("/end/{id}")
    public ResponseEntity<?> endVoiceSession(@PathVariable Long id) {
        Optional<VoiceRoom> vrOpt = voiceRoomRepo.findById(id);

        if (vrOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voice room not found");
        }

        VoiceRoom vr = vrOpt.get();
        if (!vr.isActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session already ended");
        }

        vr.setActive(false);
        vr.setEndedAt(LocalDateTime.now());
        voiceRoomRepo.save(vr);

        // ✅ Send real-time WebSocket update
        roomSocketController.broadcastVoiceRoomEnded(vr);

        return ResponseEntity.ok("Voice session ended");
    }

    // ✅ Get all active voice sessions
    @GetMapping("/active")
    public ResponseEntity<List<VoiceRoom>> getActiveSessions() {
        return ResponseEntity.ok(voiceRoomRepo.findByActiveTrue());
    }
}
