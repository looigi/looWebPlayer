package looigi.loowebplayer.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;

public class DialogMessaggio
{
    //-------- Singleton ----------//
    private static DialogMessaggio instance = null;
    private String Message;
    private String TAG="DialogMessageP";
    private boolean Error;
    private String titleDialog;
    private EditText edtValore;
    private Context context;

    private DialogMessaggio() {
    }

    public static DialogMessaggio getInstance() {
        if (instance == null) instance = new DialogMessaggio();
        return instance;
    }

    //-------- Variables ----------//
    private Dialog dialog;

    //-------- Methods ----------//
    public void show(Context a, String message, boolean Error, String titleDialog)
    {
        this.Error=Error;
        this.context=a;
        this.titleDialog=titleDialog;

        Message = message;

        create(a);
    }

    private void create(Context context)
    {
        View inflate = View.inflate(context, R.layout.dialog_messaggio, null);
        TextView txtLog = inflate.findViewById(R.id.dialog_tp_log);

        txtLog.setText(Message);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(inflate);
        builder.setTitle(titleDialog);
        if (Error) {
            builder.setIcon(R.drawable.error);
            builder.setTitle(titleDialog);
        } else {
            builder.setIcon(R.drawable.completed);
            builder.setTitle(VariabiliStaticheGlobali.NomeApplicazione);
        }
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

    private OnClickListener onClickOK = new OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            dialog.cancel();
        }
    };
}
