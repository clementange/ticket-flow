package com.kfokam.ticket.dto;

import com.kfokam.ticket.entity.Ticket;
import org.springframework.stereotype.Component;

/**
 * Couche : DTO / Mapper.
 * Responsabilité : convertir une entité Ticket en TicketReponseDTO.
 *
 * Pourquoi un mapper séparé ?
 * - Respecte le principe de responsabilité unique (SRP).
 * - Le service reste lisible : il appelle mapper.versDTO(ticket) sans se soucier
 *   du détail de la conversion.
 * - Facilite l'évolution : si le DTO change, on ne touche qu'ici.
 *
 * @Component : Spring l'injecte automatiquement dans le service via @Autowired / constructeur.
 */
@Component
public class TicketMapper {

    /**
     * Convertit une entité Ticket en TicketReponseDTO.
     * Le titreNormalise n'est PAS exposé — uniquement les champs utiles au frontend.
     *
     * @param ticket l'entité à convertir (ne doit pas être null)
     * @return le DTO de réponse correspondant
     */
    public TicketReponseDTO versDTO(Ticket ticket) {
        return new TicketReponseDTO(
                ticket.getId(),
                ticket.getTitre(),
                ticket.getDescription(),
                ticket.getStatut(),
                ticket.getDateCreation()
        );
    }

    /**
     * Convertit une entité Ticket issue d'un TicketCreationDTO.
     * Le statut et la dateCreation seront initialisés par @PrePersist dans l'entité.
     *
     * @param dto le DTO de création envoyé par le client
     * @return une nouvelle entité Ticket (pas encore persistée)
     */
    public Ticket versEntite(TicketCreationDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setTitre(dto.getTitre().trim());
        ticket.setDescription(
                dto.getDescription() != null ? dto.getDescription().trim() : null
        );
        return ticket;
    }
}
