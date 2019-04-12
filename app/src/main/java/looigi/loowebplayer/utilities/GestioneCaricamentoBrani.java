/* package looigi.loowebplayer.utilities;

import android.os.Handler;
import android.widget.LinearLayout;

import java.io.File;

import looigi.loowebplayer.MainActivity;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dialog.DialogMessaggio;
import looigi.loowebplayer.thread.NetThread;

public class GestioneCaricamentoBrani {
    private static final GestioneCaricamentoBrani ourInstance = new GestioneCaricamentoBrani();

    public static GestioneCaricamentoBrani getInstance() {
        return ourInstance;
    }

    private GestioneCaricamentoBrani() {
    }

    private Boolean RefreshBrano;
    private Runnable runScaricamentiBrano;
    private Handler hProsegueScaricamentiBrano;

    public void CaricaBrano(Boolean Refresh) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Inizio Carica Brano. Refresh: "+Refresh);

        // GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(false);

        RefreshBrano=Refresh;
        final VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

        GestioneLayout.getInstance().AzzeraFade();
        GestioneImmagini.getInstance().SettaImmagineSuIntestazione("***");
        GestioneLayout.getInstance().VisualizzaLayout();

        if (!VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
            vh.getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay());
            vh.getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop_dis());
        } else {
            vh.getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay_dis());
            vh.getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop());
        }

        try {
            if (vh.getHandlerSeekBar() != null) {
                vh.getHandlerSeekBar().removeCallbacksAndMessages(null);
                vh.getHandlerSeekBar().removeCallbacks(vh.getrSeekBar());
                vh.setHandlerSeekBar(null);
                vh.setrSeekBar(null);
            }
        } catch (Exception ignored) {

        }
        // if (vh.getMediaPlayer()!=null) {
        //     vh.getMediaPlayer().stop();
        //     VariabiliStaticheGlobali.getInstance().setStaSuonando(false);
        //     vh.setMediaPlayer(null);
        // }

        MainActivity.ScriveBraniInLista();
        VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata("***");

        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        if (NumeroBrano==-1) {
            // VariabiliStaticheHome.getInstance().getImgLoadBrano().setVisibility(LinearLayout.GONE);

            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                    "Nessun brano in archivio", true, VariabiliStaticheGlobali.NomeApplicazione);
        } else {
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setNumeroBranoInAscolto(NumeroBrano);

            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
            String NomeBrano = s.getNomeBrano();

            String sNomeBrano=NomeBrano;
            String Traccia="";
            if (sNomeBrano.contains("-")) {
                String A[] = sNomeBrano.split("-");
                if (!A[0].isEmpty() && !A[0].equals("00")) {
                    Traccia = "\nTraccia " + A[0].trim();
                }
                sNomeBrano = A[1].trim();
            }
            if (sNomeBrano.contains(".")) {
                sNomeBrano=sNomeBrano.substring(0,sNomeBrano.indexOf("."));
            }
            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
            String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
            String sAlbum=Album;
            if (sAlbum.contains("-")) {
                String A[] = sAlbum.split("-");
                if (A.length>1) {
                    if (!A[0].isEmpty() && !A[0].equals("0000")) {
                        sAlbum = A[1] + " (Anno " + A[0] + ")";
                    } else {
                        sAlbum = A[1];
                    }
                } else {
                    sAlbum="";
                }
            }
            sAlbum=Traccia+"\nAlbum: " + sAlbum;

            String nb=NomeBrano;
            if (nb.contains("-")) {
                nb=nb.substring(nb.indexOf("-")+1,nb.length());
            }
            if (nb.contains(".")) {
                nb=nb.substring(0,nb.indexOf("."));
            }
            // PronunciaFrasi pf = new PronunciaFrasi();
            // pf.PronunciaFrase(nb + ", " + Artista, "INGLESE");
            // pf.PronunciaFrase(nb, "INGLESE");

            vh.getTxtBrano().setText(sNomeBrano);
            vh.getTxtArtista().setText(Artista);
            vh.getTxtAlbum().setText(sAlbum);
            vh.getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
            vh.getTxtMembri().setText("");

            // Gestione Membri
            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getMembri()) {
                if (NumeroBrano > -1) {
                    vh.setGm(new GestioneMembri());
                    int idArtista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).getIdArtista();
                    int quantiMembri = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getMembri().size();
                    Boolean Ok = true;
                    if (quantiMembri == 1) {
                        String membro = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getMembri().get(0).getMembro().trim();
                        if (membro.isEmpty() || membro.toUpperCase().contains("NESSUN MEMBRO")) {
                            Ok = false;
                            vh.getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
                            vh.getTxtMembri().setVisibility(LinearLayout.GONE);
                        }
                    }
                    if (Ok) {
                        vh.getTxtMembriTitolo().setVisibility(LinearLayout.VISIBLE);
                        vh.getTxtMembri().setVisibility(LinearLayout.VISIBLE);

                        vh.getGm().setMembri(VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getMembri());
                        vh.getGm().setTxtCasellaTesto(vh.getTxtMembri());
                        vh.getGm().CominciaAGirare();
                    }
                }
            } else {
                vh.getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
                vh.getTxtMembri().setVisibility(LinearLayout.GONE);
            }
            // Gestione Membri

            String CompattazioneMP3 = VariabiliStaticheGlobali.EstensioneCompressione;
            if (VariabiliStaticheGlobali.getInstance().getUtente()!=null && !VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                CompattazioneMP3 = "";
            }

            // GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

            String pathBase=VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
            String PathMP3 = VariabiliStaticheGlobali.getInstance().PercorsoDIR +"/Dati/"+ pathBase+"/"+Artista + "/" + Album + "/" + NomeBrano;
            String PathMP3_Compresso = VariabiliStaticheGlobali.getInstance().PercorsoDIR +"/Dati/"+ pathBase+"/"+Artista + "/" + Album + "/" + CompattazioneMP3 + NomeBrano;
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Controllo esistenza file: "+PathMP3);
            File f = new File(PathMP3);
            File fc = new File(PathMP3_Compresso);
            if (f.exists() || fc.exists()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Brano già scaricato");
                VariabiliStaticheHome.getInstance().setBranoDaCaricare("");
                if (f.exists()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Imposta brano normale");
                    VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(true);

                    if (VariabiliStaticheGlobali.getInstance().getStaAttendendoConMusichetta()) {
                        VariabiliStaticheGlobali.getInstance().setStaAttendendoConMusichetta(false);
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                        VariabiliStaticheGlobali.getInstance().setStaSuonando(true);
                        NetThread.getInstance().setCaroselloBloccato(false);
                    }

                    GestioneImpostazioneBrani.getInstance().ImpostaBrano(PathMP3);
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Imposta brano compresso");
                    VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(true);

                    if (VariabiliStaticheGlobali.getInstance().getStaAttendendoConMusichetta()) {
                        VariabiliStaticheGlobali.getInstance().setStaAttendendoConMusichetta(false);
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                        VariabiliStaticheGlobali.getInstance().setStaSuonando(true);
                        NetThread.getInstance().setCaroselloBloccato(false);
                    }

                    GestioneImpostazioneBrani.getInstance().ImpostaBrano(PathMP3_Compresso);
                }
                if (f.exists() && fc.exists()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Elimino brano non compresso. Compresso già esistente");
                    f.delete();
                }
            } else {
                VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(false);
                VariabiliStaticheHome.getInstance().setBranoDaCaricare(Artista + ";" + Album + ";" + NomeBrano);
            }

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagine brano");
            GestioneImmagini.getInstance().RitornaImmagineBrano(vh.getContext(), Artista, Album, NomeBrano);
        }
    }

    public void ProsegueCaricaBrano1(final int NumeroOperazione) {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        if (NumeroBrano>-1) {
            if (VariabiliStaticheGlobali.getInstance().getStaSuonando() || VariabiliStaticheGlobali.getInstance().getStaAttendendoConMusichetta()) {
                final int N = NumeroBrano;

                if (hProsegueScaricamentiBrano!=null) {
                    hProsegueScaricamentiBrano.removeCallbacks(runScaricamentiBrano);
                }

                // GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);

                hProsegueScaricamentiBrano = new Handler();
                hProsegueScaricamentiBrano.postDelayed(runScaricamentiBrano = new Runnable() {
                    @Override
                    public void run() {
                        GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(false);

                        if (N==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                            hProsegueScaricamentiBrano.removeCallbacks(runScaricamentiBrano);
                            hProsegueScaricamentiBrano=null;

                            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricoDettagli()) {
                                VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(true);
                            }

                            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(N);
                            String idArtista = Integer.toString(s.getIdArtista());
                            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();

                            String nb=s.getNomeBrano();
                            if (nb.contains("-")) {
                                nb=nb.substring(nb.indexOf("-")+1,nb.length());
                            }
                            if (nb.contains(".")) {
                                nb=nb.substring(0,nb.indexOf("."));
                            }

                            PronunciaFrasi pf = new PronunciaFrasi();
                            pf.PronunciaFrase(nb + ", " + Artista, "INGLESE");

                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagini artista in caricamento brani");
                            VariabiliStaticheHome.getInstance().setImms(GestioneImmagini.getInstance().RitornaImmaginiArtista(idArtista, Artista, NumeroOperazione, false));
                        }
                    }
                }, 1000);
                // } else {
                // VariabiliStaticheGlobali.getInstance().setHaCaricatoTuttiIDettagliDelBrano(false);
            } else {
                // VariabiliStaticheHome.getInstance().getImgLoadBrano().setVisibility(LinearLayout.GONE);

                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
            }
        }
    }

    public void ProsegueCaricaBrano2(int NumeroOperazione) {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        if (NumeroBrano>-1) {
            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
            String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
            String NomeBrano = s.getNomeBrano();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna testo brano");
            String Testo = GestioneTesti.getInstance().RitornaTestoDaSD(Artista, Album, NomeBrano, NumeroOperazione);
            if (!Testo.isEmpty()) {
                GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();
            } else {
                VariabiliStaticheHome.getInstance().getTxtAscoltata().setText("");
            }
        } else {
            VariabiliStaticheHome.getInstance().getTxtAscoltata().setText("");
        }
    }
}
*/