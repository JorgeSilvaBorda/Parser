package vista;

import controlador.ParserServer;
import controlador.ParserMake;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Log;
import modelo.MakeFile;
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
                        //log.log(archivo.getAbsolutePath());
                        Utilidades u = new Utilidades();

                        if (u.getExtension(archivo) != null) {
                            if (u.getExtension(archivo).equals("mk")) {
                                //System.out.println("Se procesa: " + archivo.getName());
                                try {
                                    ParserMake parM = new ParserMake(new modelo.Lector(archivo.getAbsolutePath()).getTexto(), u.getDirBase(archivo));
                                    parM.setRutaFull(archivo.getAbsolutePath());
                                    MakeFile make = parM.parse(); //Esto devuelve un MakeFile con todo lo que se necesita dentro.
                                    if(make.getGeneraServidor()){
                                        ParserServer parseS = new ParserServer(make);
                                        parseS.parse();
                                    }
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
