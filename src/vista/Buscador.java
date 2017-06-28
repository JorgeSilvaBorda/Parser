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
import modelo.servidor.ServidorTuxedo;
import org.eclipse.core.runtime.CoreException;

/**
 * Clase que lee de forma recursiva dentro de un directorio.
 * @author Jorge Silva Borda
 */
public class Buscador {

    Log log = Parametros.log;

    public void buscar(File arc) throws CoreException {
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
                                System.out.println("Se procesa make: " + archivo.getAbsolutePath());
                                try {
                                    ParserMake parM = new ParserMake(new modelo.Lector(archivo.getAbsolutePath()).getTexto(), u.getDirBase(archivo));
                                    parM.setRutaFull(archivo.getAbsolutePath());
                                    MakeFile make = parM.parse(); //Esto devuelve un MakeFile con todo lo que se necesita dentro.
                                    if(make.getGeneraServidor()){
                                        System.out.println("El archivo fuente del servidor: " + make.getArchivoFuenteServidor().getAbsolutePath());
                                        ParserServer parseS = new ParserServer(make);
                                        ServidorTuxedo servidor = parseS.parse();
                                        servidor.setArchivoFuente(make.getArchivoFuenteServidor());
                                        System.out.println(servidor);
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
