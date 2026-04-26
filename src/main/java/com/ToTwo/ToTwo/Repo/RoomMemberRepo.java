package com.ToTwo.ToTwo.Repo;

import com.ToTwo.ToTwo.model.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface RoomMemberRepo extends JpaRepository<RoomMember, Long> {

    // existing (kept)
    List<RoomMember> findByRoomId(Long roomId);
    List<RoomMember> findByUserId(Long userId);
    Optional<RoomMember> findByUserIdAndRoomId(Long userId, Long roomId);
 // 🔒 Step 3.1 support (rejoin cooldown)
    Optional<RoomMember>
    findTopByUserIdAndRoomIdOrderByExpiresAtDesc(Long userId, Long roomId);
    // ✅ NEW: expiry-safe queries
    @Transactional
    @Modifying
    @Query("""
        DELETE FROM RoomMember rm
        WHERE rm.expiresAt IS NOT NULL
          AND rm.expiresAt <= :now
    """)
    void deleteExpiredMembers(@Param("now") LocalDateTime now);


    	List<RoomMember> findActiveMembersByRoomId(@Param("roomId") Long roomId);

    	// 🔒 Step 5.1 support — shared room check
    	@Query("select rm.roomId from RoomMember rm where rm.userId = :userId")
    	List<Long> findRoomIdsByUserId(@Param("userId") Long userId);

    	boolean existsByUserIdAndRoomIdIn(Long userId, List<Long> roomIds);



}
