package looigi.loowebplayer.utilities;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.notifiche.Notifica;
import looigi.loowebplayer.soap.DownloadImmagineNuovo;

public class ScaricoCover {
    private DownloadImmagineNuovo d;

    public int RitornaImmagineBrano(String Artista, String Album, String Brano, int nScarico) {
        int Ritorno=0;

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagine brano. Artista: "+Artista+ " Album: "+Album+" Brano: "+Brano);
        String NomeBrano = Brano;
        if (NomeBrano.contains(".")) {
            NomeBrano=NomeBrano.substring(0,NomeBrano.indexOf("."));
        }

        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String PathFile = "";
        if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/" + Album + ".jpg";
        } else {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/"+NomeBrano+".jpg";
        }
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagine brano. PathFile: "+PathFile);
        PathFile = PathFile.replace("#","_");
        File f = new File(PathFile);
        if (f.exists()) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagine brano. Già esiste. La imposto");
            GestioneImmagini.getInstance().ImpostaImmagineDiSfondo(PathFile, "IMMAGINE", -1, null);

            GestioneImmagini.getInstance().SettaImmagineSuIntestazione(PathFile);
            VariabiliStaticheGlobali.getInstance().setImmagineMostrata(PathFile);

            Notifica.getInstance().setImmagine(PathFile);
            Notifica.getInstance().AggiornaNotifica();

            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(nScarico, false);
            Ritorno=-1;
        } else {
            GestioneImmagini.getInstance().ImpostaImmagineVuota();

            // http://looigi.no-ip.biz:12345/loowebplayer/Dati/Great%20White/2005-Twice%20Shy/Cover_Great%20White.jpg
            d = new DownloadImmagineNuovo();
            d.setPath(PathFile);
            d.setInSfuma(false);
            d.setNumeroOperazione(nScarico);
            d.setCaricaImmagineBrano(true);

            int TimeOut = VariabiliStaticheGlobali.getInstance().getTimeOutImmagini();

            if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagine brano. Scarico immagine: " + VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/Cover_" + Artista + ".jpg");
                d.startDownload(VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/Cover_" + Artista + ".jpg", "Download immagine brano", TimeOut);
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagine brano. Scarico immagine: " + VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Dati/" + pathBase + "/Cover_" + Artista + ".jpg");
                d.startDownload(VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Dati/" + pathBase + "/Cover_" + Artista + ".jpg", "Download immagine brano", TimeOut);
            }
            Ritorno=0;
        }

        return Ritorno;
    }

    public void BloccaOperazione() {
        if (d!=null) {
            d.StoppaEsecuzione();
            d=null;
        }
    }
}
