package looigi.loowebplayer.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import looigi.loowebplayer.MainActivity;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;

public class MemoryBoss implements ComponentCallbacks2 {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("MemoryBoss");;
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(final int level) {
        /* if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            if (VariabiliStaticheGlobali.getInstance().getLog() != null) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Richiamata funzione onTrimMemory");
            }
        } */
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE) {
            if (VariabiliStaticheGlobali.getInstance().getLog()!=null) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass()
                                .getEnclosingMethod().getName(),
                        "Memory Boss. Esco dall'applicazione");
            }
            /* Context context = VariabiliStaticheGlobali.getInstance().getContext();
            Intent mStartActivity = new Intent(context, MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent); */
            System.exit(0);
        }

        // you might as well implement some memory cleanup here and be a nice Android dev.
    }
}