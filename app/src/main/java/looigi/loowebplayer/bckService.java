package looigi.loowebplayer;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.LinearLayout;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaUtenti;
import looigi.loowebplayer.db_locale.DBLocale;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.Utility;

import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.RANDOM;
import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.SEQUENZIALE;

public class bckService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                "Entro nell'app");
        boolean CeUtente=false;

        if (VariabiliStaticheGlobali.getInstance().getContext()!=null) {
            VariabiliStaticheGlobali.getInstance().setContext(this);
        }

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(),
                "Ottiene utente attuale");
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
                switch (modi) {
                    case 0:
                        GestioneListaBrani.getInstance().setModalitaAvanzamento(RANDOM);
                        break;
                    case 1:
                        GestioneListaBrani.getInstance().setModalitaAvanzamento(SEQUENZIALE);
                        break;
                }

                CeUtente = true;
            } while (c.moveToNext());
        }
        if (c != null) {
            c.close();
        }
        db.close();

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
        }.getClass().getEnclosingMethod().getName(), "Vado in Home");
        VariabiliStaticheGlobali.getInstance().getContextPrincipale().getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        VariabiliStaticheGlobali.getInstance().getAppBar().setVisibility(LinearLayout.VISIBLE);
        if (CeUtente) {
            Utility.getInstance().CambiaMaschera(R.id.home);
        } else {
            Utility.getInstance().CambiaMaschera(R.id.utenza);
        }

    return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}