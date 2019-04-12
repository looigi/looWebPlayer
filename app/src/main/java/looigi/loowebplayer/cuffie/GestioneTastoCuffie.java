package looigi.loowebplayer.cuffie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.maschere.Home;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;

public class GestioneTastoCuffie extends BroadcastReceiver {

    // Constructor is mandatory
    public GestioneTastoCuffie ()
    {
        super ();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent) intent .getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event == null) {
                return;
            }

            if (event.getAction()== KeyEvent.KEYCODE_MEDIA_PLAY) {
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
            }

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
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

            if (event.getAction() == KeyEvent.ACTION_UP) {
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