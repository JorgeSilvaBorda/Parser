package modelo.servidor.dependencia;

import java.io.File;
import java.util.LinkedList;

/**
 * Objeto representativo de un archivo de dependencia
 * @author Jorge Silva Borda
 */
public class Dependencia {
    private File archivoFuente;
    private LinkedList<Funcion> funcionesExpuestas;
    private LinkedList<Funcion> funcionesLlamadas;

    public Dependencia() {
        funcionesExpuestas = new LinkedList();
        funcionesLlamadas = new LinkedList();
    }

    public Dependencia(File archivoFuente) {
        this.archivoFuente = archivoFuente;
        funcionesExpuestas = new LinkedList();
        funcionesLlamadas = new LinkedList();
    }
    
    public File getArchivoFuente(){
        return archivoFuente;
    }
    
    public void setArchivoFuente(File archivo){
        this.archivoFuente = archivo;
    }
    
    public LinkedList<Funcion> getFuncionesExpuestas(){
        return funcionesExpuestas;
    }
    
    public LinkedList<Funcion> getFuncionesLlamadas(){
        return funcionesLlamadas;
    }
    
    public void addFuncionExpuesta(Funcion funcion){
        funcionesExpuestas.add(funcion);
    }
    
    public void addFuncionLlamada(Funcion funcion){
        funcionesLlamadas.add(funcion);
    }
}
