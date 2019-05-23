package looigi.loowebplayer.utilities;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
// import looigi.loowebplayer.thread.NetThreadNuovo;

import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.RANDOM;

public class GestioneListaBrani {
    private static GestioneListaBrani instance = null;
    // private Runnable runAttesaBackground =null;
    // private Handler hAttesaBackground;
    private float vol;
    private Runnable SfumaOutMp3 =null;
    private Handler hSfumaOutMP3;
    // private Boolean StavaSuonando;
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

    // public void setStavaSuonando(Boolean stavaSuonando) {
    //     StavaSuonando = stavaSuonando;
    // }

    public enum ModiAvanzamento {
        RANDOM,
        SEQUENZIALE
    }

    private ModiAvanzamento ModalitaAvanzamento;
    private List<Integer> BraniSuonati=new ArrayList<>();
    private int IndiceSuonati=0;

    public void AggiungeBrano(int idBrano) {
        BraniSuonati.add(idBrano);
        IndiceSuonati++;
    }

    public int RitornaIndiceBranoAttuale() {
        return IndiceSuonati;
    }

    public int RitornaIdInBaseAllIndice(int indice) {
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
    }

    public ModiAvanzamento getModalitaAvanzamento() {
        return ModalitaAvanzamento;
    }

    public void setModalitaAvanzamento(ModiAvanzamento modalitaAvanzamento) {
        ModalitaAvanzamento = modalitaAvanzamento;
    }

    private int ControllaProssimoBrano(Boolean Avanza) {
        int Brano = -1;

        if (VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() != -1) {
            if (IndiceSuonati < BraniSuonati.size()) {
                IndiceSuonati++;
                if (IndiceSuonati < BraniSuonati.size()) {
                    BraniSuonati.set(IndiceSuonati, VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano());
                } else {
                    AggiungeBrano(VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano());
                }
            } else {
                AggiungeBrano(VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano());
            }

            Brano = VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano();
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setQualeCanzoneStaSuonando(VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano());
            VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(-1);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Avanti. Impostato brano da background: "+Integer.toString(VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano()));
        } else {
            if (IndiceSuonati < BraniSuonati.size()) {
                IndiceSuonati++;
                Brano = BraniSuonati.get(IndiceSuonati);
                Avanza = false;
            } else {
                int NumeroBrani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
                Brano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
                // boolean ceRete = NetThreadNuovo.getInstance().isOk();

                // if (ceRete) {
                switch (ModalitaAvanzamento) {
                    case RANDOM:
                        int n = Brano;
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Cerco brano in modalità random");
                        while (n == Brano) {
                            Random r = new Random();
                            n = (NumeroBrani) + 1;
                            if (n > 0) {
                                n = r.nextInt(n);
                            } else {
                                n = -1;
                            }
                        }
                        Brano = n;
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Cerco brano in modalità random. Fatto: " + Integer.toString(Brano));
                        break;
                    case SEQUENZIALE:
                        if (NumeroBrani > -1) {
                            Brano++;
                            if (Brano > NumeroBrani) {
                                Brano = 0;
                            }
                        } else {
                            Brano = -1;
                        }
                        break;
                }
                // } else {
                //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Non c'è rete, avanzo verso il primo brano già scaricato");
                //     Brano = CercaBranoGiaScaricato(false);
                // }

                if (Brano > -1 && Avanza) {
                    AggiungeBrano(Brano);
                }
            }
        }

        return Brano;
    }

    public int CercaBranoGiaScaricato(boolean DaEsterno) {
        int NumeroBrani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
        int Brano = -1;

        boolean Ok = false;

        if (ModalitaAvanzamento==RANDOM) {
            int n = Brano;
            while (n == Brano) {
                Random r = new Random();
                n = (NumeroBrani - 0) + 1;
                if (n > 0) {
                    n = r.nextInt(n) + 0;
                } else {
                    n = -1;
                }
            }
            Brano = n;
        }

        while (!Ok) {
            if (NumeroBrani > -1) {
                Brano++;
                if (Brano > NumeroBrani) {
                    Brano = 0;
                }

                StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(Brano);
                String NomeBrano = s.getNomeBrano();
                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
                String CompattazioneMP3 = VariabiliStaticheGlobali.EstensioneCompressione;
                if (!VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                    CompattazioneMP3 = "";
                }
                String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
                String PathMP3 = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + NomeBrano;
                String PathMP3_Compresso = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + CompattazioneMP3 + NomeBrano;
                File f = new File(PathMP3);
                File fc = new File(PathMP3_Compresso);
                if (f.exists() || fc.exists()) {
                    Ok = true;
                }
                if (DaEsterno) {
                    VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista + ")");
                }
            } else {
                Brano = -1;
            }
        }

        if (DaEsterno) {
            // if (IndiceSuonati == BraniSuonati.size()) {
            //     IndiceSuonati--;
            // }
            // BraniSuonati.set(IndiceSuonati, Brano);
            AggiungeBrano(Brano);
        }

        return Brano;
    }

    public void SuonaMusicaDiAttesa() {
        if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
            VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

            if (vh.getMediaPlayer() != null) {
                vh.getMediaPlayer().stop();
                // VariabiliStaticheGlobali.getInstance().setStaSuonandoAttesa(true);
            }

            GestioneImmagini.getInstance().StoppaTimerCarosello();
            Drawable icona_attesa = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.attendere);
            GestioneImmagini.getInstance().getImgBrano().setImageDrawable(icona_attesa);

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
        }
    }

    public void TerminaMusicaDiAttesa(final String Mp3) {
        final VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

        if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
            // Drawable icona_nessuna = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.nessuna);
            // VariabiliStaticheHome.getInstance().getImgBrano().setImageDrawable(icona_nessuna);

            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getSfumaBrano()) {
                vol = 1;
                hSfumaOutMP3 = new Handler(Looper.getMainLooper());
                hSfumaOutMP3.postDelayed(SfumaOutMp3 = new Runnable() {
                    @Override
                    public void run() {
                        vol -= .03;
                        if (vol > .03) {
                            vh.getMediaPlayer().setVolume(vol, vol);
                            hSfumaOutMP3.postDelayed(SfumaOutMp3, 100);
                        } else {
                            hSfumaOutMP3.removeCallbacks(SfumaOutMp3);

                            GestioneSuonaBrano.getInstance().SuonaBranoCompleto(Mp3);
                        }
                    }
                }, 100);
            } else {
                GestioneSuonaBrano.getInstance().SuonaBranoCompleto(Mp3);
            }
        } else {
            GestioneSuonaBrano.getInstance().SuonaBranoCompleto(Mp3);
        }
    }

    public int RitornaNumeroProssimoBranoNuovo(final Boolean Avanza) {
        boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
        if (ceRete) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Controllo numero prossimo brano");

            return ControllaProssimoBrano(Avanza);
       } else {
           VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                   "Non c'è rete, evito il download in background del successivo brano e ne prendo uno già scaricato");
           VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
           return CercaBranoGiaScaricato(false);
        }
    }

    public int RitornaBranoPrecedente() {
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
            Brano = BraniSuonati.get(IndiceSuonati);
        } else {
            if (BraniSuonati.size()>0) {
                Brano = BraniSuonati.get(0);
            }
        }

        return Brano;
    }

    public void SettaIndice(int indice) {
        IndiceSuonati=indice;
    }

    public void ScaricaBranoSuccessivoInBackground() {
        boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
        if (ceRete) {
            final int NumeroBranoProssimo = RitornaNumeroProssimoBranoNuovo(false);
            VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(NumeroBranoProssimo);
            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBranoProssimo);
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
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Controllo esistenza file: " + PathMP3);
            File f = new File(PathMP3);
            File fc = new File(PathMP3_Compresso);

            VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista +")");

            if (f.exists() || fc.exists()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Brano già esistente... Non faccio niente");

                VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(NumeroBranoProssimo);
                GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ok);
                VariabiliStaticheNuove.getInstance().setDb(null);

                if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                    VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                }
            } else {
                final String Brano = Artista + ";" + Album + ";" + NomeBrano;

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Scarico brano");
                int numOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false,
                        "Download automatico: "+NomeBrano);

                String c[] = Brano.split(";", -1);
                String Converte = "N";
                String Qualita = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getRapportoCompressione();
                if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                    Converte = "S";
                }

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Ritorna dettaglio brano. Ritorna brano: " +
                        VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase() + " " +
                        c[0] + " " +
                        c[1] + " " +
                        c[2] + " " +
                        Converte + " " +
                        Qualita
                );

                DBRemotoNuovo dbr = new DBRemotoNuovo();
                VariabiliStaticheNuove.getInstance().setDb(dbr.RitornaBranoBackground(VariabiliStaticheHome.getInstance().getContext(),
                        VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase(),
                        c[0],
                        c[1],
                        c[2],
                        Converte,
                        Qualita,
                        NumeroBranoProssimo,
                        true,
                        numOperazione
                ));

                VariabiliStaticheNuove.getInstance().setDb(null);
            }
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
             "Non c'è rete, evito il download in background del successivo brano e ne prendo uno già scaricato");
            VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
            int NumeroBrano=CercaBranoGiaScaricato(false);
            VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(NumeroBrano);
            VariabiliStaticheGlobali.getInstance().setBranoImpostatoSenzaRete(NumeroBrano);

            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
            final String NomeBrano = s.getNomeBrano();
            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();

            VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista +")");

            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.folder);
        }
    }
}
