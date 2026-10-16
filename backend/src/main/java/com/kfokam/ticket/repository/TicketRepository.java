package com.kfokam.ticket.repository;

import com.kfokam.ticket.entity.Statut;
import com.kfokam.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Couche : Persistance.
 * Responsabilité : accès à la table "ticket" en base.
 *
 * Spring Data génère automatiquement l'implémentation SQL à partir des noms de méthodes.
 * Aucune logique métier ici — uniquement des requêtes.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Retourne tous les tickets d'un statut donné, triés du plus récent au plus ancien.
     * Utilisé pour le filtre par statut (GET /api/tickets?statut=OUVERT).
     */
    List<Ticket> findByStatutOrderByDateCreationDesc(Statut statut);

    /**
     * Retourne tous les tickets, triés du plus récent au plus ancien.
     * Utilisé quand aucun filtre n'est demandé (GET /api/tickets).
     */
    List<Ticket> findAllByOrderByDateCreationDesc();

    /**
     * Vérifie si un ticket avec ce titre normalisé existe déjà.
     * Utilisé pour la vérification RG-02 avant insertion
     * (en complément de la contrainte SQL d'unicité).
     */
    Optional<Ticket> findByTitreNormalise(String titreNormalise);
}
