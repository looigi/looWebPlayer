package looigi.loowebplayer.db_locale;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAscoltate;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBellezza;

import static android.content.Context.MODE_PRIVATE;

public class db_dati {
    private String PathDB = VariabiliStaticheGlobali.getInstance().PercorsoDIR +"/DB/";
    private String NomeDB = "dati.db";

    public db_dati() {
        File f = new File(PathDB);
        try {
            f.mkdirs();
        } catch (Exception ignored) {

        }
    }

    private SQLiteDatabase ApreDB() {
        return  VariabiliStaticheGlobali.getInstance().getContext().openOrCreateDatabase(
                PathDB + NomeDB, MODE_PRIVATE, null);
    }

    public void CreazioneTabellaAscoltate() {
        try {
            SQLiteDatabase myDB = ApreDB();

            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "Ascoltate "
                    + " (Path VARCHAR, idCanzone INT(5), Volte INT(3));");
        } catch (Exception ignored) {

        }
    }

    public void CreazioneTabellaBellezza() {
        try {
            SQLiteDatabase myDB = ApreDB();

            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "Bellezza "
                    + " (Path VARCHAR, idCanzone INT(5), Bellezza INT(1));");
        } catch (Exception ignored) {

        }
    }

    public List<StrutturaBellezza> RitornaTuttiDatiBellezza() {
        if (VariabiliStaticheGlobali.getInstance().getUtente()==null) {
            return null;
        }

        List<StrutturaBellezza> l = new ArrayList<>();
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();

        SQLiteDatabase myDB = ApreDB();

        Cursor c = myDB.rawQuery("SELECT * FROM Bellezza WHERE Path = ?" , new String[] {pathBase});
        c.moveToFirst();
        if (c != null) {
            do {
                StrutturaBellezza sb = new StrutturaBellezza();
                sb.setIdCanzone(c.getInt(c.getColumnIndex("idCanzone")));
                sb.setBellezza(c.getInt(c.getColumnIndex("Bellezza")));

                l.add(sb);
            } while(c.moveToNext());
        }
        c.close();

        return l;
    }

    public List<StrutturaAscoltate> RitornaTuttiDatiAscoltate() {
        if (VariabiliStaticheGlobali.getInstance().getUtente()==null) {
            return null;
        }

        List<StrutturaAscoltate> l = new ArrayList<>();
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();

        SQLiteDatabase myDB = ApreDB();

        Cursor c = myDB.rawQuery("SELECT * FROM Ascoltate WHERE Path = ?" , new String[] {pathBase});
        c.moveToFirst();
        if (c.getCount() > 0) {
            do {
                StrutturaAscoltate sb = new StrutturaAscoltate();
                sb.setIdCanzone(c.getInt(c.getColumnIndex("idCanzone")));
                sb.setQuante(c.getInt(c.getColumnIndex("Quante")));

                l.add(sb);
            } while(c.moveToNext());
        }
        c.close();

        return l;
    }

    public boolean ScriveBellezza(String idCanzone, String Bellezza) {
        if (VariabiliStaticheGlobali.getInstance().getUtente()==null) {
            return false;
        }

        SQLiteDatabase myDB = ApreDB();

        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        boolean Ok = true;
        int iBellezza = -1;

        Cursor c = myDB.rawQuery("SELECT * FROM Bellezza WHERE Path = ? AND idCanzone = ?" , new String[] {pathBase, idCanzone});
        c.moveToFirst();
        if (c.getCount() > 0) {
            iBellezza = c.getInt(c.getColumnIndex("Bellezza"));
        }
        c.close();

        if (iBellezza == -1) {
            try {
                myDB.execSQL("INSERT INTO"
                        + " Bellezza"
                        + " (Path, idCanzone, Bellezza)"
                        + " VALUES ('" + pathBase + "', " + idCanzone + ", " + Bellezza + ");");
            } catch (SQLException e) {
                Ok = false;
            }
        } else {
            try {
                myDB.execSQL("UPDATE"
                        + " Bellezza"
                        + " Set Bellezza=" + Bellezza
                        + " Where Path = '" + pathBase + "' And idCanzone = " + idCanzone + ";");
            } catch (SQLException e) {
                Ok = false;
            }
        }

        return Ok;
    }

    public boolean ScriveAscoltate(String idCanzone) {
        if (VariabiliStaticheGlobali.getInstance().getUtente()==null) {
            return false;
        }

        SQLiteDatabase myDB = ApreDB();

        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        boolean Ok = true;
        int iQuante = -1;

        Cursor c = myDB.rawQuery("SELECT * FROM Ascoltate WHERE Path = ? AND idCanzone = ?" , new String[] {pathBase, idCanzone});
        c.moveToFirst();
        if (c.getCount() > 0) {
            iQuante = c.getInt(c.getColumnIndex("Quante"));
        }
        c.close();

        if (iQuante == -1) {
            try {
                myDB.execSQL("INSERT INTO"
                        + " Ascoltate"
                        + " (Path, idCanzone, Quante)"
                        + " VALUES ('" + pathBase + "', " + idCanzone + ", 1);");
            } catch (SQLException e) {
                Ok = false;
            }
        } else {
            try {
                myDB.execSQL("UPDATE"
                        + " Ascoltate"
                        + " Set Quante = Quante + 1"
                        + " Where Path = '" + pathBase + "' And idCanzone = " + idCanzone + ";");
            } catch (SQLException e) {
                Ok = false;
            }
        }

        return Ok;
    }
}
