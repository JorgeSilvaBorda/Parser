package modelo;

import controlador.ParseServer2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Conjunto de operaciones útiles.
 *
 * @author Jorge Silva Borda
 */
public class Utilidades {

    public Utilidades() {

    }

    public String getExtension(java.io.File archivo) {
	if (archivo.exists() && archivo.isFile()) {
	    String comp[] = archivo.getName().split("\\.");
	    if (comp.length <= 1 && comp != null) {
		return null;
	    } else {
		return comp[comp.length - 1];
	    }
	}
	return null;
    }

    public String quitarComentariosMake(String texto) {
	String patron = "(^#.*?)(?:(?:\\r*\\n){1})";
	texto = texto.replaceAll(patron, "");
	return texto;
    }

    public String quitarComentariosJavaC_old(String texto) {
	if (texto != null) {
	    String patron = "//.*?\\n|/\\*.*?\\*/";
	    //String patron = "((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/";
	    texto = texto.replaceAll(patron, "");
	    return texto;
	}
	return "";
    }

    public String quitarComentariosJavaC(String texto) {
	if (texto != null) {
	    String patron = "//.*?\\n|/\\*.*?\\*/";
	    texto = texto.replaceAll(patron, "");
	    Pattern p = Pattern.compile(patron, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(texto);
	    while (m.find()) {
		texto = texto.replace(m.group(), "");
	    }
	    return texto;
	}
	return "";
    }

    public String quitarComentariosXML_HTML(String texto) {
	if (texto != null) {
	    String patron = "(<!--)(.*?)((-->)(-->)*)";
	    texto = texto.replaceAll(patron, "");
	    return texto;
	}
	return "";
    }

    public LinkedList<String> quitarRepetidosLista(LinkedList<String> listado) {
	//Revisar algoritmo para quitar duplicados
	for (int i = 0; i < listado.size(); i++) {
	    String aux = listado.get(i);
	    for (int x = i + 1; x < listado.size(); x++) {
		if (aux.equals(listado.get(x))) {
		    listado.set(x, "");
		}
	    }
	}
	return quitarNulosLista(listado);
    }

    public LinkedList<String> quitarNulosLista(LinkedList lista) {
	for (int i = 0; i < lista.size(); i++) {
	    if (lista.get(i).equals("")) {
		lista.remove(i);
		return quitarNulosLista(lista);
	    }
	}
	return lista;
    }

    public String getDirBase(File archivo) {
	return archivo.getParentFile().getAbsolutePath();
    }

    public boolean existeEnLista(Object o, LinkedList lista) {
	for (Object objeto : lista) {
	    if (objeto.toString().equals(o.toString())) {
		return true;
	    }
	}
	return false;
    }

    public String quitarCdata(String texto) {
	String patron1 = "(<\\!\\[CDATA\\[)";
	String patron2 = "\\]\\]";
	texto = texto.replaceAll(patron1, "");
	texto = texto.replaceAll(patron2, "");
	return texto;
    }

    public String getTextoArchivo(File archivo) {
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(archivo));
	    String linea;
	    StringBuilder builder = new StringBuilder();
	    while ((linea = reader.readLine()) != null) {
		builder.append(linea).append(System.getProperty("line.separator"));
	    }
	    return builder.toString();
	} catch (IOException ex) {
	    return "";
	}
    }

    public LinkedList<String[]> bloques(File archivo, String apertura, String cierre) {
	boolean inicio = false;
	int abierto = 0;
	LinkedList<String[]> bloques = new LinkedList();
	StringBuilder builder = new StringBuilder();
	String texto = getTextoArchivo(archivo);
	texto = quitarComentariosJavaC(texto);
	String[] lineas = texto.split(System.getProperty("line.separator"));
	String nombre = "";
	for (int i = 0; i < lineas.length; i++) {
	    String patron = "(void|long|char|short|double|float|int)(\\s+)(\\w+)(\\()";
	    Pattern p = Pattern.compile(patron, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(lineas[i]);
	    if (m.find() && !inicio) {
		nombre = m.group(3);
		//System.out.println("Encuentra: " + nombre);
		inicio = true;
		if (!lineas[i].contains(apertura)) {
		    builder.append(lineas[i]).append(System.getProperty("line.separator"));
		    i++;
		}
	    }
	    if (inicio) {
		builder.append(lineas[i]).append(System.getProperty("line.separator"));
		if (lineas[i].contains(apertura)) {
		    abierto++;
		}
		if (lineas[i].contains(cierre)) {
		    abierto--;
		}
		if (abierto == 0) {
		    inicio = false;
		    bloques.add(new String[]{nombre, builder.toString()});
		    builder = new StringBuilder();
		}
	    }
	}
	return bloques;
    }

    public LinkedList<String[]> bloques(File archivo, String patronInicio, int grupoNombre, String apertura, String cierre) {
	boolean inicio = false;
	int abierto = 0;
	LinkedList<String[]> bloques = new LinkedList();
	StringBuilder builder = new StringBuilder();
	String texto = getTextoArchivo(archivo);
	texto = quitarComentariosJavaC(texto);
	String[] lineas = texto.split(System.getProperty("line.separator"));
	String nombre = "";
	String contenido = "";
	for (int i = 0; i < lineas.length; i++) {
	    Pattern p = Pattern.compile(patronInicio, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(lineas[i]);
	    if (m.find() && !inicio) {
		nombre = m.group(3);
		inicio = true;
		if (!lineas[i].contains(apertura)) {
		    builder.append(lineas[i]).append(System.getProperty("line.separator"));
		    i++;
		}
	    }
	    if (inicio) {
		builder.append(lineas[i]).append(System.getProperty("line.separator"));
		if (lineas[i].contains(apertura)) {
		    abierto++;
		}
		if (lineas[i].contains(cierre)) {
		    abierto--;
		}
		if (abierto == 0) {
		    inicio = false;
		    bloques.add(new String[]{nombre, builder.toString()});
		    builder = new StringBuilder();
		}
	    }
	}
	return bloques;
    }
}
