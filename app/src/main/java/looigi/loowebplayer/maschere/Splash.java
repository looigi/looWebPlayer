package looigi.loowebplayer.maschere;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dati.NomiMaschere;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaUtenti;
import looigi.loowebplayer.db_locale.DBLocale;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.Utility;

import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.RANDOM;
import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.SEQUENZIALE;

public class Splash extends android.support.v4.app.Fragment {
    private Context context;
    private static String TAG= NomiMaschere.getInstance().getSplash();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = this.getActivity();

        View view=null;

        try {
            view=(inflater.inflate(R.layout.splash, container, false));
        } catch (Exception ignored) {

        }

        if (view!=null) {
            VariabiliStaticheGlobali.getInstance().setViewActivity(view);

            initializeGraphic();

            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    }
                    return false;
                }
            });
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        //isVisible=isVisibleToUser;

        //if (isVisible) {
        //    initializeGraphic();
        //}
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //if (isVisible) {
        //    initializeGraphic();
        //}
    }

    private Runnable runRiga;
    private Handler hSelezionaRiga;
    private boolean TastoPremuto=false;

    private void initializeGraphic() {
        final View view = VariabiliStaticheGlobali.getInstance().getViewActivity();

        if (view != null) {
            // VariabiliStaticheGlobali.getInstance().getLog().PulisceFileDiLog();

            ImageView img = (ImageView) view.findViewById(R.id.imgSplash);
            img.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Premuta immagine splash");
                    TastoPremuto=true;
                    Esce();
                }
            });

            hSelezionaRiga = new Handler();
            hSelezionaRiga.postDelayed(runRiga=new Runnable() {
                @Override
                public void run() {
                    if (!TastoPremuto) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Fine contatore attesa splash");
                        Esce();
                    }
                }
            }, 3000);
        }
    }

    private void Esce() {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Entro nell'app");
        Boolean CeUtente=false;

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ottiene utente attuale");
        DBLocale db = new DBLocale(VariabiliStaticheGlobali.getInstance().getContext());
        db.open();
        Cursor c = db.ottieniTuttiUtenti();
        if (c.moveToFirst()) {
            do {
                StrutturaUtenti s = new StrutturaUtenti();
                s.setIdUtente(c.getInt(0));
                s.setUtente(c.getString(1));
                s.setPassword(c.getString(2));
                if (c.getString(3).toUpperCase().trim().equals("S")) {
                    s.setAmministratore(true);
                } else {
                    s.setAmministratore(false);
                }
                s.setCartellaBase(c.getString(4));
                VariabiliStaticheGlobali.getInstance().setUtente(s);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setQualeCanzoneStaSuonando(c.getInt(5));

                int modi = c.getInt(6);
                switch(modi) {
                    case 0:
                        GestioneListaBrani.getInstance().setModalitaAvanzamento(RANDOM);
                        break;
                    case 1:
                        GestioneListaBrani.getInstance().setModalitaAvanzamento(SEQUENZIALE);
                        break;
                }

                CeUtente=true;
            } while (c.moveToNext());
        }
        if (c!=null) {
            c.close();
        }
        db.close();

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Vado in Home");
        VariabiliStaticheGlobali.getInstance().getContextPrincipale().getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        VariabiliStaticheGlobali.getInstance().getAppBar().setVisibility(LinearLayout.VISIBLE);
        if (CeUtente) {
            Utility.getInstance().CambiaMaschera(R.id.home);
        } else {
            Utility.getInstance().CambiaMaschera(R.id.utenza);
        }
    }
}
