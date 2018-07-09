package modelo.osb;

import java.io.File;
import java.util.LinkedList;
import modelo.servidor.ServicioTuxedo;

/**
 * @author Jorge Silva Borda
 */
public class ServicioOSB {
    private String nombre;
    private String nameSpace; //La URL donde se despliega.
    private LinkedList<ServicioTuxedo> llamadosTuxedo;
    private LinkedList<ServicioOSB> llamadosOsb;
    private File wsdl;

    public ServicioOSB() {
        llamadosTuxedo = new LinkedList();
        llamadosOsb = new LinkedList();
    }

    public ServicioOSB(String nombre, String nameSpace) {
        this.nombre = nombre;
        this.nameSpace = nameSpace;
        llamadosTuxedo = new LinkedList();
        llamadosOsb = new LinkedList();
    }
    
    public void addLlamadoTuxedo(ServicioTuxedo servicio){
        this.llamadosTuxedo.add(servicio);
    }
    
    public void addLlamadoOsb(ServicioOSB servicio){
        this.llamadosOsb.add(servicio);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public LinkedList<ServicioTuxedo> getLlamadosTuxedo() {
        return llamadosTuxedo;
    }

    public void setLlamadosTuxedo(LinkedList<ServicioTuxedo> llamadosTuxedo) {
        this.llamadosTuxedo = llamadosTuxedo;
    }

    public LinkedList<ServicioOSB> getLlamadosOsb() {
        return llamadosOsb;
    }

    public void setLlamadosOsb(LinkedList<ServicioOSB> llamadosOsb) {
        this.llamadosOsb = llamadosOsb;
    }
    
    
}
