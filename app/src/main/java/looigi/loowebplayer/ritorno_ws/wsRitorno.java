/* package looigi.loowebplayer.ritorno_ws;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaImmagini;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaVideo;
import looigi.loowebplayer.db_remoto.DBRemoto;
import looigi.loowebplayer.maschere.Utenza;
import looigi.loowebplayer.soap.CheckURLFile;
import looigi.loowebplayer.soap.EsecuzioneChiamateWEB;
import looigi.loowebplayer.thread.NetThread;
import looigi.loowebplayer.utilities.GestioneCaricamentoBrani;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.GestioneTesti;
import looigi.loowebplayer.utilities.GestioneVideo;
import looigi.loowebplayer.utilities.PronunciaFrasi;

public class wsRitorno {
    private Runnable runRiga;
    private Handler hSelezionaRiga;
    private Runnable rAttendeRispostaCheckURL;
    private Handler hAttendeRispostaCheckURL;
    private int Secondi;
    private int PerPronuncia;
    private int nn;

    private String ToglieTag(String Cosa) {
        return Cosa;
    }

    public void RitornaListaBrani(final String Ritorno, final int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna lista brani");

        if (Ritorno.equals("PROSEGUI")) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna lista brani. KO");
        } else {
            hSelezionaRiga = new Handler(Looper.getMainLooper());
            hSelezionaRiga.postDelayed(runRiga = new Runnable() {
                @Override
                public void run() {
                    hSelezionaRiga.removeCallbacks(runRiga);
                    runRiga = null;

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna lista brani. OK");
                    GestioneFiles.getInstance().EliminaFile(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Lista.dat");

                    int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download testo");

                    int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
                    EsecuzioneChiamateWEB e = new EsecuzioneChiamateWEB();
                    e.EsegueChiamataTesto(NumeroBrano, Ritorno, VariabiliStaticheGlobali.getInstance().getTimeOutListaBrani(),
                            new Date(System.currentTimeMillis()), n);
                }
            }, 50);
        }
    }

    public void RitornaDatiUtente(final String Ritorno, int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dati utente.");

        if (Ritorno.equals("PROSEGUI")) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dati utente. KO");
        } else {
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dati utente. OK");
            Utenza.RitornaUtente(Ritorno);
        }
    }

    public void RitornaMultimediaArtista(int NumeroBrano, final String Ritorno, int NumeroOperazione) {
        if (Ritorno.equals("PROSEGUI")) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna multimedia artista. KO");

            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

            VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(false);
            VariabiliStaticheGlobali.getInstance().setUltimaCanzoneSuonata(-1);
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna multimedia artista. ProsegueCaricaBrano2 in Home");
            GestioneCaricamentoBrani.getInstance().ProsegueCaricaBrano2(NumeroOperazione);
        } else {
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

            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(Integer.parseInt(idArtista)).getArtista();

            if (imm.size() > 0) {
                for (StrutturaImmagini i : imm) {
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(Integer.parseInt(idArtista)).getImmagini().add(i);
                }

                GestioneImmagini.getInstance().SalvaImmaginiSuSD(Artista, imm);
                if (VariabiliStaticheHome.getInstance().getImms().size() == 0) {
                    VariabiliStaticheHome.getInstance().setImms(GestioneImmagini.getInstance().RitornaImmaginiArtista("", Artista, -1, true));
                }
                if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                    GestioneImmagini.getInstance().CreaCarosello();
                }
            }

            if (vid.size() > 0) {
                for (StrutturaVideo v : vid) {
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(Integer.parseInt(idArtista)).getVideo().add(v);
                }
                GestioneVideo.getInstance().SalvaVideoSuSD(Artista, vid);
            }

            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna multimedia artista. ProsegueCaricaBrano2 in Home");
            GestioneCaricamentoBrani.getInstance().ProsegueCaricaBrano2(NumeroOperazione);
        }
    }

    public void RitornaDettaglioBrano(final int NumeroBrano, final String Ritorno, final int NumeroOperazione) {
        if (Ritorno.equals("PROSEGUI")) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. KO");

            VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setQuanteVolteAscoltato(0);
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setStelle(0);
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTesto("");
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTestoTradotto("");

            int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, "Ritorno vuoto");

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. ImpostaStelleAscoltata in Home da Ritorno vuoto");
            GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. SettaTesto in Home da Ritorno vuoto");
            GestioneTesti gt = new GestioneTesti();
            gt.SettaTesto(true);
            VariabiliStaticheGlobali.getInstance().setUltimaCanzoneSuonata(-1);
            VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(false);
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano");
            String Appoggio = ToglieTag(Ritorno);

            // int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
            if (Appoggio.toUpperCase().contains("ERROR:") || Appoggio.trim().isEmpty()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. Errore: " + Appoggio);
                // if (MostraMessaggio) {
                // DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                //         Appoggio, true, VariabiliStaticheGlobali.NomeApplicazione);

                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setQuanteVolteAscoltato(0);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setStelle(0);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTesto("");
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTestoTradotto("");

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. ImpostaStelleAscoltata in Home da Errore");
                GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

                int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, Appoggio);

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. SettaTesto in Home da Errore");
                GestioneTesti.getInstance().SettaTesto(true);
                // }
            } else {
                if (Appoggio.contains(";;;")) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. Ritorno vuoto");
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setQuanteVolteAscoltato(0);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setStelle(0);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTesto("");
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTestoTradotto("");

                    int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, "Ritorno vuoto");

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. ImpostaStelleAscoltata in Home da Ritorno vuoto");
                    GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. SettaTesto in Home da Ritorno vuoto");
                    GestioneTesti.getInstance().SettaTesto(true);
                } else {
                    String t[] = Appoggio.split(";", -1);
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. Oggetti: " + Integer.toString(t.length));

                    String Artista = t[0];
                    String Album = t[1];
                    String Brano = t[2];
                    String Testo = t[3];
                    String TestoTradotto = t[4];
                    int Ascoltata = Integer.parseInt(t[5]);
                    int Bellezza = Integer.parseInt(t[6]);

                    GestioneTesti.getInstance().SalvaTestoSuSD(Artista, Album, Brano, Testo, TestoTradotto,
                            Integer.toString(Ascoltata), Integer.toString(Bellezza));

                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setQuanteVolteAscoltato(Ascoltata);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setStelle(Bellezza);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTesto(Testo);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setTestoTradotto(TestoTradotto);

                    int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, "OK");

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. ImpostaStelleAscoltata in Home");
                    GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. SettaTesto in Home");
                    GestioneTesti.getInstance().SettaTesto(false);
                }
            }
        }

        if (!VariabiliStaticheHome.getInstance().getBranoDaCaricare().isEmpty()) {
            // if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
            hSelezionaRiga = new Handler(Looper.getMainLooper());
            hSelezionaRiga.postDelayed(runRiga = new Runnable() {
                @Override
                public void run() {
                    hSelezionaRiga.removeCallbacks(runRiga);
                    runRiga = null;

                    String c[] = VariabiliStaticheHome.getInstance().getBranoDaCaricare().split(";", -1);
                    String Converte = "N";
                    String Qualita = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getRapportoCompressione();
                    if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                        Converte = "S";
                    }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. Ritorna brano: " +
                            VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase() + " " +
                            c[0] + " " +
                            c[1] + " " +
                            c[2] + " " +
                            Converte + " " +
                            Qualita
                    );

                    int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Download brano");

                    DBRemoto dbr = new DBRemoto();
                    dbr.RitornaBrano(VariabiliStaticheHome.getInstance().getContext(),
                            VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase(),
                            c[0],
                            c[1],
                            c[2],
                            Converte,
                            Qualita,
                            n
                    );
                }
            }, 50);
        }
    }

    public void RitornaBrano(final String Ritorno, final int NumeroOperazione, final int NumeroBrano) {
        if (Ritorno.equals("PROSEGUI")) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna brano. KO");
            nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, "Download brano. KO");
            if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                VariabiliStaticheHome.getInstance().getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop_dis());
                VariabiliStaticheHome.getInstance().getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay());
                VariabiliStaticheGlobali.getInstance().setStaSuonando(false);
            }
            VariabiliStaticheGlobali.getInstance().setUltimaCanzoneSuonata(-1);
            VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(false);
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna brano.");
            String Appoggio = ToglieTag(Ritorno);

            final String Brano[] = Appoggio.split("\\\\", -1);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna brano. Ok: " + Appoggio);
            nn = NumeroOperazione;
            Secondi = 0;
            PerPronuncia = 0;
            final int MaxTentativi = VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3() / VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3();

            nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, false, "Preparazione download brano in background. Tentativi: " + Integer.toString(MaxTentativi));

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

                    final String Appoggio2 = Brano[5] + " (" + Brano[3] + ")";
                    Secondi++;
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download Brano '" + Appoggio2 + "'. Attesa esistenza file. Tentativi: " + Integer.toString(Secondi) + "/" + Integer.toString(MaxTentativi));
                    nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, false, "Download brano\n" + Appoggio2 + ".\nControlli: " + Integer.toString(Secondi) + "/" + Integer.toString(MaxTentativi));

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
                    String sBrano = Brano[campi];
                    String sAlbum = Brano[campi - 1];
                    String sArtista = Brano[campi - 2];

                    if (!url.contains(sArtista) && !url.contains(sAlbum)) {
                        url += sArtista + "/" + sAlbum + "/" + sBrano;
                    } else {
                        url += sBrano;
                    }

                    VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("");

                    final CheckURLFile cuf = new CheckURLFile();
                    cuf.setNumeroBrano(NumeroBrano);
                    cuf.startControl(url);

                    hAttendeRispostaCheckURL = new Handler();
                    hAttendeRispostaCheckURL.postDelayed(rAttendeRispostaCheckURL = new Runnable() {
                        @Override
                        public void run() {
                            if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                cuf.StoppaEsecuzione();
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download Brano. Cambio brano");
                                nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true, "Download brano. Cambio brano");
                                hAttendeRispostaCheckURL.removeCallbacks(rAttendeRispostaCheckURL);
                                hAttendeRispostaCheckURL = null;
                                hSelezionaRiga.removeCallbacks(runRiga);
                                hSelezionaRiga = null;
                                rAttendeRispostaCheckURL = null;
                            } else {
                                if (VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL().isEmpty()) {
                                    hAttendeRispostaCheckURL.postDelayed(rAttendeRispostaCheckURL, 500);
                                } else {
                                    hAttendeRispostaCheckURL.removeCallbacks(rAttendeRispostaCheckURL);

                                    if (VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL().contains("OK")) {
                                        cuf.StoppaEsecuzione();
                                        // GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                                        PronunciaFrasi pf = new PronunciaFrasi();
                                        pf.PronunciaFrase("Scarico brano", "ITALIANO");

                                        hSelezionaRiga.removeCallbacks(runRiga);
                                        hSelezionaRiga = null;

                                        int NBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
                                        EsecuzioneChiamateWEB e = new EsecuzioneChiamateWEB();
                                        e.EsegueChiamataMP3(NBrano, Brano, "Download MP3", VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
                                                new Date(System.currentTimeMillis()), false, nn);
                                    } else {
                                        if (VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL().contains("BRANO DIVERSO")) {
                                            cuf.StoppaEsecuzione();
                                            VariabiliStaticheGlobali.getInstance().setEsciDaCheckFile(false);
                                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download Brano. Cambio brano");
                                            nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true, "Download brano. Cambio brano");
                                            hAttendeRispostaCheckURL.removeCallbacks(rAttendeRispostaCheckURL);
                                            hAttendeRispostaCheckURL = null;
                                            hSelezionaRiga.removeCallbacks(runRiga);
                                            hSelezionaRiga = null;
                                        } else {
                                            if (Secondi <= MaxTentativi) {
                                                hSelezionaRiga.postDelayed(runRiga, VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3());
                                            } else {
                                                cuf.StoppaEsecuzione();
                                                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                                                hSelezionaRiga.removeCallbacks(runRiga);
                                                hSelezionaRiga = null;

                                                nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true, "Download brano\n'" + Appoggio2 + "'. Tentativi esauriti");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }, 500);
                }
            }, VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3());
        }
    }

    public void RitornaBranoBackground(final String Ritorno, final int NumeroOperazione, final int NumeroBrano) {
        if (Ritorno.equals("PROSEGUI")) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna brano in background. KO");
            nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true, "Download brano in background. KO");

            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ko);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download brano in background KO");
            if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                NetThread.getInstance().setCaroselloBloccato(false);

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download brano in background KO. Cerco eventuale brano già scaricato.");
                int brano = GestioneListaBrani.getInstance().CercaBranoGiaScaricato(true);
                if (brano>-1) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download brano in background KO. Cerco eventuale brano già scaricato. OK: " + Integer.toString(brano));
                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ok);
                } else {
                    VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText("Nessun brano caricato");
                }
            }
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna brano in background.");
            String Appoggio = ToglieTag(Ritorno);

            final String Brano[] = Appoggio.split("\\\\", -1);

            nn = NumeroOperazione;
            Secondi = 0;
            PerPronuncia = 0;
            final int MaxTentativi = VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3() / VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3();

            nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, false, "Preparazione download brano in background. Tentativi: " + Integer.toString(MaxTentativi));

            hSelezionaRiga = new Handler(Looper.getMainLooper());
            hSelezionaRiga.postDelayed(runRiga = new Runnable() {
                @Override
                public void run() {
                    if (!VariabiliStaticheGlobali.getInstance().getStaSuonando() || VariabiliStaticheGlobali.getInstance().getStaAttendendoConMusichetta()) {
                        PerPronuncia++;
                        if (PerPronuncia == 5) {
                            PerPronuncia = 0;
                            PronunciaFrasi pf = new PronunciaFrasi();
                            pf.PronunciaFrase("Controllo " + Integer.toString(Secondi + 1) + " di " + Integer.toString(MaxTentativi), "ITALIANO");
                        }
                    }

                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.download2);

                    final String Appoggio2 = Brano[5] + " (" + Brano[3] + ")";
                    Secondi++;
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download Brano Background '" + Appoggio2 + "'. Attesa esistenza file. Tentativi: " + Integer.toString(Secondi) + "/" + Integer.toString(MaxTentativi));
                    nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, false, "Download brano in background\n" + Appoggio2 + "\nControlli: " + Integer.toString(Secondi) + "/" + Integer.toString(MaxTentativi));

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
                    String sBrano = Brano[campi];
                    String sAlbum = Brano[campi - 1];
                    String sArtista = Brano[campi - 2];

                    if (!url.contains(sArtista) && !url.contains(sAlbum)) {
                        url += sArtista + "/" + sAlbum + "/" + sBrano;
                    } else {
                        url += sBrano;
                    }

                    VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("");

                    final CheckURLFile cuf = new CheckURLFile();
                    VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("");
                    cuf.setNumeroBrano(NumeroBrano);
                    cuf.startControl(url);

                    hAttendeRispostaCheckURL = new Handler();
                    hAttendeRispostaCheckURL.postDelayed(rAttendeRispostaCheckURL = new Runnable() {
                        @Override
                        public void run() {
                            if (VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL().isEmpty()) {
                                hAttendeRispostaCheckURL.postDelayed(rAttendeRispostaCheckURL, 500);
                            } else {
                                hAttendeRispostaCheckURL.removeCallbacks(rAttendeRispostaCheckURL);

                                if (VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL().contains("OK")) {
                                    cuf.StoppaEsecuzione();
                                    // GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                                    nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true, "Download brano. Scaricamento");
                                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.salva);

                                    hSelezionaRiga.removeCallbacks(runRiga);
                                    hSelezionaRiga = null;

                                    int NBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
                                    EsecuzioneChiamateWEB e = new EsecuzioneChiamateWEB();
                                    e.EsegueChiamataMP3(NBrano, Brano, "Download MP3", VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
                                            new Date(System.currentTimeMillis()), true, nn);
                                } else {
                                    if (VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL().contains("BRANO DIVERSO")) {
                                        cuf.StoppaEsecuzione();

                                        VariabiliStaticheGlobali.getInstance().setEsciDaCheckFile(false);
                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download Brano. Cambio brano");
                                        nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true, "Download brano. Cambio brano");
                                        hAttendeRispostaCheckURL.removeCallbacks(rAttendeRispostaCheckURL);
                                        hAttendeRispostaCheckURL = null;
                                        hSelezionaRiga.removeCallbacks(runRiga);
                                        hSelezionaRiga = null;
                                    } else {
                                        if (Secondi <= MaxTentativi) {
                                            hSelezionaRiga.postDelayed(runRiga, VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3());
                                        } else {
                                            cuf.StoppaEsecuzione();

                                            GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                                            hSelezionaRiga.removeCallbacks(runRiga);
                                            hSelezionaRiga = null;

                                            nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(nn, true, "Download brano in background\n" + Appoggio2 + ".\nTentativi esauriti");

                                            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ko);

                                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download brano in background\n" + Appoggio2 + "\nTentativi esauriti");
                                            if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                                                VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                                                NetThread.getInstance().setCaroselloBloccato(false);

                                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download brano in background. Cerco eventuale brano già scaricato.");
                                                int brano = GestioneListaBrani.getInstance().CercaBranoGiaScaricato(true);
                                                if (brano>-1) {
                                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download brano in background. Cerco eventuale brano già scaricato. OK: " + Integer.toString(brano));
                                                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ok);
                                                } else {
                                                    VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText("Nessun brano caricato");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // }
                        }
                    }, 50);
                }
            }, VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3());
        }
    }
}
*/