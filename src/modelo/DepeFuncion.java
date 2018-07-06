package modelo;

import modelo.servidor.dependencia.Funcion;

/**
 * @author Jorge Silva Borda
 */
public class DepeFuncion {
    private String rutaDepe;
    private Funcion funcion;

    public DepeFuncion(String rutaDepe, Funcion funcion) {
	this.rutaDepe = rutaDepe;
	this.funcion = funcion;
    }

    public DepeFuncion() {
    }

    public String getRutaDepe() {
	return rutaDepe;
    }

    public void setRutaDepe(String rutaDepe) {
	this.rutaDepe = rutaDepe;
    }

    public Funcion getFuncion() {
	return funcion;
    }

    public void setFuncion(Funcion funcion) {
	this.funcion = funcion;
    }
    
    
}
