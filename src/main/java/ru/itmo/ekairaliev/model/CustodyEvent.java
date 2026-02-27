package ru.itmo.ekairaliev.model;

import java.time.Instant;
import java.util.Objects;

public final class CustodyEvent {
    private final long id;              // назначается программой
    private final long sampleId;        // ссылка на Sample

    private String fromUser;            // до 64, не пусто
    private String toUser;              // до 64, не пусто
    private String location;            // до 64, не пусто
    private String comment;             // до 128, можно пусто
    private Instant transferredAt;      // если не задано — now

    private String ownerUsername;       // кто выполнил действие
    private final Instant createdAt;    // назначается программой

    public CustodyEvent(long id,
                        long sampleId,
                        String fromUser,
                        String toUser,
                        String location,
                        String comment,
                        Instant transferredAt,
                        String ownerUsername,
                        Instant createdAt) {
        this.id = id;
        this.sampleId = sampleId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.location = location;
        this.comment = comment;
        this.transferredAt = transferredAt;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public long getSampleId() { return sampleId; }
    public String getFromUser() { return fromUser; }
    public String getToUser() { return toUser; }
    public String getLocation() { return location; }
    public String getComment() { return comment; }
    public Instant getTransferredAt() { return transferredAt; }
    public String getOwnerUsername() { return ownerUsername; }
    public Instant getCreatedAt() { return createdAt; }

    public void setFromUser(String fromUser) { this.fromUser = fromUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }
    public void setLocation(String location) { this.location = location; }
    public void setComment(String comment) { this.comment = comment; }
    public void setTransferredAt(Instant transferredAt) { this.transferredAt = transferredAt; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustodyEvent that = (CustodyEvent) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CustodyEvent{" +
                "id=" + id +
                ", sampleId=" + sampleId +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", location='" + location + '\'' +
                ", comment='" + comment + '\'' +
                ", transferredAt=" + transferredAt +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}