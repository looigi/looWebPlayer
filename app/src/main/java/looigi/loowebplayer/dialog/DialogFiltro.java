package looigi.loowebplayer.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.maschere.Home;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.RiempieListaInBackground;
import looigi.loowebplayer.utilities.Utility;

public class DialogFiltro
{
    //-------- Singleton ----------//
    private static DialogFiltro instance = null;
    private String Message;
    private String TAG="DialogMessageF";
    private String Cosa;
    private String Tipo;
    private String titleDialog;
    private EditText edtValore;
    private Context context;
    private EditText edtBrano;

    private DialogFiltro() {
    }

    public static DialogFiltro getInstance() {
        if (instance == null) instance = new DialogFiltro();
        return instance;
    }

    //-------- Variables ----------//
    private Dialog dialog;

    //-------- Methods ----------//
    public void show(Context a, String message, String Tipo, String Cosa, String titleDialog)
    {
        this.Cosa=Cosa;
        this.Tipo=Tipo;
        this.context=a;
        this.titleDialog=titleDialog;

        Message = message;

        create(a);
    }

    private void create(Context context)
    {
        View inflate = View.inflate(context, R.layout.dialog_filtro, null);
        TextView txtLog = inflate.findViewById(R.id.dialog_filtro);
        LinearLayout layBrano = inflate.findViewById(R.id.layBrano);
        edtBrano = inflate.findViewById(R.id.edtBrano);

        if (Cosa.equals("BRANO")) {
            layBrano.setVisibility(LinearLayout.VISIBLE);
            edtBrano.setText(Cosa);
        } else {
            layBrano.setVisibility(LinearLayout.GONE);
        }

        txtLog.setText(Message);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(inflate);
        builder.setTitle(titleDialog);
        builder.setIcon(R.drawable.completed);
        builder.setTitle(VariabiliStaticheGlobali.NomeApplicazione);
        builder.setNegativeButton("Annulla", onClickAnnulla);
        builder.setNeutralButton("Elimina Filtro", onClickElimina);
        builder.setPositiveButton("Ok", onClickOK);

        VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private OnClickListener onClickAnnulla = new OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            dialog.cancel();
        }
    };

    private OnClickListener onClickElimina = new OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setFiltro("");

            GestioneOggettiVideo.getInstance().ScriveFiltro();
            RiempieListaInBackground r = new RiempieListaInBackground();
            r.RiempieStrutture();

            // Libreria.ReimpostaListe();
            VariabiliStaticheGlobali.getInstance().setGiaEntrato(true);
            Utility.getInstance().CambiaMaschera(R.id.home);

            dialog.cancel();
        }
    };

    private OnClickListener onClickOK = new OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            Boolean Ok = true;
            String f = edtBrano.getText().toString();

            if (Cosa.equals("BRANO")) {
                if (f.isEmpty()) {
                    Ok=false;
                }
            }

            if (Ok) {
                dialog.cancel();

                // ARTISTA_Pippo_§BRANO_W la pippa_§
                String Filtro = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getFiltro();
                if (Filtro.contains("§")) {
                    String Campi[] = Filtro.split("§");
                    for (String c : Campi) {
                        if (c.contains(Cosa)) {
                            c = Tipo + "_" + Cosa.replace("_","***UNDERLINE***") + "_§";
                            break;
                        }
                    }
                    Filtro = "";
                    for (String c : Campi) {
                        Filtro += c;
                    }
                    if (!Filtro.isEmpty()) {
                        Filtro += "_§";
                    }
                } else {
                    Filtro = Tipo + "_" + Cosa.replace("_","***UNDERLINE***") + "_§";
                }

                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setFiltro(Filtro);

                GestioneOggettiVideo.getInstance().ScriveFiltro();
                RiempieListaInBackground r = new RiempieListaInBackground();
                r.RiempieStrutture();

                // Libreria.ReimpostaListe();
                VariabiliStaticheGlobali.getInstance().setGiaEntrato(true);
                Utility.getInstance().CambiaMaschera(R.id.home);
            }
        }
    };
}
