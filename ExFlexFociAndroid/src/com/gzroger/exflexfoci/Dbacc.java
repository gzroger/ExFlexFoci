package com.gzroger.exflexfoci;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

public class Dbacc {
	
	private SQLiteDatabase db;
	static final String PLAYER_ACTIVITY = "player_activity";

	public Dbacc(SQLiteDatabase db) {
		this.db = db;
	}

	public List<Player> rgplayerAllGet() {
		List<Player> rgplayer = new ArrayList<Player>();
		Cursor cur = db.query(Player.TABN, new String[] {"name"}, null, null, null, null, "name");
		if (cur.moveToFirst()) {
			do {
				rgplayer.add(new Player(cur.getString(0)));
			} while (cur.moveToNext());
		}
		cur.close();
		return rgplayer;
	}

	public void setPaymentForCalPlayer(Calendar cal, final Player player, String stMon) {
		String date = DateFormat.format("yyyy-MM-dd", cal).toString();
		ContentValues cv = new ContentValues();
		cv.put("payment", stMon);
		db.update(Dbacc.PLAYER_ACTIVITY, cv,  "name = ? AND date = ? ", new String[] {player.stName, date});	
	}


	public Map<Player, String> mpPaymentForPlayerGet(Calendar cal) {
		Map<Player, String> mpPaymentForPlayer = new HashMap<Player, String>();
		String date = DateFormat.format("yyyy-MM-dd", cal).toString();
		Cursor cur = db.query(PLAYER_ACTIVITY, new String[] {"name", "payment"}, "date=?", new String[] {date}, null, null, null);
		if (cur.moveToFirst()) {
			do {
				mpPaymentForPlayer.put(new Player(cur.getString(0)), cur.getString(1));
			} while (cur.moveToNext());
		}
		cur.close();
		return mpPaymentForPlayer;		
	}
	
	public Player createPlayer(String stName) {
		ContentValues cv = new ContentValues();
		cv.put("name", stName);
		db.insert(Player.TABN, null, cv);
		return new Player(stName);
	}
	

	protected void setPresentPlayer(Calendar cal, Player player, boolean fPresent) {
		String date = DateFormat.format("yyyy-MM-dd", cal).toString();
		if (fPresent) {
			ContentValues cv = new ContentValues();
			cv.put("name", player.stName);
			cv.put("date", date);
			cv.put("payment", "0");
			db.insert(Dbacc.PLAYER_ACTIVITY, null, cv);		
		} else {
			db.delete(Dbacc.PLAYER_ACTIVITY, "name = ? AND date = ? ", new String[] {player.stName, date});
		}
		
	}	

	
}
