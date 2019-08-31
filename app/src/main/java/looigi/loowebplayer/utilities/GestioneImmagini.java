package looigi.loowebplayer.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.ContextCompat;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.Random;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaArtisti;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.notifiche.Notifica;
import looigi.loowebplayer.soap.DownloadImmagineNuovo;
// import looigi.loowebplayer.thread.NetThreadNuovo;

import static looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali.TempoImmagineVisibile;
import static looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali.TempoSfumatura;

public class GestioneImmagini {
    private static GestioneImmagini instance = null;

    private GestioneImmagini() {
    }

    public static GestioneImmagini getInstance() {
        if (instance == null) {
            instance = new GestioneImmagini();
        }

        return instance;
    }

    private int immNumber=-1;
    private Runnable rCambioImmagine;
    private Handler hCambioImmagine;
    private Bitmap ultimaBitmap;
    private Runnable runUltimaImmagine;
    private Handler hSelezionaUltimaImmagine;
    private PhotoView imgBrano;
    private boolean AttendeSfumatura;
    private String ImmagineDaCambiare="";
    private Handler hAttesaDownload;
    private Runnable rAttesaDownload;
    private Handler hCambioImmagine2;
    private Runnable rCambioImmagine2;

    public ImageView getImgBrano() {
        return imgBrano;
    }

    public boolean isAttendeSfumatura() {
        return AttendeSfumatura;
    }

    public void setImmNumber(int immNumber) {
        this.immNumber = immNumber;
    }

    public void setImmagineDaCambiare(String immagineDaCambiare) {
        ImmagineDaCambiare = immagineDaCambiare;
    }

    public void setImgBrano(PhotoView imgBrano) {
        this.imgBrano = imgBrano;
    }

    /* public void RitornaImmagineBrano(Context context, String Artista, String Album, String Brano) {
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
            // i.setImageBitmap(BitmapFactory.decodeFile(PathFile));
            VariabiliStaticheHome.getInstance().ImpostaImmagineDiSfondo(PathFile, "IMMAGINE", -1, null);
            // i.setScaleType(ImageView.ScaleType.MATRIX);

            SettaImmagineSuIntestazione(PathFile);

            Notifica.getInstance().setImmagine(PathFile);
            Notifica.getInstance().AggiornaNotifica();

            int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Load cover");
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagine brano. ProesgueCaricaBrano1 in Home");
            GestioneCaricamentoBrani.getInstance().ProsegueCaricaBrano1(n);
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(n, true);
        } else {
            ImpostaImmagineVuota();

            // http://looigi.no-ip.biz:12345/loowebplayer/Dati/Great%20White/2005-Twice%20Shy/Cover_Great%20White.jpg
            int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
            int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download cover");
            EsecuzioneChiamateWEB e = new EsecuzioneChiamateWEB();
            e.EsegueChiamataImmagineCOVER(NumeroBrano, PathFile, pathBase, VariabiliStaticheHome.getInstance().getImgBrano(), Artista, Album, 5000,
                    new Date(System.currentTimeMillis()), n);
        }
    } */

    public void SalvaMultimediaArtista(String Cosa) {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
        StrutturaArtisti Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista());

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Salva immagine brano. Artista: "+Artista.getArtista());

        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String pathMultimediaArtista = "";
        if (!pathBase.equals(Artista.getArtista())) {
            pathMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista.getArtista() + "/";
        } else {
            pathMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/";
        }
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Salva immagine brano. Path: "+pathMultimediaArtista);
        String NomeFile = "ListaImmagini.dat";

        File f = new File(pathMultimediaArtista, NomeFile);
        if (f.exists()) {
            f.delete();
        } else {
            f = new File(pathMultimediaArtista);
            f.mkdirs();
        }

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Salva immagine brano. Crea file di testo");
        GestioneFiles.getInstance().CreaFileDiTesto(pathMultimediaArtista, NomeFile, Cosa);
    }

    /* public void SalvaImmaginiSuSD(String Artista, List<StrutturaImmagini> i) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Salva immagine brano. Artista: "+Artista);
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String pathMultimediaArtista = "";
        if (!pathBase.equals(Artista)) {
            pathMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/";
        } else {
            pathMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/";
        }
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Salva immagine brano. Path: "+pathMultimediaArtista);
        String NomeFile = "ListaImmagini.dat";

        File f = new File(pathMultimediaArtista, NomeFile);
        if (f.exists()) {
            f.delete();
        } else {
            f = new File(pathMultimediaArtista);
            f.mkdirs();
        }

        String contenuto="";

        for (StrutturaImmagini s : i) {
            contenuto+=Integer.toString(s.getIdCartella()) + ";" + s.getNomeImmagine() + ";" + Long.toString(s.getLunghezza()) + "§";
        }

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Salva immagine brano. Crea file di testo");
        GestioneFiles.getInstance().CreaFileDiTesto(pathMultimediaArtista, NomeFile, contenuto);
    }

    public List<StrutturaImmagini> RitornaImmaginiArtista(String idArtista, String Artista, int NumeroOperazione, boolean RitornaSenzaProseguire) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagini artista in gestione immagini");
        List<StrutturaImmagini> immagini = new ArrayList<>();

        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String pathMultimediaArtista = "";
        if (!pathBase.equals(Artista)) {
            pathMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/";
        } else {
            pathMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/";
        }
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagini artista. Path: "+pathMultimediaArtista);
        String NomeFile = "ListaImmagini.dat";

        File f = new File(pathMultimediaArtista, NomeFile);
        if (f.exists()) {
            String i = GestioneFiles.getInstance().LeggeFileDiTesto(pathMultimediaArtista+NomeFile);
            String ii[] = i.split("§",-1);
            for (String iii : ii) {
                String iiii[] = iii.split(";",-1);

                if (iiii.length>2) {
                    StrutturaImmagini iiiii = new StrutturaImmagini();
                    if (!iiii[0].isEmpty()) {
                        iiiii.setIdCartella(Integer.parseInt(iiii[0]));
                    } else {
                        iiiii.setIdCartella(-1);
                    }
                    iiiii.setNomeImmagine(iiii[1]);
                    if (!iiii[2].isEmpty()) {
                        iiiii.setLunghezza(Integer.parseInt(iiii[2]));
                    } else {
                        iiiii.setLunghezza(-1);
                    }

                    immagini.add(iiiii);
                }
            }

            // VariabiliStaticheHome.getInstance().setImms(immagini);

            // if (!RitornaSenzaProseguire) {
            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            //     }.getClass().getEnclosingMethod().getName(), "Ritorna immagini artista. ProseguCaricaBrano2 in Home");
            //     GestioneCaricamentoBrani.getInstance().ProsegueCaricaBrano2(NumeroOperazione);
            // }
        // } else {
            /* if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getDownloadImmagini()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna multimedia artista");
                int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Download multimedia artista");
                DBRemoto dbr = new DBRemoto();
                dbr.RitornaMultimediaArtista(VariabiliStaticheGlobali.getInstance().getContext(),
                        Artista, n);
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna multimedia artista. Download immagini impedito, ProsegueCaricaBrano2 in Home");
                GestioneCaricamentoBrani.getInstance().ProsegueCaricaBrano2(NumeroOperazione);
            }
        }

        return immagini;
    } */

    public void CreaCarosello() {
        // if (Imms.size() > 0 && VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getDownloadImmagini()) {
        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getDownloadImmagini()) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Inizia carosello");
            hCambioImmagine = new Handler(Looper.getMainLooper());
            hCambioImmagine.postDelayed(rCambioImmagine = new Runnable() {
                @Override
                public void run() {
                if (VariabiliStaticheHome.getInstance().getImms().size()>1) {
                    Random r = new Random();
                    int s = VariabiliStaticheHome.getInstance().getImms().size() - 1;
                    immNumber = r.nextInt((s) + 1) + 0;

                    ControllaEsistenzaProssimaImmagine(VariabiliStaticheHome.getInstance().getImms().get(immNumber).getNomeImmagine());
                }

                StoppaTimerCarosello();
                }
            }, TempoImmagineVisibile);
        } else {
            StoppaTimerCarosello();
        }
    }

    public void StoppaTimerCarosello() {
        try {
            if (hCambioImmagine != null) {
                hCambioImmagine.removeCallbacksAndMessages(null);
                hCambioImmagine.removeCallbacks(rCambioImmagine);
                hCambioImmagine = null;
                rCambioImmagine = null;
            }
        } catch (Exception ignored) {

        }
    }

    public void SfumaImmagine(final boolean MetteSuccessiva) {
        if (immNumber==-1) {
            CreaCarosello();
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "Carosello: inizio animazione");
            final Animation a = new AlphaAnimation(1.00f, 0.00f);

            AttendeSfumatura = true;
            a.setDuration(TempoSfumatura);
            a.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    // VariabiliStaticheGlobali.getInstance().setMessaImmagineVuota(false);
                }

                public void onAnimationRepeat(Animation animation) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Carosello: animazione fade out bloccata per cambio brano");
                    a.cancel();

                    imgBrano.setVisibility(LinearLayout.VISIBLE);
                }

                public void onAnimationEnd(Animation animation) {
                    imgBrano.setVisibility(LinearLayout.GONE);

                    AttendeSfumatura = false;

                    if (MetteSuccessiva) {
                        RimetteImmagine();
                    }
                }
                 // }
            });

            imgBrano.startAnimation(a);
        }
    }

    public void RimetteImmagine() {
        final Animation a = new AlphaAnimation(0.00f, 1.00f);

        a.setDuration(TempoSfumatura);
        a.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                SettaImmagineSuIntestazione(ImmagineDaCambiare);

                Notifica.getInstance().setImmagine(ImmagineDaCambiare);
                Notifica.getInstance().AggiornaNotifica();

                ImpostaDimensioniImageViewRandom(imgBrano);

                if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSalvataggioOggetti()) {
                    GestioneImmagini.getInstance().ImpostaImmagineDiSfondo(ImmagineDaCambiare,"IMMAGINE", -1, null);

                    Notifica.getInstance().setImmagine(ImmagineDaCambiare);
                    Notifica.getInstance().AggiornaNotifica();
                }

                hSelezionaUltimaImmagine = new Handler(Looper.getMainLooper());
                hSelezionaUltimaImmagine.postDelayed(runUltimaImmagine = new Runnable() {
                    @Override
                    public void run() {
                        imgBrano.setVisibility(LinearLayout.VISIBLE);

                        hSelezionaUltimaImmagine.removeCallbacksAndMessages(null);
                        hSelezionaUltimaImmagine.removeCallbacks(runUltimaImmagine);
                    }
                }, 100);


                VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata(ImmagineDaCambiare);
            }

            public void onAnimationRepeat(Animation animation) {
                // if (VariabiliStaticheGlobali.getInstance().getBloccaCarosello()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "Carosello: animazione fade in bloccata per cambio brano");
                a.cancel();
                ImpostaImmagineVuota();
                imgBrano.setVisibility(LinearLayout.VISIBLE);
                // }
            }

            public void onAnimationEnd(Animation animation) {
                CreaCarosello();
            }
        });

        imgBrano.startAnimation(a);
    }

    private void ControllaEsistenzaProssimaImmagine(String Immagine) {
        int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();

        if (NumeroBrano<VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBrani().size() && NumeroBrano>-1) {
            int idArtista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBrani().get(NumeroBrano).getIdArtista();
            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getArtista();
            String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();

            StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
            String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();

            String NomeImm = Immagine.replace("\\ZZZ-ImmaginiArtista\\", "");
            // http://looigi.no-ip.biz:12345/looWebPlayer/Dati/Mp3/Nightwish/ZZZ-ImmaginiArtista/nightwish_band_members_look_suits_2392_3840x1200.jpg.dat
            String sUrl = VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Dati/" + pathBase + "/" + Artista + "/ZZZ-ImmaginiArtista/" +
                    NomeImm;
            String PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/ImmaginiArtista/" +
                    NomeImm.replace(".dat", "");

            File f = new File(PathFile.replace(".dat", ""));
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "Carosello: cambio immagine: " + Immagine);
            if (f.exists()) {
                ImmagineDaCambiare = PathFile;

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "Carosello: inizio sfuma immagine. Immagine: " + VariabiliStaticheHome.getInstance().getImms().get(immNumber).getNomeImmagine());
                SfumaImmagine(true);
            } else {
                // if (NetThreadNuovo.getInstance().isScreenOn()) {
                if (VariabiliStaticheGlobali.getInstance().getScreenON()) {
                    boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

                    if (ceRete) {
                        int NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download immagine artista");

                        SfumaImmagine(false);

                        // Ci troviamo in caso di immagine non ancora scaricata. La scarico
                        DownloadImmagineNuovo d = new DownloadImmagineNuovo();
                        d.setContext(VariabiliStaticheGlobali.getInstance().getContext());
                        d.setPath(PathFile);
                        d.setNumeroBrano(NumeroBrano);
                        d.setNumeroOperazione(NumeroOperazione);
                        d.setInSfuma(true);
                        d.startDownload(sUrl, "Download Immagine", VariabiliStaticheGlobali.getInstance().getTimeOutImmagini());
                    } else {
                        CreaCarosello();
                    }
                } else {
                    CreaCarosello();
                }
            }
        }
    }

    /* public void ToglieAlpha() {
        imgBrano.setVisibility(LinearLayout.VISIBLE);
        GestioneImmagini.getInstance().ImpostaDimensioniImageViewRandom(imgBrano);

        final Animation a = new AlphaAnimation(0.00f, 1.00f);

        a.setDuration(TempoSfumatura);
        a.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                // if (VariabiliStaticheGlobali.getInstance().getMessaImmagineVuota()) {
                imgBrano.setVisibility(LinearLayout.VISIBLE);

                if (VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().equals("***")) {
                    // Drawable icona_nessuna = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.nessuna);
                    // VariabiliStaticheHome.getInstance().getImgBrano().setImageDrawable(icona_nessuna);
                    ImpostaImmagineDiSfondo("", "ICONA", R.drawable.nessuna, null);
                } else{
                    // VariabiliStaticheHome.getInstance().getImgBrano().setImageBitmap(BitmapFactory.decodeFile(VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata()));
                    // VariabiliStaticheHome.getInstance().ImpostaImmagineDiSfondo(VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata(), "IMMAGINE", -1, null);
                    ImpostaUltimaImmagine(false);
                }
                //     VariabiliStaticheGlobali.getInstance().setMessaImmagineVuota(false);
                // }
            }

            public void onAnimationRepeat(Animation animation) {
                // if (VariabiliStaticheGlobali.getInstance().getBloccaCarosello()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Blocco animazione per cambio canzone.");
                    a.cancel();
                    GestioneImmagini.getInstance().ImpostaImmagineVuota();
                    imgBrano.setVisibility(LinearLayout.VISIBLE);
                // }
            }

            public void onAnimationEnd(Animation animation) {
                // if (!VariabiliStaticheGlobali.getInstance().getBloccaCarosello()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ricomincio carosello");
                    // VariabiliStaticheHome.getInstance().gethCambioImmagine()
                    //         .postDelayed(VariabiliStaticheHome.getInstance().getrCambioImmagine(),
                    //                 TempoImmagineVisibile);
                    CreaCarosello();
                // } else {
                //     GestioneImmagini.getInstance().ImpostaImmagineVuota();
                //     VariabiliStaticheHome.getInstance().getImgBrano().setVisibility(LinearLayout.VISIBLE);
                // }
            }
        });

        imgBrano.startAnimation(a);
    } */

    public void ImpostaImmagineVuota() {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "Pulisco Immagine");

        // String t[] = VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().split(";", -1);
//
        // if (t[0].equals("ICONA")) {
        //     VariabiliStaticheHome.getInstance().getImgBrano().setImageResource(Integer.parseInt(t[1]));
        // } else {
        //     if (t[0].equals("IMMAGINE")) {
        //         VariabiliStaticheHome.getInstance().getImgBrano().setImageBitmap(BitmapFactory.decodeFile(t[1]));
        //     } else {
        //         VariabiliStaticheHome.getInstance().getImgBrano().setImageBitmap(VariabiliStaticheHome.getInstance().getUltimaBitmap());
        //     }
        // }

        // VariabiliStaticheHome.getInstance().getImgBrano().setImageResource(0);
        // i.setImageResource(0);
        Drawable icona_nessuna = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.nessuna);
        imgBrano.setImageDrawable(icona_nessuna);

        // if (VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().equals("***")) {
        //     Drawable icona_nessuna = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.nessuna);
        //     i.setImageDrawable(icona_nessuna);
        //     // i.setImageResource(0);
        // } else{
        //     i.setImageBitmap(BitmapFactory.decodeFile(VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata()));
        // }

        // VariabiliStaticheGlobali.getInstance().setMessaImmagineVuota(true);
    }

    public void ImpostaDimensioniImageViewRandom(ImageView i) {
        /* Random st = new Random();
        int stNumber = st.nextInt(((5 - 1) - 0) + 1) + 0;

        switch (stNumber) {
            case 0:
                i.setScaleType(ImageView.ScaleType.CENTER);
                break;
            case 1:
                i.setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            case 2:
                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case 3:
                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case 4:
                i.setScaleType(ImageView.ScaleType.MATRIX);
                break;
            case 5:
                i.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
        } */
        // i.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();

        return resizedBitmap;
    }

    public void SettaImmagineSuIntestazione(final String PathFile) {
        if (PathFile != null) {
            hAttesaDownload = new Handler(Looper.getMainLooper());
            rAttesaDownload =(new Runnable() {
                @Override
                public void run() {
                    Bitmap bIniziale = null;
                    if (PathFile.equals("***")) {
                        bIniziale = BitmapFactory.decodeResource(VariabiliStaticheGlobali.getInstance().getContextPrincipale().getResources(),
                                R.drawable.ic_launcher);
                    } else {
                        bIniziale = BitmapFactory.decodeFile(PathFile);
                    }
                    if (bIniziale != null) {
                        try {
                            Bitmap b = getResizedBitmap(bIniziale, bIniziale.getWidth(), 150);
                            Bitmap bmpMonochrome = Bitmap.createBitmap(b.getWidth(), 150, Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bmpMonochrome);
                            ColorMatrix ma = new ColorMatrix();
                            ma.setSaturation(0);
                            Paint paint = new Paint();
                            paint.setColorFilter(new ColorMatrixColorFilter(ma));
                            canvas.drawBitmap(b, 0, 0, paint);
                            BlurBuilder bb = new BlurBuilder();
                            Bitmap bmpFinale = bb.blur(VariabiliStaticheGlobali.getInstance().getContextPrincipale(), bmpMonochrome);
                            bmpFinale = addWhiteBorder(bmpFinale, 2);

                            Drawable d = new BitmapDrawable(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().getResources(), bmpFinale);
                            VariabiliStaticheHome.getInstance().getLayIntestazione().setBackground(d);
                        } catch (Exception ignored) {
                            String error = Utility.getInstance().PrendeErroreDaException(ignored);
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                    "Errore su SettaImmagineSuIntestazione: " + error);
                        }
                    }
                    if (hAttesaDownload != null && rAttesaDownload != null ) {
                        hAttesaDownload.removeCallbacksAndMessages(null);
                        hAttesaDownload.removeCallbacks(rAttesaDownload);
                        hAttesaDownload = null;
                    }
                }
            });
            hAttesaDownload.postDelayed(rAttesaDownload, 50);
        }
     }

    private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.argb(200,255,230,230));
        canvas.drawBitmap(bmp, borderSize, borderSize, null);

        return bmpWithBorder;
    }

    private class BlurBuilder {
        private float BITMAP_SCALE = 0.4f;
        private float BLUR_RADIUS = 3.5f;

        @SuppressLint("NewApi")
        public Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height,
                    false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs,
                    Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }

    public void ImpostaImmagineDiSfondo(final String Immagine, final String Tipo, final int Icona, final Bitmap bitmap) {
        if (Immagine != null) {
            hCambioImmagine2 = new Handler(Looper.getMainLooper());
            rCambioImmagine2 =(new Runnable() {
                @Override
                public void run() {
                    if (Tipo.equals("ICONA")) {
                        ultimaBitmap = null;
                        VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata("ICONA;" + Integer.toString(Icona));
                        imgBrano.setImageResource(Icona);
                    } else {
                        if (Tipo.equals("IMMAGINE")) {
                            ultimaBitmap = null;
                            if (Immagine.isEmpty()) {
                                Drawable icona_nessuna = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.nessuna);
                                imgBrano.setImageDrawable(icona_nessuna);
                            } else {
                                VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata("IMMAGINE;" + Immagine);
                                imgBrano.setImageBitmap(BitmapFactory.decodeFile(Immagine.replace("IMMAGINE;", "")));
                            }
                        } else {
                            ultimaBitmap = bitmap;
                            VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata("BITMAP;");
                            imgBrano.setImageBitmap(bitmap);
                        }
                    }

                    if (hCambioImmagine2!=null && rCambioImmagine2!=null) {
                        hCambioImmagine2.removeCallbacksAndMessages(null);
                        hCambioImmagine2.removeCallbacks(rCambioImmagine2);
                        hCambioImmagine2 = null;
                    }
                    // hAttesaDownload.postDelayed(rAttesaDownload, 100);
                }
            });
            hCambioImmagine2.postDelayed(rCambioImmagine2, 100);
        }
    }

    public void ImpostaUltimaImmagine(final boolean DaDownload) {
        hSelezionaUltimaImmagine = new Handler(Looper.getMainLooper());
        hSelezionaUltimaImmagine.postDelayed(runUltimaImmagine = new Runnable() {
            @Override
            public void run() {
                imgBrano.setVisibility(LinearLayout.VISIBLE);
                String t = VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata();
                if (!t.isEmpty()) {
                    String tt[] = t.split(";",-1);
                    if (tt[0].equals("ICONA")) {
                        imgBrano.setImageResource(Integer.parseInt(tt[1]));
                    } else {
                        if (tt[0].equals("IMMAGINE")) {
                            imgBrano.setImageBitmap(BitmapFactory.decodeFile(tt[1]));
                        } else {
                            imgBrano.setImageBitmap(ultimaBitmap);
                        }
                    }
                } else {
                    Drawable icona_nessuna = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.nessuna);
                    imgBrano.setImageDrawable(icona_nessuna);
                }

                hSelezionaUltimaImmagine.removeCallbacksAndMessages(null);
                hSelezionaUltimaImmagine.removeCallbacks(runUltimaImmagine);

                // if (DaDownload) {
                //     GestioneImmagini.getInstance().ToglieAlpha();
                // }
            }
        }, 50);
    }

}

