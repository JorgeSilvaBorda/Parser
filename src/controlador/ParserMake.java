package controlador;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import modelo.MakeFile;

/**
 * Clase para parsear los archivos Make de Tuxedo.
 *
 * @author Jorge Silva Borda
 */
public class ParserMake {

    private String texto;
    private LinkedList<String> archivosRelacionados = new LinkedList();
    private LinkedList<File> dependencias = new LinkedList();
    private String rutaBase;
    private MakeFile make;
    private String rutaFull;
    private File archivoFuenteServidor;
    private String nombreServidor;
    private LinkedList<String[]> servicioAlias = new LinkedList();
    private LinkedList<String> posiblesNombresServidor = new LinkedList();
    public File archivoMakeFuente;

    public ParserMake() {
	this.make = new MakeFile();
    }

    public ParserMake(String texto, String rutaBase) {
	this.texto = texto;
	this.rutaBase = rutaBase;
	this.make = new MakeFile();
    }

    public MakeFile parse() {
	make.archivoMake = this.archivoMakeFuente;
	//Quitar los comentarios del archivo para evitar leer código comentado.
	texto = new modelo.Utilidades().quitarComentariosMake(texto);
	//System.out.println("Directorio base: " + this.rutaBase);

	//Buscar el buildserver para ubicar el patrón que contiene el nombre del servidor.
	String nomVarServer = getNombreVarServidor();

	//Luego de encontrar el nombre de la variable que contiene el nombre del servidor, se busca dicha variable en el make.
	String nomServer = getNombreServidor(nomVarServer);
	nombreServidor = nomServer;
	//Luego de encontrar el nombre del servidor, se busca los archivos asociados...
	getArchivosAsociados();

	//Limpiar las extensiones .o del make y pasarlas a .c y .pc respectivamente.
	//generarListadoReal();
	generarListadoReal2();

	
	make.setTexto(texto);
	make.setDependencias(dependencias);
	make.setNombre(nomServer);
	if (nomVarServer == null || nomVarServer.trim().equals("")) {
	    make.setGeneraServidor(false);
	} else {
	    make.setGeneraServidor(true);
	}
	make.setRuta(rutaFull);
	make.setNombre(new File(rutaFull).getName());
	make.setNombreServer(nomServer);
	make.setRutaBase(rutaBase);
	return make;
    }

    public String getNombreVarServidor() {
	
	//String re1 = "(buildserver)(.*)(-v)(\\s+)(-o)(\\s+)(\\$)(\\()";
	String re1 = "(buildserver)(.*)(-v)?(\\s+)(-o)(\\s+)(\\$)(\\()";
	//String re2 = "(([a-z][a-z]+)(_?)(([a-z][a-z]+)?))";
	String re2 = "(\\w+)";
	String re3 = "(\\))";

	Pattern p = Pattern.compile(re1 + re2 + re3, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	
	Matcher m = p.matcher(texto);
	String varNomServer = "";
	while (m.find()) {
	    varNomServer = m.group(9);
	    //System.out.println("Nombre de la variable: " + varNomServer);
	}
	return varNomServer;
    }

    public String getNombreServidor(String nomVarServer) {
	String reg = "(" + nomVarServer + ")(\\s?)(=)(\\s?)((?:[a-z]+))(_?(?:[a-z]+)|(?:[0-9]+))+"; //Nombre del servidor hasta que de intro...
	
	Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	Matcher m = p.matcher(texto);
	String nombre = "";
	while (m.find()) {
	    nombre = m.group(5) + m.group(6);
	    posiblesNombresServidor.add(m.group(5) + m.group(6));
	    //System.out.println("Nombre servidor: " + nombre);
	    return nombre;
	}
	return nombre;
    }

    public void getArchivosAsociados() {
	String patron = "(((\\$)(\\()(?:[a-z][a-z]+)+((_?)(?:[a-z][a-z]+))?(\\)))?(_?)(?:[a-z][a-z]+)((_?)(?:[a-z][a-z]+))?(\\.o))|((\\$)(\\()(?:[a-z][a-z]+)+((_?)(?:[a-z][a-z]+))?(\\))(\\.o))";
	String patronSegundo = "|(((?:[a-z]+)|(?:[0-9]+))((_?)((?:[a-z]+)|(?:[0-9]+)))+(\\.o+))";
	Pattern p = Pattern.compile(patron + patronSegundo, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	Matcher m = p.matcher(texto);
	LinkedList<String> lista = new LinkedList();
	while (m.find()) {
	    String nombre = m.group();
	    if (m.group().contains("$(")) {
		nombre = obtenerNombreDesdeVariable(nombre);
	    }
	    lista.add(nombre);
	}
	lista = new modelo.Utilidades().quitarRepetidosLista(lista);
	archivosRelacionados = lista;
    }

    public String obtenerNombreDesdeVariable(String nomConVariable) {
	String nomvar = nomConVariable.replace("$(", "");
	String[] arr = nomvar.split("\\)");
	//(\s?)(=)(\s?)((?:[a-z][a-z]+(?:[0-9]+)?)((_?)(?:[a-z]+)?))
	//String patron = "(" + arr[0] + ")(\\s?)(=)(\\s?)((?:[a-z][a-z]+)((_?)(?:[a-z][a-z]+)?|(?:[0-9]+)))";
	String patron = "(" + arr[0] + ")(\\s?)(=)(\\s?)((?:[a-z][a-z]+(?:[0-9]+)?)((_?)(?:[a-z]+)?))";

	Pattern p = Pattern.compile(patron, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	Matcher m = p.matcher(texto);

	while (m.find()) {
	    return m.group(5);
	}
	return "";
    }

    public void generarListadoReal() {
	//System.out.println("Nombre servidor: " + this.nombreServidor);
	LinkedList<String> listaFinal = new LinkedList();
	File archivo = new File(this.rutaBase);
	File[] archivos = archivo.listFiles();
	for (String archRel : archivosRelacionados) {
	    for (File ar : archivos) {
		String nomArchivo = ar.getName().trim();
		String nomPc = archRel.replace(".o", ".pc").trim();
		String nomC = archRel.replace(".o", ".c").trim();
		File arServidor = new File(this.rutaBase + "\\" + this.nombreServidor + ".c");
		if (arServidor.exists()) {
		    this.archivoFuenteServidor = arServidor;
		    make.setArchivoFuenteServidor(archivoFuenteServidor);
		}
		arServidor = new File(this.rutaBase + "\\" + this.nombreServidor + ".pc");
		if (arServidor.exists()) {
		    this.archivoFuenteServidor = arServidor;
		    make.setArchivoFuenteServidor(archivoFuenteServidor);
		}

		if (nomArchivo.split("\\.")[0].equals(nomC.split("\\.")[0]) || nomArchivo.split("\\.")[0].equals(nomPc.split("\\.")[0])) {
		    File arch = new File(nomC + ".c");
		    if (arch.exists()) {
			listaFinal.add(nomC + ".c");
			dependencias.add(arch);
		    }

		    arch = new File(nomC + ".pc");
		    if (arch.exists()) {
			listaFinal.add(nomC + ".pc");
			dependencias.add(arch);
		    }

		    arch = new File(nomPc + ".c");
		    if (arch.exists()) {
			listaFinal.add(nomPc + ".c");
			dependencias.add(arch);
		    }

		    arch = new File(nomPc + ".pc");
		    if (arch.exists()) {
			listaFinal.add(nomPc + ".pc");
			dependencias.add(arch);
		    }
		}
	    }
	}
	listaFinal = new modelo.Utilidades().quitarRepetidosLista(listaFinal);
	archivosRelacionados = listaFinal;
    }
    
    public void generarListadoReal2(){
	File archivo = new File(this.rutaBase);
	LinkedList<String> listaFinal = new LinkedList();
	File[] archivos = archivo.listFiles();
	for(File ar : archivos){
	    if (ar.isFile()){
		for(String archRel : archivosRelacionados){
		    String nombre = archRel.split("\\.")[0];
		    if(ar.getName().equals(nombre + ".c")){
			listaFinal.add(nombre + ".c");
			dependencias.add(ar);
		    }else if(ar.getName().equals(nombre + ".pc")){
			listaFinal.add(nombre + ".pc");
			dependencias.add(ar);
		    }else if(ar.getName().equals(nombre + "_sql.pc")){
			listaFinal.add(nombre + "_sql.pc");
			dependencias.add(ar);
		    }else if(ar.getName().equals(nombre + "_fun.pc")){
			listaFinal.add(nombre + "_fun.pc");
			dependencias.add(ar);
		    }else if(ar.getName().equals(nombre + "_sql.c")){
			listaFinal.add(nombre + "_sql.c");
			dependencias.add(ar);
		    }else if(ar.getName().equals(nombre + "_fun.c")){
			listaFinal.add(nombre + "_fun.c");
			dependencias.add(ar);
		    }else if(ar.getName().equals(nombre + "_fun_tux.c")){
			listaFinal.add(nombre + "_fun_tux.c");
			dependencias.add(ar);
		    }else if(ar.getName().equals(nombre + "_funTux.c")){
			listaFinal.add(nombre + "_funTux.c");
			dependencias.add(ar);
		    }else if(ar.getName().equals(nombre + "_fun_Fcd.c")){
			listaFinal.add(nombre + "_fun_Fcd.c");
			dependencias.add(ar);
		    }
		    
		    if(ar.getName().equals(this.nombreServidor + ".c")){
			this.archivoFuenteServidor = ar;
			this.make.setArchivoFuenteServidor(ar);
		    }
		    if(ar.getName().equals(this.nombreServidor + ".pc")){
			this.archivoFuenteServidor = ar;
			this.make.setArchivoFuenteServidor(ar);
		    }
		}
	    }
	}
	//Procesar dependencias con excepciones-------------------------------------------------------------------------------------------------------
	/**
	 * En algunos casos no se logra identificar correctamente los archivos relacionados, ya que el nombre de la variable que contiene el nombre
	 * del servidor, es diferente del nombre de la variable que contiene los archivos asociados.
	**/
	listaFinal = procesarDependenciasExcepciones(this.make, listaFinal);
	//Fin proceso excepciones---------------------------------------------------------------------------------------------------------------------
	
	listaFinal = new modelo.Utilidades().quitarRepetidosLista(listaFinal);
	archivosRelacionados = listaFinal;
    }

    public LinkedList<String> procesarDependenciasExcepciones(MakeFile make, LinkedList<String> lista){
	if(make.getArchivoFuenteServidor().getName().equals("cctCuentaCorrienteFcc.c")){
	    File archivo = new File(this.rutaBase + "\\cuentaCorrienteFcc_fun_Fcd.c");
	    if(archivo.exists()){
		lista.add("cuentaCorrienteFcc_fun_Fcd.c");
		dependencias.add(archivo);
	    }
	    archivo = new File(this.rutaBase + "\\cuentaCorrienteFcc_sql.pc");
	    if(archivo.exists()){
		lista.add("cuentaCorrienteFcc_sql.pc");
		dependencias.add(archivo);
	    }
	    archivo = new File(this.rutaBase + "\\cuentaCorrienteFcc_fun_tux.c");
	    if(archivo.exists()){
		lista.add("cuentaCorrienteFcc_fun_tux.c");
		dependencias.add(archivo);
	    }
	}
	return lista;
    }
    
    public File getArchivoFuenteServidor() {
	return archivoFuenteServidor;
    }

    public void setTexto(String texto) {
	this.texto = texto;
    }

    public void setRutaFull(String ruta) {
	this.rutaFull = ruta;
    }

    public LinkedList<File> getDependencias() {
	return dependencias;
    }
}
