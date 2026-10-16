import type { Ticket } from "../api/ticketApi";
import { texteStatut } from "../api/ticketApi";

interface Props {
  tickets: Ticket[];
  aufFermer: (id: number) => void;
}

export default function TicketListe({ tickets, aufFermer }: Props) {
  if (tickets.length === 0) {
    return (
      <div className="ticket-vide">
        <p>Aucun ticket</p>
      </div>
    );
  }

  return (
    <table className="ticket-table">
      <thead>
        <tr>
          <th>Titre</th>
          <th>Statut</th>
          <th>Date de création</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        {tickets.map((t) => (
          <tr key={t.id}>
            <td className="ticket-col-titre">{t.titre}</td>
            <td className="ticket-col-statut">{texteStatut(t.statut)}</td>
            <td className="ticket-col-date">{t.dateCreation}</td>
            <td className="ticket-col-action">
              {t.statut === "OUVERT" || t.statut === "EN_COURS" ? (
                <button className="btn-fermer" onClick={() => aufFermer(t.id)}>
                  Fermer
                </button>
              ) : (
                <span className="statut-ferme">Fermé</span>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
