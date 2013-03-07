package es.solofrikis.monbus;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("javadoc")
public class Horarios {

	ArrayList<Horario> horarios = new ArrayList<Horario>();
	int origen;
	int destino;
	String fechaIda;

	public Horarios(int origen, int destino, String fechaIda) {
		super();
		this.origen = origen;
		this.destino = destino;
		this.fechaIda = fechaIda;
	}

	public ArrayList<Horario> getHorario() {
		try {
			Document doc = Jsoup
					.connect(
							"http://horarios.monbus.es/?route=/php/results.php&data[searchType]=1&data[paradaOrigenAC]=&data[paradaOrigen]="
									+ this.origen
									+ "&data[paradaDestinoAC]=&data[paradaDestino]="
									+ this.destino
									+ "&data[tipoViaje]=0&data[fechaIda]="
									+ this.fechaIda + "&data[fechaVuelta]=")
					.timeout(30000).get();
			Elements lineas = doc.select("div.clickable-tr");
			for (Element el : lineas) {
				// Obtenemos datos de horarios
				Elements divs = el.select("div");
				String salida = divs.get(1).text();
				String trayecto = divs.get(2).text();
				String trayetoOrigen = trayecto.split(" - ")[0];
				String trayetoDestino = trayecto.split(" - ")[1];
				String llegada = divs.get(3).text();
				String duracion = divs.get(4).text();
				// String plazas = divs.get(5).text();

				// Obtenemos datos trayecto
				Elements inputs = el.select("form input");
				String searchType = inputs.get(0).attr("value");
				String origen = inputs.get(1).attr("value");
				String destino = inputs.get(2).attr("value");
				String linea = inputs.get(3).attr("value");
				String exp = inputs.get(4).attr("value");
				String sentido = inputs.get(5).attr("value");
				String fecha = inputs.get(6).attr("value");
				String enlace = inputs.get(7).attr("value");
				this.horarios.add(new Horario(salida, llegada, duracion,
						trayetoOrigen, trayetoDestino, searchType, origen,
						destino, linea, exp, sentido, fecha, enlace));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.horarios;
	}
}

@SuppressWarnings("javadoc")
class Horario {

	// datos horario
	String salida;
	String llegada;
	String duracion;
	String trayectoOrigen;
	String trayectoDestino;

	// Datos trayecto
	String searchType;
	String origen;
	String destino;
	String linea;
	String exp;
	String sentido;
	String fecha;
	String enlace;

	public Horario(String salida, String llegada, String duracion) {
		super();
		this.salida = salida;
		this.llegada = llegada;
		this.duracion = duracion;
	}

	public Horario(String salida, String llegada, String duracion,
			String trayectoOrigen, String trayectoDestino) {
		super();
		this.salida = salida;
		this.llegada = llegada;
		this.duracion = duracion;
		this.trayectoOrigen = trayectoOrigen;
		this.trayectoDestino = trayectoDestino;
	}

	public Horario(String salida, String llegada, String duracion,
			String trayectoOrigen, String trayectoDestino, String searchType,
			String origen, String destino, String linea, String exp,
			String sentido, String fecha, String enlace) {
		super();
		this.salida = salida;
		this.llegada = llegada;
		this.duracion = duracion;
		this.trayectoOrigen = trayectoOrigen;
		this.trayectoDestino = trayectoDestino;
		this.searchType = searchType;
		this.origen = origen;
		this.destino = destino;
		this.linea = linea;
		this.exp = exp;
		this.sentido = sentido;
		this.fecha = fecha;
		this.enlace = enlace;
	}

	public String getSearchType() {
		return this.searchType;
	}

	public String getOrigen() {
		return this.origen;
	}

	public String getDestino() {
		return this.destino;
	}

	public String getLinea() {
		return this.linea;
	}

	public String getExp() {
		return this.exp;
	}

	public String getSentido() {
		return this.sentido;
	}

	public String getFecha() {
		return this.fecha;
	}

	public String getEnlace() {
		return this.enlace;
	}

	public String getTrayectoOrigen() {
		return this.trayectoOrigen;
	}

	public String getTrayectoDestino() {
		return this.trayectoDestino;
	}

	public String getSalida() {
		return this.salida;
	}

	public String getLlegada() {
		return this.llegada;
	}

	public String getDuracion() {
		return this.duracion;
	}

}
