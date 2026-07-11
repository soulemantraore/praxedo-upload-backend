package com.praxedo.upload.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** Requete HTTP : enregistrement d'un lot de fichiers (chaque element valide). */
public record RegisterBatchRequest(@NotEmpty List<@Valid RegisterFileRequest> files) {
}
