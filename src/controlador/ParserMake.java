package controlador;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase para parsear los archivos Make de Tuxedo.
 * @author Jorge Silva Borda
 */
public class ParserMake {
    
    private String texto;
    
    public ParserMake(){
        
    }
    
    public ParserMake(String texto){
        this.texto = texto;
    }
    
    public void parse(){
        //Quitar los comentarios del archivo para evitar leer código comentado.
        texto = new modelo.Utilidades().quitarComentariosMake(texto);
        
        //Buscar el buildserver para ubicar el patrón que contiene el nombre del servidor.
        String nomVarServer = getNombreVarServidor();
        
        //Luego de encontrar el nombre de la variable que contiene el nombre del servidor, se busca dicha variable en el make.
        String nomServer = getNombreServidor(nomVarServer);
        
        //Luego de encontrar el nombre del servidor, se busca los archivos asociados...
        
    }
    
    public String getNombreVarServidor(){
        String re1 = "(buildserver)(.*)(-v)(\\s+)(-o)(\\s+)(\\$)(\\()";
        String re2 = "(([a-z][a-z]+)(_?)(([a-z][a-z]+)?))";
        String re3 = "(\\))";
        
        Pattern p = Pattern.compile(re1+re2+re3, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(texto);
        String varNomServer = "";
        while(m.find()){
            varNomServer = m.group(9);
            //System.out.println("El nombre de la variable del servidor es: " + varNomServer);
        }
        return varNomServer;
    }
    
    public String getNombreServidor(String nomVarServer){
        String reg = "(NOMBRE_SERVIDOR)(\\s?)(=)(\\s?)((?:[a-z][a-z]+)((_?)(?:[a-z][a-z]+)))"; //Nombre del servidor hasta que de intro...

        Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(texto);
        String nombre = "";
        while(m.find()){
            nombre = m.group(5);
        }
        //System.out.println("El nombre del servidor es: " + nombre);
        return nombre;
    }
    
    public void setTexto(String texto){
        this.texto = texto;
    }
}
