package modelo;

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
            if(comp.length <= 1){
                return null;
            }else{
                return comp[comp.length - 1];
            }
        }
        return null;
    }
}
