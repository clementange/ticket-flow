import { useState, useEffect, useCallback } from "react";
import TicketListe from "./components/TicketListe";
import CreationFormulaire from "./components/CreationFormulaire";
import { listerTickets, fermerTicket, type StatutFilter } from "./api/ticketApi";

type Etat = "chargement" | "donnees" | "vide" | "erreur";

export default function App() {
  const [statutFilter, setStatutFilter] = useState<StatutFilter>("TOUT");
  const [tickets, setTickets] = useState<Array<{ id: number; titre: string; description: string | null; statut: string; dateCreation: string }>>([]);
  const [etat, setEtat] = useState<Etat>("chargement");
  const [erreurServeur, setErreurServeur] = useState<string | null>(null);

  const charger = useCallback(async () => {
    setEtat("chargement");
    setErreurServeur(null);
    try {
      const resultat = await listerTickets(statutFilter);
      setTickets(resultat);
      setEtat(resultat.length === 0 ? "vide" : "donnees");
    } catch (err: any) {
      if (err && typeof err === "object" && "status" in err) {
        setErreurServeur((err as { message: string }).message);
      } else {
        setErreurServeur(String(err ?? "Impossible de joindre le serveur"));
      }
      setEtat("erreur");
    }
  }, [statutFilter]);

  useEffect(() => { charger(); }, [charger]);

  function handleFermer(id: number) {
    if (!confirm("Voulez-vous vraiment fermer ce ticket ?")) return;
    fermerTicket(id)
      .then(() => charger())
      .catch((err: any) => {
        const msg = err && typeof err === "object" && "message" in err ? (err as { message: string }).message : String(err ?? "Erreur");
        alert(msg);
      });
  }

  return (
    <>
      <header className="app-header">
        <h1>Tickets</h1>
      </header>

      <section className="app-section">
        <div className="filtre-statut">
          <span>Filtre :</span>
          <button className={`btn-filtr ${statutFilter === "TOUT" ? "actif" : ""}`} onClick={() => { setStatutFilter("TOUT"); charger(); }}>
            Tous
          </button>
          <button className={`btn-filtr ${statutFilter === "OUVERT" ? "actif" : ""}`} onClick={() => { setStatutFilter("OUVERT"); charger(); }}>
            OUVERT
          </button>
          <button className={`btn-filtr ${statutFilter === "EN_COURS" ? "actif" : ""}`} onClick={() => { setStatutFilter("EN_COURS"); charger(); }}>
            EN_COURS
          </button>
          <button className={`btn-filtr ${statutFilter === "FERME" ? "actif" : ""}`} onClick={() => { setStatutFilter("FERME"); charger(); }}>
            FERME
          </button>
        </div>
      </section>

      <section className="app-section">
        <CreationFormulaire onCreate={charger} />
      </section>

      <section className="app-section">
        {etat === "chargement" && <p className="state-chargement">Chargement des tickets…</p>}
        {etat === "donnees" && <TicketListe tickets={tickets} aufFermer={handleFermer} />}
        {etat === "vide" && <p className="state-vide">Aucun ticket</p>}
        {etat === "erreur" && (
          <div className="state-erreur">
            <p>Le serveur est injoignable : {erreurServeur}</p>
            <button className="lien btn-reessayer" onClick={charger}>
              Réessayer
            </button>
          </div>
        )}
      </section>
    </>
  );
}
