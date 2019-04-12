package looigi.loowebplayer.utilities;

import android.widget.LinearLayout;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.soap.GestioneWEBServiceSOAPNuovo;

public class GestioneTesti {
    /* private static GestioneTesti instance = null;

    private GestioneTesti() {
    }

    public static GestioneTesti getInstance() {
        if (instance == null) {
            instance = new GestioneTesti();
        }

        return instance;
    }

    private Runnable runRiga;
    private Handler hSelezionaRiga; */
    private GestioneWEBServiceSOAPNuovo GestioneWS;

    public void SalvaTestoSuSD(String Artista, String Album, String Brano, String Testo, String TestoTradotto, String Ascoltata, String Bellezza) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Salva testo su SD: "+Artista+" "+Album+" "+Brano);
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String pathTesto = "";
        if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
            pathTesto = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/";
        } else {
            pathTesto = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/";
        }
        String NomeFile = Utility.getInstance().SistemaTesto(Brano + ".dat");
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Salva testo su SD. Path: "+pathTesto+"/"+NomeFile);

        File f = new File(pathTesto, NomeFile);
        if (f.exists()) {
            f.delete();
        } else {
            f = new File(pathTesto);
            f.mkdirs();
        }

        GestioneFiles.getInstance().CreaFileDiTesto(pathTesto, NomeFile,
                Testo.replace(";", "**PV**").replace("§", "**A CAPO**") +";" +
                TestoTradotto.replace(";", "**PV**").replace("§", "**A CAPO**") +";" +
                Ascoltata + ";" +
                Bellezza);
    }

    public void SettaTesto(Boolean Pulisce) {
        // Drawable down = ContextCompat.getDrawable(VariabiliStaticheHome.getInstance().getContext(), R.drawable.testo);
        // VariabiliStaticheHome.getInstance().getImgLoadBrano().setVisibility(LinearLayout.VISIBLE);
        // VariabiliStaticheHome.getInstance().getImgLoadBrano().setImageDrawable(down);

        String sTesto = "";

        VariabiliStaticheHome.getInstance().getImgLinguettaTesto().setVisibility(LinearLayout.GONE);
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Imposta testo. Pulisce: "+Pulisce);
        if (!Pulisce) {
            int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
            if (NumeroBrano>-1) {
                String Testo = "";
                if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getTestoInInglese()) {
                    Testo = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati().get(NumeroBrano).getTesto();
                } else {
                    Testo = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati().get(NumeroBrano).getTestoTradotto();
                }
                sTesto = Testo.replace("**A CAPO**", "\n");
                sTesto = sTesto.replace("%20", " ");
                sTesto = sTesto.replace("**PV**", ";");
                if (!sTesto.toUpperCase().contains("NESSUN TESTO") && !sTesto.isEmpty()) {
                    VariabiliStaticheHome.getInstance().getImgLinguettaTesto().setVisibility(LinearLayout.VISIBLE);
                }
            }
        }

        VariabiliStaticheHome.getInstance().getTxtTesto().setText(sTesto);
    }

    // public String RitornaTestoDaSD(String Artista, String Album, String Brano, Boolean Refresh, int NumeroOperazione) {
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
            SettaTesto(false);

            if (idBrano>-1) {
                // if (Refresh && VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo da SD. Scarico dettaglio brano");

                    // int nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Ritorna testo da SD");
                    DBRemotoNuovo dbr = new DBRemotoNuovo();
                    GestioneWS = dbr.RitornaDettaglioBrano(VariabiliStaticheGlobali.getInstance().getContext(), Artista, Album, Brano, NumeroOperazione);
                } else {
                    // ProsegueElaborazioneSenzaScaricareIlDettaglio(NumeroOperazione);
                    VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
                }
            }
        } else {
            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo da SD. Scarico dettaglio brano");

                DBRemotoNuovo dbr = new DBRemotoNuovo();
                GestioneWS = dbr.RitornaDettaglioBrano(VariabiliStaticheGlobali.getInstance().getContext(), Artista, Album, Brano, NumeroOperazione);
            } else {
                // ProsegueElaborazioneSenzaScaricareIlDettaglio(NumeroOperazione);
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
            }
        }

        return Testo;
    }

    public void BloccaOperazione() {
        if (GestioneWS!=null) {
            GestioneWS.StoppaEsecuzione();
            GestioneWS=null;
        }
    }

    /* private void ProsegueElaborazioneSenzaScaricareIlDettaglio(final int NumeroOperazione) {
        if (!VariabiliStaticheHome.getInstance().getBranoDaCaricare().isEmpty()) {
            hSelezionaRiga = new Handler();
            hSelezionaRiga.postDelayed(runRiga = new Runnable() {
                @Override
                public void run() {
                    String c[] = VariabiliStaticheHome.getInstance().getBranoDaCaricare().split(";",-1);
                    String Converte = "N";
                    String Qualita = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getRapportoCompressione();
                    if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                        Converte = "S";
                    }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna dettaglio brano. Ritorna brano: "+
                            VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase()+" "+
                            c[0]+" "+
                            c[1]+" "+
                            c[2]+" "+
                            Converte+" "+
                            Qualita
                    );

                    int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Download brano");
                    DBRemoto dbr = new DBRemoto();
                    dbr.RitornaBrano(VariabiliStaticheHome.getInstance().getContext(),
                            VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase(),
                            c[0],
                            c[1],
                            c[2],
                            Converte,
                            Qualita,
                            n
                    );
                }
            }, 50);
        }

    } */
}
