package looigi.loowebplayer.db_locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.utilities.Utility;

import static android.content.Context.MODE_PRIVATE;

public class DBLocaleTraffico {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("DBLocaleTraffico");;
    private String PathDB = VariabiliStaticheGlobali.getInstance().PercorsoDIR +"/DB/";
    private String NomeDB = "traffico.db";

    private String TAG = "GestioneDBTraffico";
    private String DATABASE_NOME = "looWPcfg";
    private String DATABASE_TABELLA = "Traffico";
    private int DATABASE_VERSIONE = 1;

    private String DATABASE_CREAZIONE =
            "CREATE TABLE " + DATABASE_TABELLA + " ("+
                    "id text not null, " +
                    "Traffico text not null "+
                    ");";

    private Context context;
    // private DatabaseHelperTraffico DBHelper;
    // private SQLiteDatabase db;

    public DBLocaleTraffico()
    {
        // this.context = ctx;
        // DBHelper = new DatabaseHelperTraffico(context);
        File f = new File(PathDB);
        try {
            f.mkdirs();
        } catch (Exception ignored) {

        }
    }

    /* private class DatabaseHelperTraffico extends SQLiteOpenHelper
    {
        DatabaseHelperTraffico(Context context)
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

    public DBLocaleTraffico open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        DBHelper.close();
    } */


    private SQLiteDatabase ApreDB() {
        SQLiteDatabase db = null;
        try {
            db = VariabiliStaticheGlobali.getInstance().getContext().openOrCreateDatabase(
                    PathDB + NomeDB, MODE_PRIVATE, null);
            // CreazioneTabellaTraffico();
        } catch (Exception e) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Problemi nell'apertura del db: " + Utility.getInstance().PrendeErroreDaException(e));
        }
        return  db;
    }

    public void CreazioneTabellaTraffico() {
        try {
            SQLiteDatabase myDB = ApreDB();
            if (myDB != null) {
                myDB.execSQL(DATABASE_CREAZIONE);
            }
        } catch (Exception ignored) {

        }
    }

    public long inserisciDati(
            String id,
            String Valore)
    {
        SQLiteDatabase myDB = ApreDB();
        if (myDB != null) {
            ContentValues initialValues = new ContentValues();
            initialValues.put("id", id);
            initialValues.put("Traffico", Valore);

            long l = myDB.insert(DATABASE_TABELLA, null, initialValues);

            return l;
        } else {
            return 0;
        }
    }

    public boolean cancellaDati()
    {
        SQLiteDatabase myDB = ApreDB();
        if (myDB != null) {
            return myDB.delete(DATABASE_TABELLA, "", null) > 0;
        } else {
            return false;
        }
    }

    public Cursor ottieniValori()
    {
        SQLiteDatabase myDB = ApreDB();
        if (myDB != null) {
            return myDB.query(DATABASE_TABELLA, new String[]{
                    "id",
                    "Traffico"
            }, null, null, null, null, null);
        } else {
            return null;
        }
    }

    public Cursor ottieniValoriPerData(String oggi)
    {
        SQLiteDatabase myDB = ApreDB();
        if (myDB != null) {
            CreazioneTabellaTraffico();

            String[] tableColumns = new String[]{
                    "id",
                    "Traffico"
            };
            String whereClause = "id = ?";
            String[] whereArgs = new String[]{
                    oggi
            };
            Cursor c = myDB.query(DATABASE_TABELLA, tableColumns, whereClause, whereArgs,
                    null, null, null);

            // return db.query(DATABASE_TABELLA, new String[] {
            //         "id",
            //         "Traffico"
            // }, "id="+oggi,null, null, null, null);

            return c;
        } else {
            return null;
        }
    }

    public boolean aggiornaValori(String id, String Valore)
    {
        SQLiteDatabase myDB = ApreDB();
        if (myDB != null) {
            ContentValues args = new ContentValues();
            args.put("Traffico", Valore);

            String[] whereArgs = new String[]{
                    id
            };

            boolean r = myDB.update(DATABASE_TABELLA, args, "id=?", whereArgs) > 0;

            return r;
        } else {
            return false;
        }
    }
}
