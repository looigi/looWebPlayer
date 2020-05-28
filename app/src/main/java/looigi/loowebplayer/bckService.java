package looigi.loowebplayer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.widget.LinearLayout;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaUtenti;
import looigi.loowebplayer.db_locale.DBLocaleEsclusi;
import looigi.loowebplayer.db_locale.DBLocaleUtenti;
import looigi.loowebplayer.utilities.EliminazioneVecchiFiles;
import looigi.loowebplayer.utilities.GestioneCPU;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.Utility;

import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.RANDOM;
import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.SEQUENZIALE;

public class bckService extends Service {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("bckService");;

    @Override
    public void onCreate() {
        super.onCreate();

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass()
                        .getEnclosingMethod().getName(),
                "onCreate in bckService");

        int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, new Notification.Builder(this).build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass()
                        .getEnclosingMethod().getName(),
                "Entro in bckService");
        boolean CeUtente=false;

        if (VariabiliStaticheGlobali.getInstance().getContext()==null) {
            if (VariabiliStaticheGlobali.getInstance().getLog()!=null) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass()
                                .getEnclosingMethod().getName(),
                        "Context vuoto. Esco dall'applicazione");
            }
            // VariabiliStaticheGlobali.getInstance().setContext(this);
            /* Context context = this;
            Intent mStartActivity = new Intent(context, MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent); */
            System.exit(0);
        } else {
            VariabiliStaticheGlobali.getInstance().setContext(this);
        }

        GestioneCPU.getInstance().ImpostaValori(this);
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
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

        DBLocaleEsclusi dbe = new DBLocaleEsclusi();
        dbe.CreazioneTabellaEsclusione();

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
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

        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
        }.getClass().getEnclosingMethod().getName(), "Destroy bckservice");
        // Intent dialogIntent = new Intent(this, MainActivity.class);
        // dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(dialogIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}