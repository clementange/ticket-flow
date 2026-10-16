package com.kfokam.ticket.dto;

import com.kfokam.ticket.entity.Statut;
import java.time.LocalDateTime;

/**
 * Couche : DTO (sortie).
 * Responsabilité : représenter les données renvoyées au client après création ou consultation.
 *
 * L'entité Ticket ne sort JAMAIS du service — seul ce DTO est exposé via le contrôleur.
 * Le frontend a besoin de l'id (pour appeler PATCH /fermer) et de tous les autres champs
 * pour afficher le tableau.
 */
public class TicketReponseDTO {

    /** Identifiant technique, nécessaire pour l'action de fermeture côté frontend. */
    private Long id;

    /** Titre tel qu'il a été saisi (casse préservée). */
    private String titre;

    /** Description facultative, peut être null. */
    private String description;

    /** Statut courant : OUVERT, EN_COURS ou FERME. */
    private Statut statut;

    /** Date et heure de création au format ISO-8601 (ex. "2026-10-16T14:47:12"). */
    private LocalDateTime dateCreation;

    // -------------------------------------------------------------------------
    // Constructeurs
    // -------------------------------------------------------------------------

    public TicketReponseDTO() {}

    public TicketReponseDTO(Long id, String titre, String description,
                            Statut statut, LocalDateTime dateCreation) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
