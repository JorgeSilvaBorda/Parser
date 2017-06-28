package modelo.servidor;

import java.util.LinkedList;
import modelo.servidor.dependencia.Funcion;

/**
 * Objeto representativo de un servicio Tuxedo.
 * @author Jorge Silva Borda
 */
public class ServicioTuxedo {
    private String nombre;
    private LinkedList<Funcion> funciones;
    
    public ServicioTuxedo(){
        funciones = new LinkedList();
    }
    
    public ServicioTuxedo(String nombre){
        this.nombre = nombre;
        funciones = new LinkedList();
    }
    
    public void addLlamado(Funcion funcion){
        funciones.add(funcion);
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public LinkedList<Funcion> getFunciones(){
        return funciones;
    }
}