package looigi.loowebplayer.db_locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.utilities.Utility;

import static android.content.Context.MODE_PRIVATE;

public class DBLocaleEsclusi {
    private String PathDB = VariabiliStaticheGlobali.getInstance().PercorsoDIR +"/DB/";
    private String NomeDB = "esclusi.db";

    // private String TAG = "GestioneDB";
    // private String DATABASE_NOME = "looWebPlayer";
    private String DATABASE_TABELLA = "esclusi";
    private int DATABASE_VERSIONE = 1;

    // GestioneDB db = new GestioneDB(this);

    // db.open();
    // long id = db.inserisciCliente("Mario Rossi", "Via girasole, 10");
    // id = db.inserisciCliente("Giuseppe Verdi", "Corso Italia, 35");
    // db.close();

    private String DATABASE_CREAZIONE =
            "CREATE TABLE " + DATABASE_TABELLA + " (id integer primary key autoincrement, artista text not null, " +
                    "album text not null, brano text not null);";

    private Context context;
    // private DatabaseHelper DBHelper;
    // private SQLiteDatabase db;

    public DBLocaleEsclusi()
    {
        // this.context = ctx;
        // DBHelper = new DatabaseHelper(context);
        File f = new File(PathDB);
        try {
            f.mkdirs();
        } catch (Exception ignored) {

        }
    }

    private SQLiteDatabase ApreDB() {
        SQLiteDatabase db = null;
        try {
            db = VariabiliStaticheGlobali.getInstance().getContext().openOrCreateDatabase(
                    PathDB + NomeDB, MODE_PRIVATE, null);
        } catch (Exception e) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Problemi nell'apertura del db: " + Utility.getInstance().PrendeErroreDaException(e));
        }
        return  db;
    }

    public void CreazioneTabellaEsclusione() {
        try {
            SQLiteDatabase myDB = ApreDB();
            if (myDB != null) {
                myDB.execSQL(DATABASE_CREAZIONE);
            }
        } catch (Exception ignored) {

        }
    }

    /* private class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NOME, null, DATABASE_VERSIONE);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREAZIONE);
            }
            catch (SQLException e) {
                // e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABELLA);
            onCreate(db);
        }

    }


    public DBLocaleUtenti open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    public void close()
    {
        DBHelper.close();
    }*/

    public long inserisciEsclusione(String Artista, String Album, String Brano)
    {
        SQLiteDatabase myDB = ApreDB();
        long Ok = 0;
        if (myDB != null) {
            ContentValues initialValues = new ContentValues();
            initialValues.put("artista", Artista);
            initialValues.put("album", Album);
            initialValues.put("brano", Brano);

            return myDB.insert(DATABASE_TABELLA, null, initialValues);
        } else {
            return 0;
        }
    }

    public boolean cancellaEclusione(String Artista, String Album, String Brano)
    {
        SQLiteDatabase myDB = ApreDB();
        boolean Ok = true;
        if (myDB != null) {
            try {
                myDB.execSQL("DELETE FROM "
                        + " " + DATABASE_TABELLA + " "
                        + " WHERE artista = '" + Artista + "' and Album = '" + Album + "' And brano = '" + Brano + "' ");
                Ok = true;
            } catch (SQLException e) {
                // Ok = Utility.getInstance().PrendeErroreDaException(e);
                Ok = false;
            }
        } else {
            Ok = false;
        }

        return Ok;
        // return db.delete(DATABASE_TABELLA, "id=" + rigaId, null) > 0;
    }

    public Cursor ottieniTutteEsclusioni()
    {
        SQLiteDatabase myDB = ApreDB();
        if (myDB != null) {
            return myDB.query(DATABASE_TABELLA, new String[]{"id", "artista", "album", "brano"}, null,
                    null, null, null, null);
        } else {
            return null;
        }
    }

    /* public Cursor ottieniUtente(long rigaId) throws SQLException
    {
        SQLiteDatabase myDB = ApreDB();
        long Ok = 0;
        if (myDB != null) {
            Cursor mCursore = myDB.query(true, DATABASE_TABELLA, new String[]
                            {"id", "utente", "password", "amministratore", "cartellabase", "ultimaCanzoneSuonata",
                                    "ModalitaAvanzamento"}, "id=" + rigaId,
                    null, null, null, null, null);
            if (mCursore != null) {
                mCursore.moveToFirst();
            }
            if (mCursore != null)
                mCursore.close();

            return mCursore;
        } else {
            return null;
        }
    }

    public boolean aggiornaUtente(long rigaId, String utente, String password, String Amministratore, String CartellaBase,
                                  String UltimaCanzone, String ModoAvanzamento)
    {
        SQLiteDatabase myDB = ApreDB();
        if (myDB != null) {
            ContentValues args = new ContentValues();
            args.put("utente", utente);
            args.put("password", password);
            args.put("amministratore", Amministratore);
            args.put("cartellabase", CartellaBase);
            args.put("ultimaCanzoneSuonata", UltimaCanzone);
            args.put("ModalitaAvanzamento", ModoAvanzamento);

            boolean rit = myDB.update(DATABASE_TABELLA, args, "id=" + rigaId, null) > 0;

            return rit;
        } else {
            return false;
        }
    } */
}
