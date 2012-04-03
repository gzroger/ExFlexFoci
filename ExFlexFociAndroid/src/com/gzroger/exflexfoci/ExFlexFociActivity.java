package com.gzroger.exflexfoci;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ExFlexFociActivity extends Activity {

	public List<DataSetObserver> rgobserver = new ArrayList<DataSetObserver>();

	public class LitwaPlayer implements ListAdapter {

		//@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		//@Override
		public boolean isEnabled(int position) {
			return false;
		}

		//@Override
		public int getCount() {
			return rgplayer.size();
		}

		//@Override
		public Object getItem(int position) {
			return rgplayer.get(position);
		}

		//@Override
		public long getItemId(int position) {
			return position;
		}

		//@Override
		public int getItemViewType(int position) {
			return 0;
		}

		//@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout ll;
			if (convertView == null) {
				ll = (LinearLayout) getLayoutInflater().inflate(R.layout.listwplayer_item, null, false);
			} else {
				ll = (LinearLayout) convertView;
			}
			setListViewItemProperties(position, ll);
			
			return ll;
		}


		//@Override
		public int getViewTypeCount() {
			return 1;
		}

		//@Override
		public boolean hasStableIds() {
			return false;
		}

		//@Override
		public boolean isEmpty() {
			return rgplayer.isEmpty();
		}

		//@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			ExFlexFociActivity.this.rgobserver .add(observer);
		}

		//@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			ExFlexFociActivity.this.rgobserver.remove(observer);
		}

	}

	static final int DATE_DIALOG_ID = 0;

	private Button btnDate;    
	private ListView listwPlayer;

	private Calendar cal;
	private List<Player> rgplayer = new ArrayList<Player>();

	private Dbacc dbacc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Dbut dbut = new Dbut(getApplicationContext());
		dbacc = new Dbacc(dbut.getWritableDatabase());
		
		btnDate = (Button) findViewById(R.id.dateButton);
		btnDate.setOnClickListener(new OnClickListener() {

			//@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);				
			}
		});
		Button btnDatePrev = (Button) findViewById(R.id.dateButtonPrev);
		btnDatePrev.setOnClickListener(new OnClickListener() {

			//@Override
			public void onClick(View v) {
				cal.add(Calendar.DAY_OF_MONTH, -7);
				updateDisplay();								
			}
		});
		Button btnDateNext = (Button) findViewById(R.id.dateButtonNext);
		btnDateNext.setOnClickListener(new OnClickListener() {

			//@Override
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
		rgplayer.addAll(dbacc.rgplayerAllGet());
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

	private Map<Player, String> setPlayerPresent = new HashMap<Player, String>();

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


	private void showPlayerPayment(final Player player) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Payment for "+player.stName);
		alert.setMessage("Amount:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dbacc.setPaymentForCalPlayer(cal, player, input.getText().toString());
				refreshListwPlayer();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});
		alert.show();
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
		rgplayer.add( dbacc.createPlayer(stName) );
		refreshListwPlayer();
	}

	private void refreshListwPlayer() {
		for (DataSetObserver obs : rgobserver) {
			obs.onChanged();
		}
	}
	

	private void fillPlayerSet() {
		setPlayerPresent = dbacc.mpPaymentForPlayerGet(cal);		
		
		;
	}

	private void setListViewItemProperties(int position, LinearLayout ll) {
		TextView textw = (TextView) ll.findViewById(R.id.player_name);
		final Player player = rgplayer.get(position);
		textw.setText(player.stNameGet()+" "+setPlayerPresent.get(player));
		
		final ToggleButton togglb = (ToggleButton) ll.findViewById(R.id.player_present);
		togglb.setChecked(setPlayerPresent.keySet().contains(player));
		togglb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			//@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean fChecked) {
				dbacc.setPresentPlayer(cal, player, fChecked);
			}
		});
		
		Button btnPay = (Button) ll.findViewById(R.id.player_pay);
		btnPay.setOnClickListener(new OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				if (togglb.isChecked())
					showPlayerPayment(player);
			}
		});
	}
	
}
