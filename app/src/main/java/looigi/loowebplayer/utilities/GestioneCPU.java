package looigi.loowebplayer.utilities;

import android.content.Context;
import android.os.PowerManager;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;

import static android.content.Context.POWER_SERVICE;

public class GestioneCPU {
    private Context ctx;
    private static final GestioneCPU ourInstance = new GestioneCPU();
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private boolean GiaAttivo;

    public static GestioneCPU getInstance() {
        return ourInstance;
    }

    private GestioneCPU() {
    }

    public void ImpostaValori(Context ctx) {
        this.ctx = ctx;
        powerManager = (PowerManager) ctx.getSystemService(POWER_SERVICE);
        GiaAttivo = false;
    }

    public void AttivaCPU() {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(),
                "Attiva CPU");

        if (!GiaAttivo) {
            if (ctx == null) {
                ctx = VariabiliStaticheGlobali.getInstance().getContext();
            }
            if (powerManager == null && ctx != null) {
                powerManager = (PowerManager) ctx.getSystemService(POWER_SERVICE);
            }
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "MyApp::MyWakelockTag");
                wakeLock.acquire(60000);
                GiaAttivo = true;

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "Attivata");
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "NON Attivata");
            }
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Già Attivata");
        }
    }

    public void DisattivaCPU() {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(),
                "Interrompo CPU");

        if (wakeLock != null) {
            try {
                if (wakeLock.isHeld()) {
                    wakeLock.release();

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                            "Interrotta");
                } else {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                            "NON Held");
                }
            } catch (Exception ignored) {
                String e = Utility.getInstance().PrendeErroreDaException(ignored);

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                        "ERRORE: " + e);
            }
        }
        GiaAttivo = false;
    }
}
