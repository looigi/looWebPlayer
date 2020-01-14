package looigi.loowebplayer.ritorno_ws;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.utilities.GestioneImmagini;

public class RitornoDaWSIntermedioSOAP {
    public void ChiamaRoutinesInCasoDiOK(String result, String messErrore, int NumeroOperazione,
                                         int NumeroBrano, boolean Errore, String tOperazione) {
        String Ritorno = result;

        if (!VariabiliStaticheDebug.getInstance().getMessErrorePerDebug().isEmpty()) {
            messErrore = VariabiliStaticheDebug.getInstance().getMessErrorePerDebug();
            Ritorno = messErrore;
        }

        if (Ritorno.contains("ERROR:")) {
            messErrore = Ritorno;
            Errore = true;
        }

        wsRitornoNuovo rRit = new wsRitornoNuovo();
        boolean Ancora = true;

        // Errore = true;

        if (!Errore || NumeroBrano == -1) {
            while (Ancora) {
                switch (tOperazione) {
                    case "EliminaCanzone":
                        rRit.EliminaCanzone(Ritorno, NumeroOperazione);
                        Ancora = false;
                        break;
                    case "ModificaBellezza":
                        rRit.RitornaModificaBellezza(Ritorno, NumeroOperazione);
                        Ancora = false;
                        break;
                    case "VolteAscoltata":
                        rRit.RitornaVolteAscoltata(Ritorno, NumeroOperazione);
                        Ancora = false;
                        break;
                    case "RitornaListaBrani":
                        rRit.RitornaListaBrani(Ritorno);
                        Ancora = false;
                        break;
                    case "RitornaDatiUtente":
                        rRit.RitornaDatiUtente(Ritorno, NumeroOperazione);
                        Ancora = false;
                        break;
                    case "RitornaDettaglioBrano":
                        rRit.RitornaDettaglioBrano(Ritorno, NumeroOperazione);
                        Ancora = false;
                        break;
                    case "RitornaMultimediaArtista":
                        GestioneImmagini.getInstance().SalvaMultimediaArtista(Ritorno);
                        rRit.RitornaMultimediaArtista(Ritorno);
                        Ancora = false;
                        break;
                    case "RitornaVersioneApplicazione":
                        rRit.RitornaVersioneApplicazione(Ritorno, NumeroOperazione);
                        Ancora = false;
                        break;
                    case "RitornaSeDeveAggiornare":
                        rRit.RitornaSeDeveAggiornare(Ritorno, NumeroOperazione);
                        Ancora = false;
                        break;
                }
            }
        }
    }

    public void ChiamaRoutinesInCasoDiErrore(String result, int NumeroOperazione, String tOperazione) {
        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

        String Ritorno = result;

        wsRitornoNuovoPerErrore rRit = new wsRitornoNuovoPerErrore();
        boolean Ancora = true;

        while (Ancora) {
            switch (tOperazione) {
                case "EliminaCanzone":
                    rRit.EliminaCanzone(Ritorno, NumeroOperazione);
                    Ancora = false;
                    break;
                case "ModificaBellezza":
                    rRit.RitornaModificaBellezza(Ritorno, NumeroOperazione);
                    Ancora = false;
                    break;
                case "VolteAscoltata":
                    rRit.RitornaVolteAscoltata(Ritorno, NumeroOperazione);
                    Ancora = false;
                    break;
                case "RitornaListaBrani":
                    rRit.RitornaListaBrani(Ritorno);
                    Ancora = false;
                    break;
                case "RitornaDatiUtente":
                    rRit.RitornaDatiUtente(Ritorno, NumeroOperazione);
                    Ancora = false;
                    break;
                case "RitornaDettaglioBrano":
                    rRit.RitornaDettaglioBrano(Ritorno, NumeroOperazione);
                    Ancora = false;
                    break;
                case "RitornaMultimediaArtista":
                    GestioneImmagini.getInstance().SalvaMultimediaArtista(Ritorno);
                    rRit.RitornaMultimediaArtista(Ritorno);
                    Ancora = false;
                    break;
                case "RitornaVersioneApplicazione":
                    rRit.RitornaVersioneApplicazione(Ritorno, NumeroOperazione);
                    Ancora = false;
                    break;
            }
        }
    }
}
