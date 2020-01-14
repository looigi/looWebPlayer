package looigi.loowebplayer.utilities;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.dialog.DialogMessaggio;
// import looigi.loowebplayer.thread.NetThreadNuovo;

import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.RANDOM;

public class GestioneListaBrani {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("GestioneListaBrani");;
    private static GestioneListaBrani instance = null;

    private List<Integer> ListaBraniAscoltati = new ArrayList<>();
    private int indiceAscoltati = 0;

    // private Runnable runAttesaBackground =null;
    // private Handler hAttesaBackground;
    // private float vol;
    // private boolean VecchioStaSuonando;
    // private boolean StavaSuonando;
    // private int BranoSuccessivo;
    // private int chiacchiera=0;
    // private int SecondiDiAttesa;

    private GestioneListaBrani() {
    }

    public static GestioneListaBrani getInstance() {
        if (instance == null) {
            instance = new GestioneListaBrani();
        }

        return instance;
    }

    // public void setStavaSuonando(boolean stavaSuonando) {
    //     StavaSuonando = stavaSuonando;
    // }

    public enum ModiAvanzamento {
        RANDOM,
        SEQUENZIALE
    }

    private ModiAvanzamento ModalitaAvanzamento;
    // private List<Integer> BraniSuonati=new ArrayList<>();
    // private int IndiceSuonati=0;

    /* public void AggiungeBrano(int idBrano) {
        boolean Ultimo = false;

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                new Object(){}.getClass().getEnclosingMethod().getName(),
                "Aggiungo brano a lista brani. Brano: " + idBrano);

        if (IndiceSuonati==BraniSuonati.size()) {
            Ultimo = true;
        }
        BraniSuonati.add(idBrano);
        if (Ultimo) {
            IndiceSuonati++;
        }

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                new Object(){}.getClass().getEnclosingMethod().getName(),
                "Aggiunto brano a lista brani. Brani suonati: " + BraniSuonati.size() + " - Indice suonati: " + IndiceSuonati);

    } */

    /* public int RitornaIndiceBranoAttuale() {
        return IndiceSuonati;
    } */

    /* public int RitornaIdInBaseAllIndice(int indice) {
        if (indice<BraniSuonati.size()) {
            return BraniSuonati.get(indice);
        } else {
            return BraniSuonati.get(BraniSuonati.size()-1);
        }
    }

    public int RitornaQuantiBraniInLista() {
        return BraniSuonati.size() -1;
    }

    public List<Integer> RitornaListaBrani() {
        return BraniSuonati;
    } */

    public ModiAvanzamento getModalitaAvanzamento() {
        return ModalitaAvanzamento;
    }

    public void setModalitaAvanzamento(ModiAvanzamento modalitaAvanzamento) {
        ModalitaAvanzamento = modalitaAvanzamento;
    }

    private int ControllaProssimoBrano(boolean Avanza) {
        int Brano = -1;

        if (VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() != -1) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                    new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Numero prossimo brano diverso da -1:" + VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() );

            /* int numeroAttuale = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione()
                    .getQualeCanzoneStaSuonando();

            if (IndiceSuonati < BraniSuonati.size()) {
                while (BraniSuonati.get(IndiceSuonati) == numeroAttuale && IndiceSuonati < BraniSuonati.size()) {
                    IndiceSuonati++;
                }
                if (IndiceSuonati < BraniSuonati.size()) {
                    BraniSuonati.set(IndiceSuonati, VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano());
                } else {
                    AggiungeBrano(VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano());
                }
            } else {
                AggiungeBrano(VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano());
            } */

            Brano = VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano();
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione()
                    .setQualeCanzoneStaSuonando(VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano());
            VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(-1);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Avanti. Impostato brano da background: "+Integer.toString(VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano()));
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                    new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Numero prossimo brano = -1");

            /* if (IndiceSuonati < BraniSuonati.size()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                        new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Indice inferiore al totale dei brani suonati. Indice: " + IndiceSuonati + " - Totale: " + BraniSuonati.size());

                IndiceSuonati++;
                if (IndiceSuonati<BraniSuonati.size()) {
                    Brano = BraniSuonati.get(IndiceSuonati);
                } else {
                    boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
                    // ceRete = false;
                    if (ceRete) {
                        Brano = GestioneListaBrani.getInstance()
                                .RitornaNumeroProssimoBranoNuovo(false, false);

                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                                new Object(){}.getClass().getEnclosingMethod().getName(),
                                "Ritorno Numero prossimo brano:" + Brano);
                    } else {
                        Brano = GestioneListaBrani.getInstance().BranoSenzarete();

                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                                new Object(){}.getClass().getEnclosingMethod().getName(),
                                "Ritorno Numero prossimo brano SENZA RETE:" + Brano);
                    }
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Problemi sull'indice dei brani. Impostato il brano: "+Integer.toString(Brano));

                    // DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                    //         "Non riesco a prendere il brano successivo.\nIndice suonati più grande del vettore contenitore: " +
                    //                 Integer.toString(IndiceSuonati) + "/" + Integer.toString(BraniSuonati.size()-1),
                    //         true,
                    //         VariabiliStaticheGlobali.NomeApplicazione);
                }
                Avanza = false;
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                        new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Indice uguale o superiore al totale dei brani suonati. Indice: " + IndiceSuonati + " - Totale: " + BraniSuonati.size());
*/

                int NumeroBrani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
                Brano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
                // boolean ceRete = NetThreadNuovo.getInstance().isOk();

                // if (ceRete) {
                switch (ModalitaAvanzamento) {
                    case RANDOM:
                        int n = Brano;
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Cerco brano in modalità random");
                        StrutturaBrani b = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(n);
                        boolean escluso = true;
                        if (b != null) {
                            escluso = b.isEscluso();
                        } else {
                            int a = 0;
                        }
                        while (n == Brano || escluso) {
                            Random r = new Random();
                            n = (NumeroBrani) + 1;
                            if (n > 0) {
                                n = r.nextInt(n);
                            } else {
                                n = -1;
                            }
                            b = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(n);
                            if (b != null) {
                                escluso = b.isEscluso();
                            } else {
                                escluso = true;
                            }
                        }
                        Brano = n;
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Cerco brano in modalità random. Fatto: " + Integer.toString(Brano));
                        break;
                    case SEQUENZIALE:
                        if (NumeroBrani > -1) {
                            Brano++;
                            if (Brano > NumeroBrani) {
                                Brano = 0;
                            }

                            StrutturaBrani bs = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(Brano);
                            boolean esclusoS = bs.isEscluso();

                            while (esclusoS) {
                                Brano++;
                                if (Brano > NumeroBrani) {
                                    Brano = 0;
                                }
                                bs = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(Brano);
                                esclusoS = bs.isEscluso();
                            }
                        } else {
                            Brano = -1;
                        }
                        break;
                }
                // } else {
                //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Non c'è rete, avanzo verso il primo brano già scaricato");
                //     Brano = CercaBranoGiaScaricato(false);
                // }

                // if (Brano > -1 && Avanza) {
                //     AggiungeBrano(Brano);
                // }
            // }
        }

        return Brano;
    }

    public int CercaBranoGiaScaricato(boolean DaEsterno) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                new Object(){}.getClass().getEnclosingMethod().getName(),
                "Cerco brano già scaricato: " + DaEsterno);

        int NumeroBrani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
        int Brano = -1;

        boolean Ok = false;

        if (ModalitaAvanzamento==RANDOM) {
            int n = Brano;
            StrutturaBrani b = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(n);
            boolean escluso = true;
            if (b != null) {
                escluso = b.isEscluso();
            } else {
                int a = 0;
            }
            while (n == Brano || escluso) {
                Random r = new Random();
                n = NumeroBrani + 1;
                if (n > 0) {
                    n = r.nextInt(n);
                } else {
                    n = -1;
                }
                b = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(n);
                if (b != null) {
                    escluso = b.isEscluso();
                } else {
                    escluso = true;
                }
            }
            Brano = n;
        }

        int inizio = Brano;
        boolean puoTerminare = false;

        while (!Ok) {
            if (NumeroBrani > -1) {
                Brano++;
                if (Brano > NumeroBrani) {
                    Brano = 0;
                    puoTerminare = true;
                }
                if (puoTerminare) {
                    if (Brano > inizio) {
                        // Basta cercare... Non ho trovato niente...
                        Brano = -1;
                        break;
                    }
                }

                StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(Brano);
                if (s != null && !s.isEscluso()) {
                    String NomeBrano = s.getNomeBrano();
                    String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                    String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
                    String CompattazioneMP3 = VariabiliStaticheGlobali.EstensioneCompressione;
                    // if (!VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                    //     CompattazioneMP3 = "";
                    // }
                    String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
                    if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
                        pathBase = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/";
                    } else {
                        pathBase = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/";
                    }

                    // /storage/emulated/0/LooigiSoft/looWebPlayer/Dati/Mp3Mica/Mp3Mica/Mp3Mica/Marracash - In Radio.mp3
                    String PathMP3 = pathBase + NomeBrano;
                    String PathMP3_Compresso = pathBase + CompattazioneMP3 + NomeBrano;

                    File f = new File(PathMP3);
                    File fc = new File(PathMP3_Compresso);
                    if (f.exists() || fc.exists()) {
                        Ok = true;
                    }
                    if (DaEsterno) {
                        VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista + ")");
                    }
                }
            } else {
                Brano = -1;
            }
        }

        if (Brano>-1 && DaEsterno) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                    new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Cerco brano già scaricato. Trovato: " + Brano);
            // if (IndiceSuonati == BraniSuonati.size()) {
            //     IndiceSuonati--;
            // }
            // BraniSuonati.set(IndiceSuonati, Brano);
            // AggiungeBrano(Brano);
        } else {
            if (Brano == -1) {
                DialogMessaggio.getInstance().show(
                        VariabiliStaticheGlobali.getInstance().getContext(),
                        "Nessun brano disponibile",
                        true,
                        VariabiliStaticheGlobali.NomeApplicazione
                );
            }
        }

        return Brano;
    }

    public void SuonaMusicaDiAttesa() {
        VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

        // VecchioStaSuonando =  VariabiliStaticheGlobali.getInstance().getStaSuonando();

        if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
            if (vh.getMediaPlayer() != null) {
                vh.getMediaPlayer().stop();
                VariabiliStaticheGlobali.getInstance().setStaSuonando(false);
            }
        }

        GestioneImmagini.getInstance().StoppaTimerCarosello();
        Drawable icona_attesa = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.attendere);
        GestioneImmagini.getInstance().getImgBrano().setImageDrawable(icona_attesa);

        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSuonaAttesa()) {
            // if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                int min = 1;
                int max = 7;
                int random = new Random().nextInt((max - min) + 1) + min;

                int resID = VariabiliStaticheGlobali.getInstance().getContext().getResources().getIdentifier("m" + Integer.toString(random), "raw",
                        VariabiliStaticheGlobali.getInstance().getContext().getPackageName());
                if (vh.getMediaPlayer() != null) {
                    vh.getMediaPlayer().setVolume(100, 100);
                    vh.setMediaPlayer(MediaPlayer.create(vh.getContext(), resID));
                    if (vh.getMediaPlayer() != null) {
                        vh.getMediaPlayer().setLooping(true);
                        vh.getMediaPlayer().start();
                        // VariabiliStaticheGlobali.getInstance().setStaSuonandoAttesa(true);
                    }
                }
            // }
        }
    }

    public void TerminaMusicaDiAttesa(final String Mp3) {
        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSuonaAttesa()) {
            final VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

            if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                // Drawable icona_nessuna = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.nessuna);
                // VariabiliStaticheHome.getInstance().getImgBrano().setImageDrawable(icona_nessuna);

                // if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getSfumaBrano()) {
                    // vol = 1;
                    // hSfumaOutMP3 = new Handler(Looper.getMainLooper());
                    // hSfumaOutMP3.postDelayed(SfumaOutMp3 = new Runnable() {
                    //     @Override
                    //     public void run() {
                    //         vol -= .03;
                    //         if (vol > .03) {
                    //             vh.getMediaPlayer().setVolume(vol, vol);
                    //             hSfumaOutMP3.postDelayed(SfumaOutMp3, 100);
                    //         } else {
                    //             hSfumaOutMP3.removeCallbacks(SfumaOutMp3);
//
                    //              VariabiliStaticheGlobali.getInstance().setStaSuonando(true);
//
                                 GestioneSuonaBrano.getInstance().SuonaBranoCompleto(Mp3);
                    //         }
                    //     }
                    // }, 100);
                // } else {
                //     VariabiliStaticheGlobali.getInstance().setStaSuonando(true);
//
                //     GestioneSuonaBrano.getInstance().SuonaBranoCompleto(Mp3);
                // }
            } else {
                // VariabiliStaticheGlobali.getInstance().setStaSuonando(false);

                // GestioneSuonaBrano.getInstance().SuonaBranoCompleto(Mp3);
            }
        } else {
            // VariabiliStaticheGlobali.getInstance().setStaSuonando(true);

            GestioneSuonaBrano.getInstance().SuonaBranoCompleto(Mp3);
        }
    }

    public int RitornaNumeroProssimoBranoNuovo(final boolean Avanza, boolean branoGiaScaricato) {
        boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

        // ceRete = false;
        if (ceRete) {
            if (branoGiaScaricato) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Controllo numero prossimo brano senza rete. Cerco già scaricato");

                int idBrano = CercaBranoGiaScaricato(true);
                if (idBrano == -1) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                            "NON Trovato. Lo cerco in quelli da scaricare");

                    int brano = ControllaProssimoBrano(Avanza);
                    AggiungeBranoAgliAscoltati(brano);
                    return brano;
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                            "Trovato:" + idBrano);

                    AggiungeBranoAgliAscoltati(idBrano);
                    return idBrano;
                }
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Controllo numero prossimo brano senza rete");

                int brano = ControllaProssimoBrano(Avanza);
                AggiungeBranoAgliAscoltati(brano);
                return brano;
            }
       } else {
            if (!VariabiliStaticheGlobali.getInstance().getHaScaricatoAutomaticamente()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Non ha scaricato automaticamente. Ne prendo uno già scaricato");
                // VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);

                int brano = CercaBranoGiaScaricato(true);
                AggiungeBranoAgliAscoltati(brano);
                return brano;
            } else {
                // if (VariabiliStaticheGlobali.getInstance().getBranoImpostatoSenzaRete() > -1){
                //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                //             }.getClass().getEnclosingMethod().getName(),
                //             "Non c'è rete, imposto il brano impostato senza rete 1");
//
                //     int n = BranoSenzarete();
                //     return n; // VariabiliStaticheGlobali.getInstance().getBranoImpostatoSenzaRete();
                // } else {
                    // if (VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() > -1) {
                    //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    //             }.getClass().getEnclosingMethod().getName(),
                    //             "Non c'è rete, imposto il brano impostato senza rete 2");
//
                    //     int n = BranoSenzarete();
                    //     return n; // VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano();
                    // } else {
                    //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    //             }.getClass().getEnclosingMethod().getName(),
                    //             "Non c'è rete, imposto il brano impostato senza rete 3");
//
                    //     int n = BranoSenzarete();
                    //     return n; // -1;
                    // }
                // }
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Cerco prossimo brano. Già scaricato: " + branoGiaScaricato);

                if (branoGiaScaricato) {
                    int brano = CercaBranoGiaScaricato(true);
                    AggiungeBranoAgliAscoltati(brano);
                    return brano;
                } else {
                    int brano = VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano();
                    AggiungeBranoAgliAscoltati(brano);
                    return brano;
                }
            }
       }
    }

    public int BranoSenzarete() {
        // VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
        int NumeroBrano=CercaBranoGiaScaricato(false);
        VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(NumeroBrano);
        // VariabiliStaticheGlobali.getInstance().setBranoImpostatoSenzaRete(NumeroBrano);
        VariabiliStaticheGlobali.getInstance().setHaScaricatoAutomaticamente(true);

        StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
        if (s != null) {
            final String NomeBrano = s.getNomeBrano();
            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();

            VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista + ")");

            return NumeroBrano;
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "ERRORE: struttura nulla su brano senza rete");

            return -1;
        }
    }

    /* public int RitornaBranoPrecedente() {
        int oldBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
        int Brano = oldBrano;

        if (IndiceSuonati>0) {
            if (IndiceSuonati>BraniSuonati.size()-1) {
                IndiceSuonati=BraniSuonati.size()-2;
            } else {
                while (BraniSuonati.get(IndiceSuonati) == oldBrano) {
                    IndiceSuonati--;
                }
            }
            if (IndiceSuonati < BraniSuonati.size() && IndiceSuonati > -1) {
                Brano = BraniSuonati.get(IndiceSuonati);
            }
        } else {
            if (BraniSuonati.size()>0) {
                Brano = BraniSuonati.get(0);
            }
        }

        return Brano;
    } */

    /* public void SettaIndice(int indice) {
        IndiceSuonati=indice;
    } */

    public void ScaricaBranoSuccessivoInBackground() {
        boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
        // ceRete = false;
        if (ceRete) {
            int numeroAttuale = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
            int NumeroBranoProssimo = numeroAttuale;
            // while (numeroAttuale == NumeroBranoProssimo) {
                NumeroBranoProssimo = RitornaNumeroProssimoBranoNuovo(false, false);
            // }
            VariabiliStaticheGlobali.getInstance().setNumeroBranoNuovo(NumeroBranoProssimo);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Numero prossimo brano con rete: " + NumeroBranoProssimo);

            StrutturaBrani s = null;
            int conta = 0;
            if (s == null) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Struttura nulla del brano. Ne cerco un altro che abbia la struttura");
            }
            // while (s == null) {
                s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBranoProssimo);
                if (s == null) {
                    NumeroBranoProssimo = RitornaNumeroProssimoBranoNuovo(false, false);
                    s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBranoProssimo);
                    // conta++;
                    // if (conta==5) {
                    //     break;
                    // }
                }
            // }
            if (s==null) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Brano non rilevato in ScaricaBranoSuccessivoInBackground");
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Brano: " + Integer.toString(NumeroBranoProssimo) +
                                " - Lunghezza struttura brani: " + Integer.toString(VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani()));
                DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                        "Brano non rilevato in ScaricaBranoSuccessivoInBackground",
                        true,
                        VariabiliStaticheGlobali.NomeApplicazione);
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Trovato numero prossimo: " + NumeroBranoProssimo);

                VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(NumeroBranoProssimo);
                final String NomeBrano = s.getNomeBrano();
                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
                String CompattazioneMP3 = VariabiliStaticheGlobali.EstensioneCompressione;
                if (!VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                    CompattazioneMP3 = "";
                }
                String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
                String PathMP3 = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + NomeBrano;
                String PathMP3_Compresso = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + CompattazioneMP3 + NomeBrano;
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Controllo esistenza file: " + PathMP3);
                File f = new File(PathMP3);
                File fc = new File(PathMP3_Compresso);

                VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista +")");

                boolean ok;

                if (VariabiliStaticheDebug.ScaricaSempreIBrani) {
                    ok = false;
                } else {
                    if (f.exists() || fc.exists()) {
                        ok = true;
                    } else {
                        ok = false;
                    }
                }

                if (ok) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                            "Brano già esistente... Non faccio niente");

                    VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(NumeroBranoProssimo);
                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ok);
                    // VariabiliStaticheNuove.getInstance().setDb(null);

                    /* if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                    } */
                    VariabiliStaticheGlobali.getInstance().setHaScaricatoAutomaticamente(true);
                } else {
                    final String Brano = Artista + ";" + Album + ";" + NomeBrano;

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                            "Scarico brano");
                    int numOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false,
                            "Download automatico: " + NomeBrano);

                    String[] c = Brano.split(";", -1);
                    String Converte = "N";
                    String Qualita = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getRapportoCompressione();
                    if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                        Converte = "S";
                    }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                            "Ritorna dettaglio brano. Ritorna brano: " +
                                    VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase() + " " +
                                    c[0] + " " +
                                    c[1] + " " +
                                    c[2] + " " +
                                    Converte + " " +
                                    Qualita
                    );

                    DBRemotoNuovo dbr = new DBRemotoNuovo();
                    dbr.RitornaBranoBackground(VariabiliStaticheHome.getInstance().getContext(),
                            VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase(),
                            c[0],
                            c[1],
                            c[2],
                            Converte,
                            Qualita,
                            NumeroBranoProssimo,
                            true,
                            numOperazione
                    );
                }

                // VariabiliStaticheNuove.getInstance().setDb(null);
            }
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Cerco brano senza rete");

            int N = BranoSenzarete();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Impostato brano senza rete: " + Integer.toString(N));

            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.folder);
        }
    }

    public void AggiungeBranoAgliAscoltati(int Brano) {
        ListaBraniAscoltati.add(Brano);
        indiceAscoltati = ListaBraniAscoltati.size()-1;
    }

    public int RitornaNumeroBranoPrecedente() {
        int ritorno = -1;

        if (indiceAscoltati > 0) {
            indiceAscoltati--;
            ritorno = ListaBraniAscoltati.get(indiceAscoltati);
        }

        return ritorno;
    }
}
