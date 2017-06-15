package vista;

import java.io.File;


/**
 * Clase principal que perpetra el recorrido de los archivos.
 * @author Jorge Silva Borda
 */
public class Main {
    
    public static void main(String[] args){
        Buscador b = new Buscador();
        b.buscar(new File("c:\\Users"));
    }
    
    
}
