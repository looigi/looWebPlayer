package looigi.loowebplayer.utilities;

import android.os.Handler;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;

public class GestioneMediaPlayer {
    private static final GestioneMediaPlayer ourInstance = new GestioneMediaPlayer();

    public static GestioneMediaPlayer getInstance() {
        return ourInstance;
    }

    private GestioneMediaPlayer() {
    }

    private Runnable SfumaOutMp3;
    private Handler hSfumaOutMP3;
    private static float vol;

    public void SfumaAudio(final String Mp3) {
        final VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getSfumaBrano()) {
            vol = 1;

            hSfumaOutMP3 = new Handler();
            hSfumaOutMP3.postDelayed(SfumaOutMp3 = new Runnable() {
                @Override
                public void run() {
                    vol -= .03;
                    if (vol > .03) {
                        vh.getMediaPlayer().setVolume(vol, vol);
                        hSfumaOutMP3.postDelayed(SfumaOutMp3, 100);
                    } else {
                        hSfumaOutMP3.removeCallbacks(SfumaOutMp3);
                        vh.getMediaPlayer().pause();

                        GestioneSuonaBrano.getInstance().SuonaBrano(Mp3);
                    }
                }
            }, 100);
        } else {
            vh.getMediaPlayer().pause();
            GestioneSuonaBrano.getInstance().SuonaBrano(Mp3);
        }
    }
}
