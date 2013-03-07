package es.solofrikis.monbus;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


@SuppressWarnings("javadoc")
public class HorariosAdapter extends BaseAdapter {
	private ArrayList<Horario> lista;
	private Activity actividad;
	private int idorigen;
	private int iddestino;
	
	public HorariosAdapter(Activity actividad, int origen, int destino, String fechaIda) {
		this.actividad = actividad;
		this.idorigen = origen;
		this.iddestino = destino;
		this.lista = new Horarios(origen, destino, fechaIda).getHorario();
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
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Horario horario = this.lista.get(position);
		if(convertView==null){
			LayoutInflater inflater = this.actividad.getLayoutInflater();
			convertView = inflater.inflate(R.layout.horarioadapterlayout, null);
			if(position==0){
				//añadimos el reciente
				RecientesDBHelper dbrecientes = new RecientesDBHelper(this.actividad.getApplicationContext());
				
				((TextView)this.actividad.findViewById(R.id.tvTituloOrigen)).setText(horario.getTrayectoOrigen());
				((TextView)this.actividad.findViewById(R.id.tvTituloDestino)).setText(horario.getTrayectoDestino());
				dbrecientes.addReciente(String.valueOf(this.idorigen),String.valueOf(this.iddestino),horario.getTrayectoOrigen(),horario.getTrayectoDestino());
				dbrecientes.close();
			}
		}

		((TextView)convertView.findViewById(R.id.textViewSalida)).setText(horario.getSalida());
		((TextView)convertView.findViewById(R.id.textViewLLegada)).setText(horario.getLlegada());
		((TextView)convertView.findViewById(R.id.textViewDuracion)).setText(horario.getDuracion());
		return convertView;
	}

}
