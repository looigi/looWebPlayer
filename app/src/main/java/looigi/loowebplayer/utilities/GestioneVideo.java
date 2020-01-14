package looigi.loowebplayer.utilities;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaVideo;

public class GestioneVideo {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("GestioneVideo");;
    private static GestioneVideo instance = null;

    private GestioneVideo() {
    }

    public static GestioneVideo getInstance() {
        if (instance == null) {
            instance = new GestioneVideo();
        }

        return instance;
    }

    public void SalvaVideoSuSD(String Artista, List<StrutturaVideo> v) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Salva video su SD. "+Artista);
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String pathMultimediaArtista = "";
        if (!pathBase.equals(Artista)) {
            pathMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/Video/" + pathBase + "/" + Artista + "/";
        } else {
            pathMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/Video/" + pathBase + "/";
        }
        String NomeFile = "ListaVideo.dat";
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Salva video su SD. Path "+ pathMultimediaArtista+"/"+NomeFile);

        File f = new File(pathMultimediaArtista, NomeFile);
        if (f.exists()) {
            f.delete();
        } else {
            f = new File(pathMultimediaArtista);
            f.mkdirs();
        }

        String contenuto="";

        for (StrutturaVideo s : v) {
            contenuto+=Integer.toString(s.getIdCartella()) + ";" + s.getNomeVideo() + ";" + Long.toString(s.getLunghezza()) + "§";
        }

        GestioneFiles.getInstance().CreaFileDiTesto(pathMultimediaArtista, NomeFile, contenuto);
    }

}
