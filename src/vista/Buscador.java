package vista;

import java.io.*;

/**
 * Clase que lee de forma recursiva dentro de un directorio.
 * @author Jorge Silva Borda
 */
public class Buscador {

    public void buscar(File arc) {
        if (arc.exists()) {
            File[] archivos = arc.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isDirectory()) {
                        buscar(archivo);
                    } else {
                        //Aplicar acá la lógica del momento en que se da con un archivo.
                        System.out.println(archivo.getAbsolutePath());
                    }
                }
            }
        }
    }
}
