package looigi.loowebplayer.thread;

import android.os.Handler;
import android.os.Looper;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dialog.DialogMessaggio;
// import looigi.loowebplayer.soap.CheckURLFile;
import looigi.loowebplayer.soap.DownloadMP3Nuovo;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.PronunciaFrasi;
import looigi.loowebplayer.utilities.Utility;

public class ScaricoBranoEAttesa {
    private Runnable runRiga;
    private Handler hSelezionaRiga;
    private Runnable rAttendeRispostaCheckURL;
    private Handler hAttendeRispostaCheckURL;
    private boolean inBackground;
    private int Secondi;
    private int Conta;
    private int PerPronuncia;
    private int nn;
    // private CheckURLFile cuf;
    private String Altro="";

    public void setInBackground(boolean inBackground) {
        this.inBackground = inBackground;
    }

    /* public void AttesaScaricamentoBrano(String Appoggio, Integer NumeroOperazione) {
        if (inBackground) {
            Altro=" in background";
        } else {
            Altro ="";
        }

        VariabiliStaticheGlobali.getInstance().setLastRitorno(Appoggio);
        final int NumeroBrano;

        if (!inBackground) {
            NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        } else {
            NumeroBrano = VariabiliStaticheGlobali.getInstance().getNumeroBranoNuovo();
        }

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "Ritorna brano" + Altro + ".");

        final String Brano[] = Appoggio.split("\\\\", -1);

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "Ritorna brano" + Altro + ". Ok: " + Appoggio);
        nn = NumeroOperazione;
        Secondi = 0;
        Conta = 0;
        PerPronuncia = 0;
        final int MaxTentativi = VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3() / VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3();

        nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, false,
                "Preparazione download brano" + Altro +". Tentativi: " + Integer.toString(MaxTentativi));

        hSelezionaRiga = new Handler(Looper.getMainLooper());
        hSelezionaRiga.postDelayed(runRiga = new Runnable() {
            @Override
            public void run() {
                if (!VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                    PerPronuncia++;
                    if (PerPronuncia == 5) {
                        PerPronuncia = 0;
                        PronunciaFrasi pf = new PronunciaFrasi();
                        pf.PronunciaFrase("Controllo " + Integer.toString(Secondi + 1) + " di " + Integer.toString(MaxTentativi), "ITALIANO");
                    }
                }

                if (inBackground) {
                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.download2);
                }

                String sAppoggio2 = Brano[5];
                if (Brano.length > 7) {
                    sAppoggio2 += " (" + Brano[7] + ")";
                }
                final String Appoggio2 = sAppoggio2;
                Secondi++;
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download Brano '" + Appoggio2 +
                        "'. Attesa esistenza file. Tentativi: " + Integer.toString(Secondi) + "/" + Integer.toString(MaxTentativi));
                nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, false,
                        "Download brano" + Altro + "\n" + Appoggio2 + ".\nControlli: " + Integer.toString(Secondi) + "/" + Integer.toString(MaxTentativi));

                String url = VariabiliStaticheGlobali.getInstance().PercorsoURL + "/";
                if (Brano[1].toUpperCase().contains("COMPRESSI")) {
                    url += "Compressi/";
                } else {
                    url += "Dati/";
                }
                if (!VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase().isEmpty()) {
                    url += VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase() + "/";
                }

                int campi = Brano.length - 1;
                final String sBrano = Brano[campi];
                final String sAlbum = Brano[campi - 1];
                final String sArtista = Brano[campi - 2];

                if (!url.contains(sArtista) && !url.contains(sAlbum)) {
                    url += sArtista + "/" + sAlbum + "/" + sBrano;
                } else {
                    url += sBrano;
                }

                VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("");

                cuf = new CheckURLFile();
                cuf.setNumeroBrano(NumeroBrano);
                cuf.startControl(url);

                // VariabiliStaticheNuove.getInstance().setCuf(cuf);

                hAttendeRispostaCheckURL = new Handler(Looper.getMainLooper());
                hAttendeRispostaCheckURL.postDelayed(rAttendeRispostaCheckURL = new Runnable() {
                    @Override
                    public void run() {
                        if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando() &&
                                VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() == -1) {
                            if (cuf !=null) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                        "Stoppo CUF normale per numero brano " + Altro + " diverso dall'attuale");

                                cuf.StoppaEsecuzione(true);
                            }
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                    "Download Brano"+Altro+". Cambio brano");
                            nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true, "Download brano. Cambio brano");
                            hAttendeRispostaCheckURL.removeCallbacks(rAttendeRispostaCheckURL);
                            hAttendeRispostaCheckURL = null;
                            hSelezionaRiga.removeCallbacks(runRiga);
                            hSelezionaRiga = null;
                        } else {
                            hAttendeRispostaCheckURL.removeCallbacks(rAttendeRispostaCheckURL);

                            if (VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL().contains("OK")) {
                                if (cuf!=null) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                            "Stoppo CUF normale per OK");

                                    cuf.StoppaEsecuzione(false);
                                }

                                nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true,
                                        "Download brano. Scaricamento");
                                if (inBackground) {
                                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.salva);
                                }

                                hSelezionaRiga.removeCallbacks(runRiga);
                                hSelezionaRiga = null;

                                ScaricaBrano(NumeroBrano, Brano, nn, inBackground);

                                VariabiliStaticheGlobali.getInstance().setSbea(null);
                            } else {
                                if (VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL().contains("ESECUZIONE TERMINATA CON ESITO NEGATIVO")) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                            "Download Brano" +Altro+": " + VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL());

                                    hAttendeRispostaCheckURL = null;
                                    // VariabiliStaticheNuove.getInstance().setD(null);
                                    VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(nn, true);
                                    if (cuf!=null) {
                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                                "Stoppo CUF normale per ESECUZIONE TERMINATA CON ESITO NEGATIVO");

                                        cuf.StoppaEsecuzione(true);
                                    }
                                    // VariabiliStaticheNuove.getInstance().setCuf(null);
                                    GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                                    if (inBackground) {
                                        GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ko);
                                    }

                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                            "Tento di prendere il prossimo brano fra quelli già scaricati");
                                    VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                                    int NumeroBrano=GestioneListaBrani.getInstance().CercaBranoGiaScaricato(false);
                                    VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(NumeroBrano);
                                    VariabiliStaticheGlobali.getInstance().setBranoImpostatoSenzaRete(NumeroBrano);
                                    // GestioneListaBrani.getInstance().AggiungeBrano(NumeroBrano);

                                    StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
                                    final String NomeBrano = s.getNomeBrano();
                                    String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();

                                    VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista +")");

                                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.folder);

                                    ScaricaBrano(NumeroBrano, Brano, nn, inBackground);

                                    VariabiliStaticheGlobali.getInstance().setSbea(null);

                                    hSelezionaRiga.removeCallbacks(runRiga);
                                    hSelezionaRiga = null;
                                } else {
                                    if (Secondi <= MaxTentativi) {
                                        Secondi++;
                                        nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, false,
                                                "Download brano"+Altro+"\n" + Appoggio2 + ".\nControlli: " + Integer.toString(Secondi) + "/" + Integer.toString(MaxTentativi));

                                        hSelezionaRiga.postDelayed(runRiga, VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3());
                                    } else {
                                        // VariabiliStaticheNuove.getInstance().setD(null);
                                        if (cuf!=null) {
                                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                                    "Stoppo CUF normale per tentativi esauriti");

                                            cuf.StoppaEsecuzione(true);
                                        }
                                        // VariabiliStaticheNuove.getInstance().setCuf(null);
                                        GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                                        if (inBackground) {
                                            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ko);
                                        }

                                        hSelezionaRiga.removeCallbacks(runRiga);
                                        hSelezionaRiga = null;

                                        VariabiliStaticheGlobali.getInstance().setSbea(null);

                                        nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true,
                                                "Download brano"+Altro+"\n'" + Appoggio2 + "'. Tentativi esauriti");
                                        //***BRANO SCARICATO***

                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                                "Tento di prendere il prossimo brano fra quelli già scaricati");
                                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                                        int NumeroBrano=GestioneListaBrani.getInstance().CercaBranoGiaScaricato(false);
                                        VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(NumeroBrano);
                                        VariabiliStaticheGlobali.getInstance().setBranoImpostatoSenzaRete(NumeroBrano);
                                        // GestioneListaBrani.getInstance().AggiungeBrano(NumeroBrano);

                                        StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
                                        final String NomeBrano = s.getNomeBrano();
                                        String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();

                                        VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista +")");

                                        GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.folder);

                                        ScaricaBrano(NumeroBrano, Brano, nn, inBackground);

                                        VariabiliStaticheGlobali.getInstance().setSbea(null);
                                    }
                                }
                            }
                            // }
                        }
                    }
                }, 500);
            }
        }, VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3());
    } */

    public void ScaricaBrano(int NumeroBrano, String[] Brano, int NumeroOperazione, boolean inBackground) {
        // PronunciaFrasi pf = new PronunciaFrasi();
        // pf.PronunciaFrase("Scarico brano"+Altro, "ITALIANO");

        int campi = Brano.length - 1;
        String url = VariabiliStaticheGlobali.getInstance().PercorsoURL + "/";
        boolean compresso = false;
        // if (Brano[campi].toUpperCase().contains("COMPRESSI")) {
        if (Brano[1].toUpperCase().contains("COMPRESSI")) {
            compresso = true;
            url += "Compressi/";
        } else {
            url += "Dati/";
        }
        if (!VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase().isEmpty()) {
            url += VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase() + "/";
        }

        if (Brano.length > 2) {
            String sBrano = Brano[campi];
            String sAlbum = Brano[campi - 1];
            String sArtista = Brano[campi - 2];

            if (!url.contains(sArtista) && !url.contains(sAlbum)) {
                url += sArtista + "/" + sAlbum + "/" + sBrano;
            } else {
                url += sBrano;
            }

            if (inBackground) {
                GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.salva);
            }

            DownloadMP3Nuovo d = new DownloadMP3Nuovo();
            String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();

            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
            if (s != null) {
                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();

                if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
                    d.setPath(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + sArtista + "/" + sAlbum);
                } else {
                    d.setPath(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/");
                }

                d.setNomeBrano(sBrano);
                d.setCompresso(compresso);
                if (inBackground) {
                    d.setAutomatico(true);
                } else {
                    d.setAutomatico(false);
                }
                d.setNumeroBrano(NumeroBrano);
                d.setContext(VariabiliStaticheGlobali.getInstance().getContext());
                d.startDownload(url, NumeroOperazione);
            } else {
                DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                        "Struttura vuota in ScaricaBrano. Numero Brano: " + NumeroBrano,
                        true,
                        VariabiliStaticheGlobali.NomeApplicazione);
            }
        } else {
            String bb = "";

            for (String b : Brano) {
                bb += (b + ";");
            }
            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                    "Errore durante la routine ScaricaBrano.\nBrano: " + bb,
                    true,
                    VariabiliStaticheGlobali.NomeApplicazione);
        }
    }
}
