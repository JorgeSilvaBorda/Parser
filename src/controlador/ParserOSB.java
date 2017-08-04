package controlador;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;

/**
 * @author Jorge Silva Borda
 */
public class ParserOSB {
    private File wsdl;
    private String texto;
    
    public ParserOSB(File archivoWsdl){
        //Constructor con el procesamiento completo de limpieza del texto del archivo fuente.
        this.wsdl = archivoWsdl;
        try {
            this.texto = new modelo.Lector(this.wsdl.getAbsolutePath()).getTexto();
            this.texto = new modelo.Utilidades().quitarComentariosXML_HTML(this.texto);
        } catch (IOException ex) {
            System.out.println("No se ha podido extraer el texto del archivo: " + this.wsdl.getAbsolutePath());
            System.out.println(ex);
        }
    }
    
    public void parse(){
	JAXBContext jaxb;
	
    }
    
    public String obtenerNameSpace(){
	String patron = "";
	return "";
    }
}
