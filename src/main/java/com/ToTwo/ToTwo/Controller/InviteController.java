package com.ToTwo.ToTwo.Controller;

import com.ToTwo.ToTwo.Repo.RoomRepo;
import com.ToTwo.ToTwo.Repo.RoomMemberRepo;
import com.ToTwo.ToTwo.Repo.UserRepo;
import com.ToTwo.ToTwo.Security.JwtUtil;
import com.ToTwo.ToTwo.model.Room;
import com.ToTwo.ToTwo.model.RoomMember;
import com.ToTwo.ToTwo.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/join")
@CrossOrigin
public class InviteController {

	@Autowired
	private RoomRepo roomRepo;

	@Autowired
	private RoomMemberRepo roomMemberRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private JwtUtil jwtUtil;

	// ✅ Join room using invite link (JWT required)
	@GetMapping("/{inviteCode}")
	public ResponseEntity<?> joinRoomByInvite(@PathVariable String inviteCode,
			@RequestHeader("Authorization") String authHeader) {
		// 1️⃣ Validate token
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(401).body("Missing or invalid token");
		}

		String token = authHeader.substring(7);
		String email = jwtUtil.extractEmail(token);

		// 2️⃣ Get user
		User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		// 3️⃣ Get room by invite
		Optional<Room> roomOpt = roomRepo.findByInviteCode(inviteCode);
		if (roomOpt.isEmpty()) {
			return ResponseEntity.status(404).body("Invalid invite link");
		}

		Room room = roomOpt.get();

		// 4️⃣ Room validation
		if (!room.isActive()) {
			return ResponseEntity.status(400).body("Room is inactive");
		}

		/*
		 * if (room.getExpiresAt() != null &&
		 * room.getExpiresAt().isBefore(LocalDateTime.now())) { return
		 * ResponseEntity.status(400).body("Invite link expired"); }
		 */
		// 🔒 Invite expiry enforcement (invite-level, NOT room-level)
		if (room.getInviteGeneratedAt() != null
				&& room.getInviteGeneratedAt().isBefore(LocalDateTime.now().minusDays(7))) {

			return ResponseEntity.status(410).body("Invite link has expired");
		}

		// 🔒 Rejoin cooldown: prevent leave → immediate rejoin (5 minutes)
		Optional<RoomMember> lastMembership = roomMemberRepo.findTopByUserIdAndRoomIdOrderByExpiresAtDesc(user.getId(),
				room.getId());

		if (lastMembership.isPresent()) {
			RoomMember last = lastMembership.get();

			if (last.getExpiresAt() != null && last.getExpiresAt().isBefore(LocalDateTime.now()) && // user actually
																									// left
					last.getExpiresAt().isAfter(LocalDateTime.now().minusSeconds(10))) {

				return ResponseEntity.status(429).body("Please wait before rejoining this room");
			}
		}

		// 5️⃣ Add member if not already joined
		roomMemberRepo.findByUserIdAndRoomId(user.getId(), room.getId()).orElseGet(() -> {
			RoomMember rm = new RoomMember();
			rm.setUserId(user.getId());
			rm.setRoomId(room.getId());
			rm.setJoinedAt(LocalDateTime.now());
			rm.setExpiresAt(LocalDateTime.now().plusDays(7));
			return roomMemberRepo.save(rm);
		});

		// 6️⃣ Done
		return ResponseEntity.ok(room);
	}
}
