package com.kfokam.ticket.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité JPA mappée sur la table "ticket" en base PostgreSQL.
 *
 * Couche : Domaine / Persistance.
 * Responsabilité : représenter l'état d'un ticket tel qu'il est stocké en base.
 * Cette classe ne doit JAMAIS sortir du service (utiliser TicketReponseDTO à la place).
 */
@Entity
@Table(name = "ticket", uniqueConstraints = {
        // RG-02 : deux tickets ne peuvent pas avoir le même titre (insensible à la casse)
        // La contrainte SQL porte sur la colonne titre_normalise (voir @PrePersist)
        @UniqueConstraint(name = "uk_ticket_titre_normalise", columnNames = "titre_normalise")
})
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titre affiché (conserve la casse saisie par l'utilisateur).
     * RG-01 : obligatoire, 100 caractères max.
     */
    @Column(name = "titre", nullable = false, length = 100)
    private String titre;

    /**
     * Version normalisée du titre (minuscules + trim) utilisée pour la contrainte d'unicité.
     * Jamais exposée dans les DTO.
     */
    @Column(name = "titre_normalise", nullable = false, length = 100)
    private String titreNormalise;

    /**
     * Description facultative, sans limite de taille.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Statut stocké sous forme de chaîne ("OUVERT", "EN_COURS", "FERME").
     * Initialisé à OUVERT à la création (RG-01).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private Statut statut;

    /**
     * Date de création générée par le serveur lors du premier persist.
     * Non modifiable après création.
     */
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /**
     * Appelé automatiquement par JPA avant le premier INSERT.
     * Initialise le statut et la date de création.
     */
    @PrePersist
    private void avantCreation() {
        this.statut = Statut.OUVERT;
        this.dateCreation = LocalDateTime.now();
        if (this.titre != null) {
            this.titreNormalise = this.titre.trim().toLowerCase();
        }
    }

    // -------------------------------------------------------------------------
    // Constructeurs
    // -------------------------------------------------------------------------

    public Ticket() {}

    public Ticket(String titre, String description) {
        this.titre = titre;
        this.description = description;
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) {
        this.titre = titre;
        if (titre != null) {
            this.titreNormalise = titre.trim().toLowerCase();
        }
    }

    public String getTitreNormalise() { return titreNormalise; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
}
