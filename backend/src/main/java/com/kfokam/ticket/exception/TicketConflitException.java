package com.kfokam.ticket.exception;

/**
 * Lancée quand une règle de gestion métier est violée (conflit).
 * Le @RestControllerAdvice la capte et renvoie un 409.
 *
 * Utilisée pour :
 *   - RG-02 : doublon de titre
 *   - RG-03 : fermeture d'un ticket déjà FERME
 *
 * Le champ "regle" permet au frontend d'afficher le nom exact de la règle violée.
 */
public class TicketConflitException extends RuntimeException {

    /** Identifiant de la règle violée, ex. "RG-02", "RG-03". */
    private final String regle;

    public TicketConflitException(String regle, String message) {
        super(message);
        this.regle = regle;
    }

    public String getRegle() {
        return regle;
    }
}
