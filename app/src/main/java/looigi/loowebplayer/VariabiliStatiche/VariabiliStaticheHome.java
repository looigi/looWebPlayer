package looigi.loowebplayer.VariabiliStatiche;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

import looigi.loowebplayer.dati.dettaglio_dati.StrutturaImmagini;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaOperazioneWEB;
import looigi.loowebplayer.gif.GifImageView;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.GestioneMembri;

public class VariabiliStaticheHome {
    //-------- Singleton ----------//
    private static VariabiliStaticheHome instance = null;

    private VariabiliStaticheHome() {
    }

    public static VariabiliStaticheHome getInstance() {
        if (instance == null) {
            instance = new VariabiliStaticheHome();
        }

        return instance;
    }

    private Context context;
    private ImageView imgIndietro;
    private ImageView imgAvanti;
    private ImageView imgPlay;
    private ImageView imgStop;
    private TextView txtBrano;
    private TextView txtAlbum;
    private TextView txtArtista;
    private SeekBar seekBar1;
    private TextView txtMin;
    private TextView txtMax;
    private TextView txtFiltro;
    private LinearLayout layHome;
    private LinearLayout layIntestazione;
    private ImageView imgLinguettaTesto;
    private TextView txtTesto;
    private TextView txtAscoltata;
    private TextView txtOperazioneWEB;
    private TextView txtMembri;
    private LinearLayout txtMembriTitolo;
    private String BranoDaCaricare = "";
    private ImageView imgStella1;
    private ImageView imgStella2;
    private ImageView imgStella3;
    private ImageView imgStella4;
    private ImageView imgStella5;
    private ImageView imgStella6;
    private ImageView imgStella7;
    private ImageView imgOffline;
    private MediaPlayer mediaPlayer;
    private Handler HandlerSeekBar;
    private Runnable rSeekBar;
    // protected PowerManager.WakeLock mWakeLock;
    private List<StrutturaImmagini> Imms = new ArrayList<>();
    private RelativeLayout layOperazionWEB;
    private List<StrutturaOperazioneWEB> OperazioniWeb=new ArrayList<>();
    private ProgressBar pMP3;
    private GestioneMembri gm;
    private GifImageView gifView;
    private ImageView imgBackground;
    private ImageView imgRefresh;
    // private ImageView imgLoadBrano;
    private Boolean PuoAvanzare;
    private TextView txtTitoloBackground;
    private LinearLayout layStelle;

    // private Handler hCambioImmagine;
    // private Runnable rCambioImmagine;

    public Context getContext() {
        return context;
    }

    public LinearLayout getLayStelle() {
        return layStelle;
    }

    public void setLayStelle(LinearLayout layStelle) {
        this.layStelle = layStelle;
    }

    public TextView getTxtTitoloBackground() {
        return txtTitoloBackground;
    }

    public void setTxtTitoloBackground(TextView txtTitoloBackground) {
        this.txtTitoloBackground = txtTitoloBackground;
    }

    public Boolean getPuoAvanzare() {
        return PuoAvanzare;
    }

    public ImageView getImgRefresh() {
        return imgRefresh;
    }

    public void setImgRefresh(ImageView imgRefresh) {
        this.imgRefresh = imgRefresh;
    }

    public void setPuoAvanzare(Boolean puoAvanzare) {
        PuoAvanzare = puoAvanzare;
    }

    // public ImageView getImgLoadBrano() {
    //     return imgLoadBrano;
    // }
//
    // public void setImgLoadBrano(ImageView imgLoadBrano) {
    //     this.imgLoadBrano = imgLoadBrano;
    // }

    public ImageView getImgBackground() {
        return imgBackground;
    }

    public void setImgBackground(ImageView imgBackground) {
        this.imgBackground = imgBackground;
    }

    public GestioneMembri getGm() {
        return gm;
    }

    public GifImageView getGifView() {
        return gifView;
    }

    public void setGifView(GifImageView gifView) {
        this.gifView = gifView;
    }

    public void setGm(GestioneMembri gm) {
        this.gm = gm;
    }

    public LinearLayout getLayIntestazione() {
        return layIntestazione;
    }

    public void setLayIntestazione(LinearLayout layIntestazione) {
        this.layIntestazione = layIntestazione;
    }

    public ImageView getImgOffline() {
        return imgOffline;
    }

    public void setImgOffline(ImageView imgOffline) {
        this.imgOffline = imgOffline;
    }

    public LinearLayout getTxtMembriTitolo() {
        return txtMembriTitolo;
    }

    public void setTxtMembriTitolo(LinearLayout txtMembriTitolo) {
        this.txtMembriTitolo = txtMembriTitolo;
    }

    public TextView getTxtMembri() {
        return txtMembri;
    }

    public void setTxtMembri(TextView txtMembri) {
        this.txtMembri = txtMembri;
    }

    public ProgressBar getpMP3() {
        return pMP3;
    }

    public void setpMP3(ProgressBar pMP3) {
        this.pMP3 = pMP3;
    }

    /* public TextView getTxtOperazioneWEB() {
        return txtOperazioneWEB;
    } */

    public void setTxtOperazioneWEB(TextView txtOperazioneWEB) {
        this.txtOperazioneWEB = txtOperazioneWEB;
    }

    private int MaxNumeroOpWEB() {
        int i=0;

        for (StrutturaOperazioneWEB ii : OperazioniWeb) {
            if (ii.getNumeroOperazione()>i) {
                i=ii.getNumeroOperazione();
            }
        }

        return i+1;
    }

    public void ScriveOperazioniWEB() {
        String tt="";

        for (StrutturaOperazioneWEB ii : OperazioniWeb) {
            tt+=ii.getOperazione()+"\n";
        }
        // layOperazionWEB.setVisibility(LinearLayout.VISIBLE);

        String t2=tt;

        if (layOperazionWEB!=null) {
            if (t2.isEmpty()) {
                layOperazionWEB.setVisibility(LinearLayout.GONE);
            } else {
                t2=t2.substring(0, t2.length()-1);
                layOperazionWEB.setVisibility(LinearLayout.VISIBLE);
            }
        }

        if (txtOperazioneWEB!=null)
            txtOperazioneWEB.setText(t2);

        /* final String t = tt;

        hEliminaBarra = new Handler(Looper.getMainLooper());
        hEliminaBarra.postDelayed(runEliminaBarra = new Runnable() {
            @Override
            public void run() {
                String t2=t;

                if (layOperazionWEB!=null) {
                    if (t2.isEmpty()) {
                        layOperazionWEB.setVisibility(LinearLayout.GONE);
                    } else {
                        t2=t2.substring(0, t2.length()-1);
                        layOperazionWEB.setVisibility(LinearLayout.VISIBLE);
                    }
                }

                txtOperazioneWEB.setText(t2);

                hEliminaBarra.removeCallbacks(runEliminaBarra);
            }
        }, 50); */

    }

    private void EliminaRiga(int Numero) {
        int i = 0;
        int q = 0;
        for (StrutturaOperazioneWEB ii : OperazioniWeb) {
            if (ii.getNumeroOperazione()==Numero) {
                //if (!ii.getOperazione().toUpperCase().contains("COMPRESS")) {
                    OperazioniWeb.remove(i);
                    q++;
                    break;
                //}
            }
            i++;
        }
        if (i==q) {
            if (this.layOperazionWEB!=null) {
                this.layOperazionWEB.setVisibility(LinearLayout.GONE);
            }
        }

        ScriveOperazioniWEB();
    }

    public void EliminaOperazioneWEB(final int Numero, Boolean Immediato)  {
        Runnable runEliminaBarra;
        Handler hEliminaBarra = new Handler();

        if (Immediato) {
            EliminaRiga(Numero);
        } else {
            hEliminaBarra.postDelayed(runEliminaBarra = new Runnable() {
                @Override
                public void run() {
                    EliminaRiga(Numero);
                }
            }, 3000);
        }
    }

    public int AggiungeOperazioneWEB(int NumeroOperazione, Boolean Errore, String Operazione) {
        if (Operazione==null) {
            return -1;
        }

        if (Operazione!=null && Operazione.trim().isEmpty()) {
            return -1;
        }

        int n = 0;

        if (NumeroOperazione==-1) {
            n = MaxNumeroOpWEB();

            StrutturaOperazioneWEB s = new StrutturaOperazioneWEB();
            s.setNumeroOperazione(n);
            s.setOperazione(Operazione.trim());
            OperazioniWeb.add(s);

            if (this.layOperazionWEB != null) {
                this.layOperazionWEB.setVisibility(LinearLayout.VISIBLE);
            }
        } else {
            int i=0;
            boolean Ok = false;

            for (StrutturaOperazioneWEB ii : OperazioniWeb) {
                if (ii.getNumeroOperazione()==NumeroOperazione) {
                    StrutturaOperazioneWEB s = new StrutturaOperazioneWEB();
                    s.setNumeroOperazione(NumeroOperazione);
                    s.setOperazione(Operazione.trim());

                    OperazioniWeb.set(i, s);

                    n=NumeroOperazione;
                    Ok=true;
                    break;
                }
                i++;
            }

            if (!Ok) {
                n = MaxNumeroOpWEB();

                StrutturaOperazioneWEB s = new StrutturaOperazioneWEB();
                s.setNumeroOperazione(n);
                s.setOperazione(Operazione.trim());
                OperazioniWeb.add(s);
            }
        }

        ScriveOperazioniWEB();

        if (Errore) {
            EliminaOperazioneWEB(n, false);
        }

        return n;
    }

    public RelativeLayout getLayOperazionWEB() {
        return layOperazionWEB;
    }

    public void setLayOperazionWEB(RelativeLayout layOperazionWEB) {
        this.layOperazionWEB = layOperazionWEB;
    }

    public TextView getTxtFiltro() {
        return txtFiltro;
    }

    public void setTxtFiltro(TextView txtFiltro) {
        this.txtFiltro = txtFiltro;
    }

    public List<StrutturaImmagini> getImms() {
        return Imms;
    }

    public void setImms(List<StrutturaImmagini> imms) {
        Imms = imms;
    }

    public Handler getHandlerSeekBar() {
        return HandlerSeekBar;
    }

    public void setHandlerSeekBar(Handler handlerSeekBar) {
        HandlerSeekBar = handlerSeekBar;
    }

    public Runnable getrSeekBar() {
        return rSeekBar;
    }

    public void setrSeekBar(Runnable rSeekBar) {
        this.rSeekBar = rSeekBar;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public ImageView getImgStella1() {
        return imgStella1;
    }

    public void setImgStella1(ImageView imgStella1) {
        this.imgStella1 = imgStella1;
    }

    public ImageView getImgStella2() {
        return imgStella2;
    }

    public void setImgStella2(ImageView imgStella2) {
        this.imgStella2 = imgStella2;
    }

    public ImageView getImgStella3() {
        return imgStella3;
    }

    public void setImgStella3(ImageView imgStella3) {
        this.imgStella3 = imgStella3;
    }

    public ImageView getImgStella4() {
        return imgStella4;
    }

    public void setImgStella4(ImageView imgStella4) {
        this.imgStella4 = imgStella4;
    }

    public ImageView getImgStella5() {
        return imgStella5;
    }

    public void setImgStella5(ImageView imgStella5) {
        this.imgStella5 = imgStella5;
    }

    public ImageView getImgStella6() {
        return imgStella6;
    }

    public void setImgStella6(ImageView imgStella6) {
        this.imgStella6 = imgStella6;
    }

    public ImageView getImgStella7() {
        return imgStella7;
    }

    public void setImgStella7(ImageView imgStella7) {
        this.imgStella7 = imgStella7;
    }

    public TextView getTxtAscoltata() {
        return txtAscoltata;
    }

    public void setTxtAscoltata(TextView txtAscoltata) {
        this.txtAscoltata = txtAscoltata;
    }

    public String getBranoDaCaricare() {
        return BranoDaCaricare;
    }

    public void setBranoDaCaricare(String branoDaCaricare) {
        BranoDaCaricare = branoDaCaricare;
    }

    public TextView getTxtTesto() {
        return txtTesto;
    }

    public void setTxtTesto(TextView txtTesto) {
        this.txtTesto = txtTesto;
    }

    public ImageView getImgLinguettaTesto() {
        return imgLinguettaTesto;
    }

    public void setImgLinguettaTesto(ImageView imgLinguettaTesto) {
        this.imgLinguettaTesto = imgLinguettaTesto;
    }

    public LinearLayout getLayHome() {
        return layHome;
    }

    public void setLayHome(LinearLayout layHome) {
        this.layHome = layHome;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // public Handler gethCambioImmagine() {
    //     return hCambioImmagine;
    // }
//
    // public void sethCambioImmagine(Handler hCambioImmagine) {
    //     this.hCambioImmagine = hCambioImmagine;
    // }
//
    // public Runnable getrCambioImmagine() {
    //     return rCambioImmagine;
    // }
//
    // public void setrCambioImmagine(Runnable rCambioImmagine) {
    //     this.rCambioImmagine = rCambioImmagine;
    // }

    public TextView getTxtAlbum() {
        return txtAlbum;
    }

    public void setTxtAlbum(TextView txtAlbum) {
        this.txtAlbum = txtAlbum;
    }

    public TextView getTxtArtista() {
        return txtArtista;
    }

    public void setTxtArtista(TextView txtArtista) {
        this.txtArtista = txtArtista;
    }

    public TextView getTxtMin() {
        return txtMin;
    }

    public void setTxtMin(TextView txtMin) {
        this.txtMin = txtMin;
    }

    public TextView getTxtMax() {
        return txtMax;
    }

    public void setTxtMax(TextView txtMax) {
        this.txtMax = txtMax;
    }

    public SeekBar getSeekBar1() {
        return seekBar1;
    }

    public void setSeekBar1(SeekBar SeekBar1) {
        this.seekBar1 = SeekBar1;
    }

    public ImageView getImgIndietro() {
        return imgIndietro;
    }

    public void setImgIndietro(ImageView imgIndietro) {
        this.imgIndietro = imgIndietro;
    }

    public ImageView getImgAvanti() {
        return imgAvanti;
    }

    public void setImgAvanti(ImageView imgAvanti) {
        this.imgAvanti = imgAvanti;
    }

    public ImageView getImgPlay() {
        return imgPlay;
    }

    public void setImgPlay(ImageView imgPlay) {
        this.imgPlay = imgPlay;
    }

    public ImageView getImgStop() {
        return imgStop;
    }

    public void setImgStop(ImageView imgStop) {
        this.imgStop = imgStop;
    }

    public TextView getTxtBrano() {
        return txtBrano;
    }

    public void setTxtBrano(TextView txtBrano) {
        this.txtBrano = txtBrano;
    }
}
