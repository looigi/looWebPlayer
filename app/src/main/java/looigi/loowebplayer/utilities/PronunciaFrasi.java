package looigi.loowebplayer.utilities;

import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;

public class PronunciaFrasi {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("PronunciaFrasi");;
    private Runnable runRiga1;
    private Handler hSelezionaRiga1;
    private TextToSpeech tts=null;

    public void PronunciaFrase(String Messaggio, String Lingua) {
        if (!Messaggio.equals(VariabiliStaticheGlobali.getInstance().getUltimaCosaPronunciata())) {
            VariabiliStaticheGlobali.getInstance().setUltimaCosaPronunciata(Messaggio);

            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getPronunciaOperazioni()) {
                Legge(Messaggio, Lingua);
            }
        }
    }

    private void Legge(final String Messaggio, final String Lingua) {
        if (tts==null) {
            // if (!VariabiliStaticheGlobali.getInstance().isEseguiHandlerConMainLooper()) {
            //     hSelezionaRiga1 = new Handler();
            // } else {
                hSelezionaRiga1 = new Handler(Looper.getMainLooper());
            // }
            hSelezionaRiga1.postDelayed(runRiga1=new Runnable() {
                @Override
                public void run() {
                    tts = new TextToSpeech(VariabiliStaticheGlobali.getInstance().getContextPrincipale(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            hSelezionaRiga1.removeCallbacks(runRiga1);
                            if (status == TextToSpeech.SUCCESS) {
                                Legge2(Messaggio, Lingua);
                            } else {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "ERROR in pronuncia frasi. Initialization error");
                            }
                        }
                    });
                }
            }, 500);

        } else {
            // if (!VariabiliStaticheGlobali.getInstance().isEseguiHandlerConMainLooper()) {
            //     hSelezionaRiga1 = new Handler();
            // } else {
                hSelezionaRiga1 = new Handler(Looper.getMainLooper());
            // }
            hSelezionaRiga1.postDelayed(runRiga1=new Runnable() {
                @Override
                public void run() {
                    hSelezionaRiga1.removeCallbacks(runRiga1);
                    Legge2(Messaggio, Lingua);
                }
            }, 500);
        }
    }

    private void Legge2(String Messaggio, String Lingua) {
        if (VariabiliStaticheHome.getInstance().getMediaPlayer()!=null) {
            VariabiliStaticheHome.getInstance().getMediaPlayer().setVolume(50F, 50F);
        }
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                "Legge2 PronunciaFrasi");
        try {
            int result = -1;
            if (Lingua.equals("ITALIANO")) {
                result = tts.setLanguage(Locale.ITALIAN);
            } else {
                result = tts.setLanguage(Locale.US);
            }
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                        "ERROR in pronuncia frasi. Linguaggio non installato");
            } else {
                tts.speak(Messaggio, TextToSpeech.QUEUE_ADD, null);
            }
        } catch (Exception e) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "ERROR in pronuncia frasi.");
            int a=0;
        }
        if (VariabiliStaticheHome.getInstance().getMediaPlayer()!=null) {
            VariabiliStaticheHome.getInstance().getMediaPlayer().setVolume(100F, 100F);
        }
    }

    public void StoppaFrase() {
        if (tts!=null) {
            try {
                tts.stop();
            } catch (Exception ignored) {

            }
        }
    }
}
