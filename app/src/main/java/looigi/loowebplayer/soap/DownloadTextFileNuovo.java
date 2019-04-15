package looigi.loowebplayer.soap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import looigi.loowebplayer.MainActivity;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.utilities.RiempieListaInBackground;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class DownloadTextFileNuovo {
    private String Path;
    private String PathNomeFile;
    private String messErrore="";
    private int NumeroOperazione;
    private DownloadTxtFile downloadFile;
    private String Url;
    private boolean ApriDialog;
    private ProgressDialog progressDialog;
    private String tOperazione;

    private int QuantiTentativi;
    private int Tentativo;
    private Handler hAttesaNuovoTentativo;
    private Runnable rAttesaNuovoTentativo;

    public void setContext(Context context) {
        VariabiliStaticheGlobali.getInstance().setCtxPassaggio(context);
    }

    public void setOperazione(String Operazione) {
        tOperazione = Operazione;
    }

    public void setPathNomeFile(String pathNomeFile) {
        PathNomeFile = pathNomeFile;
    }

    public void setPath(String path) {
        Path = path;
    }

    public void startDownload(String sUrl, boolean ApriDialog, int NOperazione) {
        Url=sUrl;
        this.ApriDialog=ApriDialog;
        NumeroOperazione=NOperazione;

        this.QuantiTentativi = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQuantiTentativi();
        this.Tentativo = 0;

        ApriDialog();

        downloadFile = new DownloadTxtFile();
        downloadFile.execute(Url);
    }

    private void ChiudeDialog() {
        if (ApriDialog) {
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {
            }
        }
        VariabiliStaticheGlobali.getInstance().setOperazioneInCorso("");
        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(this.NumeroOperazione, false);
    }

    private void ApriDialog() {
        if (ApriDialog) {
            try {
                progressDialog = new ProgressDialog(VariabiliStaticheGlobali.getInstance().getContext());
                progressDialog.setMessage("Attendere Prego\n"+tOperazione);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            } catch (Exception ignored) {

            }
        }
    }

    public void StoppaEsecuzione() {
        downloadFile.cancel(true);

        ChiudeDialog();
    }

    private class DownloadTxtFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... sUrl) {
            messErrore = "";

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "Scarico del testo: " + sUrl[0]);

            try {
                URL url = new URL(sUrl[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setConnectTimeout(VariabiliStaticheGlobali.getInstance().getTimeOutImmagini());
                c.setReadTimeout(VariabiliStaticheGlobali.getInstance().getTimeOutImmagini());
                c.connect();

                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    messErrore = "ERROR: Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage();
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Scarico del testo: Errore: " + messErrore);
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Scarico del testo. Connesso e File esistente");
                    File dirFile = new File(Path + "/");
                    File outputFile = new File(Path + "/" + PathNomeFile);
                    dirFile.mkdirs();

                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(outputFile);

                    InputStream is = c.getInputStream();

                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);
                        if (isCancelled()) {
                            break;
                        }
                    }

                    fos.close();
                    is.close();
                    dirFile = null;
                    outputFile = null;

                    if (isCancelled()) {
                        try {
                            if (outputFile.exists()) {
                                outputFile.delete();
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                }.getClass().getEnclosingMethod().getName(), "File eliminato");
                            }
                        } catch (Exception e) {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                        }
                    }

                    // Traffico
                    File file = new File(Path + "/" + PathNomeFile);
                    long len = file.length();
                    long bs = VariabiliStaticheGlobali.getInstance().getBytesScaricati();
                    bs += len;
                    VariabiliStaticheGlobali.getInstance().setBytesScaricati(bs);
                    if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getVisualizzaTraffico()) {
                        Traffico.getInstance().ScriveTrafficoAVideo(VariabiliStaticheGlobali.getInstance().getBytesScaricati());
                    }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Scarico del testo. Effettuato");
                }
            } catch (Exception e) {
                messErrore = Utility.getInstance().PrendeErroreDaException(e);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ControllaFineCiclo();
        }

        public void ControllaFineCiclo() {
            ChiudeDialog();

            if (messErrore.isEmpty()) {
                // Errore... Riprovo ad eseguire la funzione
                if (Tentativo<=QuantiTentativi && VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getReloadAutomatico()) {
                    Tentativo++;
                    NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                            "Errore Dl Text File. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), "DL TExt File: Errore. Attendo 3 secondi e riprovo: " +
                            Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));

                    hAttesaNuovoTentativo = new Handler();
                    rAttesaNuovoTentativo = (new Runnable() {
                        @Override
                        public void run() {
                            ApriDialog();

                            downloadFile = new DownloadTxtFile();
                            downloadFile.execute(Url);

                            hAttesaNuovoTentativo.removeCallbacks(rAttesaNuovoTentativo);
                            hAttesaNuovoTentativo = null;
                        }
                    });
                    hAttesaNuovoTentativo.postDelayed(rAttesaNuovoTentativo, 3000);
                    // Errore... Riprovo ad eseguire la funzione
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Scarico del testo. Richiamo RiempieStrutture in Home");
                    RiempieListaInBackground r = new RiempieListaInBackground();
                    r.RiempieStrutture();
                    MainActivity.ScriveBraniInLista();
                }
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "Scarico testo. Blocco da remoto");
            }
        }
    }
}