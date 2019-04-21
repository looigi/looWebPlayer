package looigi.loowebplayer.utilities;

import android.os.Handler;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.dialog.DialogMessaggio;
import looigi.loowebplayer.ritorno_ws.wsRitornoNuovo;
import looigi.loowebplayer.soap.CheckURLFile;
import looigi.loowebplayer.soap.DownloadMP3Nuovo;
import looigi.loowebplayer.soap.GestioneWEBServiceSOAPNuovo;

public class GestioneCaricamentoBraniNuovo {
    private static final GestioneCaricamentoBraniNuovo ourInstance = new GestioneCaricamentoBraniNuovo();

    public static GestioneCaricamentoBraniNuovo getInstance() {
        return ourInstance;
    }

    private GestioneCaricamentoBraniNuovo() {
    }

    // private Runnable runScaricamentiBrano;
    // private Handler hProsegueScaricamentiBrano;

    private ArrayList<Handler> hAttesaDownload = new ArrayList<Handler>();
    private ArrayList<Runnable> rAttesaDownload = new ArrayList<Runnable>();
    private Handler hAttesaDownloadS;
    private Runnable rAttesaDownloadS;
    private Handler hAttesaProssimo;
    private Runnable rAttesaProssimo;
    private int nOperazione;
    private int secondi;

    private String pathBase;
    private String Artista;
    private String Album;
    private String NomeBrano;
    private String nb2;
    private boolean HaCaricatoBrano;

    private int stopCounter = 0;
    private int qualeWait;
    private int numOperazione;

    public boolean isHaCaricatoBrano() {
        return HaCaricatoBrano;
    }

    public void CaricaBrano() {
        if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
            GestioneListaBrani.getInstance().SuonaMusicaDiAttesa();
        }
        GestioneImmagini.getInstance().StoppaTimerCarosello();
        GestioneImmagini.getInstance().setImmagineDaCambiare("");
        GestioneImmagini.getInstance().setImmNumber(-1);
        GestioneImmagini.getInstance().ImpostaImmagineVuota();
        VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata("");

        if (VariabiliStaticheGlobali.getInstance().getnOperazioneATOW() != -1) {
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(VariabiliStaticheGlobali.getInstance().getnOperazioneATOW(), false);
            VariabiliStaticheGlobali.getInstance().setnOperazioneATOW(-1);
        }

        if (VariabiliStaticheHome.getInstance().getHandlerSeekBar() != null) {
            VariabiliStaticheHome.getInstance().getHandlerSeekBar().removeCallbacksAndMessages(null);
            VariabiliStaticheHome.getInstance().getHandlerSeekBar().removeCallbacks(VariabiliStaticheHome.getInstance().getrSeekBar());
            VariabiliStaticheHome.getInstance().setHandlerSeekBar(null);
        }

        CaricaBranoParteFinale();

        // Attesa fra un brano ed un altro per permettere lo skip senza effettuare caricamenti multipli
        final int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        secondi=0;
        numOperazione=VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, true,"Attesa brano successivo");

        hAttesaProssimo = new Handler();
        hAttesaProssimo.postDelayed(rAttesaProssimo = new Runnable() {
            @Override
            public void run() {
                if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                    if (hAttesaProssimo != null && rAttesaProssimo != null) {
                        hAttesaProssimo.removeCallbacks(rAttesaProssimo);
                        hAttesaProssimo = null;
                    }

                    CaricaBrano2();
                } else {
                    secondi++;
                    int ss = VariabiliStaticheGlobali.getInstance().getAttesaSecondiBranoSuccessivo();
                    numOperazione=VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(numOperazione, true,
                            "Attesa brano successivo. Secondi: "+Integer.toString(ss-secondi));
                    if (secondi>ss) {
                        // Non c'è skip... Proseguo
                        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(numOperazione, true);
                        if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                            // GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(false);
                            // VariabiliStaticheGlobali.getInstance().setStaGiaAttendendo(true);
                            secondi = 0;
                            VariabiliStaticheGlobali.getInstance().setnOperazioneATOW(VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Attesa termine download automatico"));
                            hAttesaDownloadS = new Handler();
                            hAttesaDownloadS.postDelayed(rAttesaDownloadS = new Runnable() {
                                @Override
                                public void run() {
                                    if (!VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                                        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(VariabiliStaticheGlobali.getInstance().getnOperazioneATOW(), false);
                                        VariabiliStaticheGlobali.getInstance().setnOperazioneATOW(-1);
                                        hAttesaDownloadS.removeCallbacks(rAttesaDownloadS);
                                        hAttesaDownloadS = null;
                                        CaricaBrano2();
                                    } else {
                                        VariabiliStaticheGlobali.getInstance().setnOperazioneATOW(VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(VariabiliStaticheGlobali.getInstance().getnOperazioneATOW(), false, "Attesa termine download automatico. Secondi: " + Integer.toString(secondi)));
                                        hAttesaDownloadS.postDelayed(rAttesaDownloadS, 1000);
                                        secondi++;
                                    }
                                }
                            }, 1000);
                        } else {
                            CaricaBrano2();
                        }
                    } else {
                        hAttesaProssimo.postDelayed(rAttesaProssimo, 1000);
                    }
                }
            }
        }, 1000);
        // Attesa fra un brano ed un altro per permettere lo skip senza effettuare caricamenti multipli

    }

    private void waitNull(final Object classe) {
        hAttesaDownload.add(new Handler());
        rAttesaDownload.add(new Runnable() {
            @Override
            public void run() {
                if (classe==null) {
                    stopCounter++;

                    hAttesaDownload.get(hAttesaDownload.size()-1).removeCallbacks(rAttesaDownload.get(rAttesaDownload.size()-1));
                    hAttesaDownload.set(hAttesaDownload.size()-1, null);
                } else {
                    hAttesaDownload.get(hAttesaDownload.size()-1).postDelayed(rAttesaDownload.get(rAttesaDownload.size()-1), 100);
                }
            }
        });
        hAttesaDownload.get(hAttesaDownload.size()-1).postDelayed(rAttesaDownload.get(rAttesaDownload.size()-1), 100);
    }

    private void CaricaBrano2() {
        stopCounter = 0;
        qualeWait = -1;

        if (VariabiliStaticheNuove.getInstance().getSc()!=null) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Stoppo Scarico Cover");

            qualeWait++;
            VariabiliStaticheNuove.getInstance().getSc().BloccaOperazione();
            // waitNull(VariabiliStaticheNuove.getInstance().getSc());
        }
        if (VariabiliStaticheNuove.getInstance().getGt()!=null) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Stoppo Gestione Testi");

            qualeWait++;
            VariabiliStaticheNuove.getInstance().getGt().BloccaOperazione();
            // waitNull(VariabiliStaticheNuove.getInstance().getGt());
        }
        if (VariabiliStaticheNuove.getInstance().getGm()!=null) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Stoppo Soap per multimedia");

            qualeWait++;
            VariabiliStaticheNuove.getInstance().getGm().StoppaEsecuzione();
            // waitNull(VariabiliStaticheNuove.getInstance().getGm());
        }
        if (VariabiliStaticheNuove.getInstance().getGb()!=null) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Stoppo Soap per brano");

            qualeWait++;
            VariabiliStaticheNuove.getInstance().getGb().StoppaEsecuzione();
            // waitNull(VariabiliStaticheNuove.getInstance().getGb());
        }
        if (VariabiliStaticheNuove.getInstance().getCuf()!=null) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Stoppo attesa per download");

            qualeWait++;
            VariabiliStaticheNuove.getInstance().getCuf().StoppaEsecuzione();
            // waitNull(VariabiliStaticheNuove.getInstance().getCuf());
        }
        if (VariabiliStaticheNuove.getInstance().getD()!=null) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Stoppo Download brano 1");

            qualeWait++;
            VariabiliStaticheNuove.getInstance().getD().StoppaEsecuzione();
            // waitNull(VariabiliStaticheNuove.getInstance().getD());
        }
        if (VariabiliStaticheNuove.getInstance().getDb()!=null) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Stoppo Soap per download");

            qualeWait++;
            VariabiliStaticheNuove.getInstance().getDb().StoppaEsecuzione();
            // waitNull(VariabiliStaticheNuove.getInstance().getDb());
        }
        if (VariabiliStaticheNuove.getInstance().getD2()!=null) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Stoppo Download brano 3");

            qualeWait++;
            VariabiliStaticheNuove.getInstance().getD2().StoppaEsecuzione();
            // waitNull(VariabiliStaticheNuove.getInstance().getD2());
        }

        qualeWait = 0;
        if (qualeWait >0 ) {
            hAttesaDownload.add(new Handler());
            rAttesaDownload.add(new Runnable() {
                @Override
                public void run() {
                    if (stopCounter < qualeWait) {
                        hAttesaDownload.get(hAttesaDownload.size()-1).postDelayed(rAttesaDownload.get(rAttesaDownload.size()-1), 100);
                    } else {
                        hAttesaDownload.get(hAttesaDownload.size()-1).removeCallbacks(rAttesaDownload.get(rAttesaDownload.size()-1));
                        hAttesaDownload.set(hAttesaDownload.size()-1, null);

                        CaricaBranoParteFinale2();
                    }
                }
            });
            hAttesaDownload.get(hAttesaDownload.size()-1).postDelayed(rAttesaDownload.get(rAttesaDownload.size()-1), 100);
        } else {
            CaricaBranoParteFinale2();
        }

        /* if (VariabiliStaticheNuove.getInstance().getD()!=null ||
                VariabiliStaticheNuove.getInstance().getD2()!=null) {
                secondi = 0;
                nOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Attesa termine operazione");
                hAttesaDownload = new Handler();
                hAttesaDownload.postDelayed(rAttesaDownload = new Runnable() {
                    @Override
                    public void run() {
                        if (VariabiliStaticheNuove.getInstance().getD2() == null || VariabiliStaticheNuove.getInstance().getCuf() == null) {
                            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(nOperazione, false);
                            CaricaBranoParteFinale();
                        } else {
                            nOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Attesa termine operazione. Secondi: " + Integer.toString(secondi));
                            secondi++;
                        }
                    }
                }, 1000);
        } else {
            CaricaBranoParteFinale();
        } */

    }

    private void CaricaBranoParteFinale2() {
        if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
            this.HaCaricatoBrano = true;

            PronunciaFrasi pf = new PronunciaFrasi();
            pf.PronunciaFrase(nb2 + ", " + Artista, "INGLESE");

            CaricaBrano3();
        } else {
            this.HaCaricatoBrano = false;
        }
    }

    private void CaricaBranoParteFinale() {
        // GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "Inizio Carica Brano.");

        final VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

        GestioneLayout.getInstance().AzzeraFade();
        GestioneImmagini.getInstance().SettaImmagineSuIntestazione("***");
        GestioneLayout.getInstance().VisualizzaLayout(10);

        try {
            if (vh.getHandlerSeekBar() != null) {
                vh.getHandlerSeekBar().removeCallbacksAndMessages(null);
                vh.getHandlerSeekBar().removeCallbacks(vh.getrSeekBar());
                vh.setHandlerSeekBar(null);
                vh.setrSeekBar(null);
            }
        } catch (Exception ignored) {

        }

        VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata("***");

        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        if (NumeroBrano==-1) {
            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                    "Nessun brano in archivio", true, VariabiliStaticheGlobali.NomeApplicazione);
        } else {
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setNumeroBranoInAscolto(NumeroBrano);

            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
            String NomeBrano = s.getNomeBrano();

            String sNomeBrano = NomeBrano;
            String Traccia = "";
            if (sNomeBrano.contains("-")) {
                String A[] = sNomeBrano.split("-");
                if (!A[0].isEmpty() && !A[0].equals("00")) {
                    Traccia = "\nTraccia " + A[0].trim();
                }
                sNomeBrano = A[1].trim();
            }
            if (sNomeBrano.contains(".")) {
                sNomeBrano = sNomeBrano.substring(0, sNomeBrano.indexOf("."));
            }
            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
            String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
            String sAlbum = Album;
            if (sAlbum.contains("-")) {
                String A[] = sAlbum.split("-");
                if (A.length > 1) {
                    if (!A[0].isEmpty() && !A[0].equals("0000")) {
                        sAlbum = A[1] + " (Anno " + A[0] + ")";
                    } else {
                        sAlbum = A[1];
                    }
                } else {
                    sAlbum = "";
                }
            }
            sAlbum = Traccia + "\nAlbum: " + sAlbum;

            String nb = NomeBrano;
            if (nb.contains("-")) {
                nb = nb.substring(nb.indexOf("-") + 1, nb.length());
            }
            if (nb.contains(".")) {
                nb = nb.substring(0, nb.indexOf("."));
            }

            vh.getTxtBrano().setText(sNomeBrano);
            vh.getTxtArtista().setText(Artista);
            vh.getTxtAlbum().setText(sAlbum);
            vh.getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
            vh.getTxtMembri().setText("");

            // Gestione Membri
            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getMembri()) {
                if (NumeroBrano > -1) {
                    vh.setGm(new GestioneMembri());
                    int idArtista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).getIdArtista();
                    int quantiMembri = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getMembri().size();
                    Boolean Ok = true;
                    if (quantiMembri == 1) {
                        String membro = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getMembri().get(0).getMembro().trim();
                        if (membro.isEmpty() || membro.toUpperCase().contains("NESSUN MEMBRO")) {
                            Ok = false;
                            vh.getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
                            vh.getTxtMembri().setVisibility(LinearLayout.GONE);
                        }
                    }
                    if (Ok) {
                        vh.getTxtMembriTitolo().setVisibility(LinearLayout.VISIBLE);
                        vh.getTxtMembri().setVisibility(LinearLayout.VISIBLE);

                        vh.getGm().setMembri(VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getMembri());
                        vh.getGm().setTxtCasellaTesto(vh.getTxtMembri());
                        vh.getGm().CominciaAGirare();
                    }
                }
            } else {
                vh.getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
                vh.getTxtMembri().setVisibility(LinearLayout.GONE);
            }
            // Gestione Membri

            nb2 = nb;
            if (nb2.contains("-")) {
                nb2 = nb2.substring(nb2.indexOf("-") + 1, nb.length());
            }
            if (nb2.contains(".")) {
                nb2 = nb2.substring(0, nb2.indexOf("."));
            }

            // Lancio scarico cover
            int nScarico = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download cover");
            VariabiliStaticheNuove.getInstance().setSc(new ScaricoCover());
            int r = VariabiliStaticheNuove.getInstance().getSc().RitornaImmagineBrano(Artista, Album, NomeBrano, nScarico);
            if (r == -1) {
                VariabiliStaticheNuove.getInstance().setSc(null);
            }
            // Lancio scarico cover

            // Testo brano
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "Ritorna testo brano");
            int nTesto = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Lettura testo");
            VariabiliStaticheNuove.getInstance().setGt(new GestioneTesti());
            String Testo = VariabiliStaticheNuove.getInstance().getGt().RitornaTestoDaSD(Artista, Album, NomeBrano, nTesto);
            if (!Testo.isEmpty()) {
                GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();
            } else {
                VariabiliStaticheHome.getInstance().getTxtAscoltata().setText("");
                VariabiliStaticheNuove.getInstance().setGt(new GestioneTesti());
            }
            // Testo brano

            // Multimedia artista
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "Ritorna multimedia artista");
            int nMultimedia = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download multimedia artista");
            String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
            String PathListaImm = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/ListaImmagini.dat";
            File f = new File(PathListaImm);
            if (f.exists()) {
                DBRemotoNuovo dbr = new DBRemotoNuovo();
                VariabiliStaticheNuove.getInstance().setGm(dbr.RitornaMultimediaArtista(VariabiliStaticheGlobali.getInstance().getContext(),
                        Artista, nMultimedia));
            } else {
                String Ritorno = GestioneFiles.getInstance().LeggeFileDiTesto(PathListaImm);
                wsRitornoNuovo w = new wsRitornoNuovo();
                w.RitornaMultimediaArtista(Ritorno);
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(nMultimedia, false);
                VariabiliStaticheNuove.getInstance().setGm(null);
            }
            // Multimedia artista

            // Caricamento Brano
            this.pathBase = pathBase;
            this.Artista = Artista;
            this.Album = Album;
            this.NomeBrano = NomeBrano;
        }
    }

    public String RitornaNomeBrano() {
        String Ritorno = "";
        String CompattazioneMP3 = VariabiliStaticheGlobali.EstensioneCompressione;
        if (VariabiliStaticheGlobali.getInstance().getUtente() != null && !VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
            CompattazioneMP3 = "";
        }

        String PathMP3 = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + NomeBrano;
        String PathMP3_Compresso = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + CompattazioneMP3 + NomeBrano;
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
        }.getClass().getEnclosingMethod().getName(), "Controllo esistenza file: " + PathMP3);
        File f = new File(PathMP3);
        File fc = new File(PathMP3_Compresso);
        if (f.exists() || fc.exists()) {
            if (f.exists()) {
                Ritorno = PathMP3;
            } else {
                Ritorno = PathMP3_Compresso;
            }
        }

        return Ritorno;
    }

    public void CaricaBrano3() {
        String CompattazioneMP3 = VariabiliStaticheGlobali.EstensioneCompressione;
        if (VariabiliStaticheGlobali.getInstance().getUtente() != null && !VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
            CompattazioneMP3 = "";
        }

        String PathMP3 = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + NomeBrano;
        String PathMP3_Compresso = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + CompattazioneMP3 + NomeBrano;
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
        }.getClass().getEnclosingMethod().getName(), "Controllo esistenza file: " + PathMP3);
        File f = new File(PathMP3);
        File fc = new File(PathMP3_Compresso);
        if (f.exists() || fc.exists()) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "Brano già scaricato");
            // VariabiliStaticheHome.getInstance().setBranoDaCaricare("");
            if (f.exists()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "Imposta brano normale");

                GestioneImpostazioneBrani.getInstance().ImpostaBrano(PathMP3);
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "Imposta brano compresso");

                GestioneImpostazioneBrani.getInstance().ImpostaBrano(PathMP3_Compresso);
            }
            if (f.exists() && fc.exists() && !CompattazioneMP3.isEmpty()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "Elimino brano non compresso. Compresso già esistente");
                f.delete();
            }
            VariabiliStaticheNuove.getInstance().setGb(null);
        } else {
            // VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(false);
            // VariabiliStaticheHome.getInstance().setBranoDaCaricare(Artista + ";" + Album + ";" + NomeBrano);

            // Scarica Brano
            String Converte = "N";
            String Qualita = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getRapportoCompressione();
            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                Converte = "S";
            }

            DBRemotoNuovo dbr2 = new DBRemotoNuovo();
            VariabiliStaticheNuove.getInstance().setGb(dbr2.RitornaBrano(VariabiliStaticheHome.getInstance().getContext(),
                    VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase(),
                    Artista,
                    Album,
                    NomeBrano,
                    Converte,
                    Qualita));
        }
    }

    /* public void ProsegueCaricaBrano1(final int NumeroOperazione) {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        if (NumeroBrano>-1) {
            if (VariabiliStaticheGlobali.getInstance().getStaSuonando() || VariabiliStaticheGlobali.getInstance().getStaAttendendoConMusichetta()) {
                final int N = NumeroBrano;

                if (hProsegueScaricamentiBrano!=null) {
                    hProsegueScaricamentiBrano.removeCallbacks(runScaricamentiBrano);
                }

                // GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                hProsegueScaricamentiBrano = new Handler();
                hProsegueScaricamentiBrano.postDelayed(runScaricamentiBrano = new Runnable() {
                    @Override
                    public void run() {
                        GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(false);

                        if (N==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                            hProsegueScaricamentiBrano.removeCallbacks(runScaricamentiBrano);
                            hProsegueScaricamentiBrano=null;

                            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                                VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(true);
                            }

                            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(N);
                            String idArtista = Integer.toString(s.getIdArtista());
                            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();

                            String nb=s.getNomeBrano();
                            if (nb.contains("-")) {
                                nb=nb.substring(nb.indexOf("-")+1,nb.length());
                            }
                            if (nb.contains(".")) {
                                nb=nb.substring(0,nb.indexOf("."));
                            }

                            PronunciaFrasi pf = new PronunciaFrasi();
                            pf.PronunciaFrase(nb + ", " + Artista, "INGLESE");

                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagini artista in caricamento brani");
                            VariabiliStaticheHome.getInstance().setImms(GestioneImmagini.getInstance().RitornaImmaginiArtista(idArtista, Artista, NumeroOperazione, false));
                        }
                    }
                }, 1000);
                // } else {
                // VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(false);
            } else {
                // VariabiliStaticheHome.getInstance().getImgLoadBrano().setVisibility(LinearLayout.GONE);

                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
            }
        }
    }

    public void ProsegueCaricaBrano2(int NumeroOperazione) {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        if (NumeroBrano>-1) {
            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
            String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
            String NomeBrano = s.getNomeBrano();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo brano");
            String Testo = GestioneTesti.getInstance().RitornaTestoDaSD(Artista, Album, NomeBrano, NumeroOperazione);
            if (!Testo.isEmpty()) {
                GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();
            } else {
                VariabiliStaticheHome.getInstance().getTxtAscoltata().setText("");
            }
        } else {
            VariabiliStaticheHome.getInstance().getTxtAscoltata().setText("");
        }
    } */
}
