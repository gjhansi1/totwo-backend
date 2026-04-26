package com.ToTwo.ToTwo.Repo;

import com.ToTwo.ToTwo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepo extends JpaRepository<Room, Long> {

    // ✅ Find a room by invite code
    Optional<Room> findByInviteCode(String inviteCode);

    // ✅ Find all rooms that expired but are still marked active
    List<Room> findByExpiresAtBeforeAndActiveTrue(LocalDateTime now);

    // ✅ NEW (ADD ONLY): ordered rooms for UI
    @Query("""
        SELECT r
        FROM Room r
        LEFT JOIN RoomMember rm ON rm.roomId = r.id
        GROUP BY r
        ORDER BY 
            r.active DESC,
            COUNT(rm.id) DESC,
            r.id DESC
    """)
    List<Room> findAllOrderedForUI();
}
