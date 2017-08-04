package controlador;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import modelo.MakeFile;

/**
 * Clase para parsear los archivos Make de Tuxedo.
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
    public ParserMake(){
        
    }
    
    public ParserMake(String texto, String rutaBase){
        this.texto = texto;
        this.rutaBase = rutaBase;
    }
    
    public MakeFile parse(){
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
        generarListadoReal();
        
        //Mostrar listado por consola
        /*
        System.out.println("Archivos relacionados:");
        archivosRelacionados.forEach((archivo) -> {
            System.out.println(archivo);
        });
        System.out.println("");
        System.out.println("");
        */
        //Preparar el MakeFile para el retorno.
        this.make = new MakeFile();
        make.setTexto(texto);
        make.setDependencias(dependencias);
        make.setNombre(nomServer);
        
        if(nomVarServer == null || nomVarServer.trim().equals("")){
            make.setGeneraServidor(false);    
        }else{
            make.setGeneraServidor(true);    
        }
        make.setRuta(rutaFull);
        make.setNombre(new File(rutaFull).getName());
        make.setNombreServer(nomServer);
        make.setRutaBase(rutaBase);
        make.setArchivoFuenteServidor(archivoFuenteServidor);
        for(File dep : dependencias){
            if(dep.getName().contains(".")){
                if(dep.getName().split("\\.")[0].equals(nombreServidor)){
                    make.setArchivoFuenteServidor(dep);
                }
            }
        }
        return make;
    }
    
    public String getNombreVarServidor(){
        String re1 = "(buildserver)(.*)(-v)(\\s+)(-o)(\\s+)(\\$)(\\()";
        String re2 = "(([a-z][a-z]+)(_?)(([a-z][a-z]+)?))";
        String re3 = "(\\))";
        
        Pattern p = Pattern.compile(re1+re2+re3, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(texto);
        String varNomServer = "";
        while(m.find()){
            varNomServer = m.group(9);
        }
        return varNomServer;
    }
    
    public String getNombreServidor(String nomVarServer){
        String reg = "(" + nomVarServer + ")(\\s?)(=)(\\s?)((?:[a-z]+))(_?(?:[a-z]+)|(?:[0-9]+))+"; //Nombre del servidor hasta que de intro...
        Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(texto);
        String nombre = "";
        while(m.find()){
            nombre = m.group(5) + m.group(6);
        }
        return nombre;
    }
    
    public void getArchivosAsociados(){
        String patron = "(((\\$)(\\()(?:[a-z][a-z]+)+((_?)(?:[a-z][a-z]+))?(\\)))?(_?)(?:[a-z][a-z]+)((_?)(?:[a-z][a-z]+))?(\\.o))|((\\$)(\\()(?:[a-z][a-z]+)+((_?)(?:[a-z][a-z]+))?(\\))(\\.o))";
        String patronSegundo = "|(((?:[a-z]+)|(?:[0-9]+))((_?)((?:[a-z]+)|(?:[0-9]+)))+(\\.o+))";
        Pattern p = Pattern.compile(patron + patronSegundo, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(texto);
        LinkedList<String> lista = new LinkedList();
        while(m.find()){
            String nombre = m.group();
            if(m.group().contains("$(")){
                nombre = obtenerNombreDesdeVariable(nombre);
            }
            lista.add(nombre);
        }
        lista = new modelo.Utilidades().quitarRepetidosLista(lista);
        archivosRelacionados = lista;
    }
    
    public String obtenerNombreDesdeVariable(String nomConVariable){
        String nomvar = nomConVariable.replace("$(", "");
        String[] arr = nomvar.split("\\)");
        //(\s?)(=)(\s?)((?:[a-z][a-z]+(?:[0-9]+)?)((_?)(?:[a-z]+)?))
        //String patron = "(" + arr[0] + ")(\\s?)(=)(\\s?)((?:[a-z][a-z]+)((_?)(?:[a-z][a-z]+)?|(?:[0-9]+)))";
        String patron = "(" + arr[0] + ")(\\s?)(=)(\\s?)((?:[a-z][a-z]+(?:[0-9]+)?)((_?)(?:[a-z]+)?))";

        Pattern p = Pattern.compile(patron, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(texto);
        
        while(m.find()){
            return m.group(5);
        }
        return "";
    }
    
    public void generarListadoReal(){
        LinkedList<String> listaFinal = new LinkedList();
        File archivo = new File(this.rutaBase);
        File[] archivos = archivo.listFiles();
        for(String archRel : archivosRelacionados){
            for(File ar : archivos){
                String nomArchivo = ar.getName().trim();
                String nomPc = archRel.replace(".o", ".pc").trim();
                String nomC = archRel.replace(".o", ".c").trim();
                if(!archRel.contains(".c")){
                    nomC = archRel + ".c";
                }
                if(nomArchivo.equals(nomPc)){
                    listaFinal.add(nomArchivo);
                    dependencias.add(ar);
                }
                
                if(nomArchivo.equals(nomC)){
                    listaFinal.add(nomArchivo);
                    dependencias.add(ar);
                }
                //System.out.println("archRel: " + archRel);
                if(nombreServidor.equals(nomC)){
                    this.archivoFuenteServidor = ar;
                }
            }
        }
        listaFinal = new modelo.Utilidades().quitarRepetidosLista(listaFinal);
        archivosRelacionados = listaFinal;
    }
    
    public File getArchivoFuenteServidor(){
        return archivoFuenteServidor;
    }
    
    public void setTexto(String texto){
        this.texto = texto;
    }
    
    public void setRutaFull(String ruta){
        this.rutaFull = ruta;
    }
    
    public LinkedList<File> getDependencias(){
        return dependencias;
    }
}
