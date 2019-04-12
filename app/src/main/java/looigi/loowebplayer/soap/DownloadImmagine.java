/* package looigi.loowebplayer.soap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaImmagini;
import looigi.loowebplayer.notifiche.Notifica;
import looigi.loowebplayer.utilities.GestioneCaricamentoBrani;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class DownloadImmagine {
    private String Path;
    private int NumeroBrano;
    private String messErrore="";
    private Bitmap bitmap;
    private Boolean inSfuma=false;
    private Boolean CaricaImmagineBrano=false;
    private DownloadImageFile downloadFile;
    private int NumeroOperazione;
    private EsecuzioneChiamateWEB ecw;
    private String Url;
    private Integer TIMEOUT;

    public void setContext(Context context) {
        VariabiliStaticheGlobali.getInstance().setCtxPassaggio(context);
    }

    public void setNumeroOperazione(int n) {
        NumeroOperazione = n;
    }

    public void setPath(String path) {
        Path = path;
    }

    // public void setImage(ImageView i) {
    //     VariabiliStaticheGlobali.getInstance().setIvPassaggio(i);
    // }

    public void setNumeroBrano(int numeroBrano) {
        NumeroBrano = numeroBrano;
    }

    public void setInSfuma(Boolean i) {
        inSfuma = i;
    }

    public void setCaricaImmagineBrano(Boolean i) {
        CaricaImmagineBrano = i;
    }

    public void startDownload(String sUrl, String sOperazione, EsecuzioneChiamateWEB e, int TO) {
        Url=sUrl;
        ecw = e;
        TIMEOUT = TO;

        ApriDialog();

        String sUrl2 = Url.substring(9, Url.length());
        Url=Url.substring(0,9);
        sUrl2=sUrl2.replace("//","/");
        Url+=sUrl2;

        downloadFile = new DownloadImageFile();
        downloadFile.execute(Url);
    }

    private void ChiudeDialog() {
        if (!messErrore.isEmpty()) {
            // if (inSfuma) {
                // GestioneImmagini.getInstance().ToglieAlpha();
            // }
        }

        VariabiliStaticheGlobali.getInstance().setOperazioneInCorso("");
    }

    private void ApriDialog() {
    }

    public void StoppaEsecuzione() {
        downloadFile.cancel(true);

        ChiudeDialog();

        messErrore="ERROR: Auto Timeout";

        // downloadFile.ControllaFineCiclo(true);
    }

    public class DownloadImageFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... sUrl) {
            messErrore="";

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Inizio lo scarico dell'immagine: "+sUrl[0]+". TIMEOUT: "+Integer.toString(TIMEOUT));
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(sUrl[0]).getContent());
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Effettuato");
            } catch (IOException e) {
                messErrore = Utility.getInstance().PrendeErroreDaException(e);
                if (messErrore.contains("java.io.FileNotFoundException")) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Errore di immagine non trovata");
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Errore: " + messErrore);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ControllaFineCiclo(false);
        }

        public void ControllaFineCiclo(boolean DaFuori) {
            if (!VariabiliStaticheGlobali.getInstance().getMessErrorePerDebug().isEmpty()) {
                messErrore = VariabiliStaticheGlobali.getInstance().getMessErrorePerDebug();
            }

            ecw.setMessErrore(messErrore);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Post execute. Errore: "+messErrore);
            if (messErrore.isEmpty() || messErrore.contains("java.io.FileNotFoundException")) {
                if (!ecw.getFerma()) {
                    ecw.setEsecuzioneRoutineWeb(1);
                    // if (!DaFuori) {
                     ecw.StoppaElaborazioneWEB();
                    // }

                    if (bitmap != null || messErrore.contains("java.io.FileNotFoundException")) {
                        if (!inSfuma) {
                            if (!messErrore.contains("java.io.FileNotFoundException")) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Non inSfuma. Riduco immagine");
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
                                VariabiliStaticheHome.getInstance().ImpostaImmagineDiSfondo("", "BITMAP", -1, bitmap);
                            }
                        }

                        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSalvataggioOggetti()) {
                            if (!messErrore.contains("java.io.FileNotFoundException")) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Salvo immagine su disco: " + Path);
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
                                // }
                            } else {
                                if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                    // VariabiliStaticheHome.getInstance().getImgBrano().setImageResource(R.drawable.nessuna);
                                    //VariabiliStaticheHome.getInstance().ImpostaImmagineDiSfondo("", "ICONA", R.drawable.nessuna, null);
                                    VariabiliStaticheHome.getInstance().ImpostaUltimaImmagine(true);
                                    // ToglieAlpha();

                                    GestioneImmagini.getInstance().SettaImmagineSuIntestazione("***");

                                    ecw.setTentativo(999);
                                    ecw.StoppaElaborazioneWEB();
                                }
                            }
                        }

                        if (inSfuma) {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. inSfuma. Proseguo carosello");
                            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSalvataggioOggetti()) {
                                if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                    //  VariabiliStaticheHome.getInstance().getImgBrano()
                                    //          .setImageBitmap(BitmapFactory.decodeFile(Path));
                                    VariabiliStaticheHome.getInstance().ImpostaImmagineDiSfondo(Path,"IMMAGINE", -1, null);

                                    Notifica.getInstance().setImmagine(Path);
                                    Notifica.getInstance().AggiornaNotifica();
                                }
                            } else {
                                if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                    // VariabiliStaticheHome.getInstance().getImgBrano()
                                    //         .setImageBitmap(bitmap);
                                    VariabiliStaticheHome.getInstance().ImpostaImmagineDiSfondo("","BITMAP", -1, bitmap);
                                }
                            }

                            if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                GestioneImmagini.getInstance().ToglieAlpha();
                            }
                        }
                    } else {
                        if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da download immagine. Sezione 1.");
                            int RitornoErrore = ecw.EsegueErrore(DaFuori);
                        }
                    }
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico immagine: Blocco remoto");
                }
            } else {
                if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da download immagine. Sezione 2.");
                    int RitornoErrore = ecw.EsegueErrore(DaFuori);
                }
            }

            ChiudeDialog();

            // if (messErrore.isEmpty() || Tentativo>Tentativi) {
            // if (messErrore.isEmpty()) {
            if (CaricaImmagineBrano) {
                // if (!ecw.getFerma()) {
                if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico dell'immagine. Prosegue Carica Brano 1 in Home");
                    GestioneCaricamentoBrani.getInstance().ProsegueCaricaBrano1(NumeroOperazione);
                }
                // }
            }
            // }
        }
    }
}
*/