package vista;

import java.io.*;
import modelo.Log;
import modelo.Parametros;

/**
 * Clase que lee de forma recursiva dentro de un directorio.
 * @author Jorge Silva Borda
 */
public class Buscador {
    Log log = Parametros.log;

    public void buscar(File arc) {
        if (arc.exists()) {
            File[] archivos = arc.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isDirectory()) {
                        buscar(archivo);
                    } else {
                        //Aplicar acá la lógica del momento en que se da con un archivo.
                        log.log(archivo.getAbsolutePath());
                    }
                }
            }
        }
    }
}
