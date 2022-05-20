package com.golan.amit.magicfour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MagicFourDbHelper extends SQLiteOpenHelper {

    public static final String DATABASENAME = "magicfour.db";
    public static final String TABLE = "tblresults";
    public static final int DATABASEVERSION = 1;
    public static final String ID_COLUMN = "id";
    public static final String SECONDS_COLUMN = "seconds";
    public static final String MOVES_COLUMN = "moves";
    public static final String PLAYER_COLUMN = "player";
    public static final String DATETIME_COLUMN = "curr_datetime";

    SQLiteDatabase database;

    public static final String CREATE_TABLE_MFRESULTS =
            "CREATE TABLE IF NOT EXISTS " + TABLE +
                    "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SECONDS_COLUMN + " INTEGER," +
                    MOVES_COLUMN + " INTEGER," +
                    PLAYER_COLUMN + " TEXT," +
                    DATETIME_COLUMN + " DATE);";

    String[] allColumns = {
            ID_COLUMN, SECONDS_COLUMN, MOVES_COLUMN, PLAYER_COLUMN, DATETIME_COLUMN
    };

    public MagicFourDbHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "create string: {" + CREATE_TABLE_MFRESULTS + "}");
        }
        try {
            db.execSQL(CREATE_TABLE_MFRESULTS);
            if (MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "database created");
            }
        } catch (Exception edb) {
            Log.e(MainActivity.DEBUGTAG, "database creation exception: " + edb);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void open() {
        database = this.getWritableDatabase();
        if(MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "database connection open");
        }
    }

    public void close() {
        if (database != null) {
            try {
                database.close();
                if(MainActivity.DEBUG) {
                    Log.i(MainActivity.DEBUGTAG, "database connection closed");
                }
            } catch (Exception edbc) {
                Log.e(MainActivity.DEBUGTAG, "database connection close exception: " + edbc);
            }
        } else {
            Log.e(MainActivity.DEBUGTAG, "database is null");
        }
    }

    public void insert(String sSeconds, String sMoves, String sPlayer) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SECONDS_COLUMN, sSeconds);
        contentValues.put(MOVES_COLUMN, sMoves);
        contentValues.put(PLAYER_COLUMN, sPlayer);
        contentValues.put(DATETIME_COLUMN, currentDate());
        long insertedId = -1;
        try {
            insertedId = database.insert(TABLE, null, contentValues);
            if(MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "inserted seconds: " + sSeconds + ", moves: " + sMoves + " to db, id: " + insertedId);
            }
        } catch (Exception eid) {
            Log.e(MainActivity.DEBUGTAG, "insert exception: " + eid);
        }
    }

    public void resetTableToScratch() {
        try {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE);
            if(MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "database dropped");
            }
        } catch (Exception edbd) {
            Log.e(MainActivity.DEBUGTAG, "database drop exception: " + edbd);
        }

        try {
            database.execSQL(CREATE_TABLE_MFRESULTS);
            if(MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "database re-created");
            }
        } catch (Exception edbc) {
            Log.e(MainActivity.DEBUGTAG, "database re-create exception: " + edbc);
        }
    }

    public void displayDatabaseContent() {
        String query = "SELECT * FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int seconds = cursor.getInt(cursor.getColumnIndex(SECONDS_COLUMN));
                int moves = cursor.getInt(cursor.getColumnIndex(MOVES_COLUMN));
                String player = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));
                String tmpInfoDisplay = String.format("id: %d, seconds: %d, moves: %d, player: %s, date; %s",
                        id, seconds, moves, player, currentdate);
                Log.i(MainActivity.DEBUGTAG, tmpInfoDisplay);
            }
        } else {
            Log.e(MainActivity.DEBUGTAG, "database is empty, no activity in account");
        }
    }


    public ArrayList<MagicFourResult> getAllWorkoutSessions() {
        ArrayList<MagicFourResult> l = new ArrayList<MagicFourResult>();
        String query = "SELECT * FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int seconds = cursor.getInt(cursor.getColumnIndex(SECONDS_COLUMN));
                int moves = cursor.getInt(cursor.getColumnIndex(MOVES_COLUMN));
                String player = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));
                MagicFourResult mfr = new MagicFourResult((int) id, seconds, moves, player, currentdate);
                l.add(mfr);
            }
        }
        return l;
    }

    public ArrayList<MagicFourResult> getAllWorkoutSessionsByFilter(String selection, String orderBy) {
        ArrayList<MagicFourResult> l = new ArrayList<>();
        Cursor cursor = database.query(TABLE, allColumns, selection, null, null, null, orderBy);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int seconds = cursor.getInt(cursor.getColumnIndex(SECONDS_COLUMN));
                int moves = cursor.getInt(cursor.getColumnIndex(MOVES_COLUMN));
                String player = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));
                MagicFourResult mfr = new MagicFourResult((int) id, seconds, moves, player, currentdate);
                l.add(mfr);
            }
        }
        return l;
    }

    public int minimumSecods() {
        int minSec = -1;
        String query = "SELECT MIN(" + SECONDS_COLUMN + ") FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToNext()) {
            try {
                minSec = cursor.getInt(0);
                if(MainActivity.DEBUG) {
                    Log.i(MainActivity.DEBUGTAG, "minimum (best highscore) time in seconds: " + minSec);
                }
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "minimum time in secods exception: " + e);
            }
        }
        return minSec;
    }


    public int minimumMoves() {
        int minMoves = -1;
        String query = "SELECT MIN(" + MOVES_COLUMN + ") FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToNext()) {
            try {
                minMoves = cursor.getInt(0);
                if (MainActivity.DEBUG) {
                    Log.i(MainActivity.DEBUGTAG, "minimum (best moves score): " + minMoves);
                }
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "minimum moves exception: " + e);
            }
        }
        return minMoves;
    }

    public String lastActivityDate() {
        String tmpDate = null;

        String query = "SELECT " + DATETIME_COLUMN + " FROM " + TABLE +
                " ORDER BY " + DATETIME_COLUMN + " DESC LIMIT 1";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToNext()) {
            try {
                tmpDate = cursor.getString(0);
                Log.d(MainActivity.DEBUGTAG, "select last date :" + tmpDate);
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "select last date exception:" + e);
            }
        }
        return tmpDate;
    }

    public MagicFourResult lastRecord() {
        MagicFourResult mfr = null;
        String query = "SELECT * FROM " + TABLE +
                " ORDER BY " + DATETIME_COLUMN + " DESC LIMIT 1";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToNext()) {
            try {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int seconds = cursor.getInt(cursor.getColumnIndex(SECONDS_COLUMN));
                int moves = cursor.getInt(cursor.getColumnIndex(MOVES_COLUMN));
                String player = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));
                mfr = new MagicFourResult((int) id, seconds, moves, player, currentdate);
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "database last record query exception: " + e);
            }
        }
        return mfr;
    }

    public long deleteRecordById(long rowId) {
        return database.delete(TABLE, ID_COLUMN + "=" + rowId, null);
    }

    private String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
