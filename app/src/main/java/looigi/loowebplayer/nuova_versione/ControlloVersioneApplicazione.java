package looigi.loowebplayer.nuova_versione;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.utilities.Utility;

public class ControlloVersioneApplicazione {
    private static String verAttuale = "";

    public void ControllaVersione() {
        verAttuale = Utility.getInstance().getVersion(VariabiliStaticheGlobali.getInstance().getContext());

        int NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1,
                false, "Controllo versione");
        DBRemotoNuovo dbr = new DBRemotoNuovo();
        dbr.RitornaVersioneApplicazione(VariabiliStaticheGlobali.getInstance().getContext(),NumeroOperazione);
    }

    public static void ControlloVersione(String NuovaVersione) {
        if (!NuovaVersione.equals(verAttuale) && !NuovaVersione.isEmpty()) {
            String path = VariabiliStaticheGlobali.RadiceWS+"/NuoveVersioni/looWebPlayer.apk";

            UpdateApp attualizaApp = new UpdateApp();
            attualizaApp.execute(path);
        // } else {
            // MainActivity.PrendeDatiStatistici(VariabiliStatiche.getInstance().getMainContext());
        }
    }
}
