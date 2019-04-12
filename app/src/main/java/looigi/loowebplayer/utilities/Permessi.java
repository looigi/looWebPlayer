package looigi.loowebplayer.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class Permessi {
    public void ControllaPermessi(Activity context) {
        int permissionRequestCode1 = 1193;

        String[] PERMISSIONS = new String[]{
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.BLUETOOTH,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WAKE_LOCK,
                // android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        };

        if(!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions(context, PERMISSIONS, permissionRequestCode1);
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

}
