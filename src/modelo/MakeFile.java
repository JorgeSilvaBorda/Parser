package modelo;

import java.io.File;
import java.util.LinkedList;

/**
 * Modelo de archivo Make que construye servidor Tuxedo.
 * @author Jorge Silva Borda
 */
public class MakeFile {
    private LinkedList<File> dependencias;
    private String rutaBase;
    private String ruta;
    private String nombre;
    private String texto;
    private String nombreServer;
    private File archivoFuenteServidor;
    boolean generaServidor = false;
    public File archivoMake;

    public MakeFile() {
    }

    public MakeFile(LinkedList<File> dependencias, String rutaBase, String ruta, String nombre, String texto) {
        this.dependencias = dependencias;
        this.rutaBase = rutaBase;
        this.ruta = ruta;
        this.nombre = nombre;
        this.texto = texto;
    }

    public LinkedList<File> getDependencias() {
        return dependencias;
    }

    public void setDependencias(LinkedList<File> dependencias) {
        this.dependencias = dependencias;
    }

    public String getRutaBase() {
        return rutaBase;
    }

    public void setRutaBase(String rutaBase) {
        this.rutaBase = rutaBase;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
    
    public String getNombreServer(){
        return nombreServer;
    }
    
    public void setNombreServer(String nombre){
        this.nombreServer = nombre;
    }
    
    public boolean getGeneraServidor(){
        return generaServidor;
    }
    
    public void setGeneraServidor(boolean genera){
        this.generaServidor = genera;
    }
    
    public void setArchivoFuenteServidor(File archivo){
        this.archivoFuenteServidor = archivo;
    }
    
    public File getArchivoFuenteServidor(){
        return archivoFuenteServidor;
    }
}
