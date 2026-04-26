package com.ToTwo.ToTwo.Scheduler;

import com.ToTwo.ToTwo.Repo.RoomMemberRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class RoomMemberCleanupScheduler {

    private final RoomMemberRepo roomMemberRepo;

    public RoomMemberCleanupScheduler(RoomMemberRepo roomMemberRepo) {
        this.roomMemberRepo = roomMemberRepo;
    }

    // Runs every 15 minutes
    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void cleanupExpiredRoomMembers() {
        roomMemberRepo.deleteExpiredMembers(LocalDateTime.now());
    }
}
