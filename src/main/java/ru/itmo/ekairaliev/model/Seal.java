package ru.itmo.ekairaliev.model;

import java.time.Instant;
import java.util.Objects;


public final class Seal {
        // Уникальный номер пломбы. Программа назначает сама.
        private final long id;

        // К какому образцу относится (id образца).
        // Должен ссылаться на реально существующий Sample.
        private long sampleId;

        // Номер пломбы (например "SEAL-9911").
        // Нельзя пустое. До 64 символов.
        private String sealNumber;

        // Статус пломбы: ACTIVE или BROKEN.
        private SealStatus status;

        // Кто поставил пломбу (логин).
        // На ранних этапах можно "SYSTEM".
        private String ownerUsername;

        // Когда поставили. Программа ставит автоматически.
        private final Instant createdAt;

        // Когда изменяли (например, ломали).
        // Программа обновляет автоматически.
        private Instant updatedAt;

        //конструктор 1

        public Seal(long id, long sampleId, SealStatus status, String sealNumber, String ownerUsername, Instant updatedAt, Instant createdAt) {
            this.id = id;
            this.sampleId = sampleId;
            this.status = status;
            this.sealNumber = sealNumber;
            this.ownerUsername = ownerUsername;
            this.updatedAt = updatedAt;
            this.createdAt = createdAt;
        }

        //геттеры
        public long getId() {
            return id;
        }

        public Instant getUpdatedAt() {
            return updatedAt;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public String getOwnerUsername() {
            return ownerUsername;
        }

        public SealStatus getStatus() {
            return status;
        }

        public String getSealNumber() {
            return sealNumber;
        }

        public long getSampleId() {
            return sampleId;
        }

        //сеттеры

        public void setSampleId(long sampleId) {
            this.sampleId = sampleId;
        }

        public void setSealNumber(String sealNumber) { this.sealNumber = sealNumber; }

        public void setStatus(SealStatus status) {
            this.status = status;
        }

        public void setOwnerUsername(String ownerUsername) {
            this.ownerUsername = ownerUsername;
        }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Seal seal = (Seal) o;
        return id == seal.id && sampleId == seal.sampleId && Objects.equals(sealNumber, seal.sealNumber) && status == seal.status && Objects.equals(ownerUsername, seal.ownerUsername) && Objects.equals(createdAt, seal.createdAt) && Objects.equals(updatedAt, seal.updatedAt);
    }
    //помечаем тачем что объект изменили
    public void touch() { this.updatedAt = Instant.now(); }

    @Override
    public int hashCode() {
        return Objects.hash(id, sampleId, sealNumber, status, ownerUsername, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Seal{" +
                "id=" + id +
                ", sampleId=" + sampleId +
                ", sealNumber='" + sealNumber + '\'' +
                ", status=" + status +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


