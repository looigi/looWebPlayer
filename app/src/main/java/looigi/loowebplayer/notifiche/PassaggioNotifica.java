package looigi.loowebplayer.notifiche;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.Utility;

public class PassaggioNotifica extends Activity {
	private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("PassaggioNotifica");;
    private Context context;
   
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		context=this;
		String action="";

		VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "onCreate PassaggioNotifica");
		try {
			action = (String)getIntent().getExtras().get("DO");
		} catch (Exception e) {
			VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
		}

		if (action!=null) {
			boolean Chiude=true;

			switch (action) {
				case "apre":
					Utility.getInstance().CambiaMaschera(R.id.home);
					break;
				case "indietro":
					GestioneOggettiVideo.getInstance().IndietroBrano();
					break;
				case "avanti":
					GestioneOggettiVideo.getInstance().AvantiBrano();
					break;
				case "play":
					if (!VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
						GestioneOggettiVideo.getInstance().PlayBrano(true);
					} else {
						GestioneOggettiVideo.getInstance().PlayBrano(false);
					}
					break;
			}

			if (Chiude) {
				finish();
			}
		}
    }
}
