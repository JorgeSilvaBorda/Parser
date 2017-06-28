package modelo.servidor.dependencia;

import java.util.LinkedList;

/**
 * Objeto representativo de una función.
 * @author Jorge Silva Borda
 */
public class Funcion {

    private String nombre;
    private String tipo; //Llamada o Declarada
    private boolean esServicio;
    private LinkedList<Funcion> llamados;

    public Funcion() {
        llamados = new LinkedList();
    }

    public Funcion(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
        llamados = new LinkedList();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setEsServicio(boolean es) {
        this.esServicio = es;
    }

    public boolean getEsServicio() {
        return esServicio;
    }

    public void addLlamado(Funcion llamada) {
        llamados.add(llamada);
    }

    public LinkedList<Funcion> getLlamados() {
        return llamados;
    }

    @Override
    public String toString() {
        return "Función: " + nombre + "|" + tipo + System.getProperty("line.separator");
    }
}
