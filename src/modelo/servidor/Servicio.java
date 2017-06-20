package modelo.servidor;

import java.util.LinkedList;

/**
 * Objeto representativo de un servicio Tuxedo.
 * @author Jorge Silva Borda
 */
public class Servicio {
    private String nombre;
    private LinkedList<Servicio> llamadosServicios;
    
    public Servicio(){
        llamadosServicios = new LinkedList();
    }
    
    public Servicio(String nombre){
        this.nombre = nombre;
    }
    
    public void addLlamado(Servicio servicio){
        
    }
}
