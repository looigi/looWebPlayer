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
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.GestioneImpostazioneBrani;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.PronunciaFrasi;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class DownloadMP3Nuovo {
    // private static ProgressDialog progressDialog;
    private String Path;
    private String NomeBrano;
    private String messErrore="";
    private Boolean Compresso=false;
    private Boolean Automatico=false;
    private int sizeMP3=0;
    private DownloadFileMP3 downloadFile;
    private int NumeroBrano;
    private int NumeroOperazione;
    private Runnable runRiga;
    private Handler hSelezionaRiga;
    private String Url;
    private long lastTimePressed = 0;

    private int QuantiTentativi;
    private int Tentativo;
    private Handler hAttesaNuovoTentativo;
    private Runnable rAttesaNuovoTentativo;
    private int SecondiAttesa;

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

    public void setCompresso(Boolean i) {
        Compresso = i;
    }

    public void setAutomatico(Boolean i) {
        Automatico = i;
    }

    public void startDownload(String sUrl, int NO) {
        Boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

        if (System.currentTimeMillis() - lastTimePressed < 1000 || !ceRete) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "DL Mp3 troppo veloce");
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
            return;
        }
        lastTimePressed = System.currentTimeMillis();

        this.Url=sUrl;
        this.NumeroOperazione = NO;

        VariabiliStaticheHome.getInstance().getpMP3().setVisibility(LinearLayout.VISIBLE);
        VariabiliStaticheHome.getInstance().getpMP3().setMax(100);
        VariabiliStaticheHome.getInstance().getpMP3().setProgress(0);

        this.QuantiTentativi = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQuantiTentativi();
        this.Tentativo = 0;

        String Chiave = this.Url;
        // if (VariabiliStaticheGlobali.getInstance().getChiaveDLMP3().isEmpty() ||
        //         (!VariabiliStaticheGlobali.getInstance().getChiaveDLMP3().isEmpty() &&
        //         !VariabiliStaticheGlobali.getInstance().getChiaveDLMP3().equals(Chiave))) {
        //     VariabiliStaticheGlobali.getInstance().setChiaveDLMP3(Chiave);

            ApriDialog();

            downloadFile = new DownloadFileMP3();
            downloadFile.execute(Url);
        // } else {
        //     VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
        //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
        //             }.getClass().getEnclosingMethod().getName(),
        //             "Skippata operazione DL Brano uguale: " + Chiave);
        // }
    }

    private void ChiudeDialog() {
        // try {
        //     progressDialog.dismiss();
        // } catch (Exception ignored) {
        // }

        VariabiliStaticheGlobali.getInstance().setOperazioneInCorso("");
        // VariabiliStaticheNuove.getInstance().setD2(null);
        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(this.NumeroOperazione, false);
    }

    private void ApriDialog() {
    }

    public void StoppaEsecuzione() {
        ChiudeDialog();

        if (downloadFile != null) {
            downloadFile.cancel(true);

            messErrore = "ESCI";
            downloadFile.ControllaFineCiclo();
        }
    }

    private class DownloadFileMP3 extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... sUrl) {
            // RoutineDiDownload(sUrl[0]);
            messErrore="";

            /* hSelezionaRiga = new Handler(Looper.getMainLooper());
            hSelezionaRiga.postDelayed(runRiga=new Runnable() {
                @Override
                public void run() {
                    VariabiliStaticheHome.getInstance().getpMP3().setVisibility(LinearLayout.VISIBLE);
                    VariabiliStaticheHome.getInstance().getpMP3().setMax(100);
                    VariabiliStaticheHome.getInstance().getpMP3().setProgress(0);
                    hSelezionaRiga.removeCallbacks(runRiga);
                }
            }, 50); */

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                    new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Scarico del MP3: "+sUrl[0]);

            try {
                URL url = new URL(sUrl[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setConnectTimeout(VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3());
                c.setReadTimeout(VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3());
                c.connect();

                if (!isCancelled()) {
                    if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        messErrore = "ERROR: Server returned HTTP " + c.getResponseCode()
                                + " " + c.getResponseMessage();
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Scarico del MP3: Errore: " + messErrore);
                    } else {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. File esistente");
                        List values = c.getHeaderFields().get("content-Length");
                        if (values != null && !values.isEmpty()) {
                            String sLength = (String) values.get(0);

                            if (sLength != null) {
                                sizeMP3 = Integer.parseInt(sLength);
                            }
                        }

                        if (sizeMP3 > 0 && !isCancelled()) {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
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
                                    fos.write(buffer, 0, len1);
                                    if (len1 > 0) {
                                        lenF += len1;
                                    }
                                    publishProgress((int) (lenF * 100 / sizeMP3));
                                    if (NumeroBrano>-1 && NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                        messErrore = "ESCI";
                                        break;
                                    }
                                    if (isCancelled()) {
                                        break;
                                    }
                                }
                                fos.flush();
                                fos.close();
                                is.close();
                            }

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
                            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico del MP3: isCancelled");
                            // }

                            if (isCancelled() || messErrore.equals("ESCI")) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Brano NON scaricato");

                                messErrore="ESCI";
                            } else {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Brano scaricato");
                            }
                        } else {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Brano 0 bytes");
                            messErrore = "ERROR 0 length o brano skippato";
                        }
                    }
                }
            } catch (Exception e) {
                messErrore = Utility.getInstance().PrendeErroreDaException(e);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Errore: "+messErrore);
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
            // VariabiliStaticheGlobali.getInstance().setChiaveDLMP3("");

            // if (VariabiliStaticheNuove.getInstance().getD()!=null) {
            //     VariabiliStaticheNuove.getInstance().setD(null);
            // }
            // if (VariabiliStaticheNuove.getInstance().getD2()!=null) {
            //     VariabiliStaticheNuove.getInstance().setD2(null);
            // }

            if (NumeroBrano>-1 && NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                        "DL MP3: Cambio brano");
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "DL MP3: Cambio brano");
            } else {
                if (!messErrore.equals("ESCI")) {
                    ChiudeDialog();

                    if (!VariabiliStaticheGlobali.getInstance().getMessErrorePerDebugMP3().isEmpty()) {
                        messErrore = VariabiliStaticheGlobali.getInstance().getMessErrorePerDebugMP3();
                    }

                    if (VariabiliStaticheGlobali.getInstance().isStaScaricandoBrano()) {
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoBrano(false);
                    }

                    // VariabiliStaticheGlobali.getInstance().setEcw(null);
                    if (messErrore.isEmpty()) {
                        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isSalvataggioOggetti()) {
                            if (!NomeBrano.contains(VariabiliStaticheGlobali.EstensioneCompressione)) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
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
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. ImpostaBrano in Home");
                            GestioneImpostazioneBrani.getInstance().ImpostaBrano(Path + "/" + NomeBrano);
                        } else {
                            GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ok);

                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(), "Scarico del MP3 in background effettuato");
                            // VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                            // NetThread.getInstance().setCaroselloBloccato(false);
                            // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(false);
                        }
                    } else {
                        // Errore... Riprovo ad eseguire la funzione
                        if (Tentativo < QuantiTentativi && messErrore.contains("ERROR:") && VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getReloadAutomatico()) {
                            Tentativo++;

                            final int TempoAttesa = (VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3() * (Tentativo-1)) / 1000;

                            NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                                    "Errore Dl Brano. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
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
                                    if (SecondiAttesa>=TempoAttesa) {
                                        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

                                        ApriDialog();

                                        downloadFile = new DownloadFileMP3();
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
                            if (Automatico) {
                                GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ko);

                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                }.getClass().getEnclosingMethod().getName(), "Scarico del MP3 in background fallito");
                                // VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                                // NetThread.getInstance().setCaroselloBloccato(false);
                                // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(false);

                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                }.getClass().getEnclosingMethod().getName(), "Cerco eventuale brano già scaricato");
                                int brano = GestioneListaBrani.getInstance().CercaBranoGiaScaricato(true);
                                if (brano > -1) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                                    }.getClass().getEnclosingMethod().getName(), "Cerco eventuale brano già scaricato. OK: " + Integer.toString(brano));
                                    GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ok);
                                } else {
                                    VariabiliStaticheHome.getInstance().getTxtTitoloBackground().setText("Nessun brano caricato");
                                }
                            }

                            // if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                            PronunciaFrasi pf = new PronunciaFrasi();
                            pf.PronunciaFrase("Errore", "ITALIANO");
                        }
                        // }
                    }
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Scarico del MP3. Brano Skippato");
                }
            }

            VariabiliStaticheHome.getInstance().getpMP3().setVisibility(LinearLayout.GONE);
            VariabiliStaticheHome.getInstance().getpMP3().setProgress(0);

            if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
            }
        }
    }

    private void PrendeImmagineDaMP3(String mPath) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Prende immagine da MP3");
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
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
            NomeBrano=NomeBrano.substring(0,NomeBrano.indexOf("."));
        }

        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String PathFile = "";
        Boolean MP3Organizzati = false;
        if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/" + Album + ".jpg";
            GestioneFiles.getInstance().CreaCartelle(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/");
            MP3Organizzati = true;
        } else {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/"+NomeBrano+".jpg";
            GestioneFiles.getInstance().CreaCartelle(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/");
            MP3Organizzati = false;
        }

        File f = new File(PathFile);
        if (f.exists()) {
            if (!MP3Organizzati) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
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
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
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
                // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Prende immagine da MP3. Errore: "+e.getMessage());
                GestioneImmagini.getInstance().StoppaTimerCarosello();
                GestioneImmagini.getInstance().setImmagineDaCambiare("");
                GestioneImmagini.getInstance().setImmNumber(-1);
                GestioneImmagini.getInstance().ImpostaImmagineVuota();
                VariabiliStaticheGlobali.getInstance().setUltimaImmagineVisualizzata("");
            }
        }
    }
}