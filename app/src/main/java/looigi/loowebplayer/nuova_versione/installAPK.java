package looigi.loowebplayer.nuova_versione;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;

public class installAPK extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri myuri;
        String outapk = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/update.apk";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            myuri = Uri.parse("file://"+outapk);
        } else {
            File o = new File(outapk);
            myuri = FileProvider.getUriForFile(this,
                    this.getApplicationContext().getPackageName() + ".provider", o);
        }
        Intent promptInstall = new Intent(Intent.ACTION_VIEW).setDataAndType(myuri,
                "application/vnd.android.package-archive");
        promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(promptInstall);
    }
}
