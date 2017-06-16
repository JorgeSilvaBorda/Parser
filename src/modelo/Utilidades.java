package modelo;

import java.util.regex.Pattern;

/**
 * Conjunto de operaciones útiles.
 * @author Jorge Silva Borda
 */
public class Utilidades {
    public Utilidades(){
        
    }
    
    public String getExtension(java.io.File archivo){
        if(archivo.exists() && archivo.isFile()){
            String comp[] = archivo.getName().split("\\.");
            if(comp.length <= 1 && comp != null){
                return null;
            }else{
                return comp[comp.length - 1];
            }
        }
        return null;
    }
    
    public String quitarComentariosMake(String texto){
        String patron = "(^#.*?)(?:(?:\\r*\\n){1})";
        texto = texto.replaceAll(patron, "");
        return texto;
    }
}
