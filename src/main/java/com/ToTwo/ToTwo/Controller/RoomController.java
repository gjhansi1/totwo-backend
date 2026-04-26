package com.ToTwo.ToTwo.Controller;
import com.ToTwo.ToTwo.Repo.RoomMemberRepo;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import com.ToTwo.ToTwo.Repo.UserRepo;
import java.time.LocalDateTime;
import com.ToTwo.ToTwo.model.Room;
import com.ToTwo.ToTwo.Repo.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@CrossOrigin
public class RoomController {
	
	@GetMapping
	public ResponseEntity<?> getRoomsForUI() {
	    return ResponseEntity.ok(roomRepo.findAll());
	}


    @Autowired
    private RoomRepo roomRepo;
    
    @Autowired
    private RoomMemberRepo roomMemberRepo;


    @Autowired
    private RoomSocketController roomSocketController; // ✅ inject WebSocket broadcaster

    @Autowired
    private UserRepo userRepo;

    
    
    
    // ✅ Create new room
    @PostMapping
    public ResponseEntity<?> createRoom(
            @RequestBody Room room,
            Authentication authentication
    ) {
        // get creator from JWT
        String email = authentication.getName();
        Long ownerId = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        room.setOwnerId(ownerId);   // 🔥 THIS WAS MISSING
        room.setActive(true);

        Room saved = roomRepo.save(room);
        roomSocketController.broadcastRoomUpdate(saved);

        return ResponseEntity.ok(saved);
    }


    @PostMapping("/{roomId}/invite")
    public ResponseEntity<?> generateInvite(
            @PathVariable Long roomId,
            Authentication authentication
    ) {
    	 System.out.println("🔥 GENERATE INVITE HIT");
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        String email = authentication.getName();
        Long currentUserId = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        boolean isMember = roomMemberRepo
                .findByUserIdAndRoomId(currentUserId, roomId)
                .isPresent();

        if (!isMember) {
            return ResponseEntity.status(403)
                    .body("Only room members can generate invites");
        }
        
        // ⏱️ Rate limit: 1 invite per room every 10 minutes
        if (room.getInviteGeneratedAt() != null &&
            room.getInviteGeneratedAt().isAfter(LocalDateTime.now().minusSeconds(10))) {

            return ResponseEntity.status(429)
                    .body("Invite recently generated. Please wait before creating a new one.");
        }


       /* String inviteCode = UUID.randomUUID().toString().substring(0, 8);
        room.setInviteCode(inviteCode);
        room.setInviteLink("http://localhost:8080/join/" + inviteCode);
        room.setInviteGeneratedAt(LocalDateTime.now());
        roomRepo.save(room);
        return ResponseEntity.ok(room); */
        String inviteCode = UUID.randomUUID().toString().substring(0, 8);

        LocalDateTime now = LocalDateTime.now();

        room.setInviteCode(inviteCode);
        room.setInviteLink("http://localhost:8080/join/" + inviteCode);
        room.setInviteGeneratedAt(now);
        room.setExpiresAt(now.plusDays(7));   // 🔥 THIS IS THE FIX

        roomRepo.save(room);

        return ResponseEntity.ok(room);
    }


    // ✅ Get all rooms (optional)
    @GetMapping("/all")
    public ResponseEntity<?> getAllRooms() {
        return ResponseEntity.ok(roomRepo.findAllOrderedForUI());
    }

    @GetMapping("/{roomId}/members")
    public ResponseEntity<?> getRoomMembers(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomMemberRepo.findByRoomId(roomId));
    }
    
    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(
            @PathVariable Long roomId,
            Authentication authentication
    ) {
        // 1️⃣ Get room
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 2️⃣ Get current user (from JWT)
        String email = authentication.getName();
        Long currentUserId = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        System.out.println("JWT email = " + email);
        System.out.println("Room ownerId = " + room.getOwnerId());

        // 3️⃣ Owner check
        if (!currentUserId.equals(room.getOwnerId())) {
            return ResponseEntity.status(403)
                    .body("Only owner can delete this room");
        }
        

        // 4️⃣ Member count check
        long memberCount = roomMemberRepo.findByRoomId(roomId).size();
        if (memberCount > 0) {
            return ResponseEntity.status(400)
                    .body("Cannot delete room with active members");
        }

        // 5️⃣ Delete
        roomRepo.delete(room);
        return ResponseEntity.ok("Room deleted");
    }
    @GetMapping("/my")
    public ResponseEntity<?> getMyRooms(Authentication authentication) {

        String email = authentication.getName();

        Long userId = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        // rooms user joined
        var memberships = roomMemberRepo.findByUserId(userId);

        var roomIds = memberships.stream()
                .map(m -> m.getRoomId())
                .toList();

        // get rooms where user is owner OR member
        var rooms = roomRepo.findAll().stream()
                .filter(r -> r.getOwnerId().equals(userId) || roomIds.contains(r.getId()))
                .toList();

        return ResponseEntity.ok(rooms);
    }

}

