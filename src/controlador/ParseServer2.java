package controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import modelo.MakeFile;
import modelo.servidor.ServidorTux;

public class ParseServer2 {

    private String nomServer;
    private MakeFile make;
    private String texto;
    private File archivoFuente = null;
    private LinkedList<File> dependencias;
    private String rutaBase;
    private LinkedList<String[]> aliases = new LinkedList();
    public boolean conError = false;

    public ParseServer2(MakeFile make) throws IOException {
	this.make = make;
	this.nomServer = make.getNombreServer();
	this.rutaBase = make.getRutaBase();
	dependencias = make.getDependencias();
	this.ajustarFuenteServidor();
	this.archivoFuente = make.getArchivoFuenteServidor();
    }

    public ServidorTux parse() {
	ServidorTux servidor = new ServidorTux();
	servidor.nombre = make.getNombreServer();
	
	//servidor.dependencias = make.getDependencias();
	servidor.archivoFuente = make.getArchivoFuenteServidor();
	servidor.archivoMake = make.archivoMake;
	buscarAliases(make.getTexto(), this.make.archivoMake.getAbsolutePath());
	servidor.servicioAlias = this.aliases;
	
	servidor.dependencias = quitarGenerados(quitarRepetidosLista(make.getDependencias()));
	servidor.dependencias.add(make.archivoMake);
	this.dependencias = quitarGenerados(quitarRepetidosLista(make.getDependencias()));
	
	//Buscar servicios en el archivo fuente del servidor:
	LinkedList<String[]> servicios = new modelo.Utilidades().bloques(make.getArchivoFuenteServidor(), "(void)(\\s+)(\\w+)(\\s*)(\\()", 4, "{", "}");
	servidor.servicios = servicios;
	
	//Buscar todas las funciones declaradas en las dependencias
	LinkedList<String[]> depeFunciones = new LinkedList();
	this.dependencias.forEach((dependencia)->{ 
	    LinkedList<String[]> bloquesDepe = new modelo.Utilidades().bloques(dependencia, "{", "}");
	    bloquesDepe.forEach((bloqueDepe)->{
		if(!dependencia.getAbsolutePath().equals(this.archivoFuente.getAbsolutePath())){
		    depeFunciones.add(new String[]{dependencia.getAbsolutePath(), bloqueDepe[0], bloqueDepe[1]});
		}
	    });
	});
	servidor.dependenciaFuncion = depeFunciones;
	
	//Por cada funcion en dependencia, buscar dentro del bloque los llamados a otras funciones
	LinkedList<String[]> dependenciaFuncionFuncion = new LinkedList();
	servidor.dependenciaFuncion.forEach((depeFuncion)->{
	    Pattern p = Pattern.compile("(=)(\\s*)(\\w+)(\\s*)(\\()", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(depeFuncion[2]);
	    while(m.find()){
		dependenciaFuncionFuncion.add(new String[]{depeFuncion[0], depeFuncion[1], m.group(3)});
	    }
	});
	servidor.dependenciaFuncionFuncion = dependenciaFuncionFuncion;
	
	//Por cada funcion en dependencia, buscar dentro del bloque los llamados a servicios core
	LinkedList<String[]> depeFuncionCore = new LinkedList();
	servidor.dependenciaFuncion.forEach((depeFuncion)->{
	    buscarLlamadoCore(depeFuncion[2]).forEach((llamado)->{
		depeFuncionCore.add(new String[]{depeFuncion[0], depeFuncion[1], llamado});
	    });
	});
	servidor.funcionCore = depeFuncionCore;
	
	//Consumo de funciones desde los servicios 
	LinkedList<String[]> servicioFuncion = new LinkedList();
	servicios.forEach((servicio)->{
	    Pattern p = Pattern.compile("(=)(\\s+)(\\w+)(\\s*)(\\()", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(servicio[1]);
	    while(m.find()){
		servicioFuncion.add(new String[]{servicio[0], m.group(3)});
	    }
	});
	servidor.servicioFuncion = servicioFuncion;
	
	//Consumo de servicios desde los servicios
	LinkedList<String[]> servicioServicio = new LinkedList();
	servicios.forEach((servicio)->{
	    Pattern p = Pattern.compile("(tpcall)(\\()(\\\")(\\w+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(servicio[1]);
	    while(m.find()){
		servicioServicio.add(new String[]{servicio[0], m.group(4)});
	    }
	});
	servidor.servicioServicio = servicioServicio;
	
	//Consumo de servicios desde las funciones
	LinkedList<String[]> funcionServicio = new LinkedList();
	depeFunciones.forEach((depeFunc)->{
	    Pattern p = Pattern.compile("(tpcall)(\\()(\\\")(\\w+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(depeFunc[2]);
	    while(m.find()){
		funcionServicio.add(new String[]{depeFunc[0], depeFunc[1], m.group(4)});
	    }
	});
	servidor.funcionServicio = funcionServicio;
	
	return servidor;
    }

    private void ajustarFuenteServidor() throws IOException {
	for (File dep : make.getDependencias()) {
	    if (dep.getName().split("\\.")[0] != null) {
		if (dep.getName().split("\\.")[0].equals(this.nomServer)) {
		    this.archivoFuente = dep;
		    this.texto = new modelo.Lector(archivoFuente.getAbsolutePath()).getTexto();
		}
	    }
	}
    }

    private void buscarAliases(String textoMake, String rutaMake) {

	String regex = "(-s)(\\s+)(\\w+)((,)(\\w+))*(:(\\w+))?";
	Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	Matcher m = p.matcher(textoMake);
	while (m.find()) {
	    this.procesarAliases(m.group(), rutaMake);
	}
    }

    public void procesarAliases(String linea, String rutaMake) {
	if (linea.contains(":")) {
	    String servicio = linea.split("\\:")[linea.split("\\:").length - 1];
	    linea = linea.replaceAll("-s", "");
	    linea = linea.replaceAll(" ", "");
	    linea = linea.replaceAll(":" + servicio, "");
	    String[] aliases = linea.split(",");
	    for (String alias : aliases) {
		if (!servicio.equals(alias)) {
		    this.aliases.add(new String[]{rutaMake, servicio, alias});
		}
	    }
	}
    }
    
    public void quitarDependenciasRepetidas(){
	/*
	for(File dependencia : dependencias){
	    String depe = dependencia.getAbsolutePath();
	    for(int i = 0; i < dependencias.size(); i++){
		if(depe.equals(dependencias.get(i).getAbsolutePath())){
		    dependencias.remove(i);
		    quitarDependenciasRepetidas();
		}
	    }
	}*/
    }
    
    public void limpiarDependenciasGeneradas(){
	String nombre = "";
	
	for(File dependencia : dependencias){
	    if(dependencia.getName().split("\\.").length > 1){
		if(dependencia.getName().split("\\.")[1].equals("pc")){
		    nombre = dependencia.getName().split("\\.")[0];
		}
	    }
	}
	
	if(!nombre.equals("")){
	    for(int i = 0; i < dependencias.size(); i++){
		if(dependencias.get(i).getName().split("\\.").length > 1){
		    if(dependencias.get(i).getName().split("\\.")[0].equals(nombre) && dependencias.get(i).getName().split("\\.")[1].equals("c")){
			dependencias.remove(i);
			break;
		    }
		}
	    }
	}
    }
    
    public LinkedList<File> quitarRepetidosLista(LinkedList<File> listado) {
	LinkedList<File> paso = new LinkedList();
	listado.forEach((archivo)->{
	    if(!paso.contains(archivo)){
		paso.add(archivo);
	    }
	});
	return paso;
    }
    
    public LinkedList<File> quitarGenerados(LinkedList<File> listado){
	LinkedList<File> aux = listado;
	listado.forEach((archivo)->{
	    if(archivo.getName().contains(".pc")){
		String nombre = archivo.getName().split("\\.")[0];
		for(int i = 0; i < aux.size(); i++){
		    if(nombre.equals(aux.get(i).getName().split("\\.")[0])){
			if(aux.get(i).getName().split("\\.")[1].equals("c")){
			    aux.set(i, new File("removido"));
			}
		    }
		}
	    }
	});
	
	LinkedList<File> salida = new LinkedList();
	aux.forEach((archivo)->{
	    if(!archivo.getName().equals("removido")){
		salida.add(archivo);
	    }
	});
	return salida;
    }
    
    public LinkedList<String> buscarLlamadoCore(String bloqueTexto){
	LinkedList<String> llamados = new LinkedList();
	File interfaz = new File("interfaces\\servicios-core");
	if(interfaz.exists() && interfaz.isFile()){
	    try{
		BufferedReader br = new BufferedReader(new FileReader(interfaz));
		String linea;
		while((linea = br.readLine()) != null){
		    if(bloqueTexto.contains(linea)){
			llamados.add(linea);
		    }
		}
	    }catch (IOException ex) {
		System.out.println("No se puede leer el archivo de interfaces\\servicios-core");
	    }
	}
	return llamados;
    }
}
