package datos;

import java.sql.ResultSet;
import java.sql.SQLException;
import logger.Logger;
import modelo.Conexion;
import modelo.servidor.ServicioTuxedo;
import modelo.servidor.ServidorTuxedo;
import modelo.servidor.dependencia.Dependencia;
import modelo.servidor.dependencia.Funcion;

/**
 * @author Jorge Silva Borda
 */
public class Datos {
    private static final Logger logger = new Logger("log.txt");
    private static final Conexion con = new Conexion("172.20.248.81", "ARQUITECTURA", "sa", "Administrador01");

    public static int insertServidorTux(ServidorTuxedo servidor) {
	String query = "EXEC INS_SERVIDORTUX('" + servidor.getNombre() + "', '" + servidor.getArchivoFuente() + "')";
	con.abrir();
	ResultSet rs = con.ejecutarQuery(query);
	try {
	    while (rs.next()) {
		int salida = rs.getInt("IDSERVIDORTUX");
		con.cerrar();
		return salida;
	    }
	} catch (SQLException ex) {
	    logger.log("No se pudo ejecutar: " + query);
	    logger.log(ex);
	    con.cerrar();
	}
	return 0;
    }

    public static int insertarServicio(ServicioTuxedo servicio) {
	String query = "EXEC INS_SERVICIOTUX('" + servicio.getNombre() + "')";
	con.abrir();
	ResultSet rs = con.ejecutarQuery(query);
	try {
	    while (rs.next()) {
		int salida = rs.getInt("IDSERVIDORTUX");
		con.cerrar();
		return salida;
	    }
	} catch (SQLException ex) {
	    logger.log("No se pudo ejecutar: " + query);
	    logger.log(ex);
	    con.cerrar();
	}
	return 0;
    }
    
    public static int insertarServidorServicio(int idServidor, int idServicio){
	    String query = "EXEC INS_SERVIDORSERVICIO(" + idServidor + ", " + idServicio + ")";
	    con.abrir();
	ResultSet rs = con.ejecutarQuery(query);
	try {
	    while (rs.next()) {
		int salida = rs.getInt("IDSERVIDORTUX");
		con.cerrar();
		return salida;
	    }
	} catch (SQLException ex) {
	    logger.log("No se pudo ejecutar: " + query);
	    logger.log(ex);
	    con.cerrar();
	}
	return 0;
    }

    public static void insertarFuncion(Funcion funcion) {

    }

    public static void insertarDependencia(Dependencia dependencia) {

    }
}
