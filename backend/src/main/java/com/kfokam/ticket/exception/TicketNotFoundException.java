package com.kfokam.ticket.exception;

/**
 * Lancée quand un ticket avec l'id demandé n'existe pas en base.
 * Le @RestControllerAdvice la capte et renvoie un 404.
 *
 * RuntimeException : pas besoin de la déclarer dans les signatures de méthodes.
 */
public class TicketNotFoundException extends RuntimeException {

    private final Long id;

    public TicketNotFoundException(Long id) {
        super("Ticket introuvable : aucun ticket avec l'id " + id + ".");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
