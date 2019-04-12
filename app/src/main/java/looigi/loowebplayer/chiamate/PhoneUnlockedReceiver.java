package looigi.loowebplayer.chiamate;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import looigi.loowebplayer.thread.NetThreadNuovo;

public class PhoneUnlockedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            // Log.d(TAG, "Phone unlocked");
            NetThreadNuovo.getInstance().setScreenOn(true);
        }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            // Log.d(TAG, "Phone locked");
            NetThreadNuovo.getInstance().setScreenOn(false);
        }
    }
}