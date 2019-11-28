package looigi.loowebplayer.nuova_versione;

import android.os.Handler;
import android.os.Looper;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.utilities.Utility;

public class ControlloVersioneApplicazione {
    private static String verAttuale = "";
    private static Runnable runRiga;
    private static Handler hSelezionaRiga;

    public void ControllaVersione() {
        verAttuale = Utility.getInstance().getVersion(VariabiliStaticheGlobali.getInstance().getContext());

        int NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1,
                false, "Controllo versione");
        DBRemotoNuovo dbr = new DBRemotoNuovo();
        dbr.RitornaVersioneApplicazione(VariabiliStaticheGlobali.getInstance().getContext(),NumeroOperazione);
    }

    public static void ControlloVersione(String NuovaVersione) {
        if (!NuovaVersione.equals(verAttuale) && !NuovaVersione.isEmpty()) {
            String path = VariabiliStaticheGlobali.RadiceWS+"NuoveVersioni/looWebPlayer.apk";

            UpdateApp attualizaApp = new UpdateApp();
            attualizaApp.execute(path);
        // } else {
            // MainActivity.PrendeDatiStatistici(VariabiliStatiche.getInstance().getMainContext());
        } else {
            String outapk = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/update.apk";
            File f = new File(outapk);
            if (f.exists()) {
                f.delete();
            }

            hSelezionaRiga = new Handler(Looper.getMainLooper());
            hSelezionaRiga.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hSelezionaRiga.removeCallbacks(runRiga);
                    runRiga = null;

                    int NumeroOperazione2 = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1,
                            false, "Controllo aggiornamenti libreria");
                    DBRemotoNuovo dbr = new DBRemotoNuovo();
                    dbr.RitornaSeDeveAggiornare(VariabiliStaticheGlobali.getInstance().getContext(),NumeroOperazione2);
                }
            }, 100);
        }
    }
}
