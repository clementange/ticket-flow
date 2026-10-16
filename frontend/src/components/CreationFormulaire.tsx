import { useState } from "react";
import { creerTicket } from "../api/ticketApi";

interface Props {
  onCreate: () => void;
}

export default function CreationFormulaire({ onCreate }: Props) {
  const [titre, setTitre] = useState("");
  const [description, setDescription] = useState("");
  const [erreur, setErreur] = useState<string | null>(null);
  const [details, setDetails] = useState<string[] | null>(null);
  const [chargement, setChargement] = useState(false);

  const submitInactif = titre.trim().length === 0;

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErreur(null);
    setDetails(null);
    setChargement(true);
    try {
      await creerTicket(titre.trim(), description.trim() || undefined);
      setTitre("");
      setDescription("");
      onCreate();
    } catch (err: any) {
      if (err && typeof err === "object" && "status" in err) {
        const s = err as { status: number; message: string; details?: string[] };
        setErreur(s.message);
        setDetails(s.details ?? null);
      } else {
        setErreur(String(err ?? "Erreur de communication"));
      }
    } finally {
      setChargement(false);
    }
  }

  return (
    <form className="formulaire" onSubmit={handleSubmit}>
      <h3>Nouveau ticket</h3>
      <label className="champ">
        <span>Titre</span>
        <input
          className="input"
          type="text"
          value={titre}
          onChange={(e) => setTitre(e.target.value)}
          placeholder="Titre du ticket"
        />
      </label>

      <label className="champ">
        <span>Description</span>
        <textarea
          className="input textarea"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Description (facultative)"
          rows={3}
        />
      </label>

      {erreur && (
        <div className="erreur-formulaire">
          <p>{erreur}</p>
          {details && details.length > 0 && (
            <ul className="erreurs-details">
              {details.map((d, i) => (
                <li key={i}>{d}</li>
              ))}
            </ul>
          )}
          <button type="button" className="lien" onClick={() => { setErreur(null); setDetails(null); }}>
            Masquer
          </button>
        </div>
      )}

      <button type="submit" className="btn-creer" disabled={submitInactif || chargement}>
        {chargement ? "Création..." : "Créer"}
      </button>
    </form>
  );
}
