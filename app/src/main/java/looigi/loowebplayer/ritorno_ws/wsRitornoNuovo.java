package looigi.loowebplayer.ritorno_ws;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaImmagini;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaMembri;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaVideo;
import looigi.loowebplayer.dialog.DialogMessaggio;
import looigi.loowebplayer.maschere.Utenza;
// import looigi.loowebplayer.soap.CheckURLFile;
import looigi.loowebplayer.nuova_versione.ControlloVersioneApplicazione;
import looigi.loowebplayer.soap.DownloadMP3Nuovo;
import looigi.loowebplayer.soap.DownloadTextFileNuovo;
import looigi.loowebplayer.soap.GestioneWEBServiceSOAPNuovo;
import looigi.loowebplayer.thread.ScaricoBranoEAttesa;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.GestioneTesti;
import looigi.loowebplayer.utilities.PronunciaFrasi;
import looigi.loowebplayer.utilities.Utility;

public class wsRitornoNuovo {
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
                "Ritorna versione applicazione: " + Appoggio);

        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

        if (Appoggio.toUpperCase().contains("ERROR:")) {
            //DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(), Appoggio, true, "Cv Calcio");
        } else {
            ControlloVersioneApplicazione.ControlloVersione(Appoggio);
        }
    }

    public void RitornaListaBrani(final String Ritorno) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "Ritorna lista brani");

        hSelezionaRiga = new Handler(Looper.getMainLooper());
        hSelezionaRiga.postDelayed(runRiga = new Runnable() {
            @Override
            public void run() {
                hSelezionaRiga.removeCallbacks(runRiga);
                runRiga = null;

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                        new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Ritorna lista brani. OK");
                GestioneFiles.getInstance().EliminaFile(
                        VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Lista.dat");

                int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download testo");

                DownloadTextFileNuovo d = new DownloadTextFileNuovo();
                d.setPath(VariabiliStaticheGlobali.getInstance().PercorsoDIR);
                d.setPathNomeFile("Lista.dat");
                d.setOperazione("Lettura lista brani");
                d.setContext(VariabiliStaticheGlobali.getInstance().getContext());
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                        new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Ritorna lista brani. Scarico lista mp3: " + VariabiliStaticheGlobali.getInstance().PercorsoURL + "/" + Ritorno.replace("\\", "/"));
                d.startDownload(
                        VariabiliStaticheGlobali.getInstance().PercorsoURL + "/" + Ritorno.replace("\\", "/"),
                        true, n);
            }
        }, 50);
    }

    public void RitornaDatiUtente(final String Ritorno, int NumeroOperazione) {
        if (Ritorno.contains("ERROR:")) {
            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                    Ritorno,
                    true,
                    VariabiliStaticheGlobali.NomeApplicazione);
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Ritorna dati utente. OK");
            Utenza.RitornaUtente(Ritorno);
        }
    }

    public void RitornaMultimediaArtista(final String Ritorno) {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
        String idArtista = Integer.toString(s.getIdArtista());
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna multimedia artista");
        String Appoggio = ToglieTag(Ritorno);

        String Oggetti[] = Appoggio.split("ç", -1);

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna multimedia artista. Oggetti: " + Integer.toString(Oggetti.length));
        String tipologia = "";
        List<StrutturaImmagini> imm = new ArrayList<>();
        List<StrutturaVideo> vid = new ArrayList<>();

        for (String oggetto : Oggetti) {
            if (!oggetto.isEmpty()) {
                String righe[] = oggetto.split("§", -1);
                tipologia = righe[0].replace("***", "");
                for (String riga : righe) {
                    if (!riga.isEmpty() && !riga.contains("***")) {
                        String campi[] = riga.split(";", -1);
                        switch (tipologia) {
                            case "IMMAGINI":
                                StrutturaImmagini i = new StrutturaImmagini();
                                i.setIdCartella(-1);
                                i.setNomeImmagine(campi[0]);
                                try {
                                    i.setLunghezza(Integer.parseInt(campi[1]));
                                } catch (Exception ignored) {

                                }

                                imm.add(i);
                                break;
                            case "VIDEO":
                                StrutturaVideo v = new StrutturaVideo();
                                v.setIdCartella(-1);
                                v.setNomeVideo(campi[0]);
                                v.setLunghezza(Integer.parseInt(campi[1]));

                                vid.add(v);
                                break;

                        }
                    }
                }
            }
        }

        // String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(Integer.parseInt(idArtista)).getArtista();

        if (imm.size() > 0) {
            // String ListaImmagini = "";

            for (StrutturaImmagini i : imm) {
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(Integer.parseInt(idArtista)).getImmagini().add(i);
                // ListaImmagini += i + ";";
            }

            // GestioneImmagini.getInstance().SalvaImmaginiSuSD(Artista, imm);
            // if (VariabiliStaticheHome.getInstance().getImms().size() == 0) {
            // }
            // if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
            //     GestioneImmagini.getInstance().CreaCarosello();
            // }
            // GestioneImmagini.getInstance().SalvaMultimediaArtista(ListaImmagini);
        }
        VariabiliStaticheHome.getInstance().setImms(imm);

        if (vid.size() > 0) {
            for (StrutturaVideo v : vid) {
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(Integer.parseInt(idArtista)).getVideo().add(v);
            }
            // GestioneVideo.getInstance().SalvaVideoSuSD(Artista, vid);
        }

        // GestioneImmagini.getInstance().CreaCarosello();
        GestioneImmagini.getInstance().SfumaImmagine(true);
    }

    public void RitornaDettaglioBrano(final String Ritorno, final int NumeroOperazione) {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "Ritorna dettaglio brano");
        String Appoggio = ToglieTag(Ritorno);

        // int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
        if (Appoggio.toUpperCase().contains("ERROR:") || Appoggio.trim().isEmpty()) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Ritorna dettaglio brano. Errore: " + Appoggio);
            // if (MostraMessaggio) {
            // DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
            //         Appoggio, true, VariabiliStaticheGlobali.NomeApplicazione);

            VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setQuanteVolteAscoltato(0);
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setStelle(0);
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTesto("");
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTestoTradotto("");

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Ritorna dettaglio brano. ImpostaStelleAscoltata in Home da Errore");
            GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

            int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, Appoggio);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Ritorna dettaglio brano. SettaTesto in Home da Errore");
            GestioneTesti gt = new GestioneTesti();
            gt.SettaTesto(true);
        } else {
            if (Appoggio.contains(";;;")) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Ritorna dettaglio brano. Ritorno vuoto");
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setQuanteVolteAscoltato(0);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setStelle(0);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTesto("");
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTestoTradotto("");

                int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                        "Ritorno vuoto");

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Ritorna dettaglio brano. ImpostaStelleAscoltata in Home da Ritorno vuoto");
                GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Ritorna dettaglio brano. SettaTesto in Home da Ritorno vuoto");
                GestioneTesti gt = new GestioneTesti();
                gt.SettaTesto(true);
            } else {
                String t[] = Appoggio.split(";", -1);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Ritorna dettaglio brano. Oggetti: " + Integer.toString(t.length));

                String Artista = t[0];
                String Album = t[1];
                String Brano = t[2];
                String Testo = t[3];
                String TestoTradotto = t[4];
                int Ascoltata = Integer.parseInt(t[5]);
                int Bellezza = Integer.parseInt(t[6]);

                GestioneTesti gt = new GestioneTesti();
                gt.SalvaTestoSuSD(Artista, Album, Brano, Testo, TestoTradotto,
                        Integer.toString(Ascoltata), Integer.toString(Bellezza));

                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setQuanteVolteAscoltato(Ascoltata);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setStelle(Bellezza);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTesto(Testo);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTestoTradotto(TestoTradotto);

                int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, "OK");

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. ImpostaStelleAscoltata in Home");
                GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. SettaTesto in Home");
                gt.SettaTesto(false);
            }
        }
    }

    public void RitornaBranoConAttesa(String Ritorno, int NumeroOperazione, boolean inBackGround) {
        // if (Ritorno.equals(VariabiliStaticheGlobali.getInstance().getLastRitorno())) {
        //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
        //             "Stesso brano. Evito la duplicazione della funzione.");
        //     return;
        // }

        int NumeroBrano = -1;

        if (!inBackGround) {
            NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        } else {
            NumeroBrano = VariabiliStaticheGlobali.getInstance().getNumeroBranoNuovo();
        }

        String Brano[] = Ritorno.split("\\\\", -1);

        ScaricoBranoEAttesa s = new ScaricoBranoEAttesa();
        s.ScaricaBrano(NumeroBrano, Brano, NumeroOperazione, inBackGround);
    }

    public void RitornaModificaBellezza(final String Ritorno, int NumeroOperazione) {
        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

        if (Ritorno.contains("ERROR:")) {
            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                    Ritorno,
                    true,
                    VariabiliStaticheGlobali.NomeApplicazione);
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Ritorna modifica bellezza. OK");
        }

        GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();
    }

    public void RitornaVolteAscoltata(final String Ritorno, int NumeroOperazione) {
        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

        if (Ritorno.contains("ERROR:")) {
            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                    Ritorno,
                    true,
                    VariabiliStaticheGlobali.NomeApplicazione);
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Ritorna volte ascoltata. OK");
        }

        GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();
    }
}
