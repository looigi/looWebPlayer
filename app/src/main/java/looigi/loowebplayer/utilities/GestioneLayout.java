package looigi.loowebplayer.utilities;

import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;

public class GestioneLayout {
    private static final GestioneLayout ourInstance = new GestioneLayout();

    public static GestioneLayout getInstance() {
        return ourInstance;
    }

    private GestioneLayout() {
    }

    private Animation fadeIn = new AlphaAnimation(0.1f, 1.0f);
    private Animation fadeOut = new AlphaAnimation(1.0f, 0.1f);
    private boolean StaGiaFacendo=false;

    public void AzzeraFade() {
        fadeIn.cancel();
        fadeOut.cancel();
    }

    public void SpegneLayout() {
        Handler hNascondeLayout;
        Runnable runNascondeLayout;

        hNascondeLayout = new Handler();
        hNascondeLayout.postDelayed(runNascondeLayout = new Runnable() {
            @Override
            public void run() {
                fadeOut.setDuration(3000);
                fadeOut.setStartOffset(100);

                fadeOut.setAnimationListener(new Animation.AnimationListener(){
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        VariabiliStaticheHome.getInstance().getLayIntestazione().setVisibility(LinearLayout.GONE);
                        StaGiaFacendo = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                    }

                    @Override
                    public void onAnimationStart(Animation arg0) {
                    }
                });

                VariabiliStaticheHome.getInstance().getLayIntestazione().startAnimation(fadeOut);
            }
        }, 5000);
    }

    public void ResettaLayout() {
        StaGiaFacendo = true;
        SpegneLayout();
    }

    public void VisualizzaLayout() {
        if (!StaGiaFacendo) {
            StaGiaFacendo = true;

            fadeIn.setDuration(500);
            fadeIn.setStartOffset(500);

            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    SpegneLayout();
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationStart(Animation arg0) {
                    VariabiliStaticheHome.getInstance().getLayIntestazione().setVisibility(LinearLayout.VISIBLE);
                }
            });

            VariabiliStaticheHome.getInstance().getLayIntestazione().startAnimation(fadeIn);
        }
    }
}
