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
    private LinkedList<String> llamadosServicios;
    
    public ServicioTuxedo(){
        funciones = new LinkedList();
	llamadosServicios = new LinkedList();
    }
    
    public ServicioTuxedo(String nombre){
        this.nombre = nombre;
        funciones = new LinkedList();
	llamadosServicios = new LinkedList();
    }
    
    public void addLlamado(Funcion funcion){
        funciones.add(funcion);
    }
    
    public void addLlamadoServicio(String nomServicio){
	llamadosServicios.add(nomServicio);
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
    
    public LinkedList<String> getLlamadosServicios(){
	return llamadosServicios;
    }
}