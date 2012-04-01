package com.gzroger.exflexfoci;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Dbut extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "dbut";

    Dbut(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPlayer(db);
        createPlayerActivity(db);
    }

	private void createPlayer(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+Player.TABN+" (NAME text)");
	}

	private void createPlayerActivity(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+Dbacc.PLAYER_ACTIVITY+" (NAME text, DATE text, payment NUMBER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		switch (oldVersion) {
		case 2:
			createPlayerActivity(db);
		}
	}
}