package looigi.loowebplayer.db_locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBLocaleTraffico {
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
    private DatabaseHelperTraffico DBHelper;
    private SQLiteDatabase db;

    public DBLocaleTraffico(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelperTraffico(context);
    }

    private class DatabaseHelperTraffico extends SQLiteOpenHelper
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
    }

    public long inserisciDati(
            String id,
            String Valore)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("id", id);
        initialValues.put("Traffico", Valore);

        long l = db.insert(DATABASE_TABELLA, null, initialValues);

        return l;
    }

    public boolean cancellaDati()
    {
        return db.delete(DATABASE_TABELLA, "", null) > 0;
    }

    public Cursor ottieniValori()
    {
        return db.query(DATABASE_TABELLA, new String[] {
                "id",
                "Traffico"
        }, null,null, null, null, null);
    }

    public Cursor ottieniValoriPerData(String oggi)
    {
        String[] tableColumns = new String[] {
                "id",
                "Traffico"
        };
        String whereClause = "id = ?";
        String[] whereArgs = new String[] {
                oggi
        };
        Cursor c = db.query(DATABASE_TABELLA, tableColumns, whereClause, whereArgs,
                null, null, null);

        // return db.query(DATABASE_TABELLA, new String[] {
        //         "id",
        //         "Traffico"
        // }, "id="+oggi,null, null, null, null);

        return c;
    }

    public boolean aggiornaValori(String id, String Valore)
    {
        ContentValues args = new ContentValues();
        args.put("Traffico", Valore);

        String[] whereArgs = new String[] {
                id
        };

        Boolean r = db.update(DATABASE_TABELLA, args, "id=?", whereArgs) > 0;

        return r;
    }
}
