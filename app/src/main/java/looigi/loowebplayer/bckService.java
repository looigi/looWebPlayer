package looigi.loowebplayer;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.LinearLayout;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaUtenti;
import looigi.loowebplayer.db_locale.DBLocaleUtenti;
import looigi.loowebplayer.utilities.EliminazioneVecchiFiles;
import looigi.loowebplayer.utilities.GestioneCPU;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.Utility;

import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.RANDOM;
import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.SEQUENZIALE;

public class bckService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass()
                        .getEnclosingMethod().getName(),
                "Entro nell'app");
        boolean CeUtente=false;

        if (VariabiliStaticheGlobali.getInstance().getContext()!=null) {
            VariabiliStaticheGlobali.getInstance().setContext(this);
        }

        GestioneCPU.getInstance().ImpostaValori(this);
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(),
                "Ottiene utente attuale");
        DBLocaleUtenti db = new DBLocaleUtenti();
        db.CreazioneTabellaUtenti();

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
        // db.close();

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
        }.getClass().getEnclosingMethod().getName(), "Vado in Home");
        VariabiliStaticheGlobali.getInstance().getContextPrincipale().getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        VariabiliStaticheGlobali.getInstance().getAppBar().setVisibility(LinearLayout.VISIBLE);
        if (CeUtente) {
            Utility.getInstance().CambiaMaschera(R.id.home);

            // Elimina vecchi files in base ai valori delle impostazioni
            EliminazioneVecchiFiles e = new EliminazioneVecchiFiles();
            e.EliminaFilesSeMaggioriAlNumeroImpostato();
            // Elimina vecchi files in base ai valori delle impostazioni
        } else {
            Utility.getInstance().CambiaMaschera(R.id.utenza);
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Intent dialogIntent = new Intent(this, MainActivity.class);
        // dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(dialogIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}