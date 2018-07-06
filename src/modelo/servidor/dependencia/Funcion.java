package modelo.servidor.dependencia;

import java.io.File;
import java.util.LinkedList;

/**
 * Objeto representativo de una función.
 * @author Jorge Silva Borda
 */
public class Funcion {

    public String nombre;
    public String tipo; //Llamada o Declarada
    public boolean esServicio;
    public LinkedList<Funcion> llamados;
    public String texto = "";
    public File dependenciaContenedora;

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
