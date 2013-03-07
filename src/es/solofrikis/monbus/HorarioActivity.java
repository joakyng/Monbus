package es.solofrikis.monbus;




import com.google.ads.*;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings({ "javadoc" })
public class HorarioActivity extends Activity {
	
	private String strOrigen;
	private String strDestino;
	private String strFechaIda;
	private int dia;
	private int mes;
	private int ano;
	private Time time;
	private DBHelper db;
	
	
	//views
	private ListView lista;
	private ImageButton botonDiaSiguiente;
	private ImageButton botonDiaAnterior;
	private TextView tvFecha;
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_horario);
		
		//Init views
		this.botonDiaSiguiente = (ImageButton)findViewById(R.id.imgBotonDiaSiguiente);
		this.botonDiaAnterior = (ImageButton)findViewById(R.id.imgBotonDiaAnterior);
		this.tvFecha = (TextView)findViewById(R.id.tvFecha);
		
		this.lista = (ListView)findViewById(R.id.listaHorarios);
		
		
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			this.strOrigen = extras.getString("origen");
			this.strDestino = extras.getString("destino");
			this.strFechaIda = extras.getString("fechaIda");
			
			this.db = new DBHelper(this);
			this.db.openDataBase();
			
			actualizarFecha(this.strFechaIda);
			leerLista();
		}
		
		this.botonDiaSiguiente.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				diaSiguiente();
			}
		});
		
		this.botonDiaAnterior.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				diaAnterior();
			}
		});
		
	}
	
	public void actualizarFecha(String fecha){
		
		this.time = new Time(Time.getCurrentTimezone());
		String[] splitFecha = fecha.split("/");
		this.time.set(Integer.parseInt(splitFecha[0]),Integer.parseInt(splitFecha[1])-1,Integer.parseInt(splitFecha[2]));
		this.time.normalize(true);
		this.tvFecha.setText(this.time.format("%A %d "+getString(R.string.deFecha)+" %B "+getString(R.string.deFecha)+" %Y"));
		this.strFechaIda = fecha;
	}
	
	public void diaSiguiente(){
		Time fechaElegida = this.time;
		fechaElegida.set(fechaElegida.monthDay+1,fechaElegida.month,fechaElegida.year);
		fechaElegida.normalize(true);
		actualizarFecha(fechaElegida.format("%d/%m/%Y"));
		leerLista();
	}
	
	public void diaAnterior(){
		Time hoy = new Time();
		hoy.setToNow();
		hoy.set(0,0,0,hoy.monthDay,hoy.month,hoy.year);
		Time fechaElegida = new Time();
		fechaElegida.set(this.time);
		fechaElegida.set(fechaElegida.monthDay-1,fechaElegida.month,fechaElegida.year);
		if(!fechaElegida.before(hoy)){
			fechaElegida.normalize(true);
			actualizarFecha(fechaElegida.format("%d/%m/%Y"));
			leerLista();
		}else{
			Toast.makeText(this,R.string.fechaAnteror,Toast.LENGTH_LONG).show();
		}
	}
	
	public void leerLista(){
		new LeerListaTask().execute();
	}
	
	private class LeerListaTask extends AsyncTask<Object, Object, HorariosAdapter> {
		Dialog dialog;
		@Override
		protected HorariosAdapter doInBackground(Object... params) {
			HorariosAdapter horariosAdapter = new HorariosAdapter(HorarioActivity.this, Integer.parseInt(HorarioActivity.this.strOrigen), Integer.parseInt(HorarioActivity.this.strDestino), HorarioActivity.this.strFechaIda);
			HorarioActivity.this.adView.loadAd(new AdRequest());
			return horariosAdapter;
		}
		@Override
		protected void onPreExecute() {
			this.dialog = dialogo();
			this.dialog.show();
			crearPubli();
		}
		@Override
		protected void onPostExecute(HorariosAdapter result) {
			HorarioActivity.this.lista.setAdapter(result);
			this.dialog.dismiss();
			
			
		}
	}
	
	public Dialog dialogo(){
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.cargando_horario);
		dialog.setTitle("Cargando Horario...");
		return dialog;
	}
	
	public void crearPubli(){
		/*
		 // Crear la adView
	    this.adView = new AdView(this, AdSize.BANNER, "a15137bf984acb6");

	    // Buscar el LinearLayout suponiendo que se le haya asignado
	    // el atributo android:id="@+id/mainLayout"
	    LinearLayout layout = (LinearLayout)findViewById(R.id.layoutPubli);

	    // Añadirle la adView
	    layout.addView(this.adView);
	    
	    AdRequest adRequest = new AdRequest();
	    adRequest.addTestDevice(AdRequest.TEST_EMULATOR);               // Emulador
	    adRequest.addTestDevice("68EDAE652512332A68CA6E889016412B");               // LG
	    

	    // Iniciar una solicitud genérica para cargarla con un anuncio
	    this.adView.loadAd(adRequest);
	    */
		
		this.adView = (AdView)this.findViewById(R.id.adView);
		this.adView.loadAd(new AdRequest());
	}
	
	@Override
	  public void onDestroy() {
		if(this.adView!=null)
			this.adView.destroy();
	    super.onDestroy();
	  }

}

