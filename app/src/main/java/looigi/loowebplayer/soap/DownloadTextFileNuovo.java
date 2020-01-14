package looigi.loowebplayer.soap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import looigi.loowebplayer.MainActivity;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.utilities.GestioneCPU;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.GestioneTesti;
import looigi.loowebplayer.utilities.RiempieListaInBackground;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class DownloadTextFileNuovo {
    private static boolean effettuaLogQui = false;
    private String Path;
    private String PathNomeFile;
    private static String messErrore="";
    private int NumeroOperazione;
    private static DownloadTxtFile downloadFile;
    private String Url;
    private String tOperazione;
    private long lastTimePressed = 0;
    // private static int Tentativo;

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
//        if (!VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente() &&
//                !VariabiliStaticheGlobali.getInstance().isStaScaricandoNormalmente()) {
            // boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
//
            // if ((System.currentTimeMillis() - lastTimePressed < 1000 && lastTimePressed >0) || !ceRete) {
            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
            //     }.getClass().getEnclosingMethod().getName(), "DL Testo troppo veloce");
            //     VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
            //     return;
            // }
            // lastTimePressed = System.currentTimeMillis();

            this.Url = sUrl;
            // this.ApriDialog=ApriDialog;
            this.NumeroOperazione = NOperazione;
            // this.Tentativo = 0;

            // String Chiave = this.Url;
            // if (VariabiliStaticheGlobali.getInstance().getChiaveDLText().isEmpty() ||
            //         (!VariabiliStaticheGlobali.getInstance().getChiaveDLText().isEmpty() &&
            //         !VariabiliStaticheGlobali.getInstance().getChiaveDLText().equals(Chiave))) {
            //     VariabiliStaticheGlobali.getInstance().setChiaveDLText(Chiave);

            // ApriDialog();
            boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

            GestioneCPU.getInstance().AttivaCPU();

            downloadFile = new DownloadTxtFile(NumeroOperazione, Path, PathNomeFile, ApriDialog, tOperazione, Url);
            if (ceRete) {
                downloadFile.execute(Url);
            } else {
                VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, "Download testo mancanza di rete");
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "DL Text mancanza di rete");

                StoppaEsecuzione();
            }
            // } else {
            //     VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
            //             "Skippata operazione DL Text uguale: "+Chiave);
            // }
/*        } else {
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
            if (VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().isEmpty() ||
                    VariabiliStaticheGlobali.getInstance().getUltimaImmagineVisualizzata().equals("***")) {
                GestioneImmagini.getInstance().ImpostaImmagineVuota();
            } else {
                GestioneImmagini.getInstance().ImpostaUltimaImmagine(true);
                GestioneImmagini.getInstance().CreaCarosello();
            }
            GestioneImmagini.getInstance().getImgBrano().setVisibility(LinearLayout.VISIBLE);
        } */
    }

    public void StoppaEsecuzione() {
        if (downloadFile != null) {
            downloadFile.cancel(true);
        }

        downloadFile.ChiudeDialog();
    }

    private static class DownloadTxtFile extends AsyncTask<String, Integer, String> {
        private String Path;
        private String PathNomeFile;
        private int NumeroBrano;
        private int NumeroOperazione;
        // private int QuantiTentativi;
        private Handler hAttesaNuovoTentativo;
        private Runnable rAttesaNuovoTentativo;
        private int SecondiAttesa;
        private boolean ApriDialog;
        private ProgressDialog progressDialog;
        private String tOperazione;
        private String Url;

        public DownloadTxtFile(int NumeroOperazione, String Path, String PathNomeFile, boolean ApriDialog, String tOperazione, String Url) {
            this.NumeroOperazione = NumeroOperazione;
            this.Path = Path;
            this.PathNomeFile = PathNomeFile;
            this.ApriDialog = ApriDialog;
            this.tOperazione = tOperazione;
            this.Url = Url;

            this.NumeroBrano = Utility.getInstance().ControllaNumeroBrano();

            // this.QuantiTentativi = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQuantiTentativi();
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ApriDialog();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            messErrore = "";

            // boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
//
            // if (!ceRete) {
            //     messErrore="ERROR: Assenza di rete";
            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "TEXT FIle; Assenza di rete");
            //     return null;
            // }

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
            }.getClass().getEnclosingMethod().getName(), "Scarico del testo: " + sUrl[0]);

            try {
                String uu = sUrl[0].replace(" ", "%20");
                uu = uu.replace("#", "%23");
                uu = uu.replace("&", "%26");

                URL url = new URL(uu);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                String redirect = c.getHeaderField("Location");
                if (redirect != null){
                    if (!redirect.toUpperCase().contains("INTERNAL")) {
                        c = (HttpURLConnection) new URL(redirect).openConnection();
                    }
                }
                c.setInstanceFollowRedirects(false);
                c.setRequestMethod("GET");
                c.setConnectTimeout(VariabiliStaticheGlobali.getInstance().getTimeOutImmagini());
                c.setReadTimeout(VariabiliStaticheGlobali.getInstance().getTimeOutImmagini());
                // c.setReadTimeout(50);
                c.connect();

                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    messErrore = "ERROR: Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage();
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Scarico del testo: Errore: " + messErrore);
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
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
                        if (NumeroBrano>-1 &&
                                NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali()
                                        .getConfigurazione().getQualeCanzoneStaSuonando()
                        ) {
                            messErrore = "ESCI";
                            break;
                        }
                        fos.write(buffer, 0, len1);
                        if (isCancelled()) {
                            messErrore = "ESCI";
                            break;
                        }
                    }

                    fos.close();
                    is.close();
                    dirFile = null;

                    if (isCancelled() || messErrore.equals("ESCI")) {
                        try {
                            if (outputFile != null) {
                                if (outputFile.exists()) {
                                    outputFile.delete();
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                    }.getClass().getEnclosingMethod().getName(), "File eliminato");
                                }
                            }
                        } catch (Exception e) {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                        }
                        outputFile = null;
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

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
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

        private void ControllaFineCiclo() {
            // VariabiliStaticheGlobali.getInstance().setChiaveDLText("***");
            ChiudeDialog();

            if (NumeroBrano>-1 &&
                    NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando() &&
                    VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() == -1) {
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                }.getClass().getEnclosingMethod().getName(), "Scarico testo. Cambio brano");
            } else {
                if (messErrore.isEmpty()) {
                    if (tOperazione.equals("Scarico testo brano")) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                }.getClass().getEnclosingMethod().getName(),
                                "Scarico testo brano. Conversione");

                        File f = new File(Path + "/" + PathNomeFile);
                        if (f.exists()) {
                            String testo = GestioneFiles.getInstance().LeggeFileDiTesto(Path + "/" + PathNomeFile);
                            if (testo.contains("<div class='lyricsbreak'>") && testo.contains("<div class='lyricbox'>")) {
                                testo = testo.substring(testo.indexOf("<div class='lyricbox'>") + 22, testo.length());
                                testo = testo.substring(0, testo.indexOf("<div class='lyricsbreak'>"));
                                testo = testo.replace("<i>", "");
                                testo = testo.replace("<b>", "");
                                testo = testo.replace("</b>", "");
                                testo = testo.replace("</i>", "");
                                testo = testo.replace("<br />", "**A CAPO**");
                                testo = testo.replace("&#", "");
                                String[] c = testo.split(";", -1);
                                String testoFinale = "";
                                for (String cc : c) {
                                    if (!cc.isEmpty()) {
                                        if (cc.contains("**A CAPO**")) {
                                            String n = cc.replace("**A CAPO**", "");
                                            int v = -1;
                                            if (Utility.getInstance().isNumeric(n)) {
                                                v = Integer.parseInt((n));
                                            } else {
                                                v = 32;
                                            }
                                            char ccc = ((char) v);
                                            testoFinale += "**A CAPO**" + ccc;
                                        } else {
                                            try {
                                                int v = Integer.parseInt(cc);
                                                char ccc = ((char) v);
                                                testoFinale += ccc;
                                            } catch (Exception ignored) {

                                            }
                                        }
                                    }
                                }

                                StrutturaBrani sb = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
                                GestioneTesti g = new GestioneTesti();
                                int Ascoltata = sb.getQuanteVolteAscoltato();
                                int Bellezza = sb.getStelle();
                                String sNomeBrano = sb.getNomeBrano();
                                if (sNomeBrano.contains("-")) {
                                    String[] A = sNomeBrano.split("-");
                                    sNomeBrano = A[1].trim();
                                }
                                if (sNomeBrano.contains(".")) {
                                    sNomeBrano = sNomeBrano.substring(0, sNomeBrano.indexOf("."));
                                }
                                int idArtista = sb.getIdArtista();
                                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getArtista();
                                String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(sb.getIdAlbum()).getNomeAlbum();

                                testoFinale = sNomeBrano.toUpperCase() + "**A CAPO**" + Artista.toUpperCase() + "**A CAPO**" + "**A CAPO**" + testoFinale;

                                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati().get(NumeroBrano).setTesto(testoFinale);
                                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati().get(NumeroBrano).setTestoTradotto("");

                                g.SalvaTestoSuSD(Artista, Album, sNomeBrano, testoFinale, "", Integer.toString(Ascoltata), Integer.toString(Bellezza));
                                g.SettaTesto(false);

                                VariabiliStaticheHome.getInstance().getImgScaricaTesto().setVisibility(LinearLayout.GONE);
                            }
                        }
                    } else {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                }.getClass().getEnclosingMethod().getName(),
                                "Scarico del testo. Richiamo RiempieStrutture in Home");
                        RiempieListaInBackground r = new RiempieListaInBackground();
                        r.RiempieStrutture(true, false);
                        // MainActivity.ScriveBraniInLista();
                    }
                } else {
                    if (messErrore.equals("ESCI")) {
                        // Errore... Riprovo ad eseguire la funzione
                        // boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

                        /* if (Tentativo < QuantiTentativi &&
                                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getReloadAutomatico()) {
                            Tentativo++;

                            final int TempoAttesa = (VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3() * (Tentativo-1)) / 1000;

                            NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                    "Errore Dl Text File. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(), "DL Text File: Errore. Attendo " + Integer.toString(TempoAttesa) + " secondi e riprovo: " +
                                    Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));

                            SecondiAttesa = 0;
                            hAttesaNuovoTentativo = new Handler(Looper.getMainLooper());
                            rAttesaNuovoTentativo = (new Runnable() {
                                @Override
                                public void run() {
                                    SecondiAttesa++;
                                    NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                            "Errore Dl Text File. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi) +
                                                    " Secondi " + Integer.toString(SecondiAttesa) + "/" + Integer.toString(TempoAttesa));
                                    if (SecondiAttesa>=TempoAttesa) {
                                        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

                                        boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
                                        if (ceRete) {
                                            ApriDialog();

                                            downloadFile = new DownloadTxtFile(NumeroOperazione, Path, PathNomeFile, ApriDialog, tOperazione, Url);
                                            downloadFile.execute(Url);
                                        } else {
                                            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
                                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                            }.getClass().getEnclosingMethod().getName(), "Scarico testo. Errore di rete");
                                        }

                                        hAttesaNuovoTentativo.removeCallbacks(rAttesaNuovoTentativo);
                                        hAttesaNuovoTentativo = null;
                                    } else {
                                        hAttesaNuovoTentativo.postDelayed(rAttesaNuovoTentativo, 1000);
                                    }
                                }
                            });
                            hAttesaNuovoTentativo.postDelayed(rAttesaNuovoTentativo, 1000);
                            // Errore... Riprovo ad eseguire la funzione
                        } else { */
                            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Scarico testo. Tentativi DL esauriti");
                        // }
                    } else {
                        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Scarico testo. Blocco da remoto");
                    }
                }
            }
            GestioneCPU.getInstance().DisattivaCPU();
        }
    }
}