package looigi.loowebplayer.nuova_versione;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.BuildConfig;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dialog.DialogMessaggio;

public class UpdateApp extends AsyncTask<String,Void,Void> {
    private ProgressDialog progressDialog;
    private String messErrore="";
    private Context context;

    public void setContext(Context contextf) {
        context = contextf;
    }

    private void ChiudeDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception ignored) {
        }
//
        if (!messErrore.isEmpty()) {
            VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().runOnUiThread(new Runnable() {
                public void run() {
                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                            "ERRORE: " + messErrore,
                            true,
                            VariabiliStaticheGlobali.NomeApplicazione);
                }
            });
        }
    }

    private void ApriDialog() {
        VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Attendere Prego...\nControllo versione in corso...");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                } catch (Exception ignored) {
////
                }
            }
        });
    }

    @Override
    protected Void doInBackground(String... arg0) {
        ApriDialog();

        VariabiliStaticheGlobali.getInstance().setStaAggiornandoLaVersione(true);
        File outputFile = null;

        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();

            String pathDestinazione = VariabiliStaticheGlobali.getInstance().PercorsoDIR;
            File file = new File(pathDestinazione);
            file.mkdirs();
            outputFile = new File(file, "update.apk");
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
//
            InputStream is = c.getInputStream();
//
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(VariabiliStaticheGlobali.getInstance().getContext(), installAPK.class);
            VariabiliStaticheGlobali.getInstance().getContext().startActivity(intent);

            messErrore="";
        } catch (Exception ignored) {
            messErrore=ignored.getMessage();
        }

        ChiudeDialog();

        return null;
    }
}
