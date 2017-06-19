package controlador;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import modelo.MakeFile;

/**
 * Clase para parsear los servidores Tuxedo y ubicar todas las
 * funciones internas.
 * @author Jorge Silva Borda
 */
public class ParserServer {
    
    private String nomServer;
    private LinkedList<String> funciones = new LinkedList();
    private MakeFile make;
    private String texto;
    private File archivoFuente = null;
    private String rutaBase;

    public ParserServer() {
    }
    
    public ParserServer(MakeFile make) throws IOException{
        this.make = make;
        this.nomServer = make.getNombreServer();
        this.rutaBase = make.getRutaBase();
        this.ajustarFuenteServidor();
    }
    
    private void ajustarFuenteServidor() throws IOException{
        for(File dep : make.getDependencias()){
            if(dep.getName().split("\\.")[0] != null){
                if(dep.getName().split("\\.")[0].equals(this.nomServer)){
                    this.archivoFuente = dep;
                    this.texto = new modelo.Lector(archivoFuente.getAbsolutePath()).getTexto();
                }
            }
        }
    }
    
    public void parse(){
        //Preparar código sin comentarios.
        texto = new modelo.Utilidades().quitarComentariosJavaC(texto);
        System.out.println("archivo make: " + this.make.getRuta());
        System.out.println("Genera Servidor: " + this.make.getGeneraServidor());
        System.out.println("Nombre servidor: " + this.getNomServer());
        
        //System.out.println("Archivo servidor: " + this.archivoFuente.getAbsolutePath());
        //Obtener las funciones del código.
        obtenerFunciones();
    }

    private void obtenerFunciones(){
        //String patron = "(\\w+)(\\s+)(\\w+)(\\s*)(([(]\\s*([^)]*)\\s*[)]))\\s*[{]";
        String patron = "(void|float|char|int)(\\s+)(\\w+)(\\s*)(([(]\\s*([^)]*)\\s*[)]))\\s*[{]";
        Pattern p = Pattern.compile(patron, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(texto);
        System.out.println("Funciones: ");
        while(m.find()){
            System.out.println(m.group(3));
            funciones.add(m.group(3));
        }
    }
    
    public String getNomServer() {
        return nomServer;
    }

    public void setNomServer(String nomServer) {
        this.nomServer = nomServer;
    }

    public LinkedList<String> getFunciones() {
        return funciones;
    }

    public void setFunciones(LinkedList<String> funciones) {
        this.funciones = funciones;
    }

    public MakeFile getMake() {
        return make;
    }

    public void setMake(MakeFile make) {
        this.make = make;
    }
    
}
