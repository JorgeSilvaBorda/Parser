package modelo.servidor;

import java.io.File;
import java.util.LinkedList;
import logger.Logger;
import modelo.servidor.dependencia.Funcion;

/**
 * Objeto representativo de servidor Tuxedo
 *
 * @author Jorge Silva Borda
 */
public class ServidorTuxedo {
    Logger log = new Logger("log.txt");
    private String nombre;
    private LinkedList<ServicioTuxedo> serviciosExpuestos;
    private LinkedList<Funcion> funciones;
    private File archivoFuente;
    private LinkedList<File> dependencias;

    public ServidorTuxedo() {
	serviciosExpuestos = new LinkedList();
	dependencias = new LinkedList();
	funciones = new LinkedList();
	this.archivoFuente = null;
    }

    public ServidorTuxedo(String nombre) {
	this.nombre = nombre;
	serviciosExpuestos = new LinkedList();
	dependencias = new LinkedList();
	funciones = new LinkedList();
	this.archivoFuente = null;
    }

    public ServidorTuxedo(String nombre, File archivoFuente) {
	this.nombre = nombre;
	this.archivoFuente = archivoFuente;
	serviciosExpuestos = new LinkedList();
	funciones = new LinkedList();
	dependencias = new LinkedList();
    }

    public void addServicio(ServicioTuxedo servicio) {
	this.serviciosExpuestos.add(servicio);
    }

    public void addDependencia(File dependencia) {
	dependencias.add(dependencia);
    }

    public void addFuncion(Funcion funcion) {
	funciones.add(funcion);
    }

    public LinkedList<File> getDependencias() {
	return dependencias;
    }

    public LinkedList<ServicioTuxedo> getServicios() {
	return serviciosExpuestos;
    }

    public LinkedList<Funcion> getFunciones() {
	return funciones;
    }

    public String getNombre() {
	return nombre;
    }

    public File getArchivoFuente() {
	return archivoFuente;
    }

    public void setNombre(String nombre) {
	this.nombre = nombre;
    }

    public void setArchivoFuente(File archivo) {
	this.archivoFuente = archivo;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	try {
	    builder.append("Servidor Tuxedo: [" + getNombre() + "]");
	    builder.append(System.getProperty("line.separator"));
	    builder.append("Archivo fuente: " + getArchivoFuente().getAbsolutePath());
	    builder.append(System.getProperty("line.separator"));
	    builder.append("Dependencias: ");
	    builder.append(System.getProperty("line.separator"));
	    for (File dependencia : dependencias) {
		builder.append(dependencia.getAbsolutePath());
		builder.append(System.getProperty("line.separator"));
	    }

	    builder.append("Servicios expuestos:");
	    builder.append(System.getProperty("line.separator"));
	    for (ServicioTuxedo servicio : serviciosExpuestos) {
		builder.append(servicio.getNombre());
		builder.append(System.getProperty("line.separator"));
		if (servicio.getFunciones().size() > 0) {
		    builder.append("Llama: ");
		    builder.append(System.getProperty("line.separator"));
		    for (Funcion f : servicio.getFunciones()) {
			builder.append("    -" + f.getNombre());
			builder.append(System.getProperty("line.separator"));
		    }
		}
	    }

	    if (getFunciones().size() > 0) {
		builder.append("Funciones declaradas: ");
		builder.append(System.getProperty("line.separator"));
		for (Funcion f : funciones) {
		    builder.append(f.getNombre());
		    builder.append(System.getProperty("line.separator"));
		    if (f.getLlamados().size() > 0) {
			builder.append("Llama:");
			builder.append(System.getProperty("line.separator"));
			for (Funcion fun : f.getLlamados()) {
			    builder.append("    -" + fun.getNombre());
			    builder.append(System.getProperty("line.separator"));
			}
		    }
		}
	    }
	} catch (Exception ex) {
	    log.log("No se pudo imprimir los datos del servidor.");
	}

	return builder.toString();
    }

}
