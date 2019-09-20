package looigi.loowebplayer.thread;

import android.os.Handler;
import android.os.Looper;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dialog.DialogMessaggio;
// import looigi.loowebplayer.soap.CheckURLFile;
import looigi.loowebplayer.soap.DownloadMP3Nuovo;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.PronunciaFrasi;
import looigi.loowebplayer.utilities.RiempieListaInBackground;
import looigi.loowebplayer.utilities.Utility;

public class ScaricoBranoEAttesa {
    private Runnable runRiga;
    private Handler hSelezionaRiga;
    private Runnable rAttendeRispostaCheckURL;
    private Handler hAttendeRispostaCheckURL;
    private boolean inBackground;
    private int Secondi;
    private int Conta;
    private int PerPronuncia;
    private int nn;
    // private CheckURLFile cuf;
    private String Altro="";

    public void setInBackground(boolean inBackground) {
        this.inBackground = inBackground;
    }

    public void ScaricaBrano(int NumeroBrano, String[] Brano, int NumeroOperazione, boolean inBackground) {
        VariabiliStaticheGlobali.getInstance().setStaScaricandoNormalmente(true);

        int campi = Brano.length - 1;
        String url = VariabiliStaticheGlobali.getInstance().PercorsoURL + "/";
        boolean compresso = false;
        // if (Brano[campi].toUpperCase().contains("COMPRESSI")) {
        if (Brano[1].toUpperCase().contains("COMPRESSI")) {
            compresso = true;
            url += "Compressi/";
        } else {
            url += "Dati/";
        }
        if (!VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase().isEmpty()) {
            url += VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase() + "/";
        }

        if (Brano.length > 1) {
            String sBrano = Brano[campi];
            String sAlbum = Brano[campi - 1];
            String sArtista = "";
            if (Brano.length > 2) {
                sArtista = Brano[campi - 2];
            }

            if (!url.contains(sArtista) && !url.contains(sAlbum)) {
                url += sArtista + "/" + sAlbum + "/" + sBrano;
            } else {
                url += sBrano;
            }

            if (inBackground) {
                GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.salva);
            }

            DownloadMP3Nuovo d = new DownloadMP3Nuovo();
            String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();

            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);

            if (s == null) {
                RiempieListaInBackground r = new RiempieListaInBackground();
                r.RiempieStrutture(true, true);

                s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
            }

            if (s != null) {
                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();

                if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
                    d.setPath(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + sArtista + "/" + sAlbum);
                } else {
                    d.setPath(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/");
                }

                d.setNomeBrano(sBrano);
                d.setCompresso(compresso);
                if (inBackground) {
                    d.setAutomatico(true);
                } else {
                    d.setAutomatico(false);
                }
                d.setNumeroBrano(NumeroBrano);
                d.setContext(VariabiliStaticheGlobali.getInstance().getContext());
                d.startDownload(url, NumeroOperazione);
            } else {
                GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.error);
                VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                // VariabiliStaticheGlobali.getInstance().setAttendeFineScaricamento(false);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Struttura vuota in scarica brano. Numero brano: " + Integer.toString(NumeroBrano));
                // DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                //         "Struttura vuota in ScaricaBrano. Numero Brano: " + NumeroBrano,
                //         true,
                //         VariabiliStaticheGlobali.NomeApplicazione);
            }
        } else {
            String bb = "";

            for (String b : Brano) {
                bb += (b + ";");
            }
            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                    "Errore durante la routine ScaricaBrano.\nBrano: " + bb,
                    true,
                    VariabiliStaticheGlobali.NomeApplicazione);
        }
    }
}
