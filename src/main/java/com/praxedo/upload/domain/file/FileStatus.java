package com.praxedo.upload.domain.file;

import java.util.Map;
import java.util.Set;

/**
 * Statut d'un fichier dans son cycle de vie et transitions autorisees.
 * Invariant de securite : seul {@link #CLEAN} est telechargeable.
 */
public enum FileStatus {
    PENDING,
    SCANNING,
    CLEAN,
    INFECTED,
    SCAN_FAILED,
    EXPIRED;

    private static final Map<FileStatus, Set<FileStatus>> ALLOWED = Map.of(
        PENDING, Set.of(SCANNING, EXPIRED),
        SCANNING, Set.of(CLEAN, INFECTED, SCAN_FAILED),
        SCAN_FAILED, Set.of(SCANNING),
        CLEAN, Set.of(),
        INFECTED, Set.of(),
        EXPIRED, Set.of()
    );

    public boolean canTransitionTo(FileStatus target) {
        return ALLOWED.getOrDefault(this, Set.of()).contains(target);
    }

    public boolean isDownloadable() {
        return this == CLEAN;
    }
}
