package modelo;

/**
 * Clase para poder mostrar los Log de procesamiento en la consola como salida.
 * @author Jorge Silva Borda
 */
public class Log {
    
    public Log(){
        
    }
    
    public void log(String msg){
        if(Parametros.LOG){
            System.out.println(msg);
        }
    }
}
