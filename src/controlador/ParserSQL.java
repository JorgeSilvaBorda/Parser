package controlador;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase para parsear querys de SQL
 *
 * @author Jorge Silva Borda
 */
public class ParserSQL {

    public ParserSQL() {

    }

    public void parsearVariable(String nodo) {
        String patron = "(\")(select|insert|update|delete)(.*?)(;;*)";
        String declarador = nodo.split("=")[0];
        String variable = declarador.split(" ")[declarador.split(" ").length - 1];
        if (variable.contains("*")) {
            variable = variable.split("\\*")[1];
        }
        //System.out.print("Variable: " + variable);
        Pattern p = Pattern.compile(patron, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(nodo);

        while (m.find()) {
            //System.out.println(" Tiene query");
            buscarTablas(m.group());
        }
    }

    public void parsearFuncion(String nodo) {
        //String patron = "(EXEC SQL)(\\s?)(SELECT|INSERT|UPDATE|DELETE)(.*?);;*";
        String patron = "(SELECT|INSERT|UPDATE|DELETE)(.*?);;*";
        nodo = emparejarTexto(nodo);
        String declarador = nodo.split("->")[1];
        String nombre = declarador.split("\\(")[0].trim().split(" ")[1];
        //System.out.println("Funcion: " + nombre);
        Pattern p = Pattern.compile(patron, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(nodo);
        while (m.find()) {
            //System.out.println(" Tiene query");
            buscarTablas(m.group());
        }
    }
    
    public String emparejarTexto(String texto){
        texto = texto.replaceAll("  ", " ");
        texto = texto.replaceAll("\\\\", "");
        texto = texto.replaceAll("\\n", "");
        texto = texto.replaceAll("\\r", "");
        return texto;
    }

    public void buscarTablas(String texto) {
        String query = texto.substring(1, texto.length() - 2).trim();
        query = emparejarTexto(query);
        query = query + ";";

        //System.out.println(query);
        //System.out.println("Query: " + query);
    }
}
