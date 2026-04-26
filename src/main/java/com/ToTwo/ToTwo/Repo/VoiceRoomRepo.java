package com.ToTwo.ToTwo.Repo;

import com.ToTwo.ToTwo.model.VoiceRoom;
import com.ToTwo.ToTwo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoiceRoomRepo extends JpaRepository<VoiceRoom, Long> {

    // 🔹 Find all active voice rooms
    List<VoiceRoom> findByActiveTrue();

    // 🔹 Find active voice rooms linked to a specific room
    List<VoiceRoom> findByRoomAndActiveTrue(Room room);

    // 🔹 Find currently active voice room by room (single result)
    Optional<VoiceRoom> findFirstByRoomAndActiveTrue(Room room);
}
