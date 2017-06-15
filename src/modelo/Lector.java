package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase para poder leer los archivos y realizar las transformaciones 
 * necesarias.
 * @author Jorge Silva Borda
 */
public class Lector {
   
    private String rutaArchivo;

    /**
     * Constructor vacío.
     */
    public Lector() {
    }

    /**
     * Constructor.
     * @param rutaArchivo String. La ruta del archivo que será leído.
     */
    public Lector(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
    
    /**
     * Método para poder extraer el texto de un archivo.
     * @return String con el contenido del archivo leído.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public String getTexto() throws FileNotFoundException, IOException{
        File archivo = new File(this.getRutaArchivo());
        if(archivo.exists()){
            FileReader reader = new FileReader(archivo);
            BufferedReader br = new BufferedReader(reader);
            String linea;
            StringBuilder builder = new StringBuilder();
            while((linea = br.readLine()) != null){
                builder.append(linea);
            }
            return builder.toString();
        }
        return null;
    }
    
    /**
     * Devuelve la ruta del archivo configurada.
     * @return String con la ruta del archivo.
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    /**
     * Ajusta la ruta del archivo a leer.
     * @param rutaArchivo 
     */
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
}