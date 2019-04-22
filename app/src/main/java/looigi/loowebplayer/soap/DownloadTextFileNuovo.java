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
    private int NumeroBrano;
    private long lastTimePressed = 0;

    private int QuantiTentativi;
    private int Tentativo;
    private Handler hAttesaNuovoTentativo;
    private Runnable rAttesaNuovoTentativo;
    private int SecondiAttesa;

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
        if (System.currentTimeMillis() - lastTimePressed < 1000) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "DL Testo troppo veloce");
            return;
        }
        lastTimePressed = System.currentTimeMillis();

        this.Url=sUrl;
        this.ApriDialog=ApriDialog;
        this.NumeroOperazione=NOperazione;

        this.QuantiTentativi = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQuantiTentativi();
        this.Tentativo = 0;

        this.NumeroBrano = Utility.getInstance().ControllaNumeroBrano();

        String Chiave = this.Url;
        if (!VariabiliStaticheGlobali.getInstance().getChiaveDLText().equals(Chiave)) {
            VariabiliStaticheGlobali.getInstance().setChiaveDLText(Chiave);

            ApriDialog();

            downloadFile = new DownloadTxtFile();
            downloadFile.execute(Url);
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Skippata operazione DL Text uguale: "+Chiave);
        }
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
        if (downloadFile != null) {
            downloadFile.cancel(true);
        }

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
                        if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                            messErrore = "ESCI";
                            break;
                        }
                        fos.write(buffer, 0, len1);
                        if (isCancelled()) {
                            break;
                        }
                    }

                    fos.close();
                    is.close();
                    dirFile = null;
                    outputFile = null;

                    if (isCancelled() || messErrore.equals("ESCI")) {
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
                messErrore = "ERROR:" + Utility.getInstance().PrendeErroreDaException(e);
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
            VariabiliStaticheGlobali.getInstance().setChiaveDLText("***");
            ChiudeDialog();

            if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "Scarico testo. Cambio brano");
            } else {
                if (messErrore.isEmpty()) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                            "Scarico del testo. Richiamo RiempieStrutture in Home");
                    RiempieListaInBackground r = new RiempieListaInBackground();
                    r.RiempieStrutture(true);
                    // MainActivity.ScriveBraniInLista();
                } else {
                    if (messErrore.equals("ESCI")) {
                        // Errore... Riprovo ad eseguire la funzione
                        if (Tentativo < QuantiTentativi && VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getReloadAutomatico()) {
                            Tentativo++;

                            final int TempoAttesa = (VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3() * (Tentativo-1)) / 1000;

                            NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                    "Errore Dl Text File. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(), "DL Text File: Errore. Attendo " + Integer.toString(TempoAttesa) + " secondi e riprovo: " +
                                    Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));

                            SecondiAttesa = 0;
                            hAttesaNuovoTentativo = new Handler();
                            rAttesaNuovoTentativo = (new Runnable() {
                                @Override
                                public void run() {
                                    SecondiAttesa++;
                                    NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                            "Errore Dl Text File. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi) +
                                                    " Secondi " + Integer.toString(SecondiAttesa) + "/" + Integer.toString(TempoAttesa));
                                    if (SecondiAttesa>=TempoAttesa) {
                                        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

                                        ApriDialog();

                                        downloadFile = new DownloadTxtFile();
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
                            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Scarico testo. Tentativi DL esauriti");
                        }
                    } else {
                        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Scarico testo. Blocco da remoto");
                    }
                }
            }
        }
    }
}