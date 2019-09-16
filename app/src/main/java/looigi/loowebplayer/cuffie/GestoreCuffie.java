package looigi.loowebplayer.cuffie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.maschere.Home;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;

public class GestoreCuffie extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
            case 0:
                // Log.d(TAG, "Headset is unplugged");
            	/* if (VariabiliStaticheGlobali.getInstance().getCuffieInserite()==null) {
                	VariabiliStaticheGlobali.getInstance().setCuffieInserite(false);
            	} */
            	
            	if (VariabiliStaticheGlobali.getInstance().getCuffieInserite()) {
                    GestioneOggettiVideo.getInstance().PlayBrano(false);
            	}
            	
                break;
            case 1:
                // Log.d(TAG, "Headset is plugged");
				VariabiliStaticheGlobali.getInstance().setCuffieInserite(true);
                break;

            default:
                //Log.d(TAG, "I have no idea what the headset state is");
            	break;
            }
        }
    }
}
