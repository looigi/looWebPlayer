package looigi.loowebplayer.utilities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;

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
                VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1,true, "Problemi nel caricamento del brano");
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

            // int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
            // StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
            // String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
            // if (VariabiliStaticheHome.getInstance().getImms().size() == 0) {
            //     VariabiliStaticheHome.getInstance().setImms(GestioneImmagini.getInstance().RitornaImmaginiArtista("", Artista, -1, false));
            // }
            // if (VariabiliStaticheHome.getInstance().getImms().size() > 0) {
            //     if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
            //         GestioneImmagini.getInstance().CreaCarosello();
            //     }
            // } else {
            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Nessuna immagine per far partire il carosello");
            // }

            // VariabiliStaticheGlobali.getInstance().setNonFermareDownload(false);
            GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
            VariabiliStaticheGlobali.getInstance().setHaScaricatoAutomaticamente(false);

            vh.getTxtTitoloBackground().setText("");
            vh.getTxtTitoloBackground().setVisibility(LinearLayout.GONE);
            // GestioneImmagini.getInstance().CreaCarosello(VariabiliStaticheHome.getInstance().getImms());
            // VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata(null);

            // if (VariabiliStaticheGlobali.getInstance().isStaScaricandoBrano()) {
            //     VariabiliStaticheGlobali.getInstance().setStaScaricandoBrano(false);
            // }

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "Parte l'handler della barra di avanzamento brano");
            vh.setHandlerSeekBar(new Handler());
            vh.getHandlerSeekBar().postDelayed(rSeekBar = new Runnable() {
                @Override
                public void run() {
                    if (vh.getrSeekBar() == null) {
                        vh.setrSeekBar(rSeekBar);
                    }
                    if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                        if (vh.getMediaPlayer() != null) {
                            int mCurrentPosition = vh.getMediaPlayer().getCurrentPosition();
                            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getCaricamentoAnticipato()) {
                                int perc15 = vh.getMediaPlayer().getDuration() * 15 / 100;
                                if (mCurrentPosition >= perc15 && VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                                    // Tenta di scaricare il brano successivo se non esiste sul disco per diminuire i ritardi fra
                                    // un brano e l'altro
                                    if (!VariabiliStaticheGlobali.getInstance().getHaScaricatoAutomaticamente()) {
                                        // NetThread.getInstance().setCaroselloBloccato(true);
                                        // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(true);
                                        if (VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata() != null) {
                                            // VariabiliStaticheHome.getInstance().getImgBrano().setImageBitmap(BitmapFactory.decodeFile(VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata()));
                                            GestioneImmagini.getInstance().ImpostaImmagineDiSfondo(VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata(), "IMMAGINE", -1, null);
                                        } else {
                                            GestioneImmagini.getInstance().ImpostaImmagineVuota();
                                        }
                                        GestioneImmagini.getInstance().getImgBrano().setVisibility(LinearLayout.VISIBLE);
                                        VariabiliStaticheGlobali.getInstance().setHaScaricatoAutomaticamente(true);
                                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(true);
                                        GestioneListaBrani.getInstance().ScaricaBranoSuccessivoInBackground();
                                    }
                                }
                            }
                            vh.getSeekBar1().setProgress(mCurrentPosition);

                            if (!vh.getMediaPlayer().isPlaying()) {
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
                        // VariabiliStaticheGlobali.getInstance().setHaScaricatoAutomaticamente(true);

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
