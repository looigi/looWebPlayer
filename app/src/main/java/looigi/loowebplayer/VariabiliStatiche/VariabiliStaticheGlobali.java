package looigi.loowebplayer.VariabiliStatiche;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.Date;

import looigi.loowebplayer.R;
import looigi.loowebplayer.cuffie.GestoreCuffie;
import looigi.loowebplayer.dati.DatiGenerali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaUtenti;
import looigi.loowebplayer.soap.AttesaScaricamentoBrano;
import looigi.loowebplayer.soap.DownloadMP3Nuovo;
import looigi.loowebplayer.soap.GestioneWEBServiceSOAPNuovo;
import looigi.loowebplayer.thread.NetThreadNuovo;
import looigi.loowebplayer.thread.ScaricoBranoEAttesa;
import looigi.loowebplayer.utilities.Log;

public class VariabiliStaticheGlobali {
    //-------- Singleton ----------//
    private static VariabiliStaticheGlobali instance = null;

    private VariabiliStaticheGlobali() {
    }

    public static VariabiliStaticheGlobali getInstance() {
        if (instance == null) {
            instance = new VariabiliStaticheGlobali();
        }

        return instance;
    }

    public String PercorsoDIR=Environment.getExternalStorageDirectory().getPath()+"/LooigiSoft/looWebPlayer";
    public String PercorsoURL="http://looigi.no-ip.biz:12345/looWebPlayer";
    private Context context;
    private AppCompatActivity contextPrincipale;
    private FragmentActivity FragmentActivityPrincipale;
    private View ViewActivity;
    private String OperazioneInCorso;
    public static String MascheraAttuale;
    public static String RadiceWS = "http://looigi.no-ip.biz:12345/looWebPlayer/";
    // public static String RadiceUpload = "http://looigi.no-ip.biz:12345/CvCalcioUploadPic/default.aspx";
    final public static String ValoreAmministratore = "1";
    final public static String EstensioneCompressione = "_cmp_";
    final public static String NomeApplicazione="Looigi's Web Player";
    final public static int TempoImmagineVisibile=15000;
    final public static int TempoSfumatura=3000;
    final public static int TempoAttesaFineDownload = 30;
    final public static int SecondiSenzaRetePerAnnullareIlDL = 2;

    private int TimeOutDownloadMP3=45000;
    private int TimeOutListaBrani=200000;
    private int AttesaControlloEsistenzaMP3=5000;
    private int TimeOutImmagini=15000;
    private int TipoSegnale=1;
    private int AttesaSecondiBranoSuccessivo=3;

    private MenuItem itemNuovo;
    private MenuItem itemMultimedia;
    private FloatingActionButton actButtonNew;
    private AppBarLayout appBar;
    private Window windowBackground;
    private StrutturaUtenti Utente;
    private DatiGenerali DatiGenerali=new DatiGenerali();
    // private boolean bloccaCarosello=false;
    private boolean StaSuonando=false;
    private boolean ScrittaAscoltata=false;
    private boolean MusicaTerminata=false;
    // private boolean StaSuonandoAttesa=false;
    private Log log = new Log();
    private Long bytesScaricati=0L;
    // private ImageView ivPassaggio;
    private Context ctxPassaggio;
    private GestoreCuffie myReceiverCuffie;
    // private GestioneTastoCuffie myReceiverGTC;
    private boolean CuffieInserite=false;
    private boolean GiaEntrato=false;
    private TextView txtBraniInLista;
    private boolean HaCaricatoTuttiIDettagliDelBrano=false;
    private Date UltimaDataCaricamento=null;
    private String UltimaCosaPronunciata="";
    private int UltimaCanzoneSuonata=-2;
    private boolean HaScaricatoAutomaticamente=false;
    private boolean StaScaricandoAutomaticamente =false;
    // private boolean StaAttendendoConMusichetta=false;
    // private boolean NonFermareDownload;
    private int BranoAutomatico;
    private String UltimaImmagineVisualizzata="";
    // private boolean MessaImmagineVuota;
    private boolean DisegnaMascheraHomeCompleta;
    private String sUltimaCanzoneSuonata="";
    private TextView txtTraffico;
    private String RitornoCheckFileURL="";
    private boolean EsciDaCheckFile=false;
    private boolean StaScaricandoBrano;
    // private EsecuzioneChiamateWEB ecw;
    private final String messErrorePerDebug = "";
    private final String messErrorePerDebugMP3 = "";
    // private boolean CaroselloBloccatoDaAutomatico;
    private boolean EseguiHandlerConMainLooper = false;
    private Integer nOperazioneATOW = -1;
    // private boolean StaGiaAttendendo = false;
    private boolean OrientationPortrait = false;
    private String ImmagineMostrata;
    private NetThreadNuovo ntn;
    private int NumeroBranoNuovo;
    private int NumeroProssimoBrano=-1;
    private AttesaScaricamentoBrano asb;
    private boolean AttendeFineScaricamento = false;

    // private String ChiaveDLSoap = "";
    // private String ChiaveDLImmagine = "";
    // private String ChiaveDLText = "";
    // private String ChiaveDLMP3 = "";
    // private String ChiaveCheckURL = "";

    private String lastRitorno = "";
    private boolean ScreenON = true;
    private Intent iServizio;
    private ScaricoBranoEAttesa sbea;
    private Integer BranoImpostatoSenzaRete=-1;
    private boolean StaScaricandoMP3=false;

    private GestioneWEBServiceSOAPNuovo gWSoap = null;
    // private DownloadImmagine gImmC = null;
    // private DownloadImmagine gImmI = null;
    private DownloadMP3Nuovo gMP3 = null;
    private AttesaScaricamentoBrano gAttesa = null;
    // private DownloadTextFile gText = null;

    private boolean StaAggiornandoLaVersione = false;
    private boolean HaControllatoLaVersione = false;

    private String UltimaOperazioneSOAP = "";
    private long lastTimePressed = 0;
    // private boolean EsciDallAttesa = false;

    private Drawable play;
    private Drawable play_dis;
    private Drawable stop;
    private Drawable stop_dis;
    private Drawable avanti;
    private Drawable avanti_dis;
    private Drawable indietro;
    private Drawable indietro_dis;
    private Drawable refresh;
    private Drawable refresh_dis;

    public void SettaIcone(Context context) {
        play = ContextCompat.getDrawable(context, R.drawable.play);
        play_dis = ContextCompat.getDrawable(context, R.drawable.play_dis);
        stop = ContextCompat.getDrawable(context, R.drawable.stop);
        stop_dis = ContextCompat.getDrawable(context, R.drawable.stop_dis);
        avanti = ContextCompat.getDrawable(context, R.drawable.avanti);
        avanti_dis = ContextCompat.getDrawable(context, R.drawable.avanti_dis);
        indietro = ContextCompat.getDrawable(context, R.drawable.indietro);
        indietro_dis = ContextCompat.getDrawable(context, R.drawable.indietro_dis);
        refresh = ContextCompat.getDrawable(context, R.drawable.refresh);
        refresh_dis = ContextCompat.getDrawable(context, R.drawable.refresh_dis);
    }

    public Context getCtxPassaggio() {
        return ctxPassaggio;
    }

    public void setCtxPassaggio(Context ctxPassaggio) {
        this.ctxPassaggio = ctxPassaggio;
    }

    /* public boolean isEsciDallAttesa() {
        return EsciDallAttesa;
    }

    public void setEsciDallAttesa(boolean esciDallAttesa) {
        EsciDallAttesa = esciDallAttesa;
    } */

    public long getLastTimePressed() {
        return lastTimePressed;
    }

    public void setLastTimePressed(long lastTimePressed) {
        this.lastTimePressed = lastTimePressed;
    }

    public String getUltimaOperazioneSOAP() {
        return UltimaOperazioneSOAP;
    }

    public void setUltimaOperazioneSOAP(String ultimaOperazioneSOAP) {
        UltimaOperazioneSOAP = ultimaOperazioneSOAP;
    }

    public boolean isHaControllatoLaVersione() {
        return HaControllatoLaVersione;
    }

    public void setHaControllatoLaVersione(boolean haControllatoLaVersione) {
        HaControllatoLaVersione = haControllatoLaVersione;
    }

    public boolean isStaAggiornandoLaVersione() {
        return StaAggiornandoLaVersione;
    }

    public void setStaAggiornandoLaVersione(boolean staAggiornandoLaVersione) {
        StaAggiornandoLaVersione = staAggiornandoLaVersione;
    }

    public AttesaScaricamentoBrano getgAttesa() {
        return gAttesa;
    }

    public void setgAttesa(AttesaScaricamentoBrano gAttesa) {
        this.gAttesa = gAttesa;
    }

    public DownloadMP3Nuovo getgMP3() {
        return gMP3;
    }

    public void setgMP3(DownloadMP3Nuovo gMP3) {
        this.gMP3 = gMP3;
    }

    public GestioneWEBServiceSOAPNuovo getgWSoap() {
        return gWSoap;
    }

    public void setgWSoap(GestioneWEBServiceSOAPNuovo gWSoap) {
        this.gWSoap = gWSoap;
    }

    public boolean isAttendeFineScaricamento() {
        return AttendeFineScaricamento;
    }

    public void setAttendeFineScaricamento(boolean attendeFineScaricamento) {
        AttendeFineScaricamento = attendeFineScaricamento;
    }

    public AttesaScaricamentoBrano getAsb() {
        return asb;
    }

    public void setAsb(AttesaScaricamentoBrano asb) {
        this.asb = asb;
    }

    public int getNumeroProssimoBrano() {
        return NumeroProssimoBrano;
    }

    public void setNumeroProssimoBrano(int numeroProssimoBrano) {
        NumeroProssimoBrano = numeroProssimoBrano;
    }

    public boolean getScrittaAscoltata() {
        return ScrittaAscoltata;
    }

    public void setScrittaAscoltata(boolean scrittaAscoltata) {
        ScrittaAscoltata = scrittaAscoltata;
    }

    public boolean getStaScaricandoMP3() {
        return StaScaricandoMP3;
    }

    public void setStaScaricandoMP3(boolean staScaricandoMP3) {
        StaScaricandoMP3 = staScaricandoMP3;
    }

    public Integer getBranoImpostatoSenzaRete() {
        return BranoImpostatoSenzaRete;
    }
//
    public void setBranoImpostatoSenzaRete(Integer branoImpostatoSenzaRete) {
        BranoImpostatoSenzaRete = branoImpostatoSenzaRete;
    }

    public int getNumeroBranoNuovo() {
        return NumeroBranoNuovo;
    }

    public void setNumeroBranoNuovo(int numeroBranoNuovo) {
        NumeroBranoNuovo = numeroBranoNuovo;
    }

    public NetThreadNuovo getNtn() {
        return ntn;
    }

    public void setNtn(NetThreadNuovo ntn) {
        this.ntn = ntn;
    }

    public ScaricoBranoEAttesa getSbea() {
        return sbea;
    }

    public void setSbea(ScaricoBranoEAttesa sbea) {
        this.sbea = sbea;
    }

    public Intent getiServizio() {
        return iServizio;
    }

    public void setiServizio(Intent iServizio) {
        this.iServizio = iServizio;
    }

    public boolean getScreenON() {
        return ScreenON;
    }

    public void setScreenON(boolean screenON) {
        ScreenON = screenON;
    }

    public String getLastRitorno() {
        return lastRitorno;
    }

    public void setLastRitorno(String lastRitorno) {
        this.lastRitorno = lastRitorno;
    }

    public int getAttesaSecondiBranoSuccessivo() {
        return AttesaSecondiBranoSuccessivo;
    }

    /* public String getChiaveCheckURL() {
        return ChiaveCheckURL;
    }

    public void setChiaveCheckURL(String chiaveCheckURL) {
        ChiaveCheckURL = chiaveCheckURL;
    }

    public String getChiaveDLMP3() {
        return ChiaveDLMP3;
    }

    public void setChiaveDLMP3(String chiaveDLMP3) {
        ChiaveDLMP3 = chiaveDLMP3;
    }

    public String getChiaveDLText() {
        return ChiaveDLText;
    }

    public void setChiaveDLText(String chiaveDLText) {
        ChiaveDLText = chiaveDLText;
    }

    public String getChiaveDLImmagine() {
        return ChiaveDLImmagine;
    }

    public void setChiaveDLImmagine(String chiaveDLImmagine) {
        ChiaveDLImmagine = chiaveDLImmagine;
    }

    public String getChiaveDLSoap() {
        return ChiaveDLSoap;
    }

    public void setChiaveDLSoap(String chiaveDLSoap) {
        ChiaveDLSoap = chiaveDLSoap;
    } */

    public String getImmagineMostrata() {
        return ImmagineMostrata;
    }

    public void setImmagineMostrata(String immagineMostrata) {
        ImmagineMostrata = immagineMostrata;
    }

    public boolean isOrientationPortrait() {
        return OrientationPortrait;
    }

    public void setOrientationPortrait(boolean orientationPortrait) {
        OrientationPortrait = orientationPortrait;
    }

    public Integer getnOperazioneATOW() {
        return nOperazioneATOW;
    }

    /* public boolean isStaGiaAttendendo() {
        return StaGiaAttendendo;
    }

    public void setStaGiaAttendendo(boolean staGiaAttendendo) {
        StaGiaAttendendo = staGiaAttendendo;
    } */

    public void setnOperazioneATOW(Integer nOperazioneATOW) {
        this.nOperazioneATOW = nOperazioneATOW;
    }
/* public DownloadImmagine getgImmI() {
        return gImmI;
    }

    public void setgImmI(DownloadImmagine gImmI) {
        this.gImmI = gImmI;
    }

    public GestioneWEBServiceSOAP getgWSoap() {
        return gWSoap;
    }

    public void setgWSoap(GestioneWEBServiceSOAP gWSoap) {
        this.gWSoap = gWSoap;
    }

    public DownloadImmagine getgImmC() {
        return gImmC;
    }

    public void setgImmC(DownloadImmagine gImmC) {
        this.gImmC = gImmC;
    }

    public DownloadMP3 getgMP3() {
        return gMP3;
    }

    public void setgMP3(DownloadMP3 gMP3) {
        this.gMP3 = gMP3;
    }

    public DownloadTextFile getgText() {
        return gText;
    }

    public void setgText(DownloadTextFile gText) {
        this.gText = gText;
    } */

    public boolean isEseguiHandlerConMainLooper() {
        return EseguiHandlerConMainLooper;
    }

    public Drawable getRefresh() {
        return refresh;
    }

    public void setRefresh(Drawable refresh) {
        this.refresh = refresh;
    }

    public Drawable getRefresh_dis() {
        return refresh_dis;
    }

    public void setRefresh_dis(Drawable refresh_dis) {
        this.refresh_dis = refresh_dis;
    }

    public Drawable getPlay() {
        return play;
    }

    public Drawable getPlay_dis() {
        return play_dis;
    }

    public Drawable getStop() {
        return stop;
    }

    public Drawable getStop_dis() {
        return stop_dis;
    }

    public Drawable getAvanti() {
        return avanti;
    }

    public Drawable getAvanti_dis() {
        return avanti_dis;
    }

    public Drawable getIndietro() {
        return indietro;
    }

    public Drawable getIndietro_dis() {
        return indietro_dis;
    }

    public String getMessErrorePerDebugMP3() {
        return messErrorePerDebugMP3;
    }

    public String getMessErrorePerDebug() {
        return messErrorePerDebug;
    }

    /* public EsecuzioneChiamateWEB getEcw() {
        return ecw;
    }

    public void setEcw(EsecuzioneChiamateWEB ecw) {
        this.ecw = ecw;
    } */

    public boolean isStaScaricandoBrano() {
        return StaScaricandoBrano;
    }

    public void setStaScaricandoBrano(boolean staScaricandoBrano) {
        StaScaricandoBrano = staScaricandoBrano;
    }

    public boolean isEsciDaCheckFile() {
        return EsciDaCheckFile;
    }

    public void setEsciDaCheckFile(boolean esciDaCheckFile) {
        EsciDaCheckFile = esciDaCheckFile;
    }

    /* public boolean getStaAttendendoConMusichetta() {
        return StaAttendendoConMusichetta;
    }

    public void setStaAttendendoConMusichetta(boolean staAttendendoConMusichetta) {
        StaAttendendoConMusichetta = staAttendendoConMusichetta;
    } */

    public boolean getMusicaTerminata() {
        return MusicaTerminata;
    }

    public void setMusicaTerminata(boolean musicaTerminata) {
        MusicaTerminata = musicaTerminata;
    }

    public int getAttesaControlloEsistenzaMP3() {
        return AttesaControlloEsistenzaMP3;
    }

    public void setAttesaControlloEsistenzaMP3(int attesaControlloEsistenzaMP3) {
        AttesaControlloEsistenzaMP3 = attesaControlloEsistenzaMP3;
    }

    public int getTimeOutDownloadMP3() {
        return TimeOutDownloadMP3;
    }

    public void setTimeOutDownloadMP3(int timeOutDownloadMP3) {
        TimeOutDownloadMP3 = timeOutDownloadMP3;
    }

    public int getTimeOutListaBrani() {
        return TimeOutListaBrani;
    }

    public void setTimeOutListaBrani(int timeOutListaBrani) {
        TimeOutListaBrani = timeOutListaBrani;
    }

    public int getTimeOutImmagini() {
        return TimeOutImmagini;
    }

    public void setTimeOutImmagini(int timeOutImmagini) {
        TimeOutImmagini = timeOutImmagini;
    }

    public int getTipoSegnale() {
        return TipoSegnale;
    }

    public void setTipoSegnale(int tipoSegnale) {
        TipoSegnale = tipoSegnale;
    }

    /* public boolean getStaSuonandoAttesa() {
        return StaSuonandoAttesa;
    }

    public void setStaSuonandoAttesa(boolean staSuonandoAttesa) {
        StaSuonandoAttesa = staSuonandoAttesa;
    } */

    public boolean getStaScaricandoAutomaticamente() {
        return StaScaricandoAutomaticamente;
    }

    public void setStaScaricandoAutomaticamente(boolean staScaricandoAutomaticamente) {
        StaScaricandoAutomaticamente = staScaricandoAutomaticamente;
    }

    public String getRitornoCheckFileURL() {
        return RitornoCheckFileURL;
    }

    public void setRitornoCheckFileURL(String ritornoCheckFileURL) {
        RitornoCheckFileURL = ritornoCheckFileURL;
    }

    public String getsUltimaCanzoneSuonata() {
        return sUltimaCanzoneSuonata;
    }

    public void setsUltimaCanzoneSuonata(String sUltimaCanzoneSuonata) {
        this.sUltimaCanzoneSuonata = sUltimaCanzoneSuonata;
    }

    public boolean getDisegnaMascheraHomeCompleta() {
        return DisegnaMascheraHomeCompleta;
    }

    public void setDisegnaMascheraHomeCompleta(boolean disegnaMascheraHomeCompleta) {
        DisegnaMascheraHomeCompleta = disegnaMascheraHomeCompleta;
    }

    // public boolean getMessaImmagineVuota() {
    //     return MessaImmagineVuota;
    // }
//
    // public void setMessaImmagineVuota(boolean messaImmagineVuota) {
    //     MessaImmagineVuota = messaImmagineVuota;
    // }

    public String getUltimaImmagineVisualizzata() {
        return UltimaImmagineVisualizzata;
    }

    public void setUltimaImmagineVisualizzata(String ultimaImmagineVisualizzata) {
        UltimaImmagineVisualizzata = ultimaImmagineVisualizzata;
    }

    public int getBranoAutomatico() {
        return BranoAutomatico;
    }

    public void setBranoAutomatico(int branoAutomatico) {
        BranoAutomatico = branoAutomatico;
    }

    // public boolean getNonFermareDownload() {
    //     return NonFermareDownload;
    // }

    // public void setNonFermareDownload(boolean nonFermareDownload) {
    //     NonFermareDownload = nonFermareDownload;
    // }

    public boolean getHaScaricatoAutomaticamente() {
        return HaScaricatoAutomaticamente;
    }

    public void setHaScaricatoAutomaticamente(boolean haScaricatoAutomaticamente) {
        HaScaricatoAutomaticamente = haScaricatoAutomaticamente;
    }

    public int getUltimaCanzoneSuonata() {
        return UltimaCanzoneSuonata;
    }

    public void setUltimaCanzoneSuonata(int ultimaCanzoneSuonata) {
        UltimaCanzoneSuonata = ultimaCanzoneSuonata;
    }

    public String getUltimaCosaPronunciata() {
        return UltimaCosaPronunciata;
    }

    public void setUltimaCosaPronunciata(String ultimaCosaPronunciata) {
        UltimaCosaPronunciata = ultimaCosaPronunciata;
    }

    public Date getUltimaDataCaricamento() {
        return UltimaDataCaricamento;
    }

    public void setUltimaDataCaricamento(Date ultimaDataCaricamento) {
        UltimaDataCaricamento = ultimaDataCaricamento;
    }

    public boolean getHaCaricatoTuttiIDettagliDelBrano() {
        return HaCaricatoTuttiIDettagliDelBrano;
    }

    public void setHaCaricatoTuttiIDettagliDelBrano(boolean haCaricatoTuttiIDettagliDelBrano) {
        HaCaricatoTuttiIDettagliDelBrano = haCaricatoTuttiIDettagliDelBrano;
    }

    public TextView getTxtBraniInLista() {
        return txtBraniInLista;
    }

    public void setTxtBraniInLista(TextView txtBraniInLista) {
        this.txtBraniInLista = txtBraniInLista;
    }

    public TextView getTxtTraffico() {
        return txtTraffico;
    }

    public void setTxtTraffico(TextView txtTraffico) {
        this.txtTraffico = txtTraffico;
    }

    public boolean getGiaEntrato() {
        return GiaEntrato;
    }

    public void setGiaEntrato(boolean giaEntrato) {
        GiaEntrato = giaEntrato;
    }

    public boolean getCuffieInserite() {
        return CuffieInserite;
    }

    public void setCuffieInserite(boolean cuffieInserite) {
        CuffieInserite = cuffieInserite;
    }

    public GestoreCuffie getMyReceiverCuffie() {
        return myReceiverCuffie;
    }

    public void setMyReceiverCuffie(GestoreCuffie myReceiverCuffie) {
        this.myReceiverCuffie = myReceiverCuffie;
    }

    // public GestioneTastoCuffie getMyReceiverGTC() {
    //     return myReceiverGTC;
    // }
//
    // public void setMyReceiverGTC(GestioneTastoCuffie myReceiverGTC) {
    //     this.myReceiverGTC = myReceiverGTC;
    // }

    // public ImageView getIvPassaggio() {
    //     return ivPassaggio;
    // }
//
    // public void setIvPassaggio(ImageView ivPassaggio) {
    //     this.ivPassaggio = ivPassaggio;
    // }

    public Long getBytesScaricati() {
        return bytesScaricati;
    }

    public void setBytesScaricati(Long bytesScaricati) {
        this.bytesScaricati = bytesScaricati;
    }

    public Log getLog() {
        return log;
    }

    public void setL(Log l) {
        this.log = l;
    }

    public boolean getStaSuonando() {
        return StaSuonando;
    }

    public void setStaSuonando(boolean staSuonando) {
        StaSuonando = staSuonando;
    }

    /* public boolean isCaroselloBloccatoDaAutomatico() {
        return CaroselloBloccatoDaAutomatico;
    }

    public void setCaroselloBloccatoDaAutomatico(boolean caroselloBloccatoDaAutomatico) {
        CaroselloBloccatoDaAutomatico = caroselloBloccatoDaAutomatico;
    }

    public boolean getBloccaCarosello() {
        return bloccaCarosello;
    } */

    /* public void setBloccaCarosello(boolean bloccaCarosello) {
        this.bloccaCarosello = bloccaCarosello;
        NetThread.getInstance().setCaroselloBloccato(bloccaCarosello);

        if (NetThread.getInstance().isScreenOn()) {
            this.CaroselloBloccatoDaAutomatico=bloccaCarosello;
        }

        if (!bloccaCarosello) {
            GestioneImmagini.getInstance().CreaCarosello();
        } else {
            GestioneImmagini.getInstance().StoppaTimerCarosello();
        }
    } */

    public looigi.loowebplayer.dati.DatiGenerali getDatiGenerali() {
        return DatiGenerali;
    }

    public StrutturaUtenti getUtente() {
        return Utente;
    }

    public void setUtente(StrutturaUtenti utente) {
        Utente = utente;
    }

    public Window getWindowBackground() {
        return windowBackground;
    }

    public void setWindowBackground(Window windowBackground) {
        this.windowBackground = windowBackground;
    }

    public AppBarLayout getAppBar() {
        return appBar;
    }

    public void setAppBar(AppBarLayout appBar) {
        this.appBar = appBar;
    }

    public FloatingActionButton getActButtonNew() {
        return actButtonNew;
    }

    public void setActButtonNew(FloatingActionButton actButtonNew) {
        this.actButtonNew = actButtonNew;
    }

    public MenuItem getItemNuovo() {
        return itemNuovo;
    }

    public void setItemNuovo(MenuItem itemNuovo) {
        this.itemNuovo = itemNuovo;
    }

    public MenuItem getItemMultimedia() {
        return itemMultimedia;
    }

    public void setItemMultimedia(MenuItem itemMultimedia) {
        this.itemMultimedia = itemMultimedia;
    }

    public AppCompatActivity getContextPrincipale() {
        return contextPrincipale;
    }

    public void setContextPrincipale(AppCompatActivity contextPrincipale) {
        this.contextPrincipale = contextPrincipale;
    }

    public String getOperazioneInCorso() {
        return OperazioneInCorso;
    }

    public void setOperazioneInCorso(String operazioneInCorso) {
        OperazioneInCorso = operazioneInCorso;
    }

    public View getViewActivity() {
        return ViewActivity;
    }

    public void setViewActivity(View viewActivity) {
        ViewActivity = viewActivity;
    }

    public FragmentActivity getFragmentActivityPrincipale() {
        return FragmentActivityPrincipale;
    }

    public void setFragmentActivityPrincipale(FragmentActivity fragmentActivityPrincipale) {
        FragmentActivityPrincipale = fragmentActivityPrincipale;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
