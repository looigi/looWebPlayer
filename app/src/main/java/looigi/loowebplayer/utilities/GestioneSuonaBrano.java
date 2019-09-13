package looigi.loowebplayer.utilities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.db_locale.db_dati;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.dialog.DialogMessaggio;

// import looigi.loowebplayer.maschere.Equalizer;

public class GestioneSuonaBrano {
    private static final GestioneSuonaBrano ourInstance = new GestioneSuonaBrano();

    public static GestioneSuonaBrano getInstance() {
        return ourInstance;
    }

    private GestioneSuonaBrano() {
    }

    // private Runnable NuovoMp3;
    // private Handler hNuovoMP3;
    private Runnable rSeekBar;

    public void SuonaBrano(String Mp3) {
        if (VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
            GestioneListaBrani.getInstance().TerminaMusicaDiAttesa(Mp3);
        }
    }

    public void SuonaBranoCompleto(String Mp3) {
        final VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

        if (VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
            VariabiliStaticheGlobali.getInstance().setsUltimaCanzoneSuonata(Mp3);
            vh.getMediaPlayer().setVolume(100, 100);
            vh.setMediaPlayer(MediaPlayer.create(vh.getContext(),
                    Uri.parse(Mp3)));
            if (vh.getMediaPlayer()!=null) {
                vh.getMediaPlayer().setLooping(false);
                if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                    vh.getMediaPlayer().start();
                }

                // Equalizzatore
                // VariabiliStaticheEqualizzatore.getInstance().setEq(new android.media.audiofx.Equalizer(0, vh.getMediaPlayer().getAudioSessionId()));
                // VariabiliStaticheEqualizzatore.getInstance().setBb(new BassBoost(0, vh.getMediaPlayer().getAudioSessionId()));
                // Equalizer.getInstance().CaricaValori();
                // Equalizer.getInstance().ImpostaValoriMaschera();
                // Equalizzatore
            } else {
                // VariabiliStaticheHome.getInstance().getLayOperazionWEB().setVisibility(LinearLayout.VISIBLE);
                // VariabiliStaticheHome.getInstance().getTxtOperazioneWEB().setText();
                VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1,true,
                        "Problemi nel caricamento del brano");
                VariabiliStaticheGlobali.getInstance().setStaSuonando(false);
                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                File ff = new File(Mp3);
                try {
                    ff.delete();
                } catch (Exception ignored) {

                }
            }
            // } else {
            //     if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
            //         vh.getMediaPlayer().start();
            //     }
        }

        if (vh.getMediaPlayer()!=null) {
            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(-1);

            if (VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
                vh.getMediaPlayer().seekTo(0);
            }
            vh.getSeekBar1().setMax(vh.getMediaPlayer().getDuration());
            vh.getSeekBar1().setProgress(0);
            VariabiliStaticheGlobali.getInstance().setMusicaTerminata(false);

            GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
            VariabiliStaticheGlobali.getInstance().setHaScaricatoAutomaticamente(false);
            VariabiliStaticheGlobali.getInstance().setScrittaAscoltata(false);

            vh.getTxtTitoloBackground().setText("");
            vh.getTxtTitoloBackground().setVisibility(LinearLayout.GONE);

            int perc2 = 0;
            if (vh.getMediaPlayer() != null) {
                int durata = vh.getMediaPlayer().getDuration();
                perc2 = durata * 75 / 100;
            }
            final int percFine = perc2;

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "Parte l'handler della barra di avanzamento brano");
            vh.setHandlerSeekBar(new Handler(Looper.getMainLooper()));
            vh.getHandlerSeekBar().postDelayed(rSeekBar = new Runnable() {
                @Override
                public void run() {
                    if (vh.getrSeekBar() == null) {
                        vh.setrSeekBar(rSeekBar);
                    }
                    if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                        if (vh.getMediaPlayer() != null) {
                            int mCurrentPosition = vh.getMediaPlayer().getCurrentPosition();

                            int perc75 = vh.getMediaPlayer().getDuration() * 75 / 100;
                            if (mCurrentPosition >= perc75 && !VariabiliStaticheGlobali.getInstance().getScrittaAscoltata()) {
                                VariabiliStaticheGlobali.getInstance().setScrittaAscoltata(true);

                                // Aggiorna il numero di volte ascoltata dal brano
                                int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getNumeroBranoInAscolto();

                                int nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false,
                                        "Aggiorna volte ascoltata");
                                int Ascoltata = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).getQuanteVolteAscoltato();
                                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).setQuanteVolteAscoltato(Ascoltata+1);

                                // DBRemotoNuovo dbr = new DBRemotoNuovo();
                                // dbr.VolteAscoltata(Integer.toString(NumeroBrano), nn);

                                db_dati db = new db_dati();
                                db.ScriveAscoltate(Integer.toString(NumeroBrano));
                                // Aggiorna il numero di volte ascoltata dal brano

                                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(nn, true);
                            }

                            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getCaricamentoAnticipato()) {
                                int perc10 = vh.getMediaPlayer().getDuration() * 10 / 100;
                                if (mCurrentPosition >= perc10 && VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                                    // Tenta di scaricare il brano successivo se non esiste sul disco per diminuire i ritardi fra
                                    // un brano e l'altro
                                    if (!VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                                        if (!VariabiliStaticheGlobali.getInstance().getHaScaricatoAutomaticamente()) {
                                            // if (VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata() != null) {
                                            //     GestioneImmagini.getInstance().ImpostaImmagineDiSfondo(VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata(), "IMMAGINE", -1, null);
                                            // } else {
                                            //     GestioneImmagini.getInstance().ImpostaImmagineVuota();
                                            // }
                                            // GestioneImmagini.getInstance().getImgBrano().setVisibility(LinearLayout.VISIBLE);
                                            VariabiliStaticheGlobali.getInstance().setHaScaricatoAutomaticamente(true);
                                            if (GestioneListaBrani.getInstance().RitornaIndiceBranoAttuale() >= GestioneListaBrani.getInstance().RitornaQuantiBraniInLista()) {
                                                VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(true);
                                                GestioneListaBrani.getInstance().ScaricaBranoSuccessivoInBackground();
                                            }
                                        }
                                    // } else {
                                        // VariabiliStaticheGlobali.getInstance().setHaScaricatoAutomaticamente(true);
                                    }
                                }
                            }
                            vh.getSeekBar1().setProgress(mCurrentPosition);

                            // if (!vh.getMediaPlayer().isPlaying()) {
                            // int perc2 = vh.getMediaPlayer().getDuration() * 50 / 100;
                            if (!vh.getMediaPlayer().isPlaying() && mCurrentPosition >= percFine) {
                                // hNuovoMP3 = new Handler();
                                // hNuovoMP3.postDelayed(NuovoMp3 = new Runnable() {
                                // @Override
                                // public void run() {
                                try {
                                    if (vh.getHandlerSeekBar() != null) {
                                        vh.getHandlerSeekBar().removeCallbacksAndMessages(null);
                                        vh.getHandlerSeekBar().removeCallbacks(VariabiliStaticheHome.getInstance().getrSeekBar());
                                        vh.setHandlerSeekBar(null);
                                    }
                                } catch (Exception ignored) {

                                }

                                // hNuovoMP3.removeCallbacks(NuovoMp3);
                                // hNuovoMP3 = null;
                                // NuovoMp3 = null;

                                // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(true);
                                VariabiliStaticheGlobali.getInstance().setMusicaTerminata(true);
                                // int NumeroBrano = GestioneListaBrani.getInstance().RitornaNumeroProssimoBrano(true, "AVANTI BRANO CON ATTESA");
                                int NumeroBrano = GestioneListaBrani.getInstance().RitornaNumeroProssimoBranoNuovo(true);
                                GestioneOggettiVideo.getInstance().ControllaAvantiBrano(NumeroBrano, false);
                                //      }
                                //  }, 50);
                            } else {
                                if (!vh.getMediaPlayer().isPlaying()) {
                                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                                            "Brano terminato ma barra non arrivata al 75%",
                                            true,
                                            VariabiliStaticheGlobali.NomeApplicazione);
                                }
                                String seconds = String.valueOf((mCurrentPosition % 60000) / 1000);
                                String minutes = String.valueOf(mCurrentPosition / 60000);

                                if (seconds.length() == 1) seconds = "0" + seconds;
                                if (minutes.length() == 1) minutes = "0" + minutes;

                                vh.getTxtMin().setText(minutes + ":" + seconds);
                            }
                        }
                    }

                    if (vh.getHandlerSeekBar() != null) {
                        vh.getHandlerSeekBar().postDelayed(this, 1000);
                    }
                }
            }, 1000);

            // vh.setHandlerSeekBar(new Handler());
            // VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().runOnUiThread(new Runnable() {
            //     @Override
            //     public void run() {
            //     }
            // });

            // if (Error) {
            //     vh.getSeekBar1().setEnabled(false);
            // } else {
            vh.getSeekBar1().setEnabled(true);
            vh.getSeekBar1().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (vh.getMediaPlayer() != null && fromUser) {
                        VariabiliStaticheGlobali.getInstance().setHaScaricatoAutomaticamente(true);
                        VariabiliStaticheGlobali.getInstance().setScrittaAscoltata(true);

                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Spostato brano tramite barra. Progress: " + Integer.toString(progress));
                        vh.getMediaPlayer().seekTo(progress);

                        String seconds = String.valueOf((progress % 60000) / 1000);
                        String minutes = String.valueOf(progress / 60000);

                        if (seconds.length() == 1) seconds = "0" + seconds;
                        if (minutes.length() == 1) minutes = "0" + minutes;

                        vh.getTxtMin().setText(minutes + ":" + seconds);
                    }
                }
            });
            // }

            // if (VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
            //     if (!VariabiliStaticheGlobali.getInstance().getHaCaricatoTuttiIDettagliDelBrano() &&
            //             VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
            //         VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(true);
            //         GestioneOggettiVideo.getInstance().PlayBrano(true);
            //     }
            //
            //     GestioneNotifiche.getInstance().GeneraNotifica();
            // }
        }
    }
}
