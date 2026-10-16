export interface Ticket {
  id: number;
  titre: string;
  description: string | null;
  statut: string;
  dateCreation: string;
}

export type StatutFilter = "TOUT" | "OUVERT" | "EN_COURS" | "FERME";

const BASE = "http://localhost:8080/api/tickets";

export function texteStatut(s: string): string {
  return s === "OUVERT" ? "Ouvert" : s === "EN_COURS" ? "En cours" : "Ferme";
}

export async function listerTickets(statut?: StatutFilter): Promise<Ticket[]> {
  const params: Record<string, string> = {};
  if (statut && statut !== "TOUT") {
    params.statut = statut;
  }
  const search = new URLSearchParams(params);
  const reponse = await fetch(`${BASE}${search.toString() ? `?${search}` : ""}`, {
    method: "GET",
    headers: { Accept: "application/json" },
  });
  if (!reponse.ok) {
    const data = await reponse.json().catch(() => ({}));
    throw {
      status: reponse.status,
      erreur: data?.erreur ?? "ERREUR_INTERNE",
      message: data?.message ?? reponse.statusText,
      details: data?.details,
    };
  }
  return reponse.json();
}

export async function creerTicket(titre: string, description?: string): Promise<Ticket> {
  const reponse = await fetch(`${BASE}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ titre, description: description ? description.trim() : null }),
  });
  if (!reponse.ok) {
    const data = await reponse.json().catch(() => ({}));
    throw {
      status: reponse.status,
      erreur: data?.erreur ?? "ERREUR_INTERNE",
      message: data?.message ?? reponse.statusText,
      details: data?.details,
    };
  }
  return reponse.json();
}

export async function fermerTicket(id: number): Promise<Ticket> {
  const reponse = await fetch(`${BASE}/${id}/fermer`, { method: "PATCH", headers: { Accept: "application/json" } });
  if (!reponse.ok) {
    const data = await reponse.json().catch(() => ({}));
    throw {
      status: reponse.status,
      erreur: data?.erreur ?? "ERREUR_INTERNE",
      message: data?.message ?? reponse.statusText,
      details: data?.details,
    };
  }
  return reponse.json();
}
