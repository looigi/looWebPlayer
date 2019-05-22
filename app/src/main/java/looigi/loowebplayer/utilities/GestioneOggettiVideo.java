package looigi.loowebplayer.utilities;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.db_locale.db_dati;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.dialog.DialogMessaggio;

public class GestioneOggettiVideo {
    //-------- Singleton ----------//
    private static GestioneOggettiVideo instance = null;

    private GestioneOggettiVideo() {
    }

    public static GestioneOggettiVideo getInstance() {
        if (instance == null) {
            instance = new GestioneOggettiVideo();
        }

        return instance;
    }

    public void ControllaAvantiBrano(int NumeroBrano, Boolean VisualizzaMessaggio) {
        if (NumeroBrano>-1) {
            if (!VariabiliStaticheGlobali.getInstance().getStaScaricandoAutomaticamente()) {
                VariabiliStaticheGlobali.getInstance().getDatiGenerali()
                        .getConfigurazione().setQualeCanzoneStaSuonando(NumeroBrano);
            } else {
                VariabiliStaticheGlobali.getInstance().setNumeroBranoNuovo(NumeroBrano);
            }

            // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(true);
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Avanti. Prossimo brano: "+Integer.toString(NumeroBrano));

            GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano();
        } else {
            if (VisualizzaMessaggio) {
                DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                        "Nessun brano in archivio", true, VariabiliStaticheGlobali.NomeApplicazione);
            }
        }
    }

    public void AvantiBrano() {
        if (VariabiliStaticheHome.getInstance().getPuoAvanzare()) {
            VariabiliStaticheHome.getInstance().getRltListaBrani().setVisibility(LinearLayout.GONE);
            int NumeroBrano;

            if (VariabiliStaticheGlobali.getInstance().getBranoImpostatoSenzaRete()>-1) {
                NumeroBrano = VariabiliStaticheGlobali.getInstance().getBranoImpostatoSenzaRete();
                VariabiliStaticheGlobali.getInstance().setBranoImpostatoSenzaRete(-1);
            } else {
                NumeroBrano = GestioneListaBrani.getInstance().RitornaNumeroProssimoBranoNuovo(true);
            }
            ControllaAvantiBrano(NumeroBrano, false);
        }
    }

    public void AccendeSpegneTastiAvantiIndietro(Boolean Accende) {
        if (Accende) {
            VariabiliStaticheHome.getInstance().getImgAvanti().setImageDrawable(VariabiliStaticheGlobali.getInstance().getAvanti());
            VariabiliStaticheHome.getInstance().getImgIndietro().setImageDrawable(VariabiliStaticheGlobali.getInstance().getIndietro());

            VariabiliStaticheHome.getInstance().getImgRefresh().setEnabled(true);
            VariabiliStaticheHome.getInstance().getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getRefresh());
            VariabiliStaticheHome.getInstance().setPuoAvanzare(true);

            if (!VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                VariabiliStaticheHome.getInstance().getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay());
                VariabiliStaticheHome.getInstance().getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop_dis());
            } else {
                VariabiliStaticheHome.getInstance().getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay_dis());
                VariabiliStaticheHome.getInstance().getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop());
            }
        } else {
            VariabiliStaticheHome.getInstance().getImgAvanti().setImageDrawable(VariabiliStaticheGlobali.getInstance().getAvanti_dis());
            VariabiliStaticheHome.getInstance().getImgIndietro().setImageDrawable(VariabiliStaticheGlobali.getInstance().getIndietro_dis());

            VariabiliStaticheHome.getInstance().getImgRefresh().setEnabled(false);
            VariabiliStaticheHome.getInstance().getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getRefresh_dis());
            VariabiliStaticheHome.getInstance().setPuoAvanzare(false);

            VariabiliStaticheHome.getInstance().getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay_dis());
            VariabiliStaticheHome.getInstance().getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop_dis());
        }
    }

    public void PlayBrano(Boolean Acceso) {
        VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

        if (vh.getPuoAvanzare()) {
            if (Acceso) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Play");
                VariabiliStaticheGlobali.getInstance().setStaSuonando(true);

                if (!GestioneCaricamentoBraniNuovo.getInstance().isHaCaricatoBrano()) {
                    vh.getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay_dis());
                    vh.getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop());
                    GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano3();
                } else {
                    vh.getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay_dis());
                    vh.getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop());
                    try {
                        vh.getMediaPlayer().start();
                    } catch (Exception e) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                        String error = Utility.getInstance().PrendeErroreDaException(e);

                        VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, true, error);
                    }
                }
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Stop");
                VariabiliStaticheGlobali.getInstance().setStaSuonando(false);

                vh.getImgPlay().setImageDrawable(VariabiliStaticheGlobali.getInstance().getPlay());
                vh.getImgStop().setImageDrawable(VariabiliStaticheGlobali.getInstance().getStop_dis());
                try {
                    vh.getMediaPlayer().pause();
                } catch (Exception e) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                    String error = Utility.getInstance().PrendeErroreDaException(e);

                    VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, true, error);
                }
            }
        }
    }

    public void IndietroBrano() {
        if (VariabiliStaticheHome.getInstance().getPuoAvanzare()) {
            VariabiliStaticheHome.getInstance().getRltListaBrani().setVisibility(LinearLayout.GONE);
            GestioneImmagini.getInstance().StoppaTimerCarosello();

            int NumeroBrano = GestioneListaBrani.getInstance().RitornaBranoPrecedente();
            if (NumeroBrano > -1) {
                VariabiliStaticheGlobali.getInstance().getDatiGenerali()
                        .getConfigurazione().setQualeCanzoneStaSuonando(NumeroBrano);

                // VariabiliStaticheGlobali.getInstance().setBloccaCarosello(true);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Indietro. Brano precedente: " + Integer.toString(NumeroBrano));
                GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano();
            }
        }
    }

    public void SpegneIconaBackground() {
        VariabiliStaticheHome.getInstance().getImgBackground().setVisibility(LinearLayout.GONE);
    }

    public void ImpostaIconaBackground(int icona) {
        if (icona==-1) {
            VariabiliStaticheHome.getInstance().getImgBackground().setVisibility(LinearLayout.GONE);
        } else {
            Drawable down = ContextCompat.getDrawable(VariabiliStaticheHome.getInstance().getContext(), icona);
            VariabiliStaticheHome.getInstance().getImgBackground().setVisibility(LinearLayout.VISIBLE);
            VariabiliStaticheHome.getInstance().getImgBackground().setImageDrawable(down);
        }
    }

    public void ScriveFiltro() {
        if (!VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getFiltro().isEmpty()) {
            VariabiliStaticheHome.getInstance().getTxtFiltro().setVisibility(LinearLayout.VISIBLE);
            String f[] = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getFiltro().split("_");
            VariabiliStaticheHome.getInstance().getTxtFiltro().setText(
                    "Filtro per " + f[0] + " (" + f[1].replace("***UNDERLINE***", "_") +")");
        } else {
            VariabiliStaticheHome.getInstance().getTxtFiltro().setVisibility(LinearLayout.GONE);
        }
    }

    public void SchermoAccesoSpento() {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SchermoAccesoSpento");
        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getSchermoSempreAcceso()) {
            VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            try {
                VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } catch (Exception e) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            }
        }
    }

    private void SettaBellezza(int Quanto) {
        int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getNumeroBranoInAscolto();

        int nn = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Modifica bellezza");

        DBRemotoNuovo dbr = new DBRemotoNuovo();
        dbr.AggiornaBellezza(Integer.toString(NumeroBrano), Integer.toString(Quanto), nn);

        db_dati db = new db_dati();
        db.ScriveBellezza(Integer.toString(NumeroBrano), Integer.toString(Quanto));
    }

    public void ImpostaStelleAscoltata() {
        int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getNumeroBranoInAscolto();
        int n = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
        if (NumeroBrano>n) {
            if (n>0) {
                NumeroBrano = 0;
            } else {
                NumeroBrano = -1;
            }
        }
        final int nBrano = NumeroBrano;

        VariabiliStaticheHome vh = VariabiliStaticheHome.getInstance();

        Drawable icona_si = ContextCompat.getDrawable(vh.getContext(), R.drawable.preferito);
        Drawable icona_no = ContextCompat.getDrawable(vh.getContext(), R.drawable.preferito_vuoto);

        vh.getImgStella1().setImageDrawable(icona_no);
        vh.getImgStella2().setImageDrawable(icona_no);
        vh.getImgStella3().setImageDrawable(icona_no);
        vh.getImgStella4().setImageDrawable(icona_no);
        vh.getImgStella5().setImageDrawable(icona_no);
        vh.getImgStella6().setImageDrawable(icona_no);
        vh.getImgStella7().setImageDrawable(icona_no);

        vh.getImgStella1().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettaBellezza(1);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(nBrano).setStelle(1);
            }
        });
        vh.getImgStella2().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettaBellezza(2);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(nBrano).setStelle(2);
            }
        });
        vh.getImgStella3().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettaBellezza(3);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(nBrano).setStelle(3);
            }
        });
        vh.getImgStella4().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettaBellezza(4);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(nBrano).setStelle(4);
            }
        });
        vh.getImgStella5().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettaBellezza(5);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(nBrano).setStelle(5);
            }
        });
        vh.getImgStella6().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettaBellezza(6);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(nBrano).setStelle(6);
            }
        });
        vh.getImgStella7().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettaBellezza(7);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(nBrano).setStelle(7);
            }
        });

        if (NumeroBrano>-1) {
            int Ascoltata = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).getQuanteVolteAscoltato();
            int Bellezza = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano).getStelle();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Imposta stelle: " + Integer.toString(Bellezza));
            vh.getTxtAscoltata().setText("Volte ascoltata: " + Integer.toString(Ascoltata));

            switch (Bellezza) {
                case 1:
                    vh.getImgStella1().setImageDrawable(icona_si);
                    break;
                case 2:
                    vh.getImgStella1().setImageDrawable(icona_si);
                    vh.getImgStella2().setImageDrawable(icona_si);
                    break;
                case 3:
                    vh.getImgStella1().setImageDrawable(icona_si);
                    vh.getImgStella2().setImageDrawable(icona_si);
                    vh.getImgStella3().setImageDrawable(icona_si);
                    break;
                case 4:
                    vh.getImgStella1().setImageDrawable(icona_si);
                    vh.getImgStella2().setImageDrawable(icona_si);
                    vh.getImgStella3().setImageDrawable(icona_si);
                    vh.getImgStella4().setImageDrawable(icona_si);
                    break;
                case 5:
                    vh.getImgStella1().setImageDrawable(icona_si);
                    vh.getImgStella2().setImageDrawable(icona_si);
                    vh.getImgStella3().setImageDrawable(icona_si);
                    vh.getImgStella4().setImageDrawable(icona_si);
                    vh.getImgStella5().setImageDrawable(icona_si);
                    break;
                case 6:
                    vh.getImgStella1().setImageDrawable(icona_si);
                    vh.getImgStella2().setImageDrawable(icona_si);
                    vh.getImgStella3().setImageDrawable(icona_si);
                    vh.getImgStella4().setImageDrawable(icona_si);
                    vh.getImgStella5().setImageDrawable(icona_si);
                    vh.getImgStella6().setImageDrawable(icona_si);
                    break;
                case 7:
                    vh.getImgStella1().setImageDrawable(icona_si);
                    vh.getImgStella2().setImageDrawable(icona_si);
                    vh.getImgStella3().setImageDrawable(icona_si);
                    vh.getImgStella4().setImageDrawable(icona_si);
                    vh.getImgStella5().setImageDrawable(icona_si);
                    vh.getImgStella6().setImageDrawable(icona_si);
                    vh.getImgStella7().setImageDrawable(icona_si);
                    break;
            }
        }
    }
}
