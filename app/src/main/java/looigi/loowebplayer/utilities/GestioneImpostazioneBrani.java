package looigi.loowebplayer.utilities;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dialog.DialogMessaggio;

public class GestioneImpostazioneBrani {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("GestioneImpostazioneBrani");;
    private static final GestioneImpostazioneBrani ourInstance = new GestioneImpostazioneBrani();

    public static GestioneImpostazioneBrani getInstance() {
        return ourInstance;
    }

    private GestioneImpostazioneBrani() {
    }

    public String setDurata(String Mp3) {
        String seconds = "";
        String minutes = "";

        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        String Ritorno="";
        try {
            metaRetriever.setDataSource(Mp3);
            String duration =
                    metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long dur = Long.parseLong(duration);
            seconds = String.valueOf((dur % 60000) / 1000);
            minutes = String.valueOf(dur / 60000);
            if (seconds.length()==1) seconds="0"+seconds;
            if (minutes.length()==1) minutes="0"+minutes;
            metaRetriever.release();
            Ritorno = minutes + ";" + seconds;
        } catch (Exception e) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            Ritorno="";
        }

        return Ritorno;
    }

    public void ImpostaBrano(final String Mp3) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass()
                .getEnclosingMethod().getName(), "Entrata imposta brano");
        final VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

        vh.setBranoDaCaricare("");

        vh.getTxtMin().setText("00:00");

        String Durata = setDurata(Mp3);
        // Durata = "";
        if (Durata.isEmpty()) {
            // down = ContextCompat.getDrawable(VariabiliStaticheHome.getInstance().getContext(), R.drawable.error);
            // VariabiliStaticheHome.getInstance().getImgLoadBrano().setVisibility(LinearLayout.VISIBLE);
            // VariabiliStaticheHome.getInstance().getImgLoadBrano().setImageDrawable(down);

            // VariabiliStaticheGlobali.getInstance().setStaSuonando(false);
            // vh.getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay());
            // vh.getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop_dis());
            // vh.getImgPlay().setEnabled(false);

            // vh.getLayOperazionWEB().setVisibility(LinearLayout.VISIBLE);
            // vh.getTxtOperazioneWEB().setText();
            VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1,true,
                    "MP3 non valido");

            File f = new File(Mp3);
            f.delete();

            PronunciaFrasi pf =new PronunciaFrasi();
            pf.PronunciaFrase("Brano non valido", "ITALIANO");

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass()
                    .getEnclosingMethod().getName(), "Durata 0: Brano non valido -> Prendo il successivo");

            // Tento di prendere il brano successivo
            GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
            GestioneOggettiVideo.getInstance().AvantiBrano();
            GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(false);

            /* if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setCompressioneMP3(false);
                GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano();
            }

            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                    "Brano non valido...",
                    true,
                    VariabiliStaticheGlobali.NomeApplicazione); */
        } else {
            // VariabiliStaticheHome.getInstance().getImgLoadBrano().setVisibility(LinearLayout.GONE);

            // vh.getLayOperazionWEB().setVisibility(LinearLayout.GONE);
            // vh.getImgPlay().setEnabled(true);
            String[] d= Durata.split(";", -1);
            String minutes=d[0];
            String seconds=d[1];

            vh.getTxtMax().setText(minutes + ":" + seconds);

            if (vh.getMediaPlayer()!=null) {
                if (VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
                    if (VariabiliStaticheGlobali.getInstance().getStaSuonando() &&
                            VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
                        GestioneMediaPlayer.getInstance().SfumaAudio(Mp3);
                    } else {
                        // if (!VariabiliStaticheGlobali.getInstance().getStaAttendendoConMusichetta()) {
                        GestioneSuonaBrano.getInstance().SuonaBrano(Mp3);
                        // }
                    }
                } else {
                    GestioneSuonaBrano.getInstance().SuonaBrano(Mp3);
                }
            } else {
                vh.setMediaPlayer(MediaPlayer.create(vh.getContext(),
                        Uri.parse(Mp3)));
                if (vh.getMediaPlayer()!=null) {
                    vh.getMediaPlayer().setLooping(false);

                    GestioneSuonaBrano.getInstance().SuonaBrano(Mp3);
                }
            }
        }
    }
}
