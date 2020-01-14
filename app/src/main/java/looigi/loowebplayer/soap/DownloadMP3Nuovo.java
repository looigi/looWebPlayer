package looigi.loowebplayer.soap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.ritorno_ws.RitornoDaWSIntermedioAttesa;
import looigi.loowebplayer.ritorno_ws.wsRitornoNuovoPerErrore;
import looigi.loowebplayer.thread.NetThreadNuovo;
import looigi.loowebplayer.utilities.GestioneCPU;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.GestioneImpostazioneBrani;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.PronunciaFrasi;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class DownloadMP3Nuovo {
    private static boolean effettuaLogQui = false;
    // private static ProgressDialog progressDialog;
    private String Path;
    private String NomeBrano;
    private static String messErrore="";
    private boolean Compresso=false;
    private boolean Automatico=false;
    private int sizeMP3=0;
    private static DownloadFileMP3 downloadFile;
    private int NumeroBrano;
    private int NumeroOperazione;
    // private int NumeroOperazione2;
    private String Url;
    private long lastTimePressed = 0;
    private static int Tentativo;

    private Handler hAttesaTermine;
    private Runnable rAttesaTermine;
    private int secondi;

    public void setContext(Context context) {
        VariabiliStaticheGlobali.getInstance().setCtxPassaggio(context);
    }

    public void setNumeroBrano(int numeroBrano) {
        NumeroBrano = numeroBrano;
    }

    public void setPath(String path) {
        Path = path;
    }

    public void setNomeBrano(String nb) {
        NomeBrano = nb;
    }

    public void setCompresso(boolean i) {
        Compresso = i;
    }

    public void setAutomatico(boolean i) {
        Automatico = i;
    }

    public void startDownload(String sUrl, int NO) {
        // boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
//
        // if ((System.currentTimeMillis() - lastTimePressed < 1000 && lastTimePressed >0) || !ceRete) {
        //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
        //     }.getClass().getEnclosingMethod().getName(), "DL Mp3 troppo veloce");
        //     VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
        //     return;
        // }
        // lastTimePressed = System.currentTimeMillis();
        // VariabiliStaticheGlobali.getInstance().setStaScaricandoMP3(true);

        Url=sUrl;
        NumeroOperazione = NO;
        Tentativo = 0;

        VariabiliStaticheHome.getInstance().getpMP3().setVisibility(LinearLayout.VISIBLE);
        VariabiliStaticheHome.getInstance().getpMP3().setMax(100);
        VariabiliStaticheHome.getInstance().getpMP3().setProgress(0);

        // String Chiave = this.Url;
        // if (VariabiliStaticheGlobali.getInstance().getChiaveDLMP3().isEmpty() ||
        //         (!VariabiliStaticheGlobali.getInstance().getChiaveDLMP3().isEmpty() &&
        //         !VariabiliStaticheGlobali.getInstance().getChiaveDLMP3().equals(Chiave))) {
        //     VariabiliStaticheGlobali.getInstance().setChiaveDLMP3(Chiave);

            // ApriDialog();

        boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

        GestioneCPU.getInstance().AttivaCPU();
        VariabiliStaticheGlobali.getInstance().setgMP3(this);

        downloadFile = new DownloadFileMP3(NumeroOperazione, Path, Compresso, NomeBrano,
                NumeroBrano, Automatico, Url, this);
        if (ceRete) {
            FaiPartireTimerDiProsecuzione();

            downloadFile.execute(Url);
        } else {
            VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, "Download mp3 mancanza di rete");
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                    "DL MP3 mancanza di rete");

            StoppaEsecuzione();
        }
        // } else {
        //     VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
        //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
        //             }.getClass().getEnclosingMethod().getName(),
        //             "Skippata operazione DL Brano uguale: " + Chiave);
        // }
    }

    private void FaiPartireTimerDiProsecuzione() {
        secondi = 0;
        // NumeroOperazione2 = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download mp3");

        hAttesaTermine = new Handler(Looper.getMainLooper());
        hAttesaTermine.postDelayed(rAttesaTermine = new Runnable() {
            @Override
            public void run() {
                /* if (VariabiliStaticheGlobali.getInstance().getNtn().getQuantiSecondiSenzaRete() > VariabiliStaticheGlobali.SecondiSenzaRetePerAnnullareIlDL) {
                    // VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione2, false,
                    //         "ERRORE Rete down. Blocco download");
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Secondi senza rete (" +
                            VariabiliStaticheGlobali.getInstance().getNtn().getQuantiSecondiSenzaRete() + ") superiori a SecondiSenzaRetePerAnnullareIlDL (" +
                            VariabiliStaticheGlobali.SecondiSenzaRetePerAnnullareIlDL + ")");

                    StoppaEsecuzione();
                } else { */
                    secondi++;
                    if (secondi<=(VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3()/1000)) {

                        if (NumeroBrano>-1 &&
                                NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando() &&
                                VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() == -1) {

                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Numero brano cambiato");

                            StoppaEsecuzione();
                        } else {
                            // VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione2, false,
                            //         "Download brano: secondi " + secondi);
                            hAttesaTermine.postDelayed(rAttesaTermine, 1000);
                        }
                    } else {
                        // VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione2, false,
                        //         "ERRORE Download brano: timeout");

                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Timeout attesa DL Mp3");

                        StoppaEsecuzione();
                    }
                // }
            }
        }, 1000);
    }

    public void TerminaTimerDiProsecuzione() {
        // VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione2, true);
        if (hAttesaTermine!=null && rAttesaTermine!=null) {
            hAttesaTermine.removeCallbacks(rAttesaTermine);
            hAttesaTermine = null;
        }
    }

    public void StoppaEsecuzione() {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
        }.getClass().getEnclosingMethod().getName(), "Stoppa esecuzione");

        TerminaTimerDiProsecuzione();

        downloadFile.ChiudeDialog();

        // if (downloadFile != null) {
            downloadFile.cancel(true);

            messErrore = "ESCI";
            downloadFile.ControllaFineCiclo();
        // }
    }

    private static class DownloadFileMP3 extends AsyncTask<String, Integer, String> {
        private Runnable runRiga;
        private Handler hSelezionaRiga;
        private String Path;
        private String NomeBrano;
        // private String messErrore="";
        private boolean Compresso=false;
        private boolean Automatico=false;
        private int sizeMP3=0;
        // private DownloadFileMP3 downloadFile;
        private int NumeroBrano;
        private int NumeroOperazione;
        private int NumeroOper;
        // private Runnable runRiga;
        // private Handler hSelezionaRiga;
        private String Url;
//
        // private int QuantiTentativi;
        // private Handler hAttesaNuovoTentativo;
        // private Runnable rAttesaNuovoTentativo;
        private int SecondiAttesa;
        private Handler hAttesaNuovoTentativo;
        private Runnable rAttesaNuovoTentativo;
        private DownloadMP3Nuovo dmp3n;

        public DownloadFileMP3(int NumeroOperazione, String Path, boolean Compresso, String NomeBrano,
                               int NumeroBrano, boolean Automatico, String Url, final DownloadMP3Nuovo dmp3n) {
            this.NumeroOperazione = NumeroOperazione;
            this.Path = Path;
            this.NomeBrano = NomeBrano;
            this.Compresso = Compresso;
            this.NumeroBrano = NumeroBrano;
            this.Automatico = Automatico;
            this.Url = Url;
            this.dmp3n = dmp3n;

            // QuantiTentativi = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQuantiTentativi();
            NumeroOper = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download in corso");
        }

        private void ChiudeDialog() {
            // try {
            //     progressDialog.dismiss();
            // } catch (Exception ignored) {
            // }

            VariabiliStaticheGlobali.getInstance().setOperazioneInCorso("");
            // VariabiliStaticheNuove.getInstance().setD2(null);
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
        }

        private void ApriDialog() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ApriDialog();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            messErrore="";
            // int numeroPassaggi = 0;

            // boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
//
            // if (!ceRete) {
            //     messErrore="ERROR: Assenza di rete";
            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
            //             "MP3: Assenza di rete");
            //     // return null;
            // }

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui,
                    new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Scarico del MP3: "+sUrl[0]);

            try {
                String uu = sUrl[0].replace(" ", "%20");
                uu = uu.replace("#", "%23");
                uu = uu.replace("&", "%26");

                URL url = new URL(uu);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setConnectTimeout(VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3());
                c.setReadTimeout(VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3());
                c.connect();

                if (!isCancelled()) {
                    if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        messErrore = "ERROR: Server returned HTTP " + c.getResponseCode()
                                + " " + c.getResponseMessage();
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Scarico del MP3: Errore: " + messErrore);
                    } else {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. File esistente");
                        List values = c.getHeaderFields().get("content-Length");
                        if (values != null && !values.isEmpty()) {
                            String sLength = (String) values.get(0);

                            if (sLength != null) {
                                sizeMP3 = Integer.parseInt(sLength);
                            }
                        }

                        if (sizeMP3 > 0 && !isCancelled()) {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                                    "Scarico del MP3. Dimensioni: " + Integer.toString(sizeMP3));
                            Path = Path.replace("#", "_");
                            File file;
                            file = new File(Path);
                            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSalvataggioOggetti()) {
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                            }
                            if (Compresso) {
                                NomeBrano = VariabiliStaticheGlobali.EstensioneCompressione + NomeBrano;
                            }
                            if (!VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSalvataggioOggetti()) {
                                Path = VariabiliStaticheGlobali.getInstance().PercorsoDIR;
                                NomeBrano = "Appo.mp3";
                                file = new File(Path + "/" + NomeBrano);
                                if (!file.exists()) {
                                    file.delete();
                                }
                                file = new File(Path);
                            }

                            File outputFile = new File(file, NomeBrano);
                            FileOutputStream fos = new FileOutputStream(outputFile);
                            if (!isCancelled()) {
                                InputStream is = c.getInputStream();
                                byte[] buffer = new byte[1024];
                                int len1 = 0;
                                int lenF = 0;
                                while ((len1 = is.read(buffer)) != -1) {
                                    // ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
//
                                    // if (!ceRete) {
                                    //     messErrore = "ESCI";
                                    //     break;
                                    // }

                                    fos.write(buffer, 0, len1);
                                    if (len1 > 0) {
                                        lenF += len1;
                                    }
                                    publishProgress((int) (lenF * 100 / sizeMP3));

                                    // numeroPassaggi++;

                                    // if (VariabiliStaticheGlobali.getInstance().getNtn().getQuantiSecondiSenzaRete() > VariabiliStaticheGlobali.SecondiSenzaRetePerAnnullareIlDL) {
                                    /* if (NumeroBrano>-1 &&
                                            NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando() &&
                                            VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() == -1 ||
                                            VariabiliStaticheGlobali.getInstance().getNtn().getQuantiSecondiSenzaRete() > VariabiliStaticheGlobali.SecondiSenzaRetePerAnnullareIlDL
                                    ) { */
                                     //    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                     //    }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Problema di rete o cambio brano");

                                     //    messErrore = "ERROR: ERRORE RETE";

                                     //    break;
                                    // }
                                    if (isCancelled()) {
                                        messErrore = "ESCI";

                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                        }.getClass().getEnclosingMethod().getName(), "Routine bloccata");

                                        break;
                                    }

                                    if (Automatico && VariabiliStaticheDebug.RitardaDownloadAutomatico) {
                                        try {
                                            Thread.sleep(VariabiliStaticheDebug.ritardoDownloadAutomatico);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (!Automatico && VariabiliStaticheDebug.RitardaDownload) {
                                        try {
                                            Thread.sleep(VariabiliStaticheDebug.ritardoDownload);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    Thread.yield();
                                }
                                fos.flush();
                                fos.close();
                                is.close();
                            }

                            if (isCancelled() || messErrore.equals("ESCI") || messErrore.equals("ERROR: ERRORE RETE")) {
                                try {
                                    if (outputFile.exists()) {
                                        outputFile.delete();
                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                        }.getClass().getEnclosingMethod().getName(), "File eliminato");
                                    }
                                } catch (Exception e) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                                }
                            } else {
                                dmp3n.TerminaTimerDiProsecuzione();
                            }

                            // if (!isCancelled()) {
                            // Traffico
                            long bs = VariabiliStaticheGlobali.getInstance().getBytesScaricati();
                            bs += sizeMP3;
                            VariabiliStaticheGlobali.getInstance().setBytesScaricati(bs);
                            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getVisualizzaTraffico()) {
                                Traffico.getInstance().ScriveTrafficoAVideo(VariabiliStaticheGlobali.getInstance().getBytesScaricati());
                            }
                            // Traffico
                            // } else {
                            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico del MP3: isCancelled");
                            // }

                            if (isCancelled() || messErrore.equals("ESCI")) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Brano NON scaricato");

                                messErrore="ESCI";
                            } else {
                                if (messErrore.equals("ERROR: ERRORE RETE")) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                    }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Problemi di rete");
                                } else {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                    }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Brano scaricato");
                                }
                            }
                        } else {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Brano 0 bytes");
                            messErrore = "ERROR 0 length o brano skippato";
                        }
                    }
                }
            } catch (Exception e) {
                messErrore = Utility.getInstance().PrendeErroreDaException(e);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Errore: "+messErrore);

            }

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            messErrore="ESCI";
        }

        @Override
        protected void onProgressUpdate(final Integer... progress) {
            super.onProgressUpdate(progress);

            hSelezionaRiga = new Handler(Looper.getMainLooper());
            hSelezionaRiga.postDelayed(runRiga=new Runnable() {
                @Override
                public void run() {
                    VariabiliStaticheHome.getInstance().getpMP3().setProgress(progress[0]);

                    hSelezionaRiga.removeCallbacks(runRiga);
                }
            }, 50);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ControllaFineCiclo();
        }

        public void ControllaFineCiclo() {
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOper, true);
            VariabiliStaticheGlobali.getInstance().setgMP3(null);
            // VariabiliStaticheGlobali.getInstance().setStaScaricandoMP3(false);

            VariabiliStaticheGlobali.getInstance().setStaAttendendoFineDownload(false);

            // if (NumeroBrano>-1 &&
            //         NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando() &&
            //         VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() == -1) {
            //     NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
            //             "DL MP3: Cambio brano");
            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
            //     }.getClass().getEnclosingMethod().getName(), "DL MP3: Cambio brano");
//
            // } else {
                if (!messErrore.equals("ESCI")) {
                    ChiudeDialog();

                    if (!VariabiliStaticheDebug.getInstance().getMessErrorePerDebugMP3().isEmpty()) {
                        messErrore = VariabiliStaticheDebug.getInstance().getMessErrorePerDebugMP3();
                    }

                    if (VariabiliStaticheGlobali.getInstance().isStaScaricandoBrano()) {
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoBrano(false);
                    }

                    // VariabiliStaticheGlobali.getInstance().setEcw(null);
                    if (messErrore.isEmpty()) {
                        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSalvataggioOggetti()) {
                            if (!NomeBrano.contains(VariabiliStaticheGlobali.EstensioneCompressione)) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Prende immagine da brano non compresso");
                                PrendeImmagineDaMP3(Path + "/" + NomeBrano);
                            }
                        }

                        if (!Automatico) {
                            // if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                            //     VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                            //     NetThread.getInstance().setCaroselloBloccato(false);
                            //     // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(false);
                            // }
                            if (NumeroBrano>-1 &&
                                    NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando() &&
                                    VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() == -1) {
                                NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                        "DL MP3: Cambio brano");
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                }.getClass().getEnclosingMethod().getName(), "DL MP3: Cambio brano");
//
                            } else {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. ImpostaBrano in Home");
                                GestioneImpostazioneBrani.getInstance().ImpostaBrano(Path + "/" + NomeBrano);
                            }
                        } else {
                            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ok);

                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Scarico del MP3 in background effettuato");
                            // VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                            // NetThread.getInstance().setCaroselloBloccato(false);
                            // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(false);

                            VariabiliStaticheHome.getInstance().setQuanteScaricate(VariabiliStaticheHome.getInstance().getQuanteScaricate() + 1);
                            Utility.getInstance().ScriveScaricateAscoltate();
                        }
                    } else {
                        // Errore... Riprovo ad eseguire la funzione
                        // boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(), messErrore);


                        if (messErrore.equals("ERROR: ERRORE RETE")){
                            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ko);

                            PrendeBranoGiaScaricato();
                        } else {
                            /*if (Tentativo < QuantiTentativi && messErrore.contains("ERROR:") &&
                                     VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getReloadAutomatico()) {
                                Tentativo++;

                                final int TempoAttesa = (VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3() * (Tentativo - 1)) / 1000;

                                NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                        "Errore Dl Brano. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                }.getClass().getEnclosingMethod().getName(), "DL Brano: Errore. Attendo " + Integer.toString(TempoAttesa) + " secondi e riprovo: " +
                                        Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));

                                SecondiAttesa = 0;
                                hAttesaNuovoTentativo = new Handler(Looper.getMainLooper());
                                rAttesaNuovoTentativo = (new Runnable() {
                                    @Override
                                    public void run() {
                                        SecondiAttesa++;
                                        NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                                "Errore Dl Brano. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi) +
                                                        " Secondi " + Integer.toString(SecondiAttesa) + "/" + Integer.toString(TempoAttesa));
                                        if (SecondiAttesa >= TempoAttesa) {
                                            boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

                                            if (ceRete) {
                                                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

                                                ApriDialog();

                                                downloadFile = new DownloadFileMP3(NumeroOperazione, Path, Compresso, NomeBrano,
                                                        NumeroBrano, Automatico, Url, dmp3n);
                                                downloadFile.execute(Url);
                                            } else {
                                                wsRitornoNuovoPerErrore rRit = new wsRitornoNuovoPerErrore();
                                                rRit.RitornaBranoConAttesa("", NumeroOperazione, Automatico);
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
                                if (Automatico) {
                                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ko);

                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                    }.getClass().getEnclosingMethod().getName(), "Scarico del MP3 in background fallito");
                                    // VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                                    // NetThread.getInstance().setCaroselloBloccato(false);
                                    // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(false);

                                    File ff = new File(NomeBrano);
                                    try {
                                        ff.delete();
                                    } catch (Exception ignored) {

                                    }

                                    PrendeBranoGiaScaricato();
                                }

                                // if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                PronunciaFrasi pf = new PronunciaFrasi();
                                pf.PronunciaFrase("Errore", "ITALIANO");
                            // }
                        }
                        // }
                    }
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Brano Skippato");

                    wsRitornoNuovoPerErrore rRit = new wsRitornoNuovoPerErrore();
                    rRit.RitornaBranoConAttesa("", NumeroOperazione, Automatico);
                }
            // }

            VariabiliStaticheHome.getInstance().getpMP3().setVisibility(LinearLayout.GONE);
            VariabiliStaticheHome.getInstance().getpMP3().setProgress(0);

            /* if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
            }

            if (VariabiliStaticheGlobali.getInstance().isStaScaricandoNormalmente()) {
                VariabiliStaticheGlobali.getInstance().setStaScaricandoNormalmente(false);
            } */

            GestioneCPU.getInstance().DisattivaCPU();
        }

        private void PrendeBranoGiaScaricato() {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
            }.getClass().getEnclosingMethod().getName(), "Cerco eventuale brano già scaricato");
            int brano = GestioneListaBrani.getInstance().CercaBranoGiaScaricato(true);
            if (brano > -1) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                }.getClass().getEnclosingMethod().getName(),
                        "Cerco eventuale brano già scaricato. OK: " + Integer.toString(brano));
                GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ok);
                // VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                VariabiliStaticheGlobali.getInstance().setNumeroProssimoBrano(brano);
                GestioneListaBrani.getInstance().AggiungeBranoAgliAscoltati(brano);

                StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(brano);
                final String NomeBrano = s.getNomeBrano();
                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();

                VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText(NomeBrano + " (" + Artista +")");
            } else {
                VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText("Nessun brano caricato");
            }
        }

        private void PrendeImmagineDaMP3(String mPath) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
            }.getClass().getEnclosingMethod().getName(), "Prende immagine da MP3");
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                mmr.setDataSource(mPath);
                InputStream inputStream = null;
                if (mmr.getEmbeddedPicture() != null) {
                    inputStream = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                }
                mmr.release();

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // int a1 = NomeBrano.indexOf(".");
                // String sNome = NomeBrano.substring(0,a1) +".jpg";

                StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
                String NomeBrano = s.getNomeBrano();
                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
                // String NomeBrano = NomeBrano;
                if (NomeBrano.contains(".")) {
                    NomeBrano = NomeBrano.substring(0, NomeBrano.indexOf("."));
                }

                String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
                String PathFile = "";
                boolean MP3Organizzati = false;
                if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
                    PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/" + Album + ".jpg";
                    GestioneFiles.getInstance().CreaCartelle(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/");
                    MP3Organizzati = true;
                } else {
                    PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + NomeBrano + ".jpg";
                    GestioneFiles.getInstance().CreaCartelle(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/");
                    MP3Organizzati = false;
                }

                File f = new File(PathFile);
                if (f.exists()) {
                    if (!MP3Organizzati) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                }.getClass().getEnclosingMethod().getName(),
                                "Ritorna immagine brano presa da mp3. La imposto");
                        GestioneImmagini.getInstance().ImpostaImmagineDiSfondo(PathFile, "IMMAGINE", -1, null);

                        GestioneImmagini.getInstance().SettaImmagineSuIntestazione(PathFile);
                        VariabiliStaticheGlobali.getInstance().setImmagineMostrata(PathFile);
                    }
                } else {
                    try {
                        FileOutputStream out = new FileOutputStream(PathFile);
                        if (out != null && bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                            if (!MP3Organizzati) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                        }.getClass().getEnclosingMethod().getName(),
                                        "Ritorna immagine brano presa da mp3. La imposto");
                                GestioneImmagini.getInstance().ImpostaImmagineDiSfondo(PathFile, "IMMAGINE", -1, null);

                                GestioneImmagini.getInstance().SettaImmagineSuIntestazione(PathFile);
                                VariabiliStaticheGlobali.getInstance().setImmagineMostrata(PathFile);
                            }
                        }
                    } catch (IOException e) {
                        // e.printStackTrace();
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                        // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Prende immagine da MP3. Errore: "+e.getMessage());
                        GestioneImmagini.getInstance().StoppaTimerCarosello();
                        GestioneImmagini.getInstance().setImmagineDaCambiare("");
                        GestioneImmagini.getInstance().setImmNumber(-1);
                        GestioneImmagini.getInstance().ImpostaImmagineVuota();
                        VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata("");
                    }
                }
            } catch (Exception e) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Prende immagine da MP3. Errore: "+e.getMessage());
                GestioneImmagini.getInstance().StoppaTimerCarosello();
                GestioneImmagini.getInstance().setImmagineDaCambiare("");
                GestioneImmagini.getInstance().setImmNumber(-1);
                GestioneImmagini.getInstance().ImpostaImmagineVuota();
                VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata("");
            }
        }
    }
}