package looigi.loowebplayer.utilities;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;

public class MemoryBoss implements ComponentCallbacks2 {
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(final int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            if (VariabiliStaticheGlobali.getInstance().getLog() != null) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Richiamata funzione onTrimMemory");
            }
        }
        // you might as well implement some memory cleanup here and be a nice Android dev.
    }
}