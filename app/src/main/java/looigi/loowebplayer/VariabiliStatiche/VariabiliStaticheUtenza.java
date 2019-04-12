package looigi.loowebplayer.VariabiliStatiche;

import android.widget.EditText;
import android.widget.TextView;

public class VariabiliStaticheUtenza {
    //-------- Singleton ----------//
    private static VariabiliStaticheUtenza instance = null;

    private VariabiliStaticheUtenza() {
    }

    public static VariabiliStaticheUtenza getInstance() {
        if (instance == null) {
            instance = new VariabiliStaticheUtenza();
        }

        return instance;
    }

    private EditText edtUtente;
    private EditText edtPassword;
    private TextView txtErrore;

    public TextView getTxtErrore() {
        return txtErrore;
    }

    public void setTxtErrore(TextView txtErrore) {
        this.txtErrore = txtErrore;
    }

    public EditText getEdtUtente() {
        return edtUtente;
    }

    public void setEdtUtente(EditText edtUtente) {
        this.edtUtente = edtUtente;
    }

    public EditText getEdtPassword() {
        return edtPassword;
    }

    public void setEdtPassword(EditText edtPassword) {
        this.edtPassword = edtPassword;
    }
}
