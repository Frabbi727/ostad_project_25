package org.ostad.ostad_assignment_25.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "sync_state")
public class SyncStateEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private Instant lastSyncedAt;

    @Column(length = 120)
    private String lastReadmeSha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(Instant lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public String getLastReadmeSha() {
        return lastReadmeSha;
    }

    public void setLastReadmeSha(String lastReadmeSha) {
        this.lastReadmeSha = lastReadmeSha;
    }
}
