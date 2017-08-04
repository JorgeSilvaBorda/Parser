package modelo;

import java.io.File;
import java.util.LinkedList;

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
    
    public String quitarComentariosJavaC(String texto){
        if(texto != null){
	    String patron = "//.*?\\n|/\\*.*?\\*/";
            //String patron = "((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/";
            texto = texto.replaceAll(patron, "");
            return texto;
        }
        return "";
    }
    
    public String quitarComentariosXML_HTML(String texto){
        if(texto != null){
            String patron = "(<!--)(.*?)((-->)(-->)*)";
            texto = texto.replaceAll(patron, "");
            return texto;
        }
        return "";
    }
    
    public LinkedList<String> quitarRepetidosLista(LinkedList<String> listado){
        //Revisar algoritmo para quitar duplicados
        for(int i = 0; i < listado.size(); i++){
            String aux = listado.get(i);
            for(int x = i+1; x < listado.size(); x++){
                if(aux.equals(listado.get(x))){
                    listado.set(x, "");
                }
            }
        }
        return quitarNulosLista(listado);
    }
    
    public LinkedList<String> quitarNulosLista(LinkedList lista){
        for(int i = 0; i< lista.size(); i++){
            if(lista.get(i).equals("")){
                lista.remove(i);
                return quitarNulosLista(lista);
            }
        }
        return lista;
    }
    
    public String getDirBase(File archivo){
        return archivo.getParentFile().getAbsolutePath();
    }
    
    public boolean existeEnLista(Object o, LinkedList lista){
        for(Object objeto : lista){
            if(objeto.toString().equals(o.toString())){
                return true;
            }
        }
        return false;
    }
    
    public String quitarCdata(String texto){
	String patron1 = "(<\\!\\[CDATA\\[)";
	String patron2 = "\\]\\]";
	texto = texto.replaceAll(patron1, "");
	texto = texto.replaceAll(patron2, "");
	return texto;
    }
}
