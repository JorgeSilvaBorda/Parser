package modelo.servidor;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public LinkedList<ServicioTuxedo> serviciosExpuestos;
    private LinkedList<Funcion> funciones;
    private LinkedList<String[]> textoFunciones = new LinkedList(); //String[] = {[NOMBRE_FUNCION], [TEXTO_FUNCION], [DEPENDENCIA_CONTENEDORA]}
    public LinkedList<String[]> funcionLlamado = new LinkedList(); //String[] = {[NOMBRE_FUNCION], [SERVICIO_CONSUMIDO], [DEPENDENCIA_CONTENEDORA]}
    private File archivoFuente;
    public LinkedList<File> dependencias;
    public LinkedList<String[]> servicioAlias;
    public boolean conError = false;
    public File makeOrigen;

    //<editor-fold defaultstate="collapsed" desc="Constructores">
    public ServidorTuxedo() {
	serviciosExpuestos = new LinkedList();
	dependencias = new LinkedList();
	funciones = new LinkedList();
	servicioAlias = new LinkedList();
	this.archivoFuente = null;
    }

    public ServidorTuxedo(String nombre) {
	this.nombre = nombre;
	serviciosExpuestos = new LinkedList();
	dependencias = new LinkedList();
	funciones = new LinkedList();
	this.archivoFuente = null;
	servicioAlias = new LinkedList();
    }

    public ServidorTuxedo(String nombre, File archivoFuente) {
	this.nombre = nombre;
	this.archivoFuente = archivoFuente;
	serviciosExpuestos = new LinkedList();
	funciones = new LinkedList();
	dependencias = new LinkedList();
	servicioAlias = new LinkedList();
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Accesores">
    public void addServicio(ServicioTuxedo servicio) {
	this.serviciosExpuestos.add(servicio);
    }

    public void addDependencia(File dependencia) {
	dependencias.add(dependencia);
    }

    public void addFuncion(Funcion funcion) {
	funciones.add(funcion);
    }

    public void addServicioAlias(String servicioAlias[]) {
	this.servicioAlias.add(servicioAlias);
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

    public LinkedList<String[]> getServiciosAlias() {
	return this.servicioAlias;
    }

    public void setAliases(LinkedList<String[]> aliases) {
	this.servicioAlias = aliases;
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

	    builder.append(System.getProperty("line.separator"));
	    builder.append(System.getProperty("line.separator"));
	    builder.append("Servicios expuestos:");
	    builder.append(System.getProperty("line.separator"));
	    for (ServicioTuxedo servicio : serviciosExpuestos) {
		builder.append(servicio.getNombre());
		builder.append(System.getProperty("line.separator"));
		if (servicio.getFunciones().size() > 0) {
		    builder.append("Llama: ");
		    builder.append(System.getProperty("line.separator"));
		    for (Funcion f : servicio.getFunciones()) {
			builder.append("    -" + f.getNombre() + " " + f.getTipo());
			builder.append(System.getProperty("line.separator"));
		    }
		}
	    }

	    builder.append(System.getProperty("line.separator"));
	    builder.append(System.getProperty("line.separator"));
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

    //</editor-fold>
    
    public void llamadosDependencias() { //Para cada dependencia, obtener funciones con su texto y buscar llamados a tuxedo dentro de ellas
	dependencias.forEach((dependencia) -> {
	    if (dependencia.exists() && dependencia.isFile()) {
		int abierto = 0;
		boolean inicio = false;
		String texto = new modelo.Utilidades().getTextoArchivo(dependencia);
		texto = new modelo.Utilidades().quitarComentariosJavaC(texto);
		String[] lineas = texto.split(System.getProperty("line.separator"));
		StringBuilder funcion = new StringBuilder();
		String nombreFuncion = "";
		for (int i = 0; i < lineas.length; i++) {
		    String patIni = "(void|float|double|long|char|short|int)(\\s+)(\\w+)(\\()";
		    Pattern p = Pattern.compile(patIni, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		    Matcher m = p.matcher(lineas[i]);
		    if (m.find() && !inicio) {
			inicio = true;
			nombreFuncion = m.group(3);
			if (!lineas[i].contains("{")) {
			    funcion.append(lineas[i]).append(System.getProperty("line.separator"));
			    i++;
			}
		    }
		    if (inicio) {
			funcion.append(lineas[i]).append(System.getProperty("line.separator"));
			if (lineas[i].contains("{")) {
			    abierto++;
			}
			if (lineas[i].contains("}")) {
			    abierto--;
			}
			if (abierto == 0) {
			    //System.out.println("Funcion: ");
			    //Acá se debe procesar la función para ver los llamados
			    //Todo el texto queda en: funcion.toString().
			    if(!nombreFuncion.equals("")){
				textoFunciones.add(new String[] {nombreFuncion, funcion.toString(), dependencia.getAbsolutePath()});
			    }
			    inicio = false;
			    funcion = new StringBuilder();
			    nombreFuncion = "";
			}
		    }
		}
	    }
	});
	
	//Una vez acumuladas las funciones y sus textos en la lista textoFunciones, se procesan para buscar llamados a servicios
	textoFunciones.forEach((textoFuncion)->{
	    String patronLlamado = "(tpcall)(\\()(\\\")(\\w+)";
	    Pattern p = Pattern.compile(patronLlamado, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(textoFuncion[1]);
	    while(m.find()){
		funcionLlamado.add(new String[]{textoFuncion[0], m.group(4), textoFuncion[2]});
	    }
	});
    }

    public void almacenar() {
	//int idServidor = datos.Datos.insertServidorTux(this);
	this.dependencias.forEach((dependencia) -> {
	    System.out.println("Dependencia: " + dependencia.getAbsolutePath());
	});
	for (ServicioTuxedo servicio : this.getServicios()) {
	    //datos.Datos.insertarServicio(servicio, idServidor);
	    System.out.println("Servicio: " + servicio.getNombre());
	    for (Funcion f : servicio.getFunciones()) {
		System.out.println("	Llama Funcion: " + f.getNombre());
		for (Funcion subF : f.getLlamados()) {
		    System.out.println("	Llama a: " + subF.getNombre());
		}
	    }
	}
	
	funcionLlamado.forEach((funcLlamado)->{
	    System.out.println("Funcion: " + funcLlamado[0]);
	    System.out.println("    En dependencia: " + funcLlamado[2]);
	    System.out.println("    Llama a: " + funcLlamado[0]);
	});

    }
}
