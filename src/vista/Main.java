package vista;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import logger.Logger;
import modelo.Parametros;
import org.eclipse.core.runtime.CoreException;

/**
 * Clase principal que perpetra el recorrido de los archivos.
 * @author Jorge Silva Borda
 */
public class Main {

    private static final String SEP = modelo.Parametros.SEP;

    public static void main(String[] args) throws CoreException {
	modelo.Escritor.iniciarInterfaces();
        if (procesaArgumentos(args) == 0) {
	    Logger logger = new Logger("log.txt");
            Buscador b = new Buscador();
	    logger.log("Inicia el proceso de parseo de fuentes.");
            b.buscar(new File(args[0]));
	    logger.log("Fin del proceso de parseo de fuentes.");
        }
    }

    private static int procesaArgumentos(String[] args) {
        //Validar que contenga argumentos
        if (args.length < 1) {
            System.out.println("Error(1): El primer argumento debe ser la ruta del directorio base del inicio de la búsqueda.");
            System.out.println("Ejemplo: ");
            System.out.println("java -jar Parser.jar \"Directorio" + SEP + "De" + SEP + "Busqueda\"");
            return 1;
        }

        if (args[0].equals("e")) {
            //Mostrar errores
            return 500;
        }
        
        for(String ar : args){
            if (ar.equals("l")){
                Parametros.LOG = true;
            }
        }

        if (args[0].equals("?")) {
            try {
                ayuda();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            return 600;
        }

        //Validar que la ruta indicada exista.
        File archivo = new File(args[0]);
        if (!archivo.exists()) {
            System.out.println("Error(2): El directorio base no existe.");
            return 2;
        }

        //Validar que la ruta indicada corresponda a un directorio
        if (!archivo.isDirectory()) {
            System.out.println("Error(3): La ruta indicada corresponde a un archivo, no a un directorio.");
            return 3;
        }

        return 0;
    }

    private static void ayuda() throws FileNotFoundException, IOException {
        System.out.println("Ayuda del programa parser de código fuente:\n"
                + "\n"
                + "## Para poder ejecutar el programa, se debe llamar desde la consola con el comando:\n"
                + "\n"
                + "java -jar Parser.jar [ayuda | (Ruta_base) | (Ruta_base log)]\n"
                + "\n"
                + "## Parámetros:\n"
                + "\n"
                + "Ruta_base: El directorio base donde se iniciará la búsqueda de los archivos.\n"
                + "ayuda: Al indicar como parámetro '?' (sin las comillas), se muestra este texto.\n"
                + "log: Si luego de la Ruta_base se agrega el caracter 'l', se mostrará el log de\n"
                + "estado del procesamiento.\n"
                + "\n"
                + "--Jorge Silva Borda");
    }
}
