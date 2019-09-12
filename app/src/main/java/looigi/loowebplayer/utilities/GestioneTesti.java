package looigi.loowebplayer.utilities;

import android.widget.LinearLayout;

import org.kobjects.util.Util;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.soap.DownloadTextFileNuovo;
import looigi.loowebplayer.soap.GestioneWEBServiceSOAPNuovo;

public class GestioneTesti {
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

    public void SettaTesto(boolean Pulisce) {
        String sTesto = "";

        VariabiliStaticheHome.getInstance().getImgLinguettaTesto().setVisibility(LinearLayout.GONE);
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "Imposta testo. Pulisce: "+Pulisce);
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
                    VariabiliStaticheHome.getInstance().getImgScaricaTesto().setVisibility(LinearLayout.GONE);
                }
            }
        }

        VariabiliStaticheHome.getInstance().getTxtTesto().setText(sTesto);
    }

    // public String RitornaTestoDaSD(String Artista, String Album, String Brano, boolean Refresh, int NumeroOperazione) {
    public String RitornaTestoDaSD(String Artista, String Album, String Brano, int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "Ritorna testo da SD. "+Artista+" "+Album+" "+Brano);
        String Testo = "";
        if (VariabiliStaticheGlobali.getInstance().getUtente()!=null) {
            String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
            String pathTesto = "";
            if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
                pathTesto = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/";
            } else {
                pathTesto = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/";
            }
            String NomeFile = Utility.getInstance().SistemaTesto(Brano + ".dat");
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Ritorna testo da SD. Path: " + pathTesto + "/" + NomeFile);

            File f = new File(pathTesto, NomeFile);
            if (f.exists()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Ritorna testo da SD. File esistente");
                Testo = GestioneFiles.getInstance().LeggeFileDiTesto(pathTesto + NomeFile);
                String t[] = Testo.split(";", -1);
                int idBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
                int n = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
                if (idBrano > n) {
                    if (n > 0) {
                        idBrano = 0;
                    } else {
                        idBrano = -1;
                    }
                }

                if (idBrano > -1) {
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBrano).setTesto(t[0]);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBrano).setTestoTradotto(t[1]);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBrano).setQuanteVolteAscoltato(Integer.parseInt(t[2]));
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBrano).setStelle(Integer.parseInt(t[3]));
                }

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Ritorna testo da SD. ImpostaStelleAscoltata in Home");
                GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Ritorna testo da SD. SettaTesto in Home");
                SettaTesto(false);

                if (idBrano > -1) {
                    // if (Refresh && VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                    if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                }.getClass().getEnclosingMethod().getName(),
                                "Ritorna testo da SD. Scarico dettaglio brano");

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
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                            "Ritorna testo da SD. Scarico dettaglio brano");

                    DBRemotoNuovo dbr = new DBRemotoNuovo();
                    GestioneWS = dbr.RitornaDettaglioBrano(VariabiliStaticheGlobali.getInstance().getContext(), Artista, Album, Brano, NumeroOperazione);
                } else {
                    // ProsegueElaborazioneSenzaScaricareIlDettaglio(NumeroOperazione);
                    VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
                }
            }

            if (Testo.toUpperCase().contains("NESSUN TESTO RILEVATO") || Testo.isEmpty()) {
                if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricaTestoBrano()) {
                    VariabiliStaticheHome.getInstance().getImgScaricaTesto().setVisibility(LinearLayout.VISIBLE);

                    int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
                    StrutturaBrani sb = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
                    GestioneTesti g = new GestioneTesti();
                    g.ScaricaTestoDaWeb(sb);
                }
            } else {
                VariabiliStaticheHome.getInstance().getImgScaricaTesto().setVisibility(LinearLayout.GONE);
            }

            // if (Testo.toUpperCase().contains("NESSUN") || Testo.trim().isEmpty()) {
            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
            //             "Ritorna testo da SD. Scarico dettaglio brano");
//
            //     DBRemotoNuovo dbr = new DBRemotoNuovo();
            //     GestioneWS = dbr.RitornaDettaglioBrano(VariabiliStaticheGlobali.getInstance().getContext(), Artista, Album, Brano, NumeroOperazione);
            // }
        } else {
            Testo = "";
        }

        return Testo;
    }

    public void BloccaOperazione() {
        if (GestioneWS!=null) {
            GestioneWS.StoppaEsecuzione();
            GestioneWS=null;
        }
    }

    public void ScaricaTestoDaWeb(StrutturaBrani sb) {
        if (!VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Ritorna dettaglio brano. Scarico testo");

            String sNomeBrano = sb.getNomeBrano();
            String ssArtista = "";
            if (sNomeBrano.contains("-")) {
                String[] A = sNomeBrano.split("-");
                sNomeBrano = A[1].trim();
                if (!Utility.getInstance().isNumeric(A[0])) {
                    ssArtista = A[0].trim();
                }
                sNomeBrano = sNomeBrano.toUpperCase().replace(".MP3", "");
                sNomeBrano = sNomeBrano.toUpperCase().replace(".WMA", "");
            }
            if (sNomeBrano.contains(".")) {
                sNomeBrano = sNomeBrano.substring(0, sNomeBrano.indexOf("."));
            }
            if (sNomeBrano.contains("(")) {
                sNomeBrano = sNomeBrano.substring(0, sNomeBrano.indexOf("("));
            }
            if (sNomeBrano.contains("[")) {
                sNomeBrano = sNomeBrano.substring(0, sNomeBrano.indexOf("["));
            }
            if (sNomeBrano.contains(",")) {
                sNomeBrano = sNomeBrano.substring(0, sNomeBrano.indexOf(","));
            }
            sNomeBrano = sNomeBrano.trim();
            int idArtista = sb.getIdArtista();
            String Artista = "";
            if (ssArtista.isEmpty()) {
                Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getArtista();
            } else {
                Artista = ssArtista;
            }

            DownloadTextFileNuovo d = new DownloadTextFileNuovo();
            d.setPath(VariabiliStaticheGlobali.getInstance().PercorsoDIR);
            d.setPathNomeFile("Testo.dat");
            d.setOperazione("Scarico testo brano");
            d.setContext(VariabiliStaticheGlobali.getInstance().getContext());

            String sArtista = Utility.getInstance().ConverteStringaInUrl(Artista);
            String NomeBrano = Utility.getInstance().ConverteStringaInUrl(sNomeBrano);

            String url = "http://lyrics.wikia.com/wiki/" + sArtista + ":" + NomeBrano;

            int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Scarico testo");
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Scarico testo brano: " + url);
            d.startDownload(
                    url,
                    false,
                    n);

        }
    }
}
