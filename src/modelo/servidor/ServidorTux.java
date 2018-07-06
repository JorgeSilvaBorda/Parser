package modelo.servidor;

import java.io.File;
import java.util.LinkedList;

public class ServidorTux {
    
    public String nombre;
    public File archivoFuente;
    public File archivoMake;
    public LinkedList<String[]> servicioAlias; //String[] = {NOMBRE_SERVICIO, ALIAS_SERVICIO}
    public LinkedList<String[]> servicios; //String[] = {NOMBRE_SERVICIO, CODIGO_SERVICIO}
    public LinkedList<File> dependencias;
    public LinkedList<String[]> dependenciaFuncion; //String[] = {PATH_DEPENDENCIA, NOMBRE_FUNCION, CODIGO_FUNCION}
    public LinkedList<String[]> dependenciaFuncionFuncion; //String[] = {PATH_DEPENDENCIA, NOMBRE_FUNCION, NOMBRE_FUNCION_EJECUTADA}
    public LinkedList<String[]> servicioFuncion; //String[] = {NOMBRE_SERVICIO, NOMBRE_FUNCION_LLAMADA}
    public LinkedList<String[]> servicioServicio; //String[] ={NOMBRE_SERVICIO_EXPUESTO, NOMBRE_SERVICIO_CONSUMIDO}
    public LinkedList<String[]> funcionServicio; //String[] = {NOMBRE_FUNCION, NOMBRE_SERVICIO_LLAMADO}
    public LinkedList<String[]> funcionCore; //String[] = {DEPENDENCIA, NOMBRE_FUNCION, SERVICIO_CORE_LLAMADO}
    
    public void mostrarme(){
	System.out.println("Nombre Servidor: " + this.nombre);
	System.out.println("Archivo fuente: " + archivoFuente.getAbsolutePath());
	System.out.println("Archivo make generador: " + archivoMake.getAbsolutePath());
	System.out.println("Dependencias:");
	dependencias.forEach((dependencia)->{
	    System.out.println("    " + dependencia.getAbsolutePath());
	});
	System.out.println("Servicios:");
	servicios.forEach((servicio) -> {
	    System.out.println("    " + servicio[0]);
	});
	System.out.println("Alias servicios:");
	servicioAlias.forEach((servicioAlias)->{
	    System.out.println("    Servicio: " + servicioAlias[0] + " => Alias: " + servicioAlias[1]);
	});
	System.out.println("Dependencia:funcion:");
	dependenciaFuncion.forEach((depeFunc)->{
	    System.out.println("    " + depeFunc[0] + ":" + depeFunc[1]);
	});
	System.out.println("Servicio llama funcion:");
	servicioFuncion.forEach((servFunc)->{
	    System.out.println("    " + servFunc[0] + " Llama => " + servFunc[1]);
	});
	funcionServicio.forEach((funcServ)->{
	    System.out.println("    Dependencia " + funcServ[0] + " declara func: " + funcServ[1] + " Llama a: " + funcServ[2]);
	});
    }
}
