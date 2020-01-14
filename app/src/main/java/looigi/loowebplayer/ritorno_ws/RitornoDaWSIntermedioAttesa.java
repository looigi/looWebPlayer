package looigi.loowebplayer.ritorno_ws;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;

public class RitornoDaWSIntermedioAttesa {
    public void ChiamaRoutinesInCasoDiOK(String result, String messErrore, int NumeroOperazione,
                                         int NumeroBrano, boolean Errore, String tOperazione, boolean inBackground) {
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
                    case "RitornaBrano":
                        rRit.RitornaBranoConAttesa(Ritorno, NumeroOperazione, inBackground);
                        Ancora = false;
                        break;
                    case "RitornaBranoBackground":
                        rRit.RitornaBranoConAttesa(Ritorno, NumeroOperazione, inBackground);
                        Ancora = false;
                        break;
                }
            }
        }
    }

    public void ChiamaRoutinesInCasoDiErrore(String result, int NumeroOperazione,
                                         int NumeroBrano, String tOperazione, boolean inBackground) {
        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroBrano, true);

        String Ritorno = result;

        wsRitornoNuovoPerErrore rRit = new wsRitornoNuovoPerErrore();
        boolean Ancora = true;

        while (Ancora) {
            switch (tOperazione) {
                case "RitornaBrano":
                    rRit.RitornaBranoConAttesa(Ritorno, NumeroOperazione, inBackground);
                    Ancora = false;
                    break;
                case "RitornaBranoBackground":
                    rRit.RitornaBranoConAttesa(Ritorno, NumeroOperazione, inBackground);
                    Ancora = false;
                    break;
            }
        }
    }
}
