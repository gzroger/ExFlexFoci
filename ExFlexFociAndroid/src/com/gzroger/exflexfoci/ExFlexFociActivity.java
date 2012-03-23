package com.gzroger.exflexfoci;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ExFlexFociActivity extends Activity {

	private static final String PLAYER = "player";

	private static final String PLAYER_ACTIVITY = "player_activity";

	public List<DataSetObserver> rgobserver = new ArrayList<DataSetObserver>();

	class Dbut extends SQLiteOpenHelper {

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
			db.execSQL("CREATE TABLE "+PLAYER+" (NAME text)");
		}

		private void createPlayerActivity(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "+PLAYER_ACTIVITY+" (NAME text, DATE text, payment NUMBER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			switch (oldVersion) {
			case 2:
				createPlayerActivity(db);
			}
		}
	}
	
	public class LitwaPlayer implements ListAdapter {

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		@Override
		public int getCount() {
			return rgplayer.size();
		}

		@Override
		public Object getItem(int position) {
			return rgplayer.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout ll;
			if (convertView == null) {
				ll = (LinearLayout) getLayoutInflater().inflate(R.layout.listwplayer_item, null, false);
				//ll.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
			} else {
				ll = (LinearLayout) convertView;
			}
			TextView textw = (TextView) ll.findViewById(R.id.player_name);
			final Player player = rgplayer.get(position);
			textw.setText(player.stNameGet());
			
			ToggleButton togglb = (ToggleButton) ll.findViewById(R.id.player_present);
			togglb.setChecked(fPresentGet(player));
			togglb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean fChecked) {
					setPresentPlayer(player, fChecked);
					
				}
			});
			
			Button btnPay = (Button) ll.findViewById(R.id.player_pay);
			
			return ll;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return rgplayer.isEmpty();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			ExFlexFociActivity.this.rgobserver .add(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			ExFlexFociActivity.this.rgobserver.remove(observer);
		}

	}

	public class Player {

		String stName;
		public Player(String stName) {
			this.stName = stName;
		}

		public CharSequence stNameGet() {
			return stName;
		}

		@Override
		public String toString() {
			return "Player [stName=" + stName + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((stName == null) ? 0 : stName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Player other = (Player) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (stName == null) {
				if (other.stName != null)
					return false;
			} else if (!stName.equals(other.stName))
				return false;
			return true;
		}

		private ExFlexFociActivity getOuterType() {
			return ExFlexFociActivity.this;
		}
		

	}


	static final int DATE_DIALOG_ID = 0;

	private Button btnDate;    
	private ListView listwPlayer;

	private Calendar cal;
	private List<Player> rgplayer = new ArrayList<Player>();

	private SQLiteDatabase db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		db = new Dbut(getApplicationContext()).getWritableDatabase();
		
		btnDate = (Button) findViewById(R.id.dateButton);
		btnDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);				
			}
		});
		Button btnDatePrev = (Button) findViewById(R.id.dateButtonPrev);
		btnDatePrev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cal.add(Calendar.DAY_OF_MONTH, -7);
				updateDisplay();								
			}
		});
		Button btnDateNext = (Button) findViewById(R.id.dateButtonNext);
		btnDateNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cal.add(Calendar.DAY_OF_MONTH, 7);
				updateDisplay();								
			}
		});
		
		loadData();
				
		listwPlayer = (ListView) findViewById(R.id.listwPlayer);
		listwPlayer.setAdapter(new LitwaPlayer());
		
		// get the current date
		cal = Calendar.getInstance();

		// display the current date (this method is below)
		updateDisplay();
	}

	private void loadData() {
		rgplayer.clear();
		Cursor cur = db.query(PLAYER, new String[] {"name"}, null, null, null, null, "name");
		if (cur.moveToFirst()) {
			do {
				rgplayer.add(new Player(cur.getString(0)));
			} while (cur.moveToNext());
		}
		cur.close();
	}

	// updates the date in the TextView
	private void updateDisplay() {
		btnDate.setText(DateFormat.format("yyyy-MM-dd", cal));
		fillPlayerSet();
		refreshListwPlayer();
	}    

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,
					mDateSetListener,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
			);
		}
		return super.onCreateDialog(id); 
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener =
		new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int moy, int dom) {
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, moy);
			cal.set(Calendar.DAY_OF_MONTH, dom);
			updateDisplay();
		}
	};

	private HashSet<String> setPlayerPresent = new HashSet<String>();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}     

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.addNewPlayer:
			addNewPlayer();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addNewPlayer() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Create");
		alert.setMessage("Player name:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				createPlayer(value);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});
		alert.show();
	}    


	protected void createPlayer(String stName) {
		ContentValues cv = new ContentValues();
		cv.put("name", stName);
		db.insert(PLAYER, null, cv);

		loadData();
		refreshListwPlayer();
	}

	private void refreshListwPlayer() {
		for (DataSetObserver obs : rgobserver) {
			obs.onChanged();
		}
	}
	

	protected void setPresentPlayer(Player player, boolean fPresent) {
		String date = DateFormat.format("yyyy-MM-dd", cal).toString();
		if (fPresent) {
			ContentValues cv = new ContentValues();
			cv.put("name", player.stName);
			cv.put("date", date);
			cv.put("name", player.stName);
			db.insert(PLAYER_ACTIVITY, null, cv);		
		} else {
			db.delete(PLAYER_ACTIVITY, "name = ? AND date = ? ", new String[] {player.stName, date});
		}
		
	}

	public boolean fPresentGet(Player player) {
		return setPlayerPresent.contains(player.stName);
	}

	private void fillPlayerSet() {
		setPlayerPresent.clear();
		String date = DateFormat.format("yyyy-MM-dd", cal).toString();
		Cursor cur = db.query(PLAYER_ACTIVITY, new String[] {"name"}, "date=?", new String[] {date}, null, null, null);
		if (cur.moveToFirst()) {
			do {
				setPlayerPresent.add(cur.getString(0));
			} while (cur.moveToNext());
		}
		cur.close();		
	}
	
}
