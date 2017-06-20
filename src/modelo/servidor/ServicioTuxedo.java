package modelo.servidor;

import java.util.LinkedList;

/**
 * Objeto representativo de un servicio Tuxedo.
 * @author Jorge Silva Borda
 */
public class ServicioTuxedo {
    private String nombre;
    private LinkedList<ServicioTuxedo> llamadosServicios;
    
    public ServicioTuxedo(){
        llamadosServicios = new LinkedList();
    }
    
    public ServicioTuxedo(String nombre){
        this.nombre = nombre;
    }
    
    public void addLlamado(ServicioTuxedo servicio){
        
    }
}
