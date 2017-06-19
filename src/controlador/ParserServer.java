package controlador;

import java.io.File;
import java.util.LinkedList;
import modelo.MakeFile;

/**
 * Clase para parsear los servidores Tuxedo y ubicar todas las
 * funciones internas.
 * @author Jorge Silva Borda
 */
public class ParserServer {
    
    private String nomServer;
    private LinkedList<String> funciones;
    private MakeFile make;
    private String texto;
    private File archivoFuente = null;
    private String rutaBase;

    public ParserServer() {
    }
    
    public ParserServer(MakeFile make){
        this.make = make;
        this.nomServer = make.getNombreServer();
        this.rutaBase = make.getRutaBase();
        this.ajustarFuenteServidor();
    }
    
    private void ajustarFuenteServidor(){
        for(File dep : make.getDependencias()){
            if(dep.getName().split("\\.")[0] != null){
                if(dep.getName().split("\\.")[0].equals(this.nomServer)){
                    this.archivoFuente = dep;
                }
            }
        }
    }
    
    public void parse(){
        //Preparar código sin comentarios.
        texto = new modelo.Utilidades().quitarComentariosJavaC(texto);
        
        //Obtener las funciones del código.
        obtenerFunciones();
    }

    private void obtenerFunciones(){
        
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
