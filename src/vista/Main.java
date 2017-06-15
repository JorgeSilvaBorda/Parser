package vista;

import java.io.File;

/**
 * Clase principal que perpetra el recorrido de los archivos.
 *
 * @author Jorge Silva Borda
 */
public class Main {

    public static void main(String[] args) {
        if (procesaArgumentos(args) == 0) {
            Buscador b = new Buscador();
            b.buscar(new File(args[0]));
        }
    }

    private static int procesaArgumentos(String[] args) {
        //Validar que contenga argumentos
        if (args.length < 1) {
            System.out.println("Error(1): El primer argumento debe ser la ruta del directorio base del inicio de la búsqueda.");
            System.out.println("Ejemplo: ");
            System.out.println("java -jar Parser.jar \"c:\\Directorio\\De\\Busqueda\"");
            return 1;
        }
        
        if(args[0].equals("e")){
            //Mostrar errores
            return 500;
        }
        
        if(args[0].equals("?")){
            //Mostrar ayuda
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
}
