package looigi.loowebplayer.maschere;

import android.content.Context;
import android.content.Intent;
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
import looigi.loowebplayer.bckService;
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
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Premuta immagine splash");
                    TastoPremuto=true;
                    Esce();
                }
            });

            hSelezionaRiga = new Handler();
            hSelezionaRiga.postDelayed(runRiga=new Runnable() {
                @Override
                public void run() {
                    if (!TastoPremuto) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                                "Fine contatore attesa splash");
                        Esce();
                    }
                }
            }, 3000);
        }
    }

    private void Esce() {
        Intent i= new Intent(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(), bckService.class);
        VariabiliStaticheGlobali.getInstance().setiServizio(i);
        // i.putExtra("KEY1", "Value to be used by the service");
        VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().startService(
                VariabiliStaticheGlobali.getInstance().getiServizio());
    }
}
