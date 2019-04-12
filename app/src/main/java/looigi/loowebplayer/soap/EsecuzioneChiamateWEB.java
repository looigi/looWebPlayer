/* package looigi.loowebplayer.soap;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;

import java.util.Date;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dialog.DialogMessaggio;
import looigi.loowebplayer.notifiche.Notifica;
import looigi.loowebplayer.thread.NetThread;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;

public class EsecuzioneChiamateWEB {
    private Runnable runRiga;
    private Handler hSelezionaRiga;
    private int Contatore;
    private String messErrore;
    private Integer EsecuzioneRoutineWeb;
    private Boolean Ferma=false;
    private String sOperazione;
    private int ContatoreAttesa;
    private int NumeroBrano=-1;
    private String TipoClasse;

    private Context context;
    private String ws;
    private String NS;
    private String SA;
    private String Urletto;
    private String TipoOperazione;
    private String Operazione;
    private int TimeOut;
    private String[] Brano;
    private String PathFile;
    private String PathBase;
    private ImageView i;
    private String Artista;
    private String Album;
    private String Ritorno;
    private Date DataUltimoCaricamento=null;
    private Boolean Automatico;
    private Boolean NonFermareDownload=false;
    private int Tentativo=0;
    private int NumeroOperazione;
    private boolean NonFernareDownload;
    private Date Ora;
    private int ritorno = 0;
    private boolean StaGiaEseguendoErrore=false;
    private boolean NuovoTentativo=false;

    public Boolean getFerma() {
        return Ferma;
    }

    public void setTentativo(int tentativo) {
        Tentativo = tentativo;
    }

    public void setFerma(Boolean ferma) {
        Ferma = ferma;
    }

    public void setEsecuzioneRoutineWeb(Integer esecuzioneRoutineWeb) {
        EsecuzioneRoutineWeb = esecuzioneRoutineWeb;
    }

    public void setMessErrore(String messErrore) {
        this.messErrore = messErrore;
    }

    private boolean ControllaPossibilitaEsecuzione(Date ora) {
        Boolean Ok = true;
        DataUltimoCaricamento=ora;
        Date ud = VariabiliStaticheGlobali.getInstance().getUltimaDataCaricamento();

        if (ud == null) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Controllo possibilità esecuzione: Ok. Data ultimo caricamento nulla");
            Ok=true;
        } else {
            if (DataUltimoCaricamento==null) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Controllo possibilità esecuzione: Ok. Data ultimo caricamento (classe) nulla");
                DataUltimoCaricamento=ud;
                Ok=true;
            } else {
                int a = DataUltimoCaricamento.compareTo(ud);
                if (a < 0) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Controllo possibilità esecuzione: KO. Data ultimo caricamento minore");
                    Ok = false;
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Controllo possibilità esecuzione: Ok. Data ultimo caricamento (classe) nulla");
                    DataUltimoCaricamento=ud;
                    Ok=true;
                }
            }
        }
        if (Ok) {
            if (NumeroBrano==-1 || NumeroBrano == VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                VariabiliStaticheGlobali.getInstance().setUltimaDataCaricamento(DataUltimoCaricamento);
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Controllo possibilità esecuzione: KO. Numero brano diverso");
                Ok = false;
            }
        }

        return Ok;
    }

    public void EsegueChiamataTesto(int NumeroBrano, String Ritorno, int TimeOut, Date ora, int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataTesto EsecuzioneChiamateWEB");

        if (VariabiliStaticheGlobali.getInstance().getgText()==null || !NuovoTentativo) {
            if (!NetThread.getInstance().isStaGirando()) {
                NetThread.getInstance().StopNetThread();
                NetThread.getInstance().start();
            }

            if (NuovoTentativo) {
                if (VariabiliStaticheGlobali.getInstance().getgText()!=null) {
                    VariabiliStaticheGlobali.getInstance().getgText().StoppaEsecuzione();
                    VariabiliStaticheGlobali.getInstance().setgText(null);
                }
            }

            if (ControllaPossibilitaEsecuzione(ora)) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataTesto EsecuzioneChiamateWEB -> Controllato possibilità esercuzione");

                this.NumeroBrano = NumeroBrano;
                this.TimeOut = TimeOut;
                this.Ritorno = Ritorno;
                // this.NumeroOperazione = NumeroOperazione;

                DownloadTextFile d = new DownloadTextFile();
                d.setPath(VariabiliStaticheGlobali.getInstance().PercorsoDIR);
                // d.setPathNomeFile("Lista.dat");
                d.setNumeroBrano(NumeroBrano);
                d.setContext(VariabiliStaticheGlobali.getInstance().getContext());
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna lista brani. Scarico lista mp3: " + VariabiliStaticheGlobali.getInstance().PercorsoURL + "/" + Ritorno.replace("\\", "/"));
                // d.startDownload(VariabiliStaticheGlobali.getInstance().PercorsoURL + "/" + Ritorno.replace("\\", "/"), this);

                VariabiliStaticheGlobali.getInstance().setgText(d);
                sOperazione = "Ritorna lista brani";
                TipoClasse = "DownloadText";

                this.NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, sOperazione);

                if (!NetThread.getInstance().isOk()) {
                    GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                    // if (VariabiliStaticheHome.getInstance().getLayOperazionWEB() != null) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Chiamata Download lista brani: KO. Problemi di rete");
                    messErrore = "ERROR: Mancanza di rete";

                    if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                        NetThread.getInstance().setCaroselloBloccato(false);
                    }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da Esegue Chiamata Testo in ElaborazioneWEB.");
                    EsegueErrore(false);
                    // }
                } else {
                    AttendeFineElaborazioneWEB(5000, sOperazione);
                }
            } else {
                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(this.NumeroOperazione, false);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download lista brani bloccato da routine 'ControllaPossibilitaEsecuzione'");
                if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                    VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                    NetThread.getInstance().setCaroselloBloccato(false);
                }
            }
        } else  {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Duplicazione EsegueChiamataTesto EsecuzioneChiamateWEB -> Richiamata stessa routine");
            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
        }
    }

    public void EsegueChiamataImmagineCOVER(int NumeroBrano, String PathFile, String pathBase, ImageView i,
                                            String Artista, String Album, int TimeOut, Date ora, int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataImmagineCOVER EsecuzioneChiamateWEB");

        if (VariabiliStaticheGlobali.getInstance().getgImmC()==null || !NuovoTentativo) {
            if (!NetThread.getInstance().isStaGirando()) {
                NetThread.getInstance().StopNetThread();
                NetThread.getInstance().start();
            }

            if (NuovoTentativo) {
                if (VariabiliStaticheGlobali.getInstance().getgImmC()!=null) {
                    VariabiliStaticheGlobali.getInstance().getgImmC().StoppaEsecuzione();
                    VariabiliStaticheGlobali.getInstance().setgImmC(null);
                }
            }

            if (ControllaPossibilitaEsecuzione(ora)) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataImmagineCOVER EsecuzioneChiamateWEB -> Controllato possibilità esercuzione");

                this.NumeroBrano = NumeroBrano;
                this.PathFile = PathFile;
                this.PathBase = pathBase;
                this.i = i;
                this.Artista = Artista;
                this.Album = Album;
                this.TimeOut = TimeOut;
                this.NumeroOperazione = NumeroOperazione;

                DownloadImmagine d = new DownloadImmagine();
                d.setContext(context);
                d.setPath(PathFile);
                // d.setImage(i);
                d.setNumeroBrano(NumeroBrano);
                d.setInSfuma(false);
                d.setNumeroOperazione(NumeroOperazione);
                d.setCaricaImmagineBrano(true);

                if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagine brano. Scarico immagine: " + VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/Cover_" + Artista + ".jpg");
                    d.startDownload(VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/Cover_" + Artista + ".jpg", "Download immagine brano", this, TimeOut);
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna immagine brano. Scarico immagine: " + VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Dati/" + pathBase + "/Cover_" + Artista + ".jpg");
                    d.startDownload(VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Dati/" + pathBase + "/Cover_" + Artista + ".jpg", "Download immagine brano", this, TimeOut);
                }

                VariabiliStaticheGlobali.getInstance().setgImmC(d);
                sOperazione = "Download cover";
                TipoClasse = "DownloadImmaginiCOVER";

                this.NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, sOperazione);

                if (!NetThread.getInstance().isOk()) {
                    GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                    // if (VariabiliStaticheHome.getInstance().getLayOperazionWEB() != null) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Chiamata Download cover: KO. Problemi di rete");
                    messErrore = "ERROR: Mancanza di rete";

                    if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                        NetThread.getInstance().setCaroselloBloccato(false);
                    }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da Esegue Chiamata Immagine COVER in ElaborazioneWEB.");
                    EsegueErrore(false);
                    // }
                } else {
                    AttendeFineElaborazioneWEB(VariabiliStaticheGlobali.getInstance().getTimeOutImmagini(), sOperazione);
                }
            } else {
                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(this.NumeroOperazione, false);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download cover bloccato da routine 'ControllaPossibilitaEsecuzione'");
                if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                    VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                    NetThread.getInstance().setCaroselloBloccato(false);
                }
            }
        } else  {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Duplicazione EsegueChiamataImmagineCOVER EsecuzioneChiamateWEB -> Richiamata stessa routine");
            this.NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, sOperazione + ": Operazione duplicata");
        }
    }

    public void EsegueChiamataImmagine(int NumeroBrano, String PathFile, ImageView i, int TimeOut, String sUrl, Date ora, int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataImmagine EsecuzioneChiamateWEB");

        if (VariabiliStaticheGlobali.getInstance().getgImmI()==null || !NuovoTentativo) {
            if (!NetThread.getInstance().isStaGirando()) {
                NetThread.getInstance().StopNetThread();
                NetThread.getInstance().start();
            }

            if (NuovoTentativo) {
                if (VariabiliStaticheGlobali.getInstance().getgImmI()!=null) {
                    VariabiliStaticheGlobali.getInstance().getgImmI().StoppaEsecuzione();
                    VariabiliStaticheGlobali.getInstance().setgImmI(null);
                }
            }

            if (ControllaPossibilitaEsecuzione(ora)) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataImmagine EsecuzioneChiamateWEB -> Controllato possibilità esercuzione");

                this.NumeroBrano = NumeroBrano;
                this.PathFile = PathFile;
                this.i = i;
                this.TimeOut = TimeOut;
                this.Urletto = sUrl;
                this.NumeroOperazione = NumeroOperazione;

                DownloadImmagine d = new DownloadImmagine();
                d.setContext(context);
                d.setPath(PathFile);
                // d.setImage(i);
                d.setNumeroBrano(NumeroBrano);
                d.setNumeroOperazione(NumeroOperazione);
                d.setInSfuma(true);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Carosello: immagine non esistente. La scarico. Url: " + sUrl);

                d.startDownload(sUrl, "Download immagine artista", this, TimeOut);

                VariabiliStaticheGlobali.getInstance().setgImmI(d);
                sOperazione = "Ritorna immagine artista";
                TipoClasse = "DownloadImmagini";

                this.NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, sOperazione);

                if (!NetThread.getInstance().isOk()) {
                    GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                    // if (VariabiliStaticheHome.getInstance().getLayOperazionWEB() != null) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Chiamata immagine artista: KO. Problemi di rete");
                    messErrore = "ERROR: Mancanza di rete";

                    if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                        NetThread.getInstance().setCaroselloBloccato(false);
                    }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da Esegue Chiamata Immagine in ElaborazioneWEB.");
                    EsegueErrore(false);
                    // }
                } else {
                    AttendeFineElaborazioneWEB(10000, sOperazione);
                }
            } else {
                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(this.NumeroOperazione, false);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download immagine artista bloccato da routine 'ControllaPossibilitaEsecuzione'");
                if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                    VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                    NetThread.getInstance().setCaroselloBloccato(false);
                }
            }
        } else  {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Duplicazione EsegueChiamataImmagine EsecuzioneChiamateWEB -> Richiamata stessa routine");
            this.NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, sOperazione + ": Operazione duplicata");
        }
    }

    public void EsegueChiamataMP3(int NumeroBrano, String[] Brano, String Operazione, int TimeOut, Date ora, Boolean Automatico, int NumeroOperazione) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataMP3 EsecuzioneChiamateWEB");

        if (VariabiliStaticheGlobali.getInstance().getgMP3()==null || !NuovoTentativo) {
            if (!NetThread.getInstance().isStaGirando()) {
                NetThread.getInstance().StopNetThread();
                NetThread.getInstance().start();
            }

            if (NuovoTentativo) {
                if (VariabiliStaticheGlobali.getInstance().getgMP3()!=null) {
                    VariabiliStaticheGlobali.getInstance().getgMP3().StoppaEsecuzione();
                    VariabiliStaticheGlobali.getInstance().setgMP3(null);
                }
            }

            if (ControllaPossibilitaEsecuzione(ora)) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataMP3 EsecuzioneChiamateWEB -> Controllato possibilità esercuzione");

                this.NumeroBrano = NumeroBrano;
                this.Brano = Brano;
                this.Operazione = Operazione;
                this.TimeOut = TimeOut;
                this.Automatico = Automatico;
                this.NumeroOperazione = NumeroOperazione;

                String url = VariabiliStaticheGlobali.getInstance().PercorsoURL + "/";
                Boolean compresso = false;
                if (Brano[1].toUpperCase().contains("COMPRESSI")) {
                    compresso = true;
                    url += "Compressi/";
                } else {
                    url += "Dati/";
                }
                if (!VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase().isEmpty()) {
                    url += VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase() + "/";
                }

                int campi = Brano.length - 1;
                String sBrano = Brano[campi];
                String sAlbum = Brano[campi - 1];
                String sArtista = Brano[campi - 2];

                if (!url.contains(sArtista) && !url.contains(sAlbum)) {
                    url += sArtista + "/" + sAlbum + "/" + sBrano;
                } else {
                    url += sBrano;
                }

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download brano. Download MP3: " + url);
                DownloadMP3 d = new DownloadMP3();
                String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
                d.setPath(VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + sArtista + "/" + sAlbum);
                d.setNomeBrano(sBrano);
                d.setCompresso(compresso);
                d.setAutomatico(Automatico);
                d.setNumeroBrano(NumeroBrano);
                d.setContext(VariabiliStaticheGlobali.getInstance().getContext());
                d.startDownload(url, this);

                VariabiliStaticheGlobali.getInstance().setgMP3(d);
                sOperazione = Operazione;
                TipoClasse = "DownloadMP3";

                this.NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, Operazione);

                if (!NetThread.getInstance().isOk()) {
                    GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                    // if (VariabiliStaticheHome.getInstance().getLayOperazionWEB() != null) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Chiamata Download MP3: KO. Problemi di rete");
                    messErrore = "ERROR: Mancanza di rete";

                    if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                        NetThread.getInstance().setCaroselloBloccato(false);
                    }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da Esegue Chiamata MP3 in ElaborazioneWEB.");
                    EsegueErrore(false);
                    // }
                } else {
                    AttendeFineElaborazioneWEB(TimeOut, Operazione);
                }
            } else {
                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(this.NumeroOperazione, false);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download MP3 bloccato da routine 'ControllaPossibilitaEsecuzione'");
                if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                    VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                    NetThread.getInstance().setCaroselloBloccato(false);
                }
            }
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Duplicazione EsegueChiamataMP3 EsecuzioneChiamateWEB -> Richiamata stessa routine");
            this.NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, sOperazione + ": Operazione duplicata");
        }
    }

    public void EsegueChiamataSOAP(Context context, String ws, String Urletto, String TipoOperazione,
                                   String NS, String SA, final String Operazione, final int TimeOut, int NumeroBrano,
                                   Boolean NonFernareDownload, int NumeroOperazione, Date ora) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataSOAP EsecuzioneChiamateWEB");

        if (VariabiliStaticheGlobali.getInstance().getgWSoap()==null || NuovoTentativo) {
            if (!NetThread.getInstance().isStaGirando()) {
                NetThread.getInstance().StopNetThread();
                NetThread.getInstance().start();
            }

            if (NuovoTentativo) {
                if (VariabiliStaticheGlobali.getInstance().getgWSoap()!=null) {
                    VariabiliStaticheGlobali.getInstance().getgWSoap().StoppaEsecuzione();
                    VariabiliStaticheGlobali.getInstance().setgWSoap(null);
                }
            }

            if (ControllaPossibilitaEsecuzione(ora)) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "EsegueChiamataSOAP EsecuzioneChiamateWEB -> Controllato possibilità esercuzione");
                this.context = context;
                this.ws = ws;
                this.Urletto = Urletto;
                this.TipoOperazione = TipoOperazione;
                this.NS = NS;
                this.SA = SA;
                this.Operazione = Operazione;
                this.TimeOut = TimeOut;
                this.NumeroBrano = NumeroBrano;
                this.NonFermareDownload = NonFernareDownload;
                this.NumeroOperazione = NumeroOperazione;
                this.Ora = ora;
                TipoClasse = "SOAP";

                this.NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, Operazione);

                if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando() &&
                        !NonFermareDownload) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Uscita da chiamata SOAP per numero brano diverso: " + NumeroBrano +
                            "/" + VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando());
                    Ferma = true;
                    if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                        NetThread.getInstance().setCaroselloBloccato(false);
                    }
                } else {
                    if (!NetThread.getInstance().isOk()) {
                        // if (VariabiliStaticheHome.getInstance().getLayOperazionWEB() != null) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Chiamata SOAP: KO. Problemi di rete");
                        messErrore = "ERROR: Mancanza di rete";

                        GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                        if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                            VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                            NetThread.getInstance().setCaroselloBloccato(false);
                        }

                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da Esegue Chiamata SOAP in ElaborazioneWEB.");
                        EsegueErrore(false);
                        // }
                    } else {
                        Notifica.getInstance().setInDownload(true);
                        Notifica.getInstance().AggiornaNotifica();

                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esegue chiamata a WS:" +
                                VariabiliStaticheGlobali.RadiceWS + ws + Urletto + " " +
                                TipoOperazione + " " +
                                NS + " " +
                                SA + " " +
                                Operazione + " " +
                                TimeOut
                        );

                        setEsecuzioneRoutineWeb(0);

                        final GestioneWEBServiceSOAP g = new GestioneWEBServiceSOAP(
                                NumeroBrano,
                                VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
                                TipoOperazione,
                                NS,
                                SA,
                                TimeOut,
                                this,
                                NumeroOperazione);
                        g.Esegue(context);

                        VariabiliStaticheGlobali.getInstance().setgWSoap(g);
                        sOperazione = Operazione;
                        TipoClasse = "GestioneWEBServiceSOAP";

                        AttendeFineElaborazioneWEB(TimeOut, Operazione);
                    }
                }
            } else {
                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(this.NumeroOperazione, false);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Non è possibile scaricare il brano ora. Routine 'ControllaPossibilitaEsecuzione' bloccante");
                if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                    VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                    NetThread.getInstance().setCaroselloBloccato(false);
                }
            }
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Duplicazione EsegueChiamataSOAP EsecuzioneChiamateWEB -> Richiamata stessa routine");
            this.NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, sOperazione + ": Operazione duplicata");
        }
    }

    public void StoppaElaborazioneWEB() {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Elaborazione terminata.");

        Ferma=true;
        if (hSelezionaRiga!=null) {
            hSelezionaRiga.removeCallbacks(runRiga);
        }
        // if (VariabiliStaticheHome.getInstance().getLayOperazionWEB()!=null) {
        //     VariabiliStaticheHome.getInstance().getLayOperazionWEB().setVisibility(LinearLayout.GONE);
        // }

        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

        if (VariabiliStaticheGlobali.getInstance().isStaScaricandoBrano()) {
            VariabiliStaticheGlobali.getInstance().setStaScaricandoBrano(false);
        }

        Notifica.getInstance().setInDownload(false);
        Notifica.getInstance().AggiornaNotifica();

        if (EsecuzioneRoutineWeb == -1) {
            // Elaborazione in errore
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ": KO. Codice di ritorno: " + Integer.toString(EsecuzioneRoutineWeb));
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da StoppaElaborazioneWEB in ElaborazioneWEB.");
            EsegueErrore(false);
        } else {
            // VariabiliStaticheGlobali.getInstance().setTentativo(0);
            // VariabiliStaticheGlobali.getInstance().setEcw(null);

            Tentativo=0;

            // if (Automatico!=null && Automatico) {
            //     VariabiliStaticheGlobali.getInstance().setBloccaCarosello(false);
            // }
        }

        BloccaEsecuzioniRemote(false);
    }

    public void AttendeFineElaborazioneWEB(final int TimeOut, final String Operazione) {
        if (NumeroBrano==VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
            EsecuzioneRoutineWeb = 0;

            Ferma = false;
            Contatore = 0;
            hSelezionaRiga = new Handler();
            hSelezionaRiga.postDelayed(runRiga = new Runnable() {
                @Override
                public void run() {
                    if (!Ferma) {
                        if (!ControllaPossibilitaEsecuzione(DataUltimoCaricamento)) {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ": KO nel ciclo di attesa per ControllaPossibilitaEsecuzione");

                            Ferma=true;

                            EsecuzioneRoutineWeb = 0;
                        }

                        if (!NetThread.getInstance().isOk()) {
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". KO per Mancanza di rete.");

                            Ferma = true;
                            messErrore = "ERROR: Mancanza di rete";

                            EsecuzioneRoutineWeb = -1;

                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da AttendeFineElaborazioneWEB in ElaborazioneWEB. Parte 1.");
                            EsegueErrore(false);
                        }

                        if (EsecuzioneRoutineWeb == 1) {
                            // Elaborazione OK
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". OK.");

                            // VariabiliStaticheHome.getInstance().getLayOperazionWEB().setVisibility(LinearLayout.GONE);
                            hSelezionaRiga.removeCallbacks(runRiga);

                            Notifica.getInstance().setInDownload(false);
                            Notifica.getInstance().AggiornaNotifica();

                            // VariabiliStaticheGlobali.getInstance().setTentativo(0);
                            Tentativo=0;

                            BloccaEsecuzioniRemote(false);
                        } else {
                            if (EsecuzioneRoutineWeb == 0) {
                                if (!ControllaPossibilitaEsecuzione(DataUltimoCaricamento)) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ": KO dopo esito OK per ControllaPossibilitaEsecuzione");

                                // if (NumeroBrano!=VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                    // VariabiliStaticheHome.getInstance().getLayOperazionWEB().setVisibility(LinearLayout.GONE);
                                    VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

                                    Ferma=true;
                                    hSelezionaRiga.removeCallbacks(runRiga);
                                    runRiga = null;

                                    Notifica.getInstance().setInDownload(false);
                                    Notifica.getInstance().AggiornaNotifica();

                                    // VariabiliStaticheGlobali.getInstance().setTentativo(0);
                                    Tentativo=0;

                                    StoppaElaborazioneWEB();
                                } else {
                                    Contatore++;
                                    if (Contatore > (TimeOut / 1000)) {
                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), Operazione + ". Blocco per timeout");

                                        hSelezionaRiga.removeCallbacks(runRiga);
                                        runRiga = null;

                                        boolean Ok = false;

                                        switch (TipoClasse) {
                                            case "GestioneWEBServiceSOAP":
                                                VariabiliStaticheGlobali.getInstance().getgWSoap().StoppaEsecuzione();
                                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), Operazione + ". GestioneWEBServiceSOAP istanza stoppata: " + TipoClasse);
                                                Ok = true;
                                                break;
                                            case "DownloadText":
                                                VariabiliStaticheGlobali.getInstance().getgText().StoppaEsecuzione();
                                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), Operazione + ". DownloadText istanza stoppata: " + TipoClasse);
                                                Ok = true;
                                                break;
                                            case "DownloadImmagini":
                                                VariabiliStaticheGlobali.getInstance().getgImmI().StoppaEsecuzione();
                                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), Operazione + ". DownloadImmagini istanza stoppata: " + TipoClasse);
                                                Ok = true;
                                                break;
                                            case "DownloadImmaginiCOVER":
                                                VariabiliStaticheGlobali.getInstance().getgImmC().StoppaEsecuzione();
                                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), Operazione + ". DownloadImmaginiCOVER istanza stoppata: " + TipoClasse);
                                                Ok = true;
                                                break;
                                            case "DownloadMP3":
                                                VariabiliStaticheGlobali.getInstance().getgMP3().StoppaEsecuzione();
                                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), Operazione + ". DownloadMP3 istanza stoppata: " + TipoClasse);
                                                Ok = true;
                                                break;
                                        }
                                        if (!Ok) {
                                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), Operazione + ". Nessuna istanza stoppata: " + TipoClasse);
                                        }

                                        EsecuzioneRoutineWeb = -1;
                                        messErrore = "ERROR: Auto timeout";

                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da AttendeFineElaborazioneWEB in ElaborazioneWEB. Parte 2.");
                                        EsegueErrore(false);
                                    } else {
                                        NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, sOperazione + ". Attesa: "+Integer.toString(Contatore));
                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ": continuo ad attendere. Contatore: "+Integer.toString(Contatore));
                                        hSelezionaRiga.postDelayed(runRiga, 1000);
                                    }
                                }
                            } else {
                                if (EsecuzioneRoutineWeb == -1) {
                                    NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, sOperazione + ": KO per ritorno da routine");
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ": KO per ritorno da routine");
                                    // Elaborazione in errore
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da AttendeFineElaborazioneWEB in ElaborazioneWEB. Parte 3.");
                                    EsegueErrore(false);
                                } else {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ": proseguo normalmente ? Caso in cui EsecuzioneRoutineWEB!=-1 && !=0");
                                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),"Punto della routine EsecuzioneChiamateWEB\nambiguo", false, "looWebPlayer");
                                }
                            }
                        }
                    } else {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ": Arrivata richiesta di Ferma=true. Esco...");
                        BloccaTutto(sOperazione + ": Arrivata richiesta di stop");
                    }
                }
            }, 1000);
        } else {
            if (Automatico!=null && !Automatico) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ": Numero brano diverso da quello che sta suonando. Esco...");
                BloccaTutto(sOperazione + ": Numero brano diverso da quello che sta suonando");
            }
        }
    }

    public int EsegueErrore(final boolean DaFuori) {
        if (!StaGiaEseguendoErrore) {
            StaGiaEseguendoErrore=true;
            ritorno = 0;

            try {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Esecuzione routine di errore. Stoppo ciclo...");
                Ferma = true;
                hSelezionaRiga.removeCallbacks(runRiga);
                runRiga = null;
            } catch (Exception ignored) {

            }

            if (NumeroBrano == VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                // Elaborazione in errore
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Esecuzione routine di errore.");

                Notifica.getInstance().setInDownload(false);
                Notifica.getInstance().AggiornaNotifica();

                if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getReloadAutomatico()) {
                    final int Tentativi = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQuantiTentativi();
                    // int Tentativo = VariabiliStaticheGlobali.getInstance().getTentativo();
                    Tentativo++;
                    if (Tentativo <= Tentativi) {
                        StaGiaEseguendoErrore=false;
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Tentativo di reload " + Integer.toString(Tentativo) + "/" + Integer.toString(Tentativi) + ".");

                        // VariabiliStaticheGlobali.getInstance().setTentativo(Tentativo);
                        final int TempoAttesa = Tentativo * 15000;

                        ContatoreAttesa = 0;

                        hSelezionaRiga = new Handler();
                        hSelezionaRiga.postDelayed(runRiga = new Runnable() {
                            @Override
                            public void run() {
                                if (!ControllaPossibilitaEsecuzione(DataUltimoCaricamento)) {
                                    hSelezionaRiga.removeCallbacks(runRiga);
                                    Ferma = true;
                                    // VariabiliStaticheHome.getInstance().getLayOperazionWEB().setVisibility(LinearLayout.GONE);

                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Routine 'ControllaPossibilitaEsecuzione' bloccante in ErroreWEB.");

                                    if (TipoClasse.equals("DownloadMP3") && NonFermareDownload) {
                                        VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                                        NetThread.getInstance().setCaroselloBloccato(false);

                                        GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.error);
                                    }

                                    VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
                                    ritorno = -5;

                                    StoppaElaborazioneWEB();
                                } else {
                                    ContatoreAttesa++;
                                    if (ContatoreAttesa < (TempoAttesa / 1000)) {
                                        String mess = messErrore + ": " +
                                                "Nuovo tent. (" + Integer.toString(Tentativo) + "/" + Integer.toString(Tentativi) + "): " +
                                                Integer.toString(ContatoreAttesa) + "/" + Integer.toString(TempoAttesa / 1000) + " -Z" +sOperazione;
                                        // VariabiliStaticheHome.getInstance().getTxtOperazioneWEB().setText(messErrore + "\n" +
                                        //         "Attesa per nuovo tentativo (" + Integer.toString(Tentativo) + "/" + Integer.toString(Tentativi) + "): " +
                                        //         Integer.toString(ContatoreAttesa) + "/" + Integer.toString(TempoAttesa / 1000));
                                        VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, mess);

                                        hSelezionaRiga.postDelayed(runRiga, 1000);
                                    } else {
                                        if (NumeroBrano == VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
                                            // PronunciaFrasi pf = new PronunciaFrasi();
                                            // pf.PronunciaFrase("Riprovo", "ITALIANO");

                                            // BloccaEsecuzioniRemote(DaFuori);

                                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Reload " +
                                                    Integer.toString(Tentativo) + "/" + Integer.toString(Tentativi) + ".");

                                            hSelezionaRiga.removeCallbacks(runRiga);
                                            // VariabiliStaticheHome.getInstance().getLayOperazionWEB().setVisibility(LinearLayout.GONE);
                                            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

                                            // PronunciaFrasi pf = new PronunciaFrasi();
                                            // pf.PronunciaFrase("Eseguo", "ITALIANO");

                                            if (TipoClasse != null) {
                                                NuovoTentativo = true;

                                                switch (TipoClasse) {
                                                    case "GestioneWEBServiceSOAP":
                                                    case "SOAP":
                                                        EsegueChiamataSOAP(context, ws, Urletto, TipoOperazione,
                                                                NS, SA, Operazione, TimeOut, NumeroBrano, NonFermareDownload,
                                                                NumeroOperazione, DataUltimoCaricamento);
                                                        break;
                                                    case "DownloadText":
                                                        EsegueChiamataTesto(NumeroBrano, Ritorno, TimeOut, DataUltimoCaricamento, NumeroOperazione);
                                                        break;
                                                    case "DownloadImmaginiCOVER":
                                                        EsegueChiamataImmagineCOVER(NumeroBrano, PathFile, PathBase, i, Artista, Album,
                                                                TimeOut, DataUltimoCaricamento, NumeroOperazione);
                                                        break;
                                                    case "DownloadImmagini":
                                                        EsegueChiamataImmagine(NumeroBrano, PathFile, i, TimeOut, Urletto, DataUltimoCaricamento,
                                                                NumeroOperazione);
                                                        break;
                                                    case "DownloadMP3":
                                                        EsegueChiamataMP3(NumeroBrano, Brano, Operazione, TimeOut, DataUltimoCaricamento,
                                                                Automatico, NumeroOperazione);
                                                }
                                                ritorno = 0;
                                            }
                                        } else {
                                            BloccaEsecuzioniRemote(DaFuori);
                                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Blocco per canzone diversa dall'attuale");
                                            if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                                                VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
                                                NetThread.getInstance().setCaroselloBloccato(false);
                                            }
                                            ritorno = -4;
                                        }
                                    }
                                }
                            }
                        }, 1000);
                    } else {
                        BloccaEsecuzioniRemote(DaFuori);
                        GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                        // GestioneOggettiVideo.getInstance().PlayBrano(false);
                        BloccaTutto(sOperazione + ". Termine tentativi di reload. Blocco tutto.");
                        ritorno = -2;
                    }
                } else {
                    BloccaEsecuzioniRemote(DaFuori);
                    GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                    // GestioneOggettiVideo.getInstance().PlayBrano(false);
                    BloccaTutto(sOperazione + ". Non sono impostati altri tentativi di reload. Blocco tutto.");
                    ritorno = -1;
                }
            } else {
                BloccaEsecuzioniRemote(DaFuori);
                BloccaTutto(sOperazione + ". Blocco per brano diverso dall'attuale.");
                ritorno = -3;
            }

            return ritorno;
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Esecuzione routine di errore. Skippo in quanto già in esecuzione");
            return -6;
        }
    }

    private void BloccaEsecuzioniRemote(boolean DaFuori) {
        // Blocca esecuzioni in azione
        if (!DaFuori) {
            if (TipoClasse != null) {
                boolean Fatto = false;

                switch (TipoClasse) {
                    case "GestioneWEBServiceSOAP":
                    case "SOAP":
                        if (VariabiliStaticheGlobali.getInstance().getgWSoap() != null) {
                            VariabiliStaticheGlobali.getInstance().getgWSoap().StoppaEsecuzione();
                            VariabiliStaticheGlobali.getInstance().setgWSoap(null);
                            Fatto=true;
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Eliminata istanza SOAP");
                        } else {
                            Fatto=true;
                        }
                        break;
                    case "DownloadText":
                        if (VariabiliStaticheGlobali.getInstance().getgText() != null) {
                            VariabiliStaticheGlobali.getInstance().getgText().StoppaEsecuzione();
                            VariabiliStaticheGlobali.getInstance().setgText(null);
                            Fatto=true;
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Eliminata istanza DownloadText");
                        } else {
                            Fatto=true;
                        }
                        break;
                    case "DownloadImmaginiCOVER":
                        if (VariabiliStaticheGlobali.getInstance().getgImmC() != null) {
                            VariabiliStaticheGlobali.getInstance().getgImmC().StoppaEsecuzione();
                            VariabiliStaticheGlobali.getInstance().setgImmC(null);
                            Fatto=true;
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Eliminata istanza DownloadImmaginiCOVER");
                        } else {
                            Fatto=true;
                        }
                        break;
                    case "DownloadImmagini":
                        if (VariabiliStaticheGlobali.getInstance().getgImmI() != null) {
                            VariabiliStaticheGlobali.getInstance().getgImmI().StoppaEsecuzione();
                            VariabiliStaticheGlobali.getInstance().setgImmI(null);
                            Fatto=true;
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Eliminata istanza DownloadImmagini");
                        } else {
                            Fatto=true;
                        }
                        break;
                    case "DownloadMP3":
                        if (VariabiliStaticheGlobali.getInstance().getgMP3() != null) {
                            VariabiliStaticheGlobali.getInstance().getgMP3().StoppaEsecuzione();
                            VariabiliStaticheGlobali.getInstance().setgMP3(null);
                            Fatto=true;
                            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Eliminata istanza DownloadMP3");
                        } else {
                            Fatto=true;
                        }
                        break;
                }
                if (!Fatto) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Eliminata nessuna istanza: " + TipoClasse);
                }
            }
            // Blocca esecuzioni in azione
        }
    }

    private void BloccaTutto(String msg) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sOperazione + ". Blocco tutto");
        // VariabiliStaticheGlobali.getInstance().setEcw(null);

        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), msg);
        if (VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
            VariabiliStaticheGlobali.getInstance().setStaScaricandoAutomaticamente(false);
            // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(false);
            NetThread.getInstance().setCaroselloBloccato(false);
        }

        if (VariabiliStaticheGlobali.getInstance().isStaScaricandoBrano()) {
            VariabiliStaticheGlobali.getInstance().setStaScaricandoBrano(false);
        }

        StoppaElaborazioneWEB();
    }
}
*/