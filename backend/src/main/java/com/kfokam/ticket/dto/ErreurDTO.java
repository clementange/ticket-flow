package com.kfokam.ticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Couche : DTO (sortie erreur).
 * Responsabilité : format JSON uniforme pour toutes les réponses d'erreur.
 *
 * Format retourné au client :
 * {
 *   "status":  409,
 *   "erreur":  "CONFLIT_RG02",
 *   "message": "RG-02 : un ticket avec ce titre existe déjà.",
 *   "details": []          ← liste des erreurs de validation (400 uniquement)
 * }
 *
 * @JsonInclude(NON_NULL) : le champ "details" n'apparaît pas si null
 * (pour ne pas polluer les réponses 404/409 avec un tableau vide).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErreurDTO {

    /** Code HTTP de l'erreur (ex. 400, 404, 409). */
    private int status;

    /** Code court identifiant le type d'erreur (ex. "CONFLIT_RG02", "NON_TROUVE"). */
    private String erreur;

    /** Message lisible par l'humain, nominant la règle violée. */
    private String message;

    /**
     * Détails de validation (uniquement pour les 400).
     * Chaque entrée = un champ invalide avec son message.
     */
    private List<String> details;

    // -------------------------------------------------------------------------
    // Constructeurs
    // -------------------------------------------------------------------------

    public ErreurDTO() {}

    public ErreurDTO(int status, String erreur, String message) {
        this.status = status;
        this.erreur = erreur;
        this.message = message;
    }

    public ErreurDTO(int status, String erreur, String message, List<String> details) {
        this.status = status;
        this.erreur = erreur;
        this.message = message;
        this.details = details;
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getErreur() { return erreur; }
    public void setErreur(String erreur) { this.erreur = erreur; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
