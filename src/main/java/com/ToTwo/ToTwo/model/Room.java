package com.ToTwo.ToTwo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;



@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerId;

    private String name;
    private String description;
    private LocalDateTime inviteGeneratedAt;

    // ✅ New fields for invite system
    private String inviteCode;
    private String inviteLink;

    // lifecycle fields
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private boolean active = true; // default true

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (expiresAt == null) expiresAt = createdAt.plusDays(7); // default 7 days lifetime
        if (inviteCode == null) {
            inviteCode = java.util.UUID.randomUUID().toString().substring(0, 8);
            inviteLink = "http://localhost:8080/join/" + inviteCode;
        }
        active = true;
    }

    // --- getters & setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public String getInviteLink() { return inviteLink; }
    public void setInviteLink(String inviteLink) { this.inviteLink = inviteLink; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getInviteGeneratedAt() {
        return inviteGeneratedAt;
    }

    public void setInviteGeneratedAt(LocalDateTime inviteGeneratedAt) {
        this.inviteGeneratedAt = inviteGeneratedAt;
    }


}


