package looigi.loowebplayer.ritorno_ws;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

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
    private Runnable runRiga;
    private Handler hSelezionaRiga;
    private Runnable rAttendeRispostaCheckURL;
    private Handler hAttendeRispostaCheckURL;
    private int Secondi;
    private int Conta;
    private int PerPronuncia;
    private int nn;

    private String ToglieTag(String Cosa) {
        return Cosa;
    }

    public void RitornaVersioneApplicazione(String Ritorno, int NumeroOperazione) {
        String Appoggio=ToglieTag(Ritorno);

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna versione applicazione: " + Appoggio);

        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

        if (Appoggio.toUpperCase().contains("ERROR:")) {
            //DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(), Appoggio, true, "Cv Calcio");
        } else {
            // ControlloVersioneApplicazione.ControlloVersione(Appoggio);
        }
    }

    public void RitornaListaBrani(final String Ritorno) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna lista brani: " + Ritorno);

        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                "Errore nel ritorno della lista brani:\n" + Ritorno,
                true,
                VariabiliStaticheGlobali.NomeApplicazione);
        System.exit(0);
    }

    public void RitornaDatiUtente(final String Ritorno, int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna dati utente:" + Ritorno);

        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                "Errore nel ritorno dell'utente:\n" + Ritorno,
                true,
                VariabiliStaticheGlobali.NomeApplicazione);
        System.exit(0);
    }

    public void RitornaMultimediaArtista(final String Ritorno) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
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

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna dettaglio brano. ImpostaStelleAscoltata in Home");
        GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna dettaglio brano. SettaTesto in Home");
        GestioneTesti gt = new GestioneTesti();
        gt.SettaTesto(false);
    }

    public void RitornaBranoConAttesa(String Ritorno, int NumeroOperazione, boolean inBackGround) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "ERRORE Ritorna brano. Prendo il successivo senza rete");

        int NumeroBranoProssimo = GestioneListaBrani.getInstance().BranoSenzarete();

        VariabiliStaticheGlobali.getInstance().getDatiGenerali()
                .getConfigurazione().setQualeCanzoneStaSuonando(NumeroBranoProssimo);
        VariabiliStaticheGlobali.getInstance().setBranoImpostatoSenzaRete(-1);
        GestioneCaricamentoBraniNuovo.getInstance().ImpostaProssimaCanzone();
        // if (NumeroBrano == VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
        GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano2();

        // int brano = GestioneListaBrani.getInstance().CercaBranoGiaScaricato(false);
        // if (brano > -1) {
        //     StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(brano);
        //     final String NomeBrano = s.getNomeBrano();
        //     String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
//
        //     VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista +")");
        // } else {
        //     DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
        //             "Errore nel ritorno brano:\n" + Ritorno,
        //             true,
        //             VariabiliStaticheGlobali.NomeApplicazione);
        // }
    }

    public void RitornaModificaBellezza(final String Ritorno, int NumeroOperazione) {
    }

    public void RitornaVolteAscoltata(final String Ritorno, int NumeroOperazione) {
    }
}
