package modelo;

import java.util.Calendar;

/**
 * @author Jorge Silva Borda
 */
public class Fechas {

    public String getFecha() {
	return formatDias(getDia()) + "-" + formatDosDigitos(getMes()) + "-" + getAnio();
    }
    
    public String getFechaHora(){
	return getFecha() + " " + getHoraCompleta();
    }
    
    public String getHoraCompleta(){
	return formatDosDigitos(getHora()) + ":" + formatDosDigitos(getMinutos()) + ":" + formatDosDigitos(getSegundos());
    }

    private String mesAPalabra(int mes) {
	switch (mes) {
	    case 1:
		return "Enero";
	    case 2:
		return "Febrero";
	    case 3:
		return "Marzo";
	    case 4:
		return "Abril";
	    case 5:
		return "Mayo";
	    case 6:
		return "Junio";
	    case 7:
		return "Julio";
	    case 8:
		return "Agosto";
	    case 9:
		return "Septiembre";
	    case 10:
		return "Octubre";
	    case 11:
		return "Noviembre";
	    case 12:
		return "Diciembre";
	    default:
		return "";
	}
    }

    private String mesAPalabra(String mes) {
	try {
	    return mesAPalabra(Integer.parseInt(mes));
	} catch (NumberFormatException ex) {
	    return "";
	}
    }

    private String formatDias(int dia) {
	if (dia < 10) {
	    return "0" + Integer.toString(dia);
	} else {
	    return Integer.toString(dia);
	}
    }

    private String formatDias(String dia) {

	try {
	    return formatDias(Integer.parseInt(dia));
	} catch (NumberFormatException ex) {
	    return "";
	}
    }

    private String formatDosDigitos(int numero) {
	if (numero < 10) {
	    return "0" + Integer.toString(numero);
	}
	return Integer.toString(numero);
    }

    private String formatDosDigitos(String numero) {
	if (Integer.parseInt(numero) < 10) {
	    return "0" + Integer.toString(Integer.parseInt(numero));
	}
	return numero;
    }

    public int getDia() {
	return Calendar.getInstance().get(Calendar.DATE);
    }

    public int getMes() {
	return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    private int getAnio() {
	return Calendar.getInstance().get(Calendar.YEAR);
    }

    private int getHora() {
	return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    private int getMinutos() {
	return Calendar.getInstance().get(Calendar.MINUTE);
    }

    private int getSegundos() {
	return Calendar.getInstance().get(Calendar.SECOND);
    }
}
