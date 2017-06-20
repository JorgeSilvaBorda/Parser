package modelo.servidor;

import java.io.File;
import java.util.LinkedList;

/**
 * Objeto representativo de servidor Tuxedo
 * @author Jorge Silva Borda
 */
public class Servidor {
    private String nombre;
    private LinkedList<Servicio> serviciosExpuestos;
    private File archivoFuente;
    private LinkedList<File> dependencias;
    
    public Servidor(){
        serviciosExpuestos = new LinkedList();
        dependencias = new LinkedList();
        this.archivoFuente = null;
    }
    
    public Servidor(String nombre){
        this.nombre = nombre;
        serviciosExpuestos = new LinkedList();
        dependencias = new LinkedList();
        this.archivoFuente = null;
    }
    
    public Servidor(String nombre, File archivoFuente){
        this.nombre = nombre;
        this.archivoFuente = archivoFuente;
        serviciosExpuestos = new LinkedList();
        dependencias = new LinkedList();
    }
    
    public void addServicio(Servicio servicio){
        this.serviciosExpuestos.add(servicio);
    }
    
    public void addDependencia(File dependencia){
        dependencias.add(dependencia);
    }
    
    public LinkedList<File> getDependencias(){
        return dependencias;
    }
    
    public LinkedList<Servicio> getServicios(){
        return serviciosExpuestos;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public File getArchivoFuente(){
        return archivoFuente;
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public void setArchivoFuente(File archivo){
        this.archivoFuente = archivo;
    }
}
