package com.kfokam.ticket.service;

import com.kfokam.ticket.dto.TicketCreationDTO;
import com.kfokam.ticket.dto.TicketMapper;
import com.kfokam.ticket.dto.TicketReponseDTO;
import com.kfokam.ticket.entity.Statut;
import com.kfokam.ticket.entity.Ticket;
import com.kfokam.ticket.exception.TicketConflitException;
import com.kfokam.ticket.exception.TicketNotFoundException;
import com.kfokam.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Couche : Service (logique métier).
 * Responsabilité : orchestrer les opérations sur les tickets en appliquant
 * les règles de gestion RG-01, RG-02, RG-03.
 *
 * AUCUNE logique métier dans le contrôleur — tout est ici.
 * L'entité Ticket ne sort jamais de cette couche : on retourne toujours un DTO.
 */
@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    // Injection par constructeur (recommandée, facilite les tests)
    public TicketService(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    // =========================================================================
    // Création — POST /api/tickets
    // =========================================================================

    /**
     * Crée un nouveau ticket.
     *
     * RG-01 : le titre est obligatoire et ≤ 100 caractères (validé en amont par @Valid).
     *         Le statut est initialisé à OUVERT par @PrePersist dans l'entité.
     * RG-02 : deux tickets ne peuvent pas avoir le même titre (insensible à la casse, trim).
     *         On vérifie d'abord en service pour renvoyer un 409 lisible ;
     *         la contrainte SQL unique est un filet de sécurité supplémentaire.
     *
     * @param dto données envoyées par le client (titre + description)
     * @return le ticket créé sous forme de DTO
     * @throws TicketConflitException (RG-02) si le titre existe déjà
     */
    public TicketReponseDTO creer(TicketCreationDTO dto) {
        String titreNormalise = dto.getTitre().trim().toLowerCase();

        // RG-02 : vérification du doublon avant insertion
        if (ticketRepository.findByTitreNormalise(titreNormalise).isPresent()) {
            throw new TicketConflitException(
                    "RG-02",
                    "RG-02 : un ticket avec le titre « " + dto.getTitre().trim() + " » existe déjà."
            );
        }

        Ticket ticket = ticketMapper.versEntite(dto);
        Ticket sauvegarde = ticketRepository.save(ticket);
        return ticketMapper.versDTO(sauvegarde);
    }

    // =========================================================================
    // Liste — GET /api/tickets
    // =========================================================================

    /**
     * Retourne la liste des tickets, triée du plus récent au plus ancien.
     * Si un statut est fourni, filtre par ce statut.
     * Si statut est null, retourne tous les tickets.
     *
     * @param statut filtre optionnel (OUVERT, EN_COURS, FERME) ou null
     * @return liste de DTO de réponse
     */
    @Transactional(readOnly = true)
    public List<TicketReponseDTO> lister(Statut statut) {
        List<Ticket> tickets;

        if (statut != null) {
            tickets = ticketRepository.findByStatutOrderByDateCreationDesc(statut);
        } else {
            tickets = ticketRepository.findAllByOrderByDateCreationDesc();
        }

        return tickets.stream()
                .map(ticketMapper::versDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Fermeture — PATCH /api/tickets/{id}/fermer
    // =========================================================================

    /**
     * Ferme un ticket existant.
     *
     * RG-03 : un ticket ne peut être fermé que s'il est OUVERT ou EN_COURS.
     *         Un ticket FERME ne change plus d'état.
     *
     * @param id identifiant du ticket à fermer
     * @return le ticket mis à jour sous forme de DTO
     * @throws TicketNotFoundException  si aucun ticket avec cet id n'existe
     * @throws TicketConflitException   (RG-03) si le ticket est déjà FERME
     */
    public TicketReponseDTO fermer(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        // RG-03 : un ticket FERME ne peut plus changer d'état
        if (ticket.getStatut() == Statut.FERME) {
            throw new TicketConflitException(
                    "RG-03",
                    "RG-03 : le ticket « " + ticket.getTitre() + " » est déjà fermé et ne peut plus changer d'état."
            );
        }

        ticket.setStatut(Statut.FERME);
        Ticket mis_a_jour = ticketRepository.save(ticket);
        return ticketMapper.versDTO(mis_a_jour);
    }
}
