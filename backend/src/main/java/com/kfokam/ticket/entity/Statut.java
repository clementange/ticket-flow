package com.kfokam.ticket.entity;

/**
 * Représente les états possibles d'un ticket.
 * Stocké en base sous forme de chaîne (ex. "OUVERT") grâce à @Enumerated(EnumType.STRING).
 *
 * Transitions autorisées (RG-03) :
 *   OUVERT   → FERME
 *   EN_COURS → FERME
 *   FERME    → (aucune transition possible)
 */
public enum Statut {
    OUVERT,
    EN_COURS,
    FERME
}
