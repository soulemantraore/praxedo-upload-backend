package com.praxedo.upload.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

/** DTOs de requete HTTP (validation cote entree). */
public final class ApiDtos {

    private ApiDtos() {
    }

    public record RegisterFileRequest(
        @NotBlank String filename,
        @NotBlank String contentType,
        @Positive long size) {
    }

    public record RegisterBatchRequest(@NotEmpty List<@Valid RegisterFileRequest> files) {
    }
}
