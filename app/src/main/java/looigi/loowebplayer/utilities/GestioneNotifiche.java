package looigi.loowebplayer.utilities;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaArtisti;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.notifiche.Notifica;

public class GestioneNotifiche {
    private static final GestioneNotifiche ourInstance = new GestioneNotifiche();

    public static GestioneNotifiche getInstance() {
        return ourInstance;
    }

    private GestioneNotifiche() {
    }

    public void GeneraNotifica() {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        if (NumeroBrano>-1) {
            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati().get(NumeroBrano);
            int idArtista = s.getIdArtista();
            StrutturaArtisti ar = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista);
            int idAlbum = s.getIdAlbum();
            StrutturaAlbum al = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlbum);
            String Immagine = "";
            String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
            String PathFile = "";
            if (!pathBase.equals(ar.getArtista()) && !ar.getArtista().equals(al.getNomeAlbum())) {
                PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + ar.getArtista() + "/" + al.getNomeAlbum() + ".jpg";
            } else {
                PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/Album.jpg";
            }
            File f = new File(PathFile);
            if (f.exists()) {
                Immagine = PathFile;
            }
            Notifica.getInstance().setContext(VariabiliStaticheGlobali.getInstance().getContext());
            Notifica.getInstance().setTitolo(s.getNomeBrano());
            Notifica.getInstance().setArtista(ar.getArtista());
            Notifica.getInstance().setAlbum(al.getNomeAlbum());
            Notifica.getInstance().setImmagine(Immagine);

            Notifica.getInstance().AggiornaNotifica();
        }
    }
}
