package looigi.loowebplayer.cuffie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.maschere.Home;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;

public class GestioneTastoCuffie extends BroadcastReceiver {
    private Runnable runRiga;
    private Handler hSelezionaRiga;

    // Constructor is mandatory
    public GestioneTastoCuffie ()
    {
        super ();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event == null) {
                return;
            }

            long sec = (System.currentTimeMillis() - VariabiliStaticheGlobali.getInstance().getLastTimePressed()) / 1000;
            if (sec < 5 &&
                    VariabiliStaticheGlobali.getInstance().getLastTimePressed() >0) {
                return;
            }
            VariabiliStaticheGlobali.getInstance().setLastTimePressed(System.currentTimeMillis());

            if (event.getKeyCode() == 127) {
                Toast toast = Toast.makeText(
                        VariabiliStaticheGlobali.getInstance().getContext(),
                        "Premuto tasto cuffie looWP",
                        Toast.LENGTH_SHORT);
                toast.show();

                hSelezionaRiga = new Handler();
                hSelezionaRiga.postDelayed(runRiga=new Runnable() {
                    @Override
                    public void run() {
                        Intent it = new Intent("com.looigi.detector.scattafoto");
                        VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().sendBroadcast(it);

                        hSelezionaRiga.removeCallbacks(runRiga);
                    }
                }, 50);
            }

            /* if (event.getKeyCode() == 126) {
                if (VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                    GestioneOggettiVideo.getInstance().PlayBrano(false);
                } else {
                    GestioneOggettiVideo.getInstance().PlayBrano(true);
                }
                // Bluetooth
            	// Calendar c = Calendar.getInstance();
            	// int seconds = c.get(Calendar.SECOND);
            	// int diffe=PlayerOne.SecondoUltimoCambio-seconds;
            	// if (diffe<0) {
            	// 	diffe=-diffe;
            	// }
            	//
            	// if (diffe<3) {
            	//
            	// } else {
	    	    // 	Home.AvantiBrano();
	    	    	
	    	    // 	PlayerOne.SecondoUltimoCambio=seconds;
            	// }
            } */

            if (event.getKeyCode() == 88) {
            	// Calendar c = Calendar.getInstance();
            	// int seconds = c.get(Calendar.SECOND);
            	// int diffe=PlayerOne.SecondoUltimoCambio-seconds;
            	// if (diffe<0) {
            	// 	diffe=-diffe;
            	// }
            	//
            	// if (diffe<3) {
            	//
            	// } else {
                GestioneOggettiVideo.getInstance().AvantiBrano();

	    	    // 	PlayerOne.SecondoUltimoCambio=seconds;
            	// }
            }

            if (event.getKeyCode() == 87) {
                // Calendar c = Calendar.getInstance();
                // int seconds = c.get(Calendar.SECOND);
                // int diffe=PlayerOne.SecondoUltimoCambio-seconds;
                // if (diffe<0) {
                // 	diffe=-diffe;
                // }
                //
                // if (diffe<3) {
                //
                // } else {
                GestioneOggettiVideo.getInstance().IndietroBrano();

                // 	PlayerOne.SecondoUltimoCambio=seconds;
                // }
            }
        }
    }

}