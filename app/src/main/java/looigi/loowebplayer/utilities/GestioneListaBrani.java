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
    private Runnable runAttesaBackground =null;
    private Handler hAttesaBackground;
    private float vol;
    private Runnable SfumaOutMp3 =null;
    private Handler hSfumaOutMP3;
    private Boolean StavaSuonando;
    private int BranoSuccessivo;
    private int chiacchiera=0;
    private int SecondiDiAttesa;

    private GestioneListaBrani() {
    }

    public static GestioneListaBrani getInstance() {
        if (instance == null) {
            instance = new GestioneListaBrani();
        }

        return instance;
    }

    public void setStavaSuonando(Boolean stavaSuonando) {
        StavaSuonando = stavaSuonando;
    }

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

    public ModiAvanzamento getModalitaAvanzamento() {
        return ModalitaAvanzamento;
    }

    public void setModalitaAvanzamento(ModiAvanzamento modalitaAvanzamento) {
        ModalitaAvanzamento = modalitaAvanzamento;
    }

    private int ControllaProssimoBrano(Boolean Avanza) {
        int Brano = -1;

        if (IndiceSuonati<BraniSuonati.size()) {
            IndiceSuonati++;
            Brano = BraniSuonati.get(IndiceSuonati-1);
            Avanza=false;
        } else {
            int NumeroBrani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
            Brano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
            // Boolean ceRete = NetThreadNuovo.getInstance().isOk();

            // if (ceRete) {
                switch (ModalitaAvanzamento) {
                    case RANDOM:
                        int n = Brano;
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Cerco brano in modalità random");
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
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Cerco brano in modalità random. Fatto: "+Integer.toString(Brano));
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
                BraniSuonati.add(Brano);
                IndiceSuonati++;
            }
        }

        return Brano;
    }

    public int CercaBranoGiaScaricato(boolean DaEsterno) {
        int NumeroBrani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
        int Brano = -1;

        Boolean Ok = false;

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

    /* private void ProsegueConIlBrano(VariabiliStaticheHome vh, String DaDove, Boolean Avanza) {
        // VariabiliStaticheGlobali.getInstance().setStaSuonandoAttesa(true);

        vh.getMediaPlayer().pause();
        vh.getMediaPlayer().setVolume(100, 100);

        int i = -1;
        if (DaDove.equals("AVANTI BRANO") || DaDove.equals("AVANTI BRANO AUTOMATICO")) {
            i = ControllaProssimoBrano(Avanza);
        }

        GestioneOggettiVideo.getInstance().SpegneIconaBackground();

        if (DaDove.equals("AVANTI BRANO")) {
            GestioneOggettiVideo.getInstance().ControllaAvantiBrano(i, true);
            if (StavaSuonando) {
                GestioneOggettiVideo.getInstance().PlayBrano(true);
            }
        } else {
            if (DaDove.equals("AVANTI BRANO AUTOMATICO")) {
                GestioneOggettiVideo.getInstance().ControllaAvantiBrano(i, true);
                if (StavaSuonando) {
                    GestioneOggettiVideo.getInstance().PlayBrano(true);
                }
            }
        }
    }

    public void MetteImmagineDiAttesa() {
        // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(true);
        VariabiliStaticheHome.getInstance().getImgBrano().setVisibility(LinearLayout.GONE);
        VariabiliStaticheHome.getInstance().getGifView().setVisibility(LinearLayout.VISIBLE);

        VariabiliStaticheHome.getInstance().getGifView().setGifImageResource(R.drawable.load);
    }

    private void ToglieImmagineDiAttesa(boolean CambiaImm) {
        // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(false);
        VariabiliStaticheHome.getInstance().getImgBrano().setVisibility(LinearLayout.VISIBLE);
        VariabiliStaticheHome.getInstance().getGifView().setVisibility(LinearLayout.GONE);

        if (CambiaImm) {
            GestioneImmagini.getInstance().ImpostaImmagineVuota();
        }
    } */

    /* public int RitornaNumeroProssimoBrano(final Boolean Avanza, final String DaDove) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Controllo numero prossimo brano con funzione "+DaDove);

        if (DaDove.equals("BACKGROUND")) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Avanzo");
            return ControllaProssimoBrano(Avanza);
        } else {
            if (DaDove.equals("AVANTI BRANO CON ATTESA")) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Sta effettuando un download per la canzone successiva con musichetta.");

                if (!VariabiliStaticheGlobali.getInstance().getStaAttendendoConMusichetta()) {
                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.elabora);
                }
                // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(true);
                NetThread.getInstance().setCaroselloBloccato(true);
                StavaSuonando=false;
                if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                    GestioneOggettiVideo.getInstance().PlayBrano(false);
                    StavaSuonando=true;

                    try {
                        if (VariabiliStaticheHome.getInstance().getHandlerSeekBar() != null) {
                            VariabiliStaticheHome.getInstance().getHandlerSeekBar().removeCallbacksAndMessages(null);
                            VariabiliStaticheHome.getInstance().getHandlerSeekBar().removeCallbacks(VariabiliStaticheHome.getInstance().getrSeekBar());
                            VariabiliStaticheHome.getInstance().setHandlerSeekBar(null);
                            VariabiliStaticheHome.getInstance().setrSeekBar(null);
                        }
                    } catch (Exception ignored) {

                    }
                }

                MetteImmagineDiAttesa();
                SuonaMusicaDiAttesa();
                chiacchiera = 0;
                SecondiDiAttesa=0;

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Attesa Termine Scarico brano con attesa in automatico da " + DaDove);
                hAttesaBackground = new Handler();
                hAttesaBackground.postDelayed(runAttesaBackground = new Runnable() {
                    @Override
                    public void run() {
                        SecondiDiAttesa++;
                        if (SecondiDiAttesa>VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3()) {
                            // Deve bloccare tutto perchè ha sforato il timeout
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Blocco scarico brano in automatico: Timeout");
                            hAttesaBackground.removeCallbacks(runAttesaBackground);

                            PronunciaFrasi pf = new PronunciaFrasi();
                            pf.PronunciaFrase("Taim aut su scarico brano", "ITALIANO");
                            VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                            NetThread.getInstance().setCaroselloBloccato(false);
                            VariabiliStaticheHome.getInstance().getMediaPlayer().stop();
                            VariabiliStaticheGlobali.getInstance().setStaSuonando(false);
                            VariabiliStaticheGlobali.getInstance().setStaAttendendoConMusichetta(false);
                            GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                            ToglieImmagineDiAttesa(false);
                        } else {
                            if (!VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Termine Scarico brano in automatico. Proseguo a " + DaDove);
                                hAttesaBackground.removeCallbacks(runAttesaBackground);

                                // TerminaMusicaDiAttesa(Avanza, DaDove);
                                VariabiliStaticheHome.getInstance().getMediaPlayer().pause();
                                VariabiliStaticheHome.getInstance().getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay_dis());
                                VariabiliStaticheHome.getInstance().getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop());
                                GestioneOggettiVideo.getInstance().PlayBrano(true);
                                VariabiliStaticheGlobali.getInstance().setStaSuonando(true);
                                VariabiliStaticheGlobali.getInstance().setStaAttendendoConMusichetta(false);
                                NetThread.getInstance().setCaroselloBloccato(false);

                                ToglieImmagineDiAttesa(false);
                            } else {
                                chiacchiera++;
                                if (chiacchiera == 10) {
                                    PronunciaFrasi pf = new PronunciaFrasi();
                                    pf.PronunciaFrase("Attendere prego", "ITALIANO");
                                    chiacchiera = 0;
                                }

                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "AVANTI BRANO CON ATTESA: Continuo ad attendere");

                                hAttesaBackground.postDelayed(runAttesaBackground, 1000);
                            }
                        }
                    }
                }, 1000);

                BranoSuccessivo = ControllaProssimoBrano(Avanza);
                return BranoSuccessivo;
            } else {
                if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Sta effettuando un download per la canzone successiva. Attendo e metto la musichetta");

                    if (!VariabiliStaticheGlobali.getInstance().getStaAttendendoConMusichetta()) {
                        GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.elabora);
                    }
                    // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(true);
                    NetThread.getInstance().setCaroselloBloccato(true);

                    StavaSuonando = false;
                    if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                        StavaSuonando = true;
                        GestioneOggettiVideo.getInstance().PlayBrano(false);

                        try {
                            if (VariabiliStaticheHome.getInstance().getHandlerSeekBar() != null) {
                                VariabiliStaticheHome.getInstance().getHandlerSeekBar().removeCallbacksAndMessages(null);
                                VariabiliStaticheHome.getInstance().getHandlerSeekBar().removeCallbacks(VariabiliStaticheHome.getInstance().getrSeekBar());
                                VariabiliStaticheHome.getInstance().setHandlerSeekBar(null);
                                VariabiliStaticheHome.getInstance().setrSeekBar(null);
                            }
                        } catch (Exception ignored) {

                        }
                    }

                    MetteImmagineDiAttesa();
                    SuonaMusicaDiAttesa();
                    chiacchiera = 0;
                    SecondiDiAttesa=0;

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Attesa Termine Scarico brano in automatico da " + DaDove);
                    hAttesaBackground = new Handler();
                    hAttesaBackground.postDelayed(runAttesaBackground = new Runnable() {
                        @Override
                        public void run() {
                            SecondiDiAttesa++;
                            if (SecondiDiAttesa>VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3()) {
                                // Deve bloccare tutto perchè ha sforato il timeout
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Blocco scarico brano in background: Timeout");
                                hAttesaBackground.removeCallbacks(runAttesaBackground);

                                PronunciaFrasi pf = new PronunciaFrasi();
                                pf.PronunciaFrase("Taim aut su scarico brano", "ITALIANO");

                                NetThread.getInstance().setCaroselloBloccato(false);
                                VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                                VariabiliStaticheHome.getInstance().getMediaPlayer().stop();
                                VariabiliStaticheGlobali.getInstance().setStaSuonando(false);
                                VariabiliStaticheGlobali.getInstance().setStaAttendendoConMusichetta(false);
                                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                                ToglieImmagineDiAttesa(false);
                            } else {
                                if (!VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Termine Scarico brano in automatico. Proseguo a " + DaDove);
                                    hAttesaBackground.removeCallbacks(runAttesaBackground);

                                    TerminaMusicaDiAttesa(Avanza, DaDove);
                                    ToglieImmagineDiAttesa(true);
                                } else {
                                    chiacchiera++;
                                    if (chiacchiera == 10) {
                                        PronunciaFrasi pf = new PronunciaFrasi();
                                        pf.PronunciaFrase("Attendere prego", "ITALIANO");
                                        chiacchiera = 0;
                                    }

                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "DOWNLOAD BACKGROUND: Continuo ad attendere");

                                    hAttesaBackground.postDelayed(runAttesaBackground, 1000);
                                }
                            }
                        }
                    }, 1000);

                    return -1;
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Vado avanti visto che non c'è nessun brano successivo in download");

                    GestioneOggettiVideo.getInstance().SpegneIconaBackground();

                    if (DaDove.equals("AVANTI BRANO DURANTE BACKGROUND")) {
                        int numeroDaButtare =  ControllaProssimoBrano(Avanza);
                    }

                    return ControllaProssimoBrano(Avanza);
                }
            }
        }
    } */

    public int RitornaNumeroProssimoBranoNuovo(final Boolean Avanza) {
        Boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
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
                IndiceSuonati=BraniSuonati.size()-1;
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

    public void ScaricaBranoSuccessivoInBackground() {
        Boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
        if (ceRete) {
            final int NumeroBranoProssimo = RitornaNumeroProssimoBranoNuovo(false);
            VariabiliStaticheGlobali.getInstance().setBranoAutomatico(NumeroBranoProssimo);
            BraniSuonati.add(NumeroBranoProssimo);
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

                GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ok);
                VariabiliStaticheNuove.getInstance().setDb(null);

                if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                    VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                }
            } else {
                final String Brano = Artista + ";" + Album + ";" + NomeBrano;

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico brano");
                        int numOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download automatico: "+NomeBrano);

                        String c[] = Brano.split(";", -1);
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

                //     }
                // }, 50);
            }
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
             "Non c'è rete, evito il download in background del successivo brano e ne prendo uno già scaricato");
            VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
            // ***BRANO SCARICATO***
            int NumeroBrano=CercaBranoGiaScaricato(true);
            VariabiliStaticheGlobali.getInstance().setBranoAutomatico(NumeroBrano);
            VariabiliStaticheGlobali.getInstance().setBranoImpostatoSenzaRete(NumeroBrano);
            // BraniSuonati.add(NumeroBrano);

            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.folder);
            // VariabiliStaticheGlobali.getInstance().getDatiGenerali()
            //         .getConfigurazione().setQualeCanzoneStaSuonando(NumeroBrano);
        }
    }
}
