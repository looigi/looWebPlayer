package looigi.loowebplayer.maschere;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.NomiMaschere;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.gif.GifImageView;
import looigi.loowebplayer.notifiche.Notifica;
import looigi.loowebplayer.thread.NetThreadNuovo;
import looigi.loowebplayer.utilities.GestioneCaricamentoBraniNuovo;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneImmagini;
import looigi.loowebplayer.utilities.GestioneImpostazioneBrani;
import looigi.loowebplayer.utilities.GestioneLayout;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.GestioneTesti;
import looigi.loowebplayer.utilities.RiempieListaInBackground;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class Home extends android.support.v4.app.Fragment {
    // private Context context;
    private static String TAG = NomiMaschere.getInstance().getHome();
    private Boolean TestoMostrato=false;
    private boolean haCliccatoSuBackground=false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = this.getActivity();

        View view=null;

        try {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
                view=(inflater.inflate(R.layout.home_oriz, container, false));
                VariabiliStaticheGlobali.getInstance().setOrientationPortrait(false);
            } else {
                // In portrait
                view=(inflater.inflate(R.layout.home_vert, container, false));
                VariabiliStaticheGlobali.getInstance().setOrientationPortrait(true);
            }
        } catch (Exception ignored) {
            int e=0;
        }

        VariabiliStaticheGlobali.getInstance().setViewActivity(view);

        if (view!=null && !VariabiliStaticheGlobali.getInstance().getGiaEntrato()) {
            VariabiliStaticheGlobali.getInstance().setDisegnaMascheraHomeCompleta(true);
            initializeGraphic();
        } else {
            if (view!=null) {
                VariabiliStaticheGlobali.getInstance().setDisegnaMascheraHomeCompleta(false);
                initializeGraphic();
                VariabiliStaticheGlobali.getInstance().setDisegnaMascheraHomeCompleta(true);
            }
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void initializeGraphic() {
        Context context = VariabiliStaticheGlobali.getInstance().getContext();
        VariabiliStaticheHome.getInstance().setContext(context);
        View view = VariabiliStaticheGlobali.getInstance().getViewActivity();

        if (view != null) {
            final VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Entrato in Home");

            Notifica.getInstance().setContext(context);
            Notifica.getInstance().setIcona(R.drawable.ic_launcher);
            Notifica.getInstance().setTitolo("");

            Notifica.getInstance().CreaNotifica();

            vh.setImgAvanti((ImageView) view.findViewById(R.id.imgAvanti));
            vh.setImgIndietro((ImageView) view.findViewById(R.id.imgIndietro));
            vh.setImgPlay((ImageView) view.findViewById(R.id.imgPlay));
            vh.setImgStop((ImageView) view.findViewById(R.id.imgStop));
            GestioneImmagini.getInstance().setImgBrano((PhotoView) view.findViewById(R.id.imgBrano));
            vh.setGifView((GifImageView) view.findViewById(R.id.GifImageView));
            vh.setImgBackground((ImageView) view.findViewById(R.id.imgBackground));
            vh.getImgBackground().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // if (!NetThread.getInstance().isScreenOn() && VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                    //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Riattivato carosello con tasto background");
                    //     NetThread.getInstance().SbloccoCarosello();
                    // }

                    if (!haCliccatoSuBackground) {
                        haCliccatoSuBackground = true;
                        vh.getTxtTitoloBackground().setVisibility(LinearLayout.VISIBLE);

                        Runnable runEliminaTxt;
                        Handler hEliminaTxt;

                        hEliminaTxt = new Handler();
                        hEliminaTxt.postDelayed(runEliminaTxt = new Runnable() {
                            @Override
                            public void run() {
                                vh.getTxtTitoloBackground().setVisibility(LinearLayout.GONE);
                                haCliccatoSuBackground = false;
                            }
                        }, 3000);
                    }
                }
            });

            // vh.setImgLoadBrano((ImageView) view.findViewById(R.id.imgLoadBrano));

            vh.getGifView().setVisibility(LinearLayout.GONE);
            vh.getImgBackground().setVisibility(LinearLayout.GONE);
            // vh.getImgLoadBrano().setVisibility(LinearLayout.GONE);

            // PhotoViewAttacher photoAttacher;
            // photoAttacher = new PhotoViewAttacher(vh.getImgBrano());
            // photoAttacher.update();

            vh.setTxtBrano((TextView) view.findViewById(R.id.txtBrano));
            vh.setTxtAlbum((TextView) view.findViewById(R.id.txtAlbum));
            vh.setTxtFiltro((TextView) view.findViewById(R.id.txtFiltro));
            vh.setTxtArtista((TextView) view.findViewById(R.id.txtArtista));
            vh.setSeekBar1((SeekBar) view.findViewById(R.id.seekBar1));
            vh.setTxtMin((TextView) view.findViewById(R.id.txtMin));
            vh.setTxtMax((TextView) view.findViewById(R.id.txtMax));
            vh.setTxtMembri((TextView) view.findViewById(R.id.txtMembri));
            vh.setTxtMembriTitolo((LinearLayout) view.findViewById(R.id.layMembri));
            // vh.setTxtTraffico((TextView) view.findViewById(R.id.txtTraffico));
            vh.setTxtAscoltata((TextView) view.findViewById(R.id.txtAscoltata));
            vh.setLayHome((LinearLayout) view.findViewById(R.id.layTesto));
            vh.setLayIntestazione((LinearLayout) view.findViewById(R.id.layIntestazione));
            vh.setImgLinguettaTesto((ImageView) view.findViewById(R.id.imgLinguettaTesto));
            vh.setTxtTesto((TextView) view.findViewById(R.id.txTesto));
            vh.setImgStella1((ImageView) view.findViewById(R.id.imgStella1));
            vh.setImgStella2((ImageView) view.findViewById(R.id.imgStella2));
            vh.setImgStella3((ImageView) view.findViewById(R.id.imgStella3));
            vh.setImgStella4((ImageView) view.findViewById(R.id.imgStella4));
            vh.setImgStella5((ImageView) view.findViewById(R.id.imgStella5));
            vh.setImgStella6((ImageView) view.findViewById(R.id.imgStella6));
            vh.setImgStella7((ImageView) view.findViewById(R.id.imgStella7));
            vh.setImgOffline((ImageView) view.findViewById(R.id.imgoffline));
            vh.setLayOperazionWEB((RelativeLayout) view.findViewById(R.id.layOperazioneWEB));
            vh.setTxtOperazioneWEB((TextView) view.findViewById(R.id.txtOperazioneWEB));
            vh.setpMP3((ProgressBar) view.findViewById(R.id.pbarMP3));
            vh.setTxtTitoloBackground((TextView) view.findViewById(R.id.txtTitoloBackground));
            vh.setLayStelle((LinearLayout) view.findViewById(R.id.layStelle));

            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getVisualizzaBellezza()) {
                vh.getLayStelle().setVisibility(LinearLayout.VISIBLE);
            } else {
                vh.getLayStelle().setVisibility(LinearLayout.GONE);
            }
            // vh.getLayOperazionWEB().setVisibility(LinearLayout.GONE);
            // vh.getTxtOperazioneWEB().setText("");

            vh.getImgOffline().setVisibility(LinearLayout.GONE);

            vh.getTxtMembri().setVisibility(LinearLayout.GONE);
            vh.getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
            vh.getTxtTitoloBackground().setVisibility(LinearLayout.GONE);

            vh.getpMP3().setVisibility(LinearLayout.GONE);
            vh.getImgLinguettaTesto().setVisibility(LinearLayout.GONE);

            if (VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
                vh.setMediaPlayer(new MediaPlayer());
                VariabiliStaticheGlobali.getInstance().setStaSuonando(false);

                vh.getTxtMembri().setText("");
                vh.getTxtTesto().setText("");
                vh.getTxtAscoltata().setText("");
                VariabiliStaticheGlobali.getInstance().setBytesScaricati(0L);
            } else {
                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
            }

            // RelativeLayout layTraffico = view.findViewById(R.id.layTraffico);
            // if (!VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getVisualizzaTraffico()) {
            //     layTraffico.setVisibility(LinearLayout.GONE);
            // }
            Traffico.getInstance().ModificaTraffico(VariabiliStaticheGlobali.getInstance().getBytesScaricati());

            if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getMascheraTestoAperta()) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Apro maschera testo per config");
                TestoMostrato=true;
                vh.getLayHome().setVisibility(LinearLayout.VISIBLE);
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Chiudo maschera testo per config");
                TestoMostrato=false;
                vh.getLayHome().setVisibility(LinearLayout.GONE);
            }

            ImageView imgInglese = view.findViewById(R.id.imgInglese);
            imgInglese.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // if (!NetThread.getInstance().isScreenOn() && VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                    //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Riattivato carosello con tasto Inglese");
                    //     NetThread.getInstance().SbloccoCarosello();
                    // }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Impostata lingua inglese");
                    String sTesto="";

                    int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
                    if (NumeroBrano>-1) {
                        String Testo = "";
                        Testo = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati().get(NumeroBrano).getTesto();
                        sTesto = Testo.replace("**A CAPO**", "\n");
                        sTesto = sTesto.replace("%20", " ");
                        sTesto = sTesto.replace("**PV**", ";");

                        VariabiliStaticheHome.getInstance().getTxtTesto().setText(sTesto);
                    } else {
                        VariabiliStaticheHome.getInstance().getTxtTesto().setText("");
                    }
                }
            });

            ImageView imgItaliano = view.findViewById(R.id.imgItaliano);
            imgItaliano.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // if (!NetThread.getInstance().isScreenOn() && VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                    //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Riattivato carosello con tasto Italiano");
                    //     NetThread.getInstance().SbloccoCarosello();
                    // }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Impostata lingua italiana");
                    String sTesto="";
                    int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
                    if (NumeroBrano>-1) {
                        String Testo = "";
                        Testo = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati().get(NumeroBrano).getTestoTradotto();
                        sTesto = Testo.replace("**A CAPO**", "\n");
                        sTesto = sTesto.replace("%20", " ");
                        sTesto = sTesto.replace("**PV**", ";");

                        VariabiliStaticheHome.getInstance().getTxtTesto().setText(sTesto);
                    } else {
                        VariabiliStaticheHome.getInstance().getTxtTesto().setText("");
                    }
                }
            });

            GestioneImmagini.getInstance().getImgBrano().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GestioneLayout.getInstance().VisualizzaLayout();
                }
            });

            if (!VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
                GestioneLayout.getInstance().ResettaLayout();
            }

            vh.getImgLinguettaTesto().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // if (!NetThread.getInstance().isScreenOn() && VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                    //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Riattivato carosello con tasto Linguetta Testo");
                    //     NetThread.getInstance().SbloccoCarosello();
                    // }

                    if (!TestoMostrato) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Apro maschera testo");
                        TestoMostrato=true;
                        VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setMascheraTestoAperta(true);
                        vh.getLayHome().setVisibility(LinearLayout.VISIBLE);
                    } else {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Chiudo maschera testo");
                        TestoMostrato=false;
                        VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setMascheraTestoAperta(false);
                        vh.getLayHome().setVisibility(LinearLayout.GONE);
                    }
                }
            });

            vh.getImgAvanti().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GestioneOggettiVideo.getInstance().AvantiBrano();
                }
            });

            vh.getImgIndietro().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GestioneOggettiVideo.getInstance().IndietroBrano();
                }
            });

            vh.getImgPlay().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GestioneOggettiVideo.getInstance().PlayBrano(true);
                }
            });

            vh.getImgStop().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GestioneOggettiVideo.getInstance().PlayBrano(false);
                }
            });

            if (VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
                GestioneOggettiVideo.getInstance().AccendeSpegneTastiAvantiIndietro(true);
                if (GestioneFiles.getInstance().fileExistsInSD("Lista.dat", VariabiliStaticheGlobali.getInstance().PercorsoDIR)) {
                    if (!VariabiliStaticheGlobali.getInstance().getDatiGenerali().getValoriCaricati()) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Riempio strutture per valori non caricati");
                        RiempieListaInBackground r = new RiempieListaInBackground();
                        r.RiempieStrutture();
                    } else {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Carica brano");
                        GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano();
                    }
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), "Interpello il ws per la lista brani");
                    int NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download Lista Brani");
                    DBRemotoNuovo dbr = new DBRemotoNuovo();
                    dbr.RitornaListaBrani(context, "", "", "", "", "S", "N", NumeroOperazione);
                }
            } else {
                // Rientrato nella maschera o cambiato orientamento
                int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();

                VariabiliStaticheGlobali.getInstance().getAppBar().setVisibility(LinearLayout.VISIBLE);
                VariabiliStaticheHome.getInstance().getLayOperazionWEB().setVisibility(LinearLayout.GONE);

                String Mp3 = GestioneCaricamentoBraniNuovo.getInstance().RitornaNomeBrano();
                String Durata = GestioneImpostazioneBrani.getInstance().setDurata(Mp3);
                String d[] = Durata.split(";", -1);
                String minutes=d[0];
                String seconds=d[1];

                vh.getTxtMax().setText(minutes + ":" + seconds);
                vh.getSeekBar1().setMax(vh.getMediaPlayer().getDuration());

                StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
                String sNomeBrano = s.getNomeBrano();
                String Traccia = "";
                if (sNomeBrano.contains("-")) {
                    String A[] = sNomeBrano.split("-");
                    if (!A[0].isEmpty() && !A[0].equals("00")) {
                        Traccia = "\nTraccia " + A[0].trim();
                    }
                    sNomeBrano = A[1].trim();
                }
                if (sNomeBrano.contains(".")) {
                    sNomeBrano = sNomeBrano.substring(0, sNomeBrano.indexOf("."));
                }
                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                String sAlbum = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
                if (sAlbum.contains("-")) {
                    String A[] = sAlbum.split("-");
                    if (A.length > 1) {
                        if (!A[0].isEmpty() && !A[0].equals("0000")) {
                            sAlbum = A[1] + " (Anno " + A[0] + ")";
                        } else {
                            sAlbum = A[1];
                        }
                    } else {
                        sAlbum = "";
                    }
                }
                sAlbum = Traccia + "\nAlbum: " + sAlbum;
                vh.getTxtBrano().setText(sNomeBrano);
                vh.getTxtArtista().setText(Artista);
                vh.getTxtAlbum().setText(sAlbum);
                vh.getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
                vh.getTxtMembri().setText("");

                GestioneImmagini.getInstance().ImpostaUltimaImmagine(true);
                GestioneImmagini.getInstance().CreaCarosello();

                String PathFile = VariabiliStaticheGlobali.getInstance().getImmagineMostrata();
                GestioneImmagini.getInstance().ImpostaImmagineDiSfondo(PathFile, "IMMAGINE", -1, null);
                GestioneImmagini.getInstance().SettaImmagineSuIntestazione(PathFile);

                GestioneOggettiVideo.getInstance().ImpostaStelleAscoltata();
                GestioneTesti gt = new GestioneTesti();
                gt.SettaTesto(false);

                if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getMembri()) {
                    vh.getTxtMembriTitolo().setVisibility(LinearLayout.VISIBLE);
                    vh.getTxtMembri().setVisibility(LinearLayout.VISIBLE);

                    int idArtista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).getIdArtista();
                    vh.getGm().setMembri(VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getMembri());
                    vh.getGm().setTxtCasellaTesto(vh.getTxtMembri());
                    vh.getGm().CominciaAGirare();
                }
                // Rientrato nella maschera o cambiato orientamento
            }

            GestioneOggettiVideo.getInstance().SchermoAccesoSpento();
            GestioneOggettiVideo.getInstance().ScriveFiltro();
            Traffico.getInstance().LeggeTrafficoDiOggi();

            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // moveTaskToBack(true);
                    if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    }
                    return false;
                }
            });

            NetThreadNuovo.getInstance().start();

            VariabiliStaticheGlobali.getInstance().setGiaEntrato(true);
        }
    }
}
