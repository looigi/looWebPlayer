package looigi.loowebplayer.utilities;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;

public class ScaricoTesto {
    public String RitornaTestoDaSD(String Artista, String Album, String Brano, int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo da SD. "+Artista+" "+Album+" "+Brano);
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String pathTesto = "";
        if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
            pathTesto = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/";
        } else {
            pathTesto = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/";
        }
        String NomeFile = Utility.getInstance().SistemaTesto(Brano + ".dat");
        String Testo="";
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo da SD. Path: "+pathTesto+"/"+NomeFile);

        File f = new File(pathTesto, NomeFile);
        if (f.exists()) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo da SD. File esistente");
            Testo = GestioneFiles.getInstance().LeggeFileDiTesto(pathTesto+NomeFile);
            String t[] = Testo.split(";",-1);
            int idBrano=VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
            int n = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
            if (idBrano>n) {
                if (n>0) {
                    idBrano = 0;
                } else {
                    idBrano = -1;
                }
            }

            if (idBrano>-1) {
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBrano).setTesto(t[0]);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBrano).setTestoTradotto(t[1]);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBrano).setQuanteVolteAscoltato(Integer.parseInt(t[2]));
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBrano).setStelle(Integer.parseInt(t[3]));
            }

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo da SD. ImpostaStelleAscoltata in Home");
            GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo da SD. SettaTesto in Home");
            GestioneTesti gt = new GestioneTesti();
            gt.SettaTesto(false);

            if (idBrano>-1) {
                // if (Refresh && VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo da SD. Scarico dettaglio brano");
                    int nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Ritorna testo da SD");
                    DBRemotoNuovo dbr = new DBRemotoNuovo();
                    dbr.RitornaDettaglioBrano(VariabiliStaticheGlobali.getInstance().getContext(), Artista, Album, Brano, nn);
                // } else {
                //     ProsegueElaborazioneSenzaScaricareIlDettaglio(NumeroOperazione);
                }
            }
        } else {
            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo da SD. Scarico dettaglio brano");
                int nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download dettaglio brano");
                DBRemotoNuovo dbr = new DBRemotoNuovo();
                dbr.RitornaDettaglioBrano(VariabiliStaticheGlobali.getInstance().getContext(), Artista, Album, Brano, nn);
            // } else {
            //     ProsegueElaborazioneSenzaScaricareIlDettaglio(NumeroOperazione);
            }
        }

        return Testo;
    }
}
