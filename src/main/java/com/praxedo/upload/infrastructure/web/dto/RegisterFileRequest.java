package com.praxedo.upload.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** Requete HTTP : enregistrement d'un upload unitaire (validation cote entree). */
public record RegisterFileRequest(
    @NotBlank String filename,
    @NotBlank String contentType,
    @Positive long size) {
}
