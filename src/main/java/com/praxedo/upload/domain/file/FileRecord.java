package com.praxedo.upload.domain.file;

import com.praxedo.upload.domain.file.exceptions.IllegalFileTransitionException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entite du domaine : un fichier et son etat dans le cycle de vie.
 * POJO pur (aucune annotation framework) : la persistance a son propre modele (adapter JPA).
 * Les transitions passent toutes par {@link #transitionTo} qui applique la machine a etats.
 */
public class FileRecord {

    private final UUID id;
    private final UUID ownerId;
    private final UUID batchId; // nullable
    private final String filename;
    private final String contentType;
    private final long sizeBytes;
    private final String storageKey;
    private FileStatus status;
    private ScanVerdict scanVerdict; // nullable
    private int scanAttempts;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant scannedAt; // nullable

    private FileRecord(UUID id, UUID ownerId, UUID batchId, String filename, String contentType,
                       long sizeBytes, String storageKey, FileStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.batchId = batchId;
        this.filename = Objects.requireNonNull(filename);
        this.contentType = Objects.requireNonNull(contentType);
        this.sizeBytes = sizeBytes;
        this.storageKey = Objects.requireNonNull(storageKey);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = createdAt;
    }

    public static FileRecord pending(UUID id, UUID ownerId, UUID batchId, String filename,
                                     String contentType, long sizeBytes, String storageKey, Instant now) {
        return new FileRecord(id, ownerId, batchId, filename, contentType, sizeBytes, storageKey,
            FileStatus.PENDING, now);
    }

    /** Reconstruction depuis la persistance (adapter JPA, jalon ulterieur). */
    public static FileRecord rehydrate(UUID id, UUID ownerId, UUID batchId, String filename,
                                        String contentType, long sizeBytes, String storageKey,
                                        FileStatus status, ScanVerdict scanVerdict, int scanAttempts,
                                        Instant createdAt, Instant updatedAt, Instant scannedAt) {
        FileRecord f = new FileRecord(id, ownerId, batchId, filename, contentType, sizeBytes, storageKey, status, createdAt);
        f.scanVerdict = scanVerdict;
        f.scanAttempts = scanAttempts;
        f.updatedAt = updatedAt;
        f.scannedAt = scannedAt;
        return f;
    }

    private void transitionTo(FileStatus target, Instant now) {
        if (!status.canTransitionTo(target)) {
            throw new IllegalFileTransitionException(status, target);
        }
        this.status = target;
        this.updatedAt = now;
    }

    public void markScanning(Instant now) {
        transitionTo(FileStatus.SCANNING, now);
        this.scanAttempts++;
    }

    public void markClean(ScanVerdict verdict, Instant now) {
        transitionTo(FileStatus.CLEAN, now);
        this.scanVerdict = verdict;
        this.scannedAt = now;
    }

    public void markInfected(ScanVerdict verdict, Instant now) {
        transitionTo(FileStatus.INFECTED, now);
        this.scanVerdict = verdict;
        this.scannedAt = now;
    }

    public void markScanFailed(Instant now) {
        transitionTo(FileStatus.SCAN_FAILED, now);
    }

    public void markExpired(Instant now) {
        transitionTo(FileStatus.EXPIRED, now);
    }

    public boolean isDownloadable() {
        return status.isDownloadable();
    }

    public UUID id() { return id; }
    public UUID ownerId() { return ownerId; }
    public UUID batchId() { return batchId; }
    public String filename() { return filename; }
    public String contentType() { return contentType; }
    public long sizeBytes() { return sizeBytes; }
    public String storageKey() { return storageKey; }
    public FileStatus status() { return status; }
    public ScanVerdict scanVerdict() { return scanVerdict; }
    public int scanAttempts() { return scanAttempts; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant scannedAt() { return scannedAt; }
}
