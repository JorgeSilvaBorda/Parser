package vista;

import controlador.ParserMake;
import java.io.*;
import logger.Logger;
import modelo.Escritor;
import modelo.Log;
import modelo.MakeFile;
import modelo.Parametros;
import modelo.Utilidades;
import modelo.servidor.ServidorTux;
import org.eclipse.core.runtime.CoreException;

/**
 * Clase que lee de forma recursiva dentro de un directorio.
 *
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
			Utilidades u = new Utilidades();

			if (u.getExtension(archivo) != null && !archivo.getAbsolutePath().contains("batch")) {
			    if (u.getExtension(archivo).equals("mk")) {
				System.out.println("Se procesa make: " + archivo.getAbsolutePath());
				try {
				    ParserMake parM = new ParserMake(new modelo.Lector(archivo.getAbsolutePath()).getTexto(), u.getDirBase(archivo));
				    parM.archivoMakeFuente = archivo;
				    parM.setRutaFull(archivo.getAbsolutePath());
				    MakeFile make = parM.parse(); //Esto devuelve un MakeFile con todo lo que se necesita dentro.
				    if (make.getGeneraServidor()) {
					try {
					    //System.out.println("El archivo fuente del servidor: " + make.getArchivoFuenteServidor().getAbsolutePath());
					} catch (Exception ex) {
					    new Logger("log.txt").log("Error: No se pudo obtener el archivo fuente del servidor.");
					    new Logger("log.txt").log("Archivo Make en proceso: " + archivo.getAbsolutePath());
					}

					if (make.getArchivoFuenteServidor() == null) {
					    modelo.Escritor.escribirError(make);
					} else {
					    //ParserServer parseS = new ParserServer(make);
					    controlador.ParseServer2 parser = new controlador.ParseServer2(make);
					    ServidorTux serv = parser.parse();
					    //Acá generar las interfaces para guardar el servidor
					    //modelo.Escritor.escribirServidor(serv);
					    serv.mostrarme();
					    Escritor.escribirServidor(serv);
					}
				    }
				} catch (IOException ex) {
				    System.out.println("No se pudo procesar el archivo: " + archivo.getAbsolutePath());
				    System.out.println(ex);
				}
			    }/* else if (u.getExtension(archivo).equals("wsdl")) {
				ParserOSB parO = new ParserOSB(archivo);
				System.out.println("Se procesa WSDL: " + archivo.getAbsolutePath());
				parO.parse();
			    }*/
			}
		    }
		}
	    }
	}
    }
}
