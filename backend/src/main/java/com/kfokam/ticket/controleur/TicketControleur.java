package com.kfokam.ticket.controleur;

import com.kfokam.ticket.dto.TicketCreationDTO;
import com.kfokam.ticket.dto.TicketReponseDTO;
import com.kfokam.ticket.entity.Statut;
import com.kfokam.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Couche : Contrôleur (API REST).
 * Responsabilité : exposer les endpoints HTTP et déléguer ENTIÈREMENT au service.
 *
 * Règle respectée : AUCUNE logique métier ici.
 * Le contrôleur ne fait que :
 *   1. Recevoir la requête et valider le format (@Valid)
 *   2. Appeler le service
 *   3. Retourner la réponse HTTP appropriée
 *
 * Les erreurs sont gérées par GestionnaireExceptions (@RestControllerAdvice).
 */
@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Gestion des tickets de support")
public class TicketControleur {

    private final TicketService ticketService;

    public TicketControleur(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // =========================================================================
    // POST /api/tickets — Créer un ticket
    // =========================================================================

    @Operation(
            summary = "Créer un ticket",
            description = "Crée un nouveau ticket avec le statut OUVERT. " +
                    "RG-01 : titre obligatoire ≤ 100 caractères. " +
                    "RG-02 : le titre doit être unique (insensible à la casse)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Titre absent ou > 100 caractères",
                    content = @Content(schema = @Schema(implementation = com.kfokam.ticket.dto.ErreurDTO.class))),
            @ApiResponse(responseCode = "409", description = "RG-02 : un ticket avec ce titre existe déjà",
                    content = @Content(schema = @Schema(implementation = com.kfokam.ticket.dto.ErreurDTO.class)))
    })
    @PostMapping
    public ResponseEntity<TicketReponseDTO> creer(
            @Valid @RequestBody TicketCreationDTO dto) {
        TicketReponseDTO reponse = ticketService.creer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    // =========================================================================
    // GET /api/tickets — Lister les tickets (avec filtre optionnel par statut)
    // =========================================================================

    @Operation(
            summary = "Lister les tickets",
            description = "Retourne tous les tickets triés du plus récent au plus ancien. " +
                    "Filtrable par statut via le paramètre ?statut=OUVERT|EN_COURS|FERME."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès (peut être vide)"),
            @ApiResponse(responseCode = "400", description = "Valeur de statut invalide",
                    content = @Content(schema = @Schema(implementation = com.kfokam.ticket.dto.ErreurDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<TicketReponseDTO>> lister(
            @Parameter(description = "Filtre par statut : OUVERT, EN_COURS ou FERME")
            @RequestParam(required = false) Statut statut) {
        return ResponseEntity.ok(ticketService.lister(statut));
    }

    // =========================================================================
    // PATCH /api/tickets/{id}/fermer — Fermer un ticket
    // =========================================================================

    @Operation(
            summary = "Fermer un ticket",
            description = "Passe le statut du ticket à FERME. " +
                    "RG-03 : seuls les tickets OUVERT ou EN_COURS peuvent être fermés."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket fermé avec succès"),
            @ApiResponse(responseCode = "404", description = "Ticket introuvable",
                    content = @Content(schema = @Schema(implementation = com.kfokam.ticket.dto.ErreurDTO.class))),
            @ApiResponse(responseCode = "409", description = "RG-03 : ticket déjà fermé",
                    content = @Content(schema = @Schema(implementation = com.kfokam.ticket.dto.ErreurDTO.class)))
    })
    @PatchMapping("/{id}/fermer")
    public ResponseEntity<TicketReponseDTO> fermer(
            @Parameter(description = "Identifiant du ticket à fermer")
            @PathVariable Long id) {
        return ResponseEntity.ok(ticketService.fermer(id));
    }
}
