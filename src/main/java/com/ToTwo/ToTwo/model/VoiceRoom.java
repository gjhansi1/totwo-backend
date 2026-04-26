package com.ToTwo.ToTwo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voice_rooms")
public class VoiceRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link this voice session to an existing Room
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // participants in the voice session
    private String participant1Email;
    private String participant2Email;

    // room activity tracking
    private boolean active = true;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @PrePersist
    public void onStart() {
        if (startedAt == null) startedAt = LocalDateTime.now();
        active = true;
    }

    // When call ends, mark inactive and set end time
    public void endCall() {
        this.active = false;
        this.endedAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getParticipant1Email() { return participant1Email; }
    public void setParticipant1Email(String participant1Email) { this.participant1Email = participant1Email; }

    public String getParticipant2Email() { return participant2Email; }
    public void setParticipant2Email(String participant2Email) { this.participant2Email = participant2Email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }

    // Optional: easy status display
    @Override
    public String toString() {
        return "VoiceRoom{" +
                "id=" + id +
                ", roomId=" + (room != null ? room.getId() : null) +
                ", active=" + active +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                '}';
    }
}
