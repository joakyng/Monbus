package es.solofrikis.monbus;

import java.io.IOException;
import java.util.Calendar;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("javadoc")
public class MainActivity extends Activity {

	private DBHelper DBHelper; // database adapter / helper
	private OrigenAdapter origenAdapter; // cursor adapter
	private DestinoAdapter destinoAdapter; // cursor adapter
	private Cursor origenCursor; // and the cursor itself
	private Cursor destinoCursor; // and the cursor itself

	Calendar cal = Calendar.getInstance();

	// Views
	AutoCompleteTextView origenFiltro;
	AutoCompleteTextView destinoFiltro;
	TextView textViewOrigenBuffer;
	TextView textViewDestinoBuffer;
	DatePicker fecha;
	ImageButton imgButtonBuscar;
	ListView listaRecientes;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init views
		this.origenFiltro = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextViewOrigen);
		this.destinoFiltro = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextViewDestino);
		this.textViewOrigenBuffer = (TextView) findViewById(R.id.textViewOrigenBuffer);
		this.textViewDestinoBuffer = (TextView) findViewById(R.id.textViewDestinoBuffer);
		this.fecha = (DatePicker) findViewById(R.id.fecha);
		this.imgButtonBuscar = (ImageButton) findViewById(R.id.imgBotonBuscar);
		this.listaRecientes = (ListView)findViewById(R.id.listaReciente);

		this.DBHelper = new DBHelper(this);
		try {
			this.DBHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		try {
			this.DBHelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}

		initCursorAdapter();
		initFilter();
		leerRecientes();

		// control de fecha no pasada
		final int minYear = this.cal.get(Calendar.YEAR);
		final int minMonth = this.cal.get(Calendar.MONTH);
		final int minDay = this.cal.get(Calendar.DAY_OF_MONTH);

		this.fecha.init(minYear, minMonth, minDay, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int month,
					int day) {
				Calendar newDate = Calendar.getInstance();
				newDate.set(year, month, day);

				if (MainActivity.this.cal.after(newDate)) {
					view.init(minYear, minMonth, minDay, this);
				}
			}
		});

		// control de origen y destino
		this.origenFiltro.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					MainActivity.this.origenFiltro.setText("");
					MainActivity.this.textViewOrigenBuffer.setText("");
					MainActivity.this.destinoFiltro.setText("");
					MainActivity.this.textViewDestinoBuffer.setText("");
				}
			}
		});
		this.destinoFiltro.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					MainActivity.this.destinoFiltro.setText("");
					MainActivity.this.textViewDestinoBuffer.setText("");
				}
			}
		});
		
		// Control de busqueda
		this.imgButtonBuscar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isOnline() && checkDestino()) {
					String fechaElegida = MainActivity.this.fecha.getDayOfMonth() + "/"
							+ (MainActivity.this.fecha.getMonth() + 1) + "/" + MainActivity.this.fecha.getYear();

					Intent intent = new Intent(getApplicationContext(),
							HorarioActivity.class);
					intent.putExtra("origen", MainActivity.this.textViewOrigenBuffer.getText()
							.toString());
					intent.putExtra("destino", MainActivity.this.textViewDestinoBuffer.getText()
							.toString());
					intent.putExtra("fechaIda", fechaElegida);
					startActivityForResult(intent,0);
				} else if (!checkDestino()) {
					Toast.makeText(getApplicationContext(), R.string.noDestino,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), R.string.noInet,
							Toast.LENGTH_LONG).show();
				}
			}
		});
		
		
		// Pulsar sobre un reciente
		this.listaRecientes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String fechaElegida = MainActivity.this.fecha.getDayOfMonth() + "/"
						+ (MainActivity.this.fecha.getMonth() + 1) + "/" + MainActivity.this.fecha.getYear();
				Reciente reciente = (Reciente)parent.getItemAtPosition(position);
				Intent intent = new Intent(getApplicationContext(),
						HorarioActivity.class);
				intent.putExtra("origen", String.valueOf(reciente.getIdOrigen()));
				intent.putExtra("destino", String.valueOf(reciente.getIdDestino()));
				intent.putExtra("fechaIda", fechaElegida);
				startActivityForResult(intent,1);
			}
		});

	}

	// inits el cursor adapter
	private void initCursorAdapter() {

		// lanzamos cursor
		this.origenCursor = this.DBHelper.getOrigenCursor("");
		startManagingCursor(this.origenCursor);
		this.destinoCursor = this.DBHelper.getDestinoCursor("", 0);
		startManagingCursor(this.destinoCursor);

		// Creamos el adapter
		this.origenAdapter = new OrigenAdapter(getApplicationContext(),
				this.origenCursor, this);
		this.destinoAdapter = new DestinoAdapter(getApplicationContext(),
				this.destinoCursor, this);
	}

	// inits AutocompleteTextView
	private void initFilter() {

		// Origen

		this.origenFiltro.setAdapter(this.origenAdapter);
		this.origenFiltro.setOnItemClickListener(this.origenAdapter);
		this.origenFiltro.setThreshold(3);

		// Destino

		this.destinoFiltro.setAdapter(this.destinoAdapter);
		this.destinoFiltro.setOnItemClickListener(this.destinoAdapter);
		this.destinoFiltro.setThreshold(2);

	}
	
	// Leemos los recientes
	public void leerRecientes(){
		RecientesDBHelper dbrecientes = new RecientesDBHelper(this);
		RecientesAdapter recientesAdapter = new RecientesAdapter(this, dbrecientes.getRecientes());
		this.listaRecientes.setAdapter(recientesAdapter);
		dbrecientes.close();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		leerRecientes();
	}
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public boolean checkDestino() {
		if (!this.textViewDestinoBuffer.getText().equals(""))
			return true;
		return false;
	}

}
