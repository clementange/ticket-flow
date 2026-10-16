package com.kfokam.ticket.controleur;

import com.kfokam.ticket.dto.ErreurDTO;
import com.kfokam.ticket.exception.TicketConflitException;
import com.kfokam.ticket.exception.TicketNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Couche : Contrôleur / Gestion d'erreurs globale.
 * Responsabilité : intercepter toutes les exceptions et les convertir
 * en réponses JSON uniformes (ErreurDTO).
 *
 * @RestControllerAdvice s'applique à tous les @RestController de l'application.
 * Le contrôleur métier reste ainsi totalement propre — aucun try/catch dedans.
 */
@RestControllerAdvice
public class GestionnaireExceptions {

    // =========================================================================
    // 404 — Ticket introuvable
    // =========================================================================

    /**
     * Ticket avec cet id inexistant en base.
     * Lancé par : TicketService.fermer()
     */
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErreurDTO> gererNonTrouve(TicketNotFoundException ex) {
        ErreurDTO erreur = new ErreurDTO(
                404,
                "NON_TROUVE",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erreur);
    }

    // =========================================================================
    // 409 — Conflit métier (RG-02, RG-03)
    // =========================================================================

    /**
     * Règle de gestion violée : RG-02 (doublon titre) ou RG-03 (fermeture impossible).
     * Le message nomme explicitement la règle violée.
     * Lancé par : TicketService.creer() et TicketService.fermer()
     */
    @ExceptionHandler(TicketConflitException.class)
    public ResponseEntity<ErreurDTO> gererConflit(TicketConflitException ex) {
        String codeErreur = "CONFLIT_" + ex.getRegle().replace("-", "");
        ErreurDTO erreur = new ErreurDTO(
                409,
                codeErreur,
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erreur);
    }

    // =========================================================================
    // 400 — Erreurs de validation (@Valid)
    // =========================================================================

    /**
     * Titre absent ou > 100 caractères, ou tout autre champ invalide.
     * Lancé automatiquement par Spring quand @Valid échoue sur le DTO d'entrée.
     * Chaque violation est listée dans "details" pour que le frontend affiche
     * le bon message à côté du bon champ.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurDTO> gererValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        ErreurDTO erreur = new ErreurDTO(
                400,
                "VALIDATION",
                "La requête contient des données invalides.",
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    // =========================================================================
    // 400 — Valeur de paramètre invalide (?statut=BLABLA)
    // =========================================================================

    /**
     * Paramètre de type enum invalide dans l'URL (ex. ?statut=BLABLA).
     * Spring tente de convertir la chaîne en Statut et échoue.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErreurDTO> gererTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String valeurAcceptees = "OUVERT, EN_COURS, FERME";
        ErreurDTO erreur = new ErreurDTO(
                400,
                "PARAMETRE_INVALIDE",
                "La valeur « " + ex.getValue() + " » est invalide pour le paramètre « "
                        + ex.getName() + " ». Valeurs acceptées : " + valeurAcceptees + "."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    // =========================================================================
    // 400 — Corps de requête illisible (JSON malformé)
    // =========================================================================

    /**
     * Corps JSON mal formé ou manquant.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurDTO> gererCorpsMalForme(HttpMessageNotReadableException ex) {
        ErreurDTO erreur = new ErreurDTO(
                400,
                "CORPS_INVALIDE",
                "Le corps de la requête est absent ou mal formé."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    // =========================================================================
    // 500 — Erreur inattendue (filet de sécurité)
    // =========================================================================

    /**
     * Toute exception non gérée explicitement ci-dessus.
     * On ne remonte jamais de trace technique au client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurDTO> gererErreurInattendue(Exception ex) {
        ErreurDTO erreur = new ErreurDTO(
                500,
                "ERREUR_INTERNE",
                "Une erreur interne est survenue. Veuillez réessayer."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
    }
}
