package com.kfokam.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Couche : DTO (entrée).
 * Responsabilité : représenter les données envoyées par le client lors de la création d'un ticket.
 *
 * Seuls titre et description sont acceptés — le serveur détermine tout le reste
 * (id, statut, dateCreation). Les annotations de validation déclenchent un 400
 * automatiquement via @Valid dans le contrôleur.
 */
public class TicketCreationDTO {

    /**
     * Titre du ticket.
     * RG-01 : obligatoire (@NotBlank rejette null, vide et espaces seuls),
     *          100 caractères max.
     * Pas de maxlength côté client — le refus vient du serveur.
     */
    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 100, message = "Le titre ne doit pas dépasser 100 caractères.")
    private String titre;

    /**
     * Description facultative. Aucune contrainte de longueur.
     */
    private String description;

    // -------------------------------------------------------------------------
    // Constructeurs
    // -------------------------------------------------------------------------

    public TicketCreationDTO() {}

    public TicketCreationDTO(String titre, String description) {
        this.titre = titre;
        this.description = description;
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
