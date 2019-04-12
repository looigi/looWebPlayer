package looigi.loowebplayer.db_locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBLocale {
    private String TAG = "GestioneDB";
    private String DATABASE_NOME = "looWebPlayer";
    private String DATABASE_TABELLA = "utenti";
    private int DATABASE_VERSIONE = 1;

    // GestioneDB db = new GestioneDB(this);

    // db.open();
    // long id = db.inserisciCliente("Mario Rossi", "Via girasole, 10");
    // id = db.inserisciCliente("Giuseppe Verdi", "Corso Italia, 35");
    // db.close();

    private String DATABASE_CREAZIONE =
            "CREATE TABLE " + DATABASE_TABELLA + " (id integer primary key autoincrement, utente text not null, " +
                    "password text not null, amministratore text not null, cartellabase text not null, " +
                    "ultimaCanzoneSuonata integer, ModalitaAvanzamento integer);";

    private Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBLocale(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private class DatabaseHelper extends SQLiteOpenHelper
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


    public DBLocale open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    public void close()
    {
        DBHelper.close();
    }


    public long inserisciUtente(String utente, String password, String Amministratore, String CartellaBase,
                                String UltimaCanzoneSuonata, String ModalitaAvanzamento)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("utente", utente);
        initialValues.put("password", password);
        initialValues.put("amministratore", Amministratore);
        initialValues.put("cartellabase", CartellaBase);
        initialValues.put("ultimaCanzoneSuonata", UltimaCanzoneSuonata);
        initialValues.put("ModalitaAvanzamento", ModalitaAvanzamento);

        return db.insert(DATABASE_TABELLA, null, initialValues);
    }


    public boolean cancellaUtente(long rigaId)
    {
        return db.delete(DATABASE_TABELLA, "id=" + rigaId, null) > 0;
    }


    public Cursor ottieniTuttiUtenti()
    {
        return db.query(DATABASE_TABELLA, new String[] {"id", "utente", "password", "amministratore", "cartellabase",
                "ultimaCanzoneSuonata", "ModalitaAvanzamento"}, null,
                null, null, null, null);
    }


    public Cursor ottieniUtente(long rigaId) throws SQLException
    {
        Cursor mCursore = db.query(true, DATABASE_TABELLA, new String[]
                {"id", "utente", "password", "amministratore", "cartellabase","ultimaCanzoneSuonata",
                        "ModalitaAvanzamento"},  "id=" + rigaId,
                null, null, null, null, null);
        if (mCursore != null) {
            mCursore.moveToFirst();
        }
        if(mCursore != null)
            mCursore.close();

        return mCursore;
    }


    public boolean aggiornaUtente(long rigaId, String utente, String password, String Amministratore, String CartellaBase,
                                  String UltimaCanzone, String ModoAvanzamento)
    {
        ContentValues args = new ContentValues();
        args.put("utente", utente);
        args.put("password", password);
        args.put("amministratore", Amministratore);
        args.put("cartellabase", CartellaBase);
        args.put("ultimaCanzoneSuonata", UltimaCanzone);
        args.put("ModalitaAvanzamento", ModoAvanzamento);

        Boolean rit = db.update(DATABASE_TABELLA, args, "id=" + rigaId, null) > 0;

        return rit;
    }
}
