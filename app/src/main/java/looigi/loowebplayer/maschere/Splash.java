package looigi.loowebplayer.maschere;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.bckService;
import looigi.loowebplayer.dati.NomiMaschere;

public class Splash extends android.support.v4.app.Fragment {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("Splash");;
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
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Premuta immagine splash");
                    TastoPremuto=true;
                    Esce();
                }
            });

            hSelezionaRiga = new Handler(Looper.getMainLooper());
            hSelezionaRiga.postDelayed(runRiga=new Runnable() {
                @Override
                public void run() {
                    if (!TastoPremuto) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
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
        if (VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale()!=null) {
            VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().startService(
                    VariabiliStaticheGlobali.getInstance().getiServizio());
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                    "ERROR: Fragment principale nullo");
        }

        /* if (VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale()!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(
                        VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                        VariabiliStaticheGlobali.getInstance().getiServizio());
            } else {
                VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().startService(
                        VariabiliStaticheGlobali.getInstance().getiServizio());
            }
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                    "ERROR: Fragment principale nullo");
        } */
    }
}
