package looigi.loowebplayer.ritorno_ws;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaImmagini;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaVideo;
import looigi.loowebplayer.dialog.DialogMessaggio;
import looigi.loowebplayer.maschere.Utenza;
import looigi.loowebplayer.nuova_versione.ControlloVersioneApplicazione;
import looigi.loowebplayer.soap.DownloadTextFileNuovo;
import looigi.loowebplayer.thread.ScaricoBranoEAttesa;
import looigi.loowebplayer.utilities.GestioneCaricamentoBraniNuovo;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.GestioneTesti;
import looigi.loowebplayer.utilities.Utility;

// import looigi.loowebplayer.soap.CheckURLFile;

public class wsRitornoNuovoPerErrore {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("wsRitornoNuovoPerErrore");;
   /* private Runnable runRiga;
    private Handler hSelezionaRiga;
    private Runnable rAttendeRispostaCheckURL;
    private Handler hAttendeRispostaCheckURL;
    private int Secondi;
    private int Conta;
    private int PerPronuncia;
    private int nn; */

    private String ToglieTag(String Cosa) {
        return Cosa;
    }

    public void RitornaVersioneApplicazione(String Ritorno, int NumeroOperazione) {
        String Appoggio=ToglieTag(Ritorno);

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna versione applicazione: " + Appoggio);

        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

        if (Appoggio.toUpperCase().contains("ERROR:")) {
            //DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(), Appoggio, true, "Cv Calcio");
        } else {
            // ControlloVersioneApplicazione.ControlloVersione(Appoggio);
        }
    }

    public void RitornaListaBrani(final String Ritorno) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna lista brani: " + Ritorno);

        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                "Errore nel ritorno della lista brani:\n" + Ritorno,
                true,
                VariabiliStaticheGlobali.NomeApplicazione);
        System.exit(0);
    }

    public void RitornaDatiUtente(final String Ritorno, int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna dati utente:" + Ritorno);

        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                "Errore nel ritorno dell'utente:\n" + Ritorno,
                true,
                VariabiliStaticheGlobali.NomeApplicazione);
        System.exit(0);
    }

    public void RitornaMultimediaArtista(final String Ritorno) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna multimedia artista:" + Ritorno);

        GestioneImmagini.getInstance().ImpostaUltimaImmagine(false);
        GestioneImmagini.getInstance().SfumaImmagine(true);
    }

    public void RitornaDettaglioBrano(final String Ritorno, final int NumeroOperazione) {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();

        VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setQuanteVolteAscoltato(-1);
        VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setStelle(-1);
        VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTesto("");
        VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTestoTradotto("");

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna dettaglio brano. ImpostaStelleAscoltata in Home");
        GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna dettaglio brano. SettaTesto in Home");
        GestioneTesti gt = new GestioneTesti();
        gt.SettaTesto(false);
    }

    public void EliminaCanzone(String Ritorno, int NumeroOperazione) {
        String Appoggio=ToglieTag(Ritorno);

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Elimina canzone: " + Appoggio);

        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                "Errore nell'eliminazione del brano",
                true, VariabiliStaticheGlobali.NomeApplicazione);
    }

    public void RitornaBranoConAttesa(String Ritorno, int NumeroOperazione, boolean inBackGround) {
        if (!inBackGround) {
            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.folder);
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "ERRORE Ritorna brano. Prendo il successivo senza rete");

            // int numeroAttuale = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
            // int NumeroBranoProssimo = numeroAttuale;
            // while (NumeroBranoProssimo == numeroAttuale) {
                int NumeroBranoProssimo = GestioneListaBrani.getInstance().BranoSenzarete();
            // }

            VariabiliStaticheGlobali.getInstance().getDatiGenerali()
                    .getConfigurazione().setQualeCanzoneStaSuonando(NumeroBranoProssimo);
            // VariabiliStaticheGlobali.getInstance().setBranoImpostatoSenzaRete(-1);
            GestioneCaricamentoBraniNuovo.getInstance().ImpostaProssimaCanzone();
            GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano2();
            VariabiliStaticheGlobali.getInstance().setImpostatoBranoPerProblemiDiRete(true);

            VariabiliStaticheGlobali.getInstance().setStaAttendendoFineDownload(false);
        } else {
            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.folder);
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

            // boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
            // ceRete = false;
            // int NumeroBranoProssimo = -1;
            // if (ceRete) {
            //     NumeroBranoProssimo = GestioneListaBrani.getInstance()
            //             .RitornaNumeroProssimoBranoNuovo(false);
            // } else {

            // int numeroAttuale = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
            // int NumeroBranoProssimo = numeroAttuale;
            // while (NumeroBranoProssimo == numeroAttuale) {
                int NumeroBranoProssimo = GestioneListaBrani.getInstance().BranoSenzarete();
            // }
            // }
            VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(NumeroBranoProssimo);
            VariabiliStaticheGlobali.getInstance().setImpostatoBranoPerProblemiDiRete(true);

            // int NumeroBrano = NumeroBranoProssimo;
            // VariabiliStaticheGlobali.getInstance().getDatiGenerali()
            //         .getConfigurazione().setQualeCanzoneStaSuonando(NumeroBranoProssimo);

            // VariabiliStaticheGlobali.getInstance().getDatiGenerali()
            //         .getConfigurazione().setQualeCanzoneStaSuonando(NumeroBranoProssimo);
            // GestioneCaricamentoBraniNuovo.getInstance().ImpostaProssimaCanzone();
            // GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano2();

            VariabiliStaticheGlobali.getInstance().setStaAttendendoFineDownload(false);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "ERRORE Ritorna brano. Imposto brano successivo senza rete");
        }
    }

    public void RitornaModificaBellezza(final String Ritorno, int NumeroOperazione) {
    }

    public void RitornaVolteAscoltata(final String Ritorno, int NumeroOperazione) {
    }
}
