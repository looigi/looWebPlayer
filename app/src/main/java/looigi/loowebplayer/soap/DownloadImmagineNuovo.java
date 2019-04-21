package looigi.loowebplayer.soap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaImmagini;
import looigi.loowebplayer.notifiche.Notifica;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class DownloadImmagineNuovo {
    private String Path;
    private int NumeroBrano;
    private String messErrore="";
    private Bitmap bitmap;
    private Boolean inSfuma=false;
    private Boolean CaricaImmagineBrano=false;
    private DownloadImageFile downloadFile;
    private int NumeroOperazione;
    private String Url;
    private Integer TIMEOUT;
    private Runnable runAttendeSfumatura;
    private Handler hAttendeSfumatura;

    private int QuantiTentativi;
    private int Tentativo;
    private Handler hAttesaNuovoTentativo;
    private Runnable rAttesaNuovoTentativo;
    private int SecondiAttesa;

    public void setContext(Context context) {
        VariabiliStaticheGlobali.getInstance().setCtxPassaggio(context);
    }

    public void setNumeroOperazione(int n) {
        NumeroOperazione = n;
    }

    public void setPath(String path) {
        Path = path;
    }

    public void setNumeroBrano(int numeroBrano) {
        NumeroBrano = numeroBrano;
    }

    public void setInSfuma(Boolean i) {
        inSfuma = i;
    }

    public void setCaricaImmagineBrano(Boolean i) {
        CaricaImmagineBrano = i;
    }

    public void startDownload(String sUrl, String sOperazione, int TO) {
        this.Url=sUrl;
        this.TIMEOUT = TO;

        this.QuantiTentativi = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQuantiTentativi();
        this.Tentativo = 0;

        String Chiave = this.Url+";"+sOperazione;
        if (!VariabiliStaticheGlobali.getInstance().getChiaveDLImmagine().equals(Chiave)) {
            VariabiliStaticheGlobali.getInstance().setChiaveDLImmagine(Chiave);

            ApriDialog();

            String sUrl2 = Url.substring(9, Url.length());
            Url = Url.substring(0, 9);
            sUrl2 = sUrl2.replace("//", "/");
            Url += sUrl2;

            downloadFile = new DownloadImageFile();
            downloadFile.execute(Url);
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Skippata operazione DL Immagine uguale: "+Chiave);
        }
    }

    private void ChiudeDialog() {
        if (!messErrore.isEmpty()) {
            // if (inSfuma) {
                // GestioneImmagini.getInstance().ToglieAlpha();
            // }
        }

        VariabiliStaticheGlobali.getInstance().setOperazioneInCorso("");
        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(this.NumeroOperazione, false);
    }

    private void ApriDialog() {
    }

    public void StoppaEsecuzione() {
        ChiudeDialog();

        if (downloadFile != null) {
            downloadFile.cancel(true);

            downloadFile.ControllaFineCiclo();
        }
    }

    public class DownloadImageFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... sUrl) {
            messErrore="";

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Inizio lo scarico dell'immagine: "+sUrl[0]+". TIMEOUT: "+Integer.toString(TIMEOUT));
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(sUrl[0]).getContent());
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Scarico dell'immagine. Effettuato");
            } catch (IOException e) {
                String PathFile = VariabiliStaticheGlobali.getInstance().getImmagineMostrata();
                GestioneImmagini.getInstance().ImpostaImmagineDiSfondo(PathFile, "IMMAGINE", -1, null);
                GestioneImmagini.getInstance().SettaImmagineSuIntestazione(PathFile);

                messErrore = "ERROR: " + Utility.getInstance().PrendeErroreDaException(e);
                if (messErrore.contains("java.io.FileNotFoundException")) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Scarico dell'immagine. Errore di immagine non trovata");
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Scarico dell'immagine. Errore: " + messErrore);
                }
            }

            if (isCancelled()) {
                messErrore="ESCI";
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            messErrore="ESCI";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ControllaFineCiclo();
        }

        public void ControllaFineCiclo() {
            if (VariabiliStaticheNuove.getInstance().getSc()!=null) {
                VariabiliStaticheNuove.getInstance().setSc(null);
            }

            if (!VariabiliStaticheGlobali.getInstance().getMessErrorePerDebug().isEmpty()) {
                messErrore = VariabiliStaticheGlobali.getInstance().getMessErrorePerDebug();
            }

            ChiudeDialog();

            if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                if (VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().isEmpty() ||
                        VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().equals("***")) {
                    GestioneImmagini.getInstance().ImpostaImmagineVuota();
                } else {
                    GestioneImmagini.getInstance().ImpostaUltimaImmagine(true);
                    GestioneImmagini.getInstance().CreaCarosello();
                }

                NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                        "DL Immagine: Cambio brano");
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "DL Immagine: Cambio brano");
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Scarico dell'immagine. Post execute. Errore: " + messErrore);
                if (messErrore.equals("ESCI")) {
                    // sVariabiliStaticheNuove.getInstance().setSc(null);
                } else {
                    if (messErrore.contains("ERROR:") && !messErrore.contains("java.io.FileNotFoundException")) {
                        // Errore... Riprovo ad eseguire la funzione
                        if (Tentativo < QuantiTentativi && VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getReloadAutomatico()) {
                            Tentativo++;

                            final int TempoAttesa = (VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3() * (Tentativo-1)) / 1000;

                            NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                    "Errore Dl Immagine. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(), "DL Immagine: Errore. Attendo " + Integer.toString(TempoAttesa) + " secondi e riprovo: " +
                                    Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));

                            SecondiAttesa = 0;
                            hAttesaNuovoTentativo = new Handler();
                            rAttesaNuovoTentativo = (new Runnable() {
                                @Override
                                public void run() {
                                    SecondiAttesa++;
                                    NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                            "Errore Dl Immagine. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi) +
                                            " Secondi " + Integer.toString(SecondiAttesa) + "/" + Integer.toString(TempoAttesa));
                                    if (SecondiAttesa>=TempoAttesa) {
                                        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

                                        ApriDialog();

                                        downloadFile = new DownloadImageFile();
                                        downloadFile.execute(Url);

                                        hAttesaNuovoTentativo.removeCallbacks(rAttesaNuovoTentativo);
                                        hAttesaNuovoTentativo = null;
                                    } else {
                                        hAttesaNuovoTentativo.postDelayed(rAttesaNuovoTentativo, 1000);
                                    }
                                }
                            });
                            hAttesaNuovoTentativo.postDelayed(rAttesaNuovoTentativo, 1000);
                            // Errore... Riprovo ad eseguire la funzione
                        } else {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Tentativi di DL esauriti");
                            if (VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().isEmpty() ||
                                    VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().equals("***")) {
                                GestioneImmagini.getInstance().ImpostaImmagineVuota();
                            } else {
                                GestioneImmagini.getInstance().ImpostaUltimaImmagine(true);
                                GestioneImmagini.getInstance().CreaCarosello();
                            }
                        }
                    } else {
                        if (messErrore.isEmpty() || messErrore.contains("java.io.FileNotFoundException")) {
                            if (bitmap != null || messErrore.contains("java.io.FileNotFoundException")) {
                                if (!inSfuma) {
                                    if (!messErrore.contains("java.io.FileNotFoundException")) {
                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                        }.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Non inSfuma. Riduco immagine");
                                        DisplayMetrics displaymetrics = new DisplayMetrics();
                                        VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().
                                                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                                        int height = displaymetrics.heightPixels;
                                        int width = displaymetrics.widthPixels;

                                        int bmWidth = bitmap.getWidth();
                                        int bmHeight = bitmap.getHeight();

                                        if (bmWidth > width || bmHeight > height) {
                                            float p1 = (float) bmWidth / ((float) width);
                                            float p2 = (float) bmHeight / ((float) height);
                                            float p;
                                            if (p1 > p2) {
                                                p = p1;
                                                p2 = height / p;
                                                bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) p2, true);
                                            } else {
                                                p = p2;
                                                p1 = width / p;
                                                bitmap = Bitmap.createScaledBitmap(bitmap, (int) p1, (int) height, true);
                                            }
                                        } else {
                                            float p1 = (float) width / ((float) bmWidth);
                                            float p2 = (float) height / ((float) bmHeight);
                                            float p;
                                            if (p1 < p2) {
                                                p = p1;
                                                p1 = bmWidth * p;
                                                p2 = bmHeight * p;
                                                bitmap = Bitmap.createScaledBitmap(bitmap, (int) p1, (int) p2, true);
                                            } else {
                                                p = p2;
                                                p1 = bmWidth * p;
                                                p2 = bmHeight * p;
                                                bitmap = Bitmap.createScaledBitmap(bitmap, (int) p1, (int) p2, true);
                                            }
                                        }

                                        // VariabiliStaticheGlobali.getInstance().getIvPassaggio().setImageBitmap(bitmap);
                                        GestioneImmagini.getInstance().ImpostaImmagineDiSfondo("", "BITMAP", -1, bitmap);
                                    }
                                }

                                if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSalvataggioOggetti()) {
                                    if (!messErrore.contains("java.io.FileNotFoundException")) {
                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                        }.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Salvo immagine su disco: " + Path);
                                        Path = Path.replace("#", "_");

                                        String Cartelle[] = Path.split("/", -1);
                                        String Path2 = "";
                                        for (int i = 0; i < Cartelle.length - 1; i++) {
                                            Path2 += Cartelle[i] + "/";
                                        }
                                        GestioneFiles.getInstance().CreaCartelle(Path2);
                                        Utility.getInstance().saveImageFile(bitmap, Path);

                                        // Traffico
                                        File file = new File(Path);
                                        long len = file.length();
                                        long bs = VariabiliStaticheGlobali.getInstance().getBytesScaricati();
                                        bs += len;
                                        VariabiliStaticheGlobali.getInstance().setBytesScaricati(bs);
                                        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getVisualizzaTraffico()) {
                                            Traffico.getInstance().ScriveTrafficoAVideo(VariabiliStaticheGlobali.getInstance().getBytesScaricati());
                                        }
                                        // Traffico

                                        // if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                        GestioneImmagini.getInstance().SettaImmagineSuIntestazione(Path);
                                        VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata(Path);

                                        StrutturaImmagini ss = new StrutturaImmagini();
                                        ss.setNomeImmagine(Path);
                                        ss.setLunghezza(0);
                                        if (VariabiliStaticheHome.getInstance().getImms() == null) {
                                            VariabiliStaticheHome.getInstance().setImms(new ArrayList<StrutturaImmagini>());
                                        }
                                        VariabiliStaticheHome.getInstance().getImms().add(ss);

                                        GestioneImmagini.getInstance().setImmagineDaCambiare(Path);
                                        // }
                                    } else {
                                        // VariabiliStaticheHome.getInstance().getImgBrano().setImageResource(R.drawable.nessuna);
                                        //VariabiliStaticheHome.getInstance().ImpostaImmagineDiSfondo("", "ICONA", R.drawable.nessuna, null);
                                        if (VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().isEmpty() ||
                                                VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().equals("***")) {
                                            GestioneImmagini.getInstance().ImpostaImmagineVuota();
                                        } else {
                                            GestioneImmagini.getInstance().ImpostaUltimaImmagine(true);
                                            // ToglieAlpha();

                                            GestioneImmagini.getInstance().SettaImmagineSuIntestazione("***");
                                        }
                                    }
                                }

                                if (inSfuma) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                    }.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. inSfuma. Proseguo carosello");
                        /* if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSalvataggioOggetti()) {
                            // if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                //  VariabiliStaticheHome.getInstance().getImgBrano()
                                //          .setImageBitmap(BitmapFactory.decodeFile(Path));
                            GestioneImmagini.getInstance().ImpostaImmagineDiSfondo(Path,"IMMAGINE", -1, null);

                                Notifica.getInstance().setImmagine(Path);
                                Notifica.getInstance().AggiornaNotifica();
                            // }
                        } else {
                            GestioneImmagini.getInstance().ImpostaImmagineDiSfondo("","BITMAP", -1, bitmap);
                        } */

                                    hAttendeSfumatura = new Handler(Looper.getMainLooper());
                                    hAttendeSfumatura.postDelayed(runAttendeSfumatura = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!GestioneImmagini.getInstance().isAttendeSfumatura()) {
                                                GestioneImmagini.getInstance().RimetteImmagine();

                                                hAttendeSfumatura.removeCallbacks(runAttendeSfumatura);
                                                hAttendeSfumatura = null;
                                            } else {
                                                hAttendeSfumatura.postDelayed(runAttendeSfumatura, 500);
                                            }
                                        }
                                    }, 500);
                                }
                            } else {
                                if (VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().isEmpty() ||
                                        VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().equals("***")) {
                                    GestioneImmagini.getInstance().ImpostaImmagineVuota();
                                } else {
                                    GestioneImmagini.getInstance().ImpostaUltimaImmagine(true);
                                    GestioneImmagini.getInstance().CreaCarosello();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}