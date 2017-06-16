package vista;

import controlador.ParserMake;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Log;
import modelo.Parametros;
import modelo.Utilidades;

/**
 * Clase que lee de forma recursiva dentro de un directorio.
 *
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
                        Utilidades u = new Utilidades();

                        if (u.getExtension(archivo) != null) {
                            if (u.getExtension(archivo).equals("mk")) {
                                System.out.println("Se procesa: " + archivo.getName());
                                try {
                                    ParserMake parM = new ParserMake(new modelo.Lector(archivo.getAbsolutePath()).getTexto());
                                    parM.parse();
                                } catch (IOException ex) {
                                    Logger.getLogger(Buscador.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
