package com.ToTwo.ToTwo.Controller; 

import com.ToTwo.ToTwo.model.User;
import com.ToTwo.ToTwo.Repo.UserRepo;

import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired; 

import org.springframework.web.bind.annotation.*;

import com.ToTwo.ToTwo.model.Room;
import com.ToTwo.ToTwo.model.RoomMember; 

import com.ToTwo.ToTwo.Repo.RoomMemberRepo;
import com.ToTwo.ToTwo.Repo.RoomRepo;

import java.util.List; 

 

@RestController 

@RequestMapping("/room-members")

@CrossOrigin // allow requests from any origin 

public class RoomMemberController { 

	@Autowired
	private UserRepo userRepo;


@Autowired 

private RoomMemberRepo roomMemberRepo; 



@Autowired
private RoomRepo roomRepo;



 

// Add a user to a room (avoids duplicates) 

@PostMapping("/{roomId}/members")
public RoomMember addSelfToRoom(@PathVariable Long roomId,
                                Authentication authentication) {

    // JWT → email
    String email = authentication.getName();

    // email → User
    User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    

    Long currentUserId = user.getId();

   
	 Room room = roomRepo.findById(roomId)
	            .orElseThrow(() -> new RuntimeException("Room not found"));

	    if (!room.isActive()) {
	        throw new RuntimeException("Room is expired or inactive");
	    }	
	    return roomMemberRepo.findByUserIdAndRoomId(currentUserId, roomId)


.orElseGet(() -> { 

RoomMember rm = new RoomMember(); 

rm.setRoomId(roomId); 

rm.setUserId(currentUserId);


return roomMemberRepo.save(rm); 

}); 

} 

 

// List all members of a room 

// Remove a user from a room (safe if not present) 

@DeleteMapping("/{roomId}/members")
public String leaveRoom(@PathVariable Long roomId,
                         Authentication authentication) {

    // JWT → email
    String email = authentication.getName();

    // email → User
    User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Long currentUserId = user.getId();

    roomMemberRepo.findByUserIdAndRoomId(currentUserId, roomId)
            .ifPresent(roomMemberRepo::delete);

    return "Left room " + roomId;
}



 

// Optional: Get all rooms a user belongs to 

@GetMapping("/{roomId}/members/active")
public List<RoomMember> getActiveMembers(@PathVariable Long roomId,
                                         Authentication authentication) {

    // JWT → email
    String email = authentication.getName();

    // email → User
    User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Long currentUserId = user.getId();

    // 🔒 membership check
    boolean isMember = roomMemberRepo
            .findByUserIdAndRoomId(currentUserId, roomId)
            .isPresent();

    if (!isMember) {
        throw new RuntimeException("Access denied: not a room member");
    }

    return roomMemberRepo.findActiveMembersByRoomId(roomId);
}
   
}

