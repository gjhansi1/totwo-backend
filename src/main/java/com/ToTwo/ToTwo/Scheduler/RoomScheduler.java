package com.ToTwo.ToTwo.Scheduler;

import com.ToTwo.ToTwo.Repo.RoomRepo;
import com.ToTwo.ToTwo.model.Room;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component


public class RoomScheduler {

    private final RoomRepo roomRepo;

    public RoomScheduler(RoomRepo roomRepo) {
        this.roomRepo = roomRepo;
    }

    // Runs once every 24 hours
    @Scheduled(cron = "0 0 0 * * *")  // midnight every day
    public void deactivateExpiredRooms() {
        LocalDateTime now = LocalDateTime.now();
        List<Room> expiredRooms = roomRepo.findByExpiresAtBeforeAndActiveTrue(now);
        for (Room room : expiredRooms) {
            room.setActive(false);
            roomRepo.save(room);
        }
    }
}
