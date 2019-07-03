package looigi.loowebplayer.utilities;

import android.content.Context;
import android.os.PowerManager;

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
        if (!GiaAttivo) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyApp::MyWakelockTag");
            wakeLock.acquire();
            GiaAttivo = true;
        }
    }

    public void DisattivaCPU() {
        if (wakeLock != null) {
            try {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            } catch (Exception ignored) {

            }
        }
        GiaAttivo = false;
    }
}
