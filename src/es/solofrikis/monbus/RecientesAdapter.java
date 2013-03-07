package es.solofrikis.monbus;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


@SuppressWarnings("javadoc")
public class RecientesAdapter extends BaseAdapter {
	
	
	ArrayList<Reciente> lista = new ArrayList<Reciente>();
	Activity actividad;
	
	public RecientesAdapter(Activity actividad, Cursor c) {
		this.actividad = actividad;
		if(c.moveToFirst()){
			do{
				int id = c.getInt(c.getColumnIndex("_id"));
				int idOrigen = c.getInt(c.getColumnIndex("idorigen"));
				int idDestino = c.getInt(c.getColumnIndex("iddestino"));
				String nombreorigen = c.getString(c.getColumnIndex("nombreorigen"));
				String nombredestino = c.getString(c.getColumnIndex("nombredestino"));
				this.lista.add(new Reciente(id, idOrigen, idDestino, nombreorigen, nombredestino));
			}while(c.moveToNext());
		}
		
	}

	@Override
	public int getCount() {
		return this.lista.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.lista.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return this.lista.get(arg0).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			LayoutInflater inflater = this.actividad.getLayoutInflater();
			convertView = inflater.inflate(R.layout.recienteadapterlayout, null);
		}
		Reciente reciente = this.lista.get(position);
		((TextView)convertView.findViewById(R.id.tvRecienteOrigen)).setText(reciente.getNombreOrigen());
		((TextView)convertView.findViewById(R.id.tvRecienteDestino)).setText(reciente.getNombreDestino());
		return convertView;
	}
}

@SuppressWarnings("javadoc")
class Reciente{
	private int id;
	private int idOrigen;
	private int idDestino;
	private String nombreOrigen;
	private String nombreDestino;

	public Reciente(int id, int idOrigen, int idDestino, String nombreOrigen,
			String nombreDestino) {
		super();
		this.id = id;
		this.idOrigen = idOrigen;
		this.idDestino = idDestino;
		this.nombreOrigen = nombreOrigen;
		this.nombreDestino = nombreDestino;
	}
	
	public int getId(){
		return this.id;
	}

	public int getIdOrigen() {
		return this.idOrigen;
	}

	public int getIdDestino() {
		return this.idDestino;
	}

	public String getNombreOrigen() {
		return this.nombreOrigen;
	}

	public String getNombreDestino() {
		return this.nombreDestino;
	}
}

