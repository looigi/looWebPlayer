package looigi.loowebplayer.chiamate;

import java.util.Date;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;

public class CallReceiver extends GestioneChiamate {
	private boolean StaS = false;
	
    @Override
    protected void onIncomingCallStarted(String number, Date start) {
    	StaS=VariabiliStaticheGlobali.getInstance().getStaSuonando();
    	// if (StaS==null) {
    	// 	StaS=false;
    	// }
    	if (StaS) {
			GestioneOggettiVideo.getInstance().PlayBrano(false);
    	}
    }

    @Override
    protected void onOutgoingCallStarted(String number, Date start) {
		StaS=VariabiliStaticheGlobali.getInstance().getStaSuonando();
		// if (StaS==null) {
		// 	StaS=false;
		// }
		if (StaS) {
			GestioneOggettiVideo.getInstance().PlayBrano(false);
		}
    }

    @Override
    protected void onIncomingCallEnded(String number, Date start, Date end) {
    	// if (StaS==null) {
    	// 	StaS=false;
    	// }
    	if (StaS) {
			GestioneOggettiVideo.getInstance().PlayBrano(true);
    	}
    }

    @Override
    protected void onOutgoingCallEnded(String number, Date start, Date end) {
    	// if (StaS==null) {
    	// 	StaS=false;
    	// }
    	if (StaS) {
			GestioneOggettiVideo.getInstance().PlayBrano(true);
    	}
    }

    @Override
    protected void onMissedCall(String number, Date start) {
    	// if (StaS==null) {
    	// 	StaS=false;
    	// }
    	if (StaS) {
			GestioneOggettiVideo.getInstance().PlayBrano(true);
    	}
    }
}